package org.jballs.ballerelo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreboardManager {

    private final ballerelo plugin;
    private final Map<UUID, Scoreboard> playerScoreboards = new HashMap<>();
    private final Set<UUID> disabledScoreboards = new HashSet<>();


    public ScoreboardManager(ballerelo plugin) {
        this.plugin = plugin;
    }
    public void toggleScoreboard(Player player) {
        UUID uuid = player.getUniqueId();
        if (disabledScoreboards.contains(uuid)) {
            disabledScoreboards.remove(uuid);
            player.sendMessage(ChatColor.GOLD + "Elo scoreboard has been enabled.");
            updatePlayerScoreboard(player);
        } else {
            disabledScoreboards.add(uuid);
            clearScoreboard(player);
            player.sendMessage(ChatColor.GOLD + "Elo scoreboard has been disabled.");
        }
    }
    public void updatePlayerScoreboard(Player player) {
        EloManager eloManager = plugin.getEloManager();
        if (disabledScoreboards.contains(player.getUniqueId())) {
            clearScoreboard(player);
            return;
        }
        Scoreboard scoreboard = playerScoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            playerScoreboards.put(player.getUniqueId(), scoreboard);
        }

        Objective objective = scoreboard.getObjective("eloBoard");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("eloBoard", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.GOLD + "baller pvpers");
        }

        // clears all the previous scores
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        int playerElo = eloManager.getElo(player);
        String playerRank = eloManager.getRank(playerElo);

        objective.getScore("" + ChatColor.YELLOW + "Elo: " + ChatColor.GREEN + playerElo).setScore(16);
        objective.getScore("" + ChatColor.YELLOW + "Rank: " + ChatColor.GREEN + playerRank).setScore(15);

        objective.getScore(" ").setScore(14);
        objective.getScore("" + ChatColor.AQUA + "Top 10 Elo Players").setScore(13);

        List<Map.Entry<String, Integer>> topPlayers = plugin.databaseManager.getEloMap().entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .collect(Collectors.toList());

        int scoreIndex = 12;
        for (int i = 0; i < topPlayers.size(); i++) {
            Map.Entry<String, Integer> entry = topPlayers.get(i);
            UUID uuid = UUID.fromString(entry.getKey());
            int elo = entry.getValue();
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (name == null) name = "Unknown";

            String face = "";
            if (i == 0) face = " \u263A"; // ☺
            else if (i == 1) face = " \u263A"; // ☺
            else if (i == 2) face = " \u263A"; // ☺

            String line = "" + ChatColor.WHITE + (i + 1) + ". " + ChatColor.GREEN + name + ChatColor.GRAY + " (" + elo + ")" + face;

            objective.getScore(line).setScore(scoreIndex);
            scoreIndex--;
        }

        objective.getScore("").setScore(scoreIndex);
        scoreIndex--;

        objective.getScore("" + ChatColor.YELLOW + "Your Stats").setScore(scoreIndex);
        scoreIndex--;
        objective.getScore("" + ChatColor.YELLOW + "Elo: " + ChatColor.GREEN + playerElo).setScore(scoreIndex);
        scoreIndex--;
        objective.getScore("" + ChatColor.YELLOW + "Rank: " + ChatColor.GREEN + playerRank).setScore(scoreIndex);

        player.setScoreboard(scoreboard);
    }

    public void clearScoreboard(Player player) {
        playerScoreboards.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); // Empty scoreboard
    }
}
