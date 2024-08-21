package de.kamiql.util;

import de.kamiql.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreBoard {
    private final ScoreboardManager manager;
    private final Scoreboard scoreboard;
    private final Objective objective;

    public ScoreBoard() {
        manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("example", "dummy", "Title: " + Main.getPrefix());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void setPlayerScore(Player player) {
        Score score = objective.getScore(player.getName());
        score.setScore(1);

        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard(Player player) {
        player.setScoreboard(scoreboard);
    }
}
