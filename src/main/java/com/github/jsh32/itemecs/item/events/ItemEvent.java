package com.github.jsh32.itemecs.item.events;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemEvent<T> {
    /**
     * Data stored within the item instance.
     */
    private final Map<String, Object> componentStore;

    private final ItemStack itemStack;
    private final NBTItem rootNbt;
    private final T config;

    /**
     * Get the root NBT tag of the item.
     *
     * @return NBT Item
     */
    public NBTItem getRootNbt() { return rootNbt; }

    /**
     * Get a persistent store of the component.
     * The item will store this data in NBT.
     *
     * @return component persistent store.
     */
    public Map<String, Object> getComponentStore() { return componentStore; }

    /**
     * Get the configuration for this system for this item.
     *
     * @return item configuration.
     */
    public T getConfig() { return config; }

    /**
     * Get the {@link ItemStack} of the item.
     *
     * @return item stack
     */
    public ItemStack getItemStack() { return itemStack; }

    public ItemEvent(ItemStack itemStack, NBTItem rootNbt, T config, Map<String, Object> componentStore) {
        this.itemStack = itemStack;
        this.rootNbt = rootNbt;
        this.config = config;
        this.componentStore = componentStore;
    }
}
