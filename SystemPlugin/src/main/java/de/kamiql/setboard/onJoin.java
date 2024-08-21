package de.kamiql.setboard;

import de.kamiql.util.ScoreBoard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.setPlayerScore(player);
    }
}
