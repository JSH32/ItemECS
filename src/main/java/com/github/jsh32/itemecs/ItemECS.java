package com.github.jsh32.itemecs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jsh32.itemecs.commands.CommandTest;
import com.github.jsh32.itemecs.item.ItemConfiguration;
import com.github.jsh32.itemecs.item.ItemManager;
import com.github.jsh32.itemecs.systems.TestSystem;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

class Items {
    public Map<String, ItemConfiguration> items;
}

public final class ItemECS extends JavaPlugin {
    private final ItemManager itemManager = new ItemManager();

    @Override
    public void onEnable() {
        System.out.println("pre");
        // Plugin startup logic
        itemManager.registerSystems(new TestSystem());
        System.out.println("hi");

        String config = "";
        try {
            config = Files.readString(new File(getDataFolder(), "items.yml").toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Yaml yaml = new Yaml(new CustomClassLoaderConstructor(Items.class.getClassLoader()));
        Items itemConfigs = yaml.loadAs(config, Items.class);
        for (var itemConfig : itemConfigs.items.entrySet()) {
            try {
                itemManager.registerItem(itemConfig.getKey(), itemConfig.getValue());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        Objects.requireNonNull(this.getCommand("test")).setExecutor(new CommandTest(itemManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
