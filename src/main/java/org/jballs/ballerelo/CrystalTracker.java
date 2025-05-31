package org.jballs.ballerelo;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class CrystalTracker {

    private final Map<EnderCrystal, UUID> crystalMap = new WeakHashMap<>();

    public void trackCrystal(EnderCrystal crystal, Player placer) {
        crystalMap.put(crystal, placer.getUniqueId());
    }

    public Player getPlacer(EnderCrystal crystal) {
        UUID uuid = crystalMap.get(crystal);
        if (uuid == null) return null;
        return crystal.getWorld().getPlayers().stream()
                .filter(player -> player.getUniqueId().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public void untrackCrystal(EnderCrystal crystal) {
        crystalMap.remove(crystal);
    }
}