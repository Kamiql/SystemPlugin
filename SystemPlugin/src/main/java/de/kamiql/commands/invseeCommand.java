package de.kamiql.commands;

import de.kamiql.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record invseeCommand(Main plugin) implements TabExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (!args[0].equals(player.getName())) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        if (Bukkit.getOnlinePlayers().contains(target)) {
                            player.openInventory(target.getInventory());
                        } else {
                            player.sendMessage(Main.getPrefix() + "The player is offline");
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                        }
                    } else {
                        player.sendMessage(Main.getPrefix() + "The player name is invalid!");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                    }
                } else {
                    player.sendMessage(Main.getPrefix() + "You can´t open your own inventory! §8Press §bE §8noob x.x");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                }
            } else {
                player.sendMessage(Main.getPrefix() + "Wrong usage! §8/invsee <player>");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            }
        }
        return false;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return completions;
        }
        return null;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Inventory inventory = event.getInventory();
            if (player.hasPermission("system.command.invsee.admin")) {
                return;
            }

            if (inventory.getType() == InventoryType.PLAYER) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            }
        }
    }
}
