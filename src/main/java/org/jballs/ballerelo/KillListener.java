package org.jballs.ballerelo;

import org.bukkit.Bukkit;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.UUID;

public class KillListener implements Listener {

    private final ballerelo plugin;

    public KillListener(ballerelo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Entity killer = victim.getKiller();

        if (killer == null) {
            EntityDamageEvent lastDamage = victim.getLastDamageCause();
            if (lastDamage instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityDamage = (EntityDamageByEntityEvent) lastDamage;
                if (entityDamage.getDamager() instanceof EnderCrystal) {
                    EnderCrystal crystal = (EnderCrystal) entityDamage.getDamager();
                    if (crystal.hasMetadata("dmp.enderCrystalPlacer")) {
                        final List<MetadataValue> metadataValues = crystal.getMetadata("dmp.enderCrystalPlacer");
                        if (!metadataValues.isEmpty()) {
                            final Player finalKiller = Bukkit.getPlayer(
                                    UUID.fromString(metadataValues.get(0).asString())
                            );
                            if (finalKiller == victim) return;
                            doElo(getPlayer(finalKiller.getUniqueId()), victim);
                        }
                    }
                }
            }
        }

        if (killer == null || killer.equals(victim)) return;
        doElo(getPlayer(killer.getUniqueId()), victim);
    }

    public void doElo(Player killer, Player victim) {
        EloManager eloManager = plugin.getEloManager();

        if (eloManager.checkDelay(victim.getUniqueId().toString())) {
            return;
        }

        eloManager.doDelay(victim.getUniqueId().toString());

        int eloGain = plugin.getConfig().getInt("elo-on-kill", 10);
        int eloLoss = plugin.getConfig().getInt("elo-on-death", 5);

        if (eloManager.getElo(victim) < 0) {
            eloGain = 0;
        }

        eloManager.addElo(killer, eloGain);
        eloManager.addElo(victim, -eloLoss);

        killer.sendMessage("\u00a7a[CSGO Ranks] You gained " + eloGain + " ELO! Total: " + eloManager.getElo(killer));
        victim.sendMessage("\u00a7c[CSGO Ranks] You lost " + eloLoss + " ELO! Total: " + eloManager.getElo(victim));

        if (plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().updatePlayerScoreboard(killer);
            plugin.getScoreboardManager().updatePlayerScoreboard(victim);
        }
    }

    public Player getPlayer(UUID playerUUID) {
        return plugin.getServer().getPlayer(playerUUID);
    }
}