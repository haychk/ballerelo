package org.jballs.ballerelo;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class EloManager {

    private final ballerelo plugin;
    private final Map<UUID, Integer> eloMap = new HashMap<>();
    private final TreeMap<Integer, String> rankMap = new TreeMap<>();

    public EloManager(ballerelo plugin) {
        this.plugin = plugin;
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
        return eloMap.getOrDefault(player.getUniqueId(), 0);
    }

    public void addElo(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int newElo = getElo(player) + amount;
        eloMap.put(uuid, newElo);
    }

    public String getRank(int elo) {
        Map.Entry<Integer, String> entry = rankMap.floorEntry(elo);
        return entry != null ? entry.getValue() : "UnRank";
    }

    // exposes an unmodifiable copy of the eloMap for safe external use
    public Map<UUID, Integer> getEloMap() {
        return Collections.unmodifiableMap(eloMap);
    }
    public void resetAllElo() {
        eloMap.clear(); // assuming the elo data is stored in a Map<UUID, Integer> called eloMap
    }

}
