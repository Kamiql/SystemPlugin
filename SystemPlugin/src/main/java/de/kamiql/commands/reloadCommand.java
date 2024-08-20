package de.kamiql.commands;

import de.kamiql.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record reloadCommand(Main plugin) implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (args[0].equals("config")) {
                    player.sendMessage(Main.getPrefix() + "Config is reloading...");
                    plugin.reloadConfig();
                    player.sendMessage(Main.getPrefix() + "§aReload completed!");
                }
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>();
                completions.add("config");
                return completions;
            }
        }
        return null;
    }
}
