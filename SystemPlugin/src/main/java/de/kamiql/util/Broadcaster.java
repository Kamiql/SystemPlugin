package de.kamiql.util;

import de.kamiql.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Broadcaster {

    private static final String ROOT = "BroadcastMessages";

    private static FileConfiguration config;
    private static Main plugin;


    public Broadcaster(Main plugin) {
        Broadcaster.plugin = plugin;
        createDefaults();
    }

    public void startBroadcast() {
        config = plugin.getConfig();
        long MESSAGE_DELAY = config.getLong(ROOT + ".delay") * 60 * 20;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (config.getBoolean(ROOT + ".enabled")) {
                if (!Bukkit.getOnlinePlayers().isEmpty()) {

                    String message;
                    if ((message = pickMessage()) != null) {
                        Bukkit.broadcastMessage("-----");
                        Bukkit.broadcastMessage(Main.getPrefix() + ChatColor.translateAlternateColorCodes('&', message));
                        Bukkit.broadcastMessage("-----");

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        }
                    }
                }
            }
        }, 0, MESSAGE_DELAY );
    }

    private void createDefaults() {
        config = plugin.getConfig();
        if (config.contains(ROOT)) return;
        List<String> defaults = new ArrayList<>();
        config.set(ROOT + ".enabled", true);
        config.set(ROOT + ".delay", 1);
        for (int i = 0; i < 3; i++)
            defaults.add("&7Testnachricht NR " + i);
        config.set(ROOT + ".messages", defaults);
        plugin.saveConfig();
    }

    private String pickMessage() {
        config = plugin.getConfig();
        List<String> messages = config.getStringList(ROOT + ".messages");
        int random = new Random().nextInt(messages.size());

        if (random > 0) {
            return messages.get(random);
        }

        return null;
    }

}
