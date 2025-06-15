package org.jballs.ballerelo;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EloManager {

    private final ballerelo plugin;
    private final BukkitScheduler scheduler;
    public final Set<String> eloDelay = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final TreeMap<Integer, String> rankMap = new TreeMap<>();

    public EloManager(ballerelo plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
        loadRanks();
    }

    public void loadRanks() {
        FileConfiguration config = plugin.getConfig();
        if (config.getConfigurationSection("ranks") == null) return; // avoid NPE

        for (String rank : config.getConfigurationSection("ranks").getKeys(false)) {
            int elo = config.getInt("ranks." + rank);
            rankMap.put(elo, rank);
        }
    }

    public int getElo(Player player) {
        return plugin.databaseManager.getElo(player.getUniqueId().toString());
    }

    public void addElo(Player player, int amount) {
        plugin.databaseManager.addElo(player.getUniqueId().toString(), amount);
    }

    public void doDelay(String uuid) {
        eloDelay.add(uuid);
        scheduler.runTaskLater(plugin, () -> eloDelay.remove(uuid), 300);
    }

    public boolean checkDelay(String playerUUID) {
        return eloDelay.contains(playerUUID);
    }

    public String getRank(int elo) {
        Map.Entry<Integer, String> entry = rankMap.floorEntry(elo);
        return entry != null ? entry.getValue() : "UnRank";
    }
}
