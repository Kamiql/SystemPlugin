package de.kamiql.commands;

import de.kamiql.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;

import static org.bukkit.Bukkit.getServer;

public class vanishCommand implements CommandExecutor, Listener {

    private final Main plugin;
    private static final HashSet<Player> vanishedPlayers = new HashSet<>();

    public vanishCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (vanishedPlayers.contains(player)) {
            for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                onlinePlayer.showPlayer(plugin, player);
            }
            vanishedPlayers.remove(player);
            player.sendTitle("§eVanish", "§7Du bist nun §aSICHTBAR§7!");
        } else {
            for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("system.commands.vanish.bypass")) {
                    onlinePlayer.hidePlayer(plugin, player);
                }
            }
            vanishedPlayers.add(player);
            player.sendTitle("§eVanish", "§7Du bist nun §cUNSICHTBAR§7!");
        }


        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joinedPlayer = event.getPlayer();
        if (vanishedPlayers.contains(joinedPlayer)) {
            joinedPlayer.sendTitle("§eVanish", "§7Du bist noch im §cVANISH§7!");
        } else {
            for (Player vanishedPlayer : vanishedPlayers) {
                joinedPlayer.hidePlayer(plugin, vanishedPlayer);
            }
        }
    }
}
