package com.github.jsh32.itemecs.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.jsh32.itemecs.ItemSystem;
import com.github.jsh32.itemecs.item.events.ItemCreateEvent;
import com.github.jsh32.itemecs.item.events.ItemEvent;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Item ECS manager. Creates and manages items and their components.
 */
// So this whole class is kind of weird and technically unsafe.
// Since we use reflection in order to get the T parameter, it will always match with the systems generic.
// TODO: Make this (and the whole class) normal.
public class ItemManager {
    private final Map<String, ItemSystem<?>> systems = new HashMap<>();
    private final Map<String, ItemConfig> items = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * Item's default configuration and component configurations.
     */
    private record ItemConfig(
            ItemConfiguration item,
            Set<Pair<ItemSystem<?>, Object>> components
    ) {}

    public void registerItem(String itemName, ItemConfiguration item) throws JsonProcessingException {
        // Make sure that the item is not already registered
        if (items.entrySet()
                .stream()
                .filter(i -> i.getKey().equals(itemName) || i.getValue().item.uuid == item.uuid)
                .collect(Collectors.toUnmodifiableSet())
                .size() > 0) {
            throw new IllegalArgumentException(String.format("Item with the UUID %s or name %s was already registered.", item.uuid, itemName));
        }

        Set<Pair<ItemSystem<?>, Object>> components = new HashSet<>();
        for (var entry : item.components.entrySet()) {
            // The key is the component name
            ItemSystem<?> system = this.systems.get(entry.getKey());

            if (system == null)
                throw new IllegalArgumentException(String.format("System responding to component %s does not exist.", entry.getKey()));

            // Two of the same component could not be defined on one entity.
            if (systems.entrySet().stream()
                    .filter(i -> i.getValue().getComponentName().equals(system.getComponentName()))
                    .collect(Collectors.toUnmodifiableSet()).size() > 0) {
                throw new IllegalArgumentException(String.format("Multiple definitions of the %s component.", system.getComponentName()));
            }

            // For some reason we can't use an Object directly as a loader, so we dump it to a string.
            String yamlString = mapper.writeValueAsString(entry.getValue());

            // Map the yaml object to the Component class of the System. Even though this is an object it should still match up
            Object componentInstance = mapper.readValue(yamlString, system.getComponentClass());
            components.add(Pair.of(system, componentInstance));
        }

        this.items.put(itemName, new ItemConfig(item, components));
    }

    @SafeVarargs
    public final <T extends ItemSystem<?>> void registerSystems(@NotNull T... systems) {
        for (T system : systems)
            this.systems.put(system.getComponentName(), system);
    }

    private ItemConfig getItemFromUuid(UUID uuid) {
        for (ItemConfig item : items.values()) {
            if (item.item.uuid.equals(uuid))
                return item;
        }

        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ItemStack createItem(String name) {
        ItemConfig config = this.items.get(name);
        if (config == null)
            throw new IllegalArgumentException(String.format("Invalid item: %s", name));

        ItemStack itemStack = new ItemStack(Material.valueOf(config.item.type));
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(Component.text(config.item.name));
        meta.lore(List.of(Component.text(config.item.description)));
        itemStack.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(itemStack);

        // ItemECS root data container in the NBT data
        Map<String, Object> itemEcsRoot = new HashMap<>();
        itemEcsRoot.put("uuid", config.item.uuid);

        // Root components object container
        Map<String, Object> componentRoot = new HashMap<>();

        for (Pair<ItemSystem<?>, Object> components : config.components) {
            HashMap<String, Object> itemStore = new HashMap<>();

            ItemCreateEvent event = new ItemCreateEvent(itemStack, nbtItem, components.value(), itemStore);
            components.first().onCreate(event);

            // Put the modified internal store as a key in the root store.
            componentRoot.put(components.first().getComponentName(), itemStore);
        }

        // Set the root key after generation
        itemEcsRoot.put("components", componentRoot);
        nbtItem.setObject("itemEcs", itemEcsRoot);

        return nbtItem.getItem();
    }

    @SuppressWarnings("unchecked")
    public <E extends ItemEvent<?>> void dispatchItemEvent(
            ItemStack itemStack,
            BiConsumer<E, ItemSystem<?>> runEvent,
            Class<E> event,
            Object... additionalArgs
    ) {
        NBTItem nbtItem = new NBTItem(itemStack);

        Map<String, Object> itemEcsRoot = nbtItem.getObject("itemEcs", Map.class);
        UUID uuid = UUID.fromString((String) itemEcsRoot.get("uuid"));

        ItemConfig itemConfig = getItemFromUuid(uuid);
        if (itemConfig == null) {
            // TODO: Handle this
            return;
        }

        // Root component object
        Map<String, Object> componentRoot = (Map<String, Object>) itemEcsRoot.get("components");

        for (Pair<ItemSystem<?>, Object> component : itemConfig.components) {
            // Nester component object
            HashMap<String, Object> componentStore = (HashMap<String, Object>) componentRoot.get(component.first().getComponentName());

            try {
                List<Object> arguments = new ArrayList<>();
                arguments.addAll(Arrays.asList(itemStack, nbtItem, component.value(), componentStore));
                arguments.addAll(Arrays.asList(additionalArgs));

                E eventInstance = (E) event.getConstructors()[0].newInstance(arguments.toArray());

                // TODO: Do event
                runEvent.accept(eventInstance, component.first());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Put the modified internal store as a key in the root store.
            componentRoot.put(component.first().getComponentName(), componentStore);
        }

        // Set the root key after event
        itemEcsRoot.put("components", componentRoot);
        nbtItem.setObject("itemEcs", itemEcsRoot);
    }
}
