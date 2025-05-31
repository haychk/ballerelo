package org.jballs.ballerelo;

import org.bukkit.plugin.java.JavaPlugin;

public class ballerelo extends JavaPlugin {

    private static ballerelo instance;
    private EloManager eloManager;
    private ScoreboardManager scoreboardManager;  // <-- Add this

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        eloManager = new EloManager(this);
        scoreboardManager = new ScoreboardManager(this);

        getServer().getPluginManager().registerEvents(new KillListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new CrystalPlaceListener(this), this);

        getCommand("elo").setExecutor(new EloCommand(this));
        getCommand("reloadelo").setExecutor(new ReloadCommand(this));
        getCommand("resetelo").setExecutor(new ResetEloCommand(this));
    }



    public static ballerelo getInstance() {
        return instance;
    }

    public EloManager getEloManager() {
        return eloManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;  // <-- Add this getter
    }
    public void reloadPluginConfig() {
        reloadConfig();
        saveConfig();  // ensures defaults are saved if missing

        if (eloManager != null) {
            eloManager.loadRanks();  // reload ranks from new config
        }
    }
    private final CrystalTracker crystalTracker = new CrystalTracker();

    public CrystalTracker getCrystalTracker() {
        return crystalTracker;
    }

}
