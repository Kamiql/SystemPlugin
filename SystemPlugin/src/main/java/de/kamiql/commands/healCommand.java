package de.kamiql.commands;

import de.kamiql.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record healCommand(Main plugin) implements TabExecutor {

    private static final Map<String, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        long COOLDOWN_TIME = plugin.getConfig().getLong("Cooldown_Commands.heal.cooldown") * 1000;

        if (sender instanceof Player player) {
            boolean hasBypassPermission = player.hasPermission("system.command.heal.bypass");
            long currentTime = System.currentTimeMillis();
            String playerName = player.getName();

            if (hasBypassPermission) {
                if (args.length > 0) {
                    Player targetPlayer = player.getServer().getPlayer(args[0]);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        healPlayer(targetPlayer);
                        player.sendMessage(Main.getPrefix() + "You have healed " + targetPlayer.getName() + "!");
                        return true;
                    } else {
                        player.sendMessage(Main.getPrefix() + "Player not found or not online.");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                        return false;
                    }
                } else {
                    player.sendMessage(Main.getPrefix() + "You need to specify a player to heal.");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                    return false;
                }
            } else {
                if (cooldowns.containsKey(playerName)) {
                    long lastUsed = cooldowns.get(playerName);
                    long timePassed = currentTime - lastUsed;
                    if (timePassed < COOLDOWN_TIME) {
                        long timeLeft = (COOLDOWN_TIME - timePassed) / 1000;
                        if (timeLeft > 60) {
                            long minutesLeft = timeLeft / 60;
                            player.sendMessage(Main.getPrefix() + "You need to wait §b" + minutesLeft + " §7minutes before using this command again.");
                        } else {
                            player.sendMessage(Main.getPrefix() + "You need to wait §b" + timeLeft + " §7seconds before using this command again.");
                        }
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                        return true;
                    }
                }
                cooldowns.put(playerName, currentTime);
            }

            healPlayer(player);
            player.sendMessage(Main.getPrefix() + "You have been healed!");
            return true;
        }
        return false;
    }

    private void healPlayer(Player player) {
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>();
                for (Player online : Bukkit.getOnlinePlayers()) {
                    completions.add(online.getName());
                }
                return completions;
            }
        }
        return null;
    }
}
