package org.jballs.ballerelo;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillListener implements Listener {

    private final ballerelo plugin;

    public KillListener(ballerelo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // if there is no direct killer, check for EnderCrystal damage (lowk buggy af rn. doesn't even work)
        if (killer == null) {
            EntityDamageEvent lastDamage = victim.getLastDamageCause();
            if (lastDamage instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityDamage = (EntityDamageByEntityEvent) lastDamage;
                if (entityDamage.getDamager() instanceof EnderCrystal) {
                    EnderCrystal crystal = (EnderCrystal) entityDamage.getDamager();
                    killer = plugin.getCrystalTracker().getPlacer(crystal);
                }
            }
        }

        if (killer == null || killer.equals(victim)) return;

        EloManager eloManager = plugin.getEloManager();
        int eloGain = plugin.getConfig().getInt("elo-on-kill", 10);
        int eloLoss = plugin.getConfig().getInt("elo-on-death", 5);

        eloManager.addElo(killer, eloGain);
        eloManager.addElo(victim, -eloLoss);

        killer.sendMessage("\u00a7a[CSGO Ranks] You gained " + eloGain + " ELO! Total: " + eloManager.getElo(killer));
        victim.sendMessage("\u00a7c[CSGO Ranks] You lost " + eloLoss + " ELO! Total: " + eloManager.getElo(victim));

        if (plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().updatePlayerScoreboard(killer);
            plugin.getScoreboardManager().updatePlayerScoreboard(victim);
        }
    }
}