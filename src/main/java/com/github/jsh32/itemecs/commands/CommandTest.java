package com.github.jsh32.itemecs.commands;

import com.github.jsh32.itemecs.item.ItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandTest implements CommandExecutor {
    private final ItemManager loader;

    public CommandTest(ItemManager loader) {
        this.loader = loader;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        ((Player) sender).getInventory().addItem(loader.createItem("special_sword"));

        return true;
    }
}
