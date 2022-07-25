package com.github.jsh32.itemecs.item.events;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemInteractEvent<T> extends ItemEvent<T> {
    private final PlayerInteractEvent bukkitEvent;

    public ItemInteractEvent(
            ItemStack itemStack,
            NBTItem rootNbt,
            T config,
            Map<String, Object> componentStore,
            PlayerInteractEvent playerInteractEvent
    ) {
        super(itemStack, rootNbt, config, componentStore);
        this.bukkitEvent = playerInteractEvent;
    }

    /**
     * Get bukkit event corresponding to this interacting.
     *
     * @return PlayerInteractEvent
     */
    public PlayerInteractEvent getBukkitEvent() { return bukkitEvent; }
}
