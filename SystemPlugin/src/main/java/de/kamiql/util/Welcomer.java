package de.kamiql.util;

import de.kamiql.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Welcomer implements Listener {

    private static final String ROOT = "Welcomer";
    private static FileConfiguration config;
    private static Main plugin;

    public Welcomer(Main plugin) {
        Welcomer.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        config = plugin.getConfig();

        if (config.getBoolean(ROOT + ".leave.broadcast.enabled")) {
            String message = config.getString(ROOT + ".leave.broadcast.message");
            message = replacePlaceholders(message, event.getPlayer());
            message = ChatColor.translateAlternateColorCodes('&', message);
            event.setQuitMessage(message);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        config = plugin.getConfig();
        Player player = event.getPlayer();

        if (config.getBoolean(ROOT + ".join.broadcast.enabled")) {
            String messagePath = !player.hasPlayedBefore() ? ROOT + ".join.broadcast.firstjoin.message" : ROOT + ".join.broadcast.before.message";
            List<String> messages = config.getStringList(messagePath);
            for (String msg : messages) {
                msg = replacePlaceholders(msg, player);
                msg = ChatColor.translateAlternateColorCodes('&', msg);
                plugin.getServer().broadcastMessage(msg);
                event.setJoinMessage("");
            }
        }

        if (config.getBoolean(ROOT + ".join.player.enabled")) {
            String messagePath = !player.hasPlayedBefore() ? ROOT + ".join.player.firstjoin.message" : ROOT + ".join.player.before.message";
            List<String> messages = config.getStringList(messagePath);
            for (String msg : messages) {
                msg = replacePlaceholders(msg, player);
                msg = ChatColor.translateAlternateColorCodes('&', msg);
                player.sendMessage(msg);
            }
        }
    }

    private String replacePlaceholders(String message, Player player) {
        message = message.replace("{name}", player.getName());
        message = message.replace("{nick}", player.getDisplayName());

        int timeIndex = message.indexOf("{time:");
        while (timeIndex != -1) {
            int endIndex = message.indexOf('}', timeIndex);
            if (endIndex != -1) {
                String format = message.substring(timeIndex + 6, endIndex); // extract format
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                String currentTime = dateFormat.format(new Date());
                message = message.substring(0, timeIndex) + currentTime + message.substring(endIndex + 1);
                timeIndex = message.indexOf("{time:", timeIndex + currentTime.length());
            } else {
                break;
            }
        }

        return message;
    }
}
