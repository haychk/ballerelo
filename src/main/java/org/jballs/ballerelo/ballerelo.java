package org.jballs.ballerelo;

import org.bukkit.plugin.java.JavaPlugin;

public class ballerelo extends JavaPlugin {

    private EloManager eloManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        eloManager = new EloManager(this);
        scoreboardManager = new ScoreboardManager(this);

        getServer().getPluginManager().registerEvents(new KillListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        getCommand("elo").setExecutor(new EloCommand(this));
        getCommand("reloadelo").setExecutor(new ReloadCommand(this));
        getCommand("resetelo").setExecutor(new ResetEloCommand(this));
        this.getCommand("togglescoreboard").setExecutor(new ToggleCommand(this));


    }

    public EloManager getEloManager() {
        return eloManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
    public void reloadPluginConfig() {
        reloadConfig();
        saveConfig();

        if (eloManager != null) {
            eloManager.loadRanks();
        }
    }
}
