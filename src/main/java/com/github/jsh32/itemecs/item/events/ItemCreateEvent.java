package com.github.jsh32.itemecs.item.events;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemCreateEvent<T> extends ItemEvent<T> {
    public ItemCreateEvent(ItemStack itemStack, NBTItem rootNbt, T config, Map<String, Object> componentStore) {
        super(itemStack, rootNbt, config, componentStore);
    }
}
