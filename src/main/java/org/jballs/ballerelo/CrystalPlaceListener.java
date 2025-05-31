package org.jballs.ballerelo;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class CrystalPlaceListener implements Listener {

    private final ballerelo plugin;

    public CrystalPlaceListener(ballerelo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCrystalSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EnderCrystal)) return;

        EnderCrystal crystal = (EnderCrystal) entity;

        // checks if spawned on obsidian or bedrock
        Block blockBelow = crystal.getLocation().subtract(0, 1, 0).getBlock();
        if (blockBelow.getType() != Material.OBSIDIAN && blockBelow.getType() != Material.BEDROCK) return;

        // tries to find nearby player as the placer
        for (Player player : crystal.getWorld().getNearbyPlayers(crystal.getLocation(), 5)) {
            plugin.getCrystalTracker().trackCrystal(crystal, player);
            plugin.getLogger().info("Crystal placed by " + player.getName() + " is now tracked.");
            break;
        }
    }
}