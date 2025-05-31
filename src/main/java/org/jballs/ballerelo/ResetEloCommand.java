package org.jballs.ballerelo;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetEloCommand implements CommandExecutor {

    private final ballerelo plugin;

    public ResetEloCommand(ballerelo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("ballerelo.admin")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        // resets all ELO scores back to 0
        plugin.getEloManager().resetAllElo();

        // updates the scoreboard for all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getScoreboardManager().updatePlayerScoreboard(player);
        }

        sender.sendMessage("§aAll players' ELO has been reset and scoreboards updated.");
        return true;
    }
}
