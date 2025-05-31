package org.jballs.ballerelo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EloCommand implements CommandExecutor {

    private final ballerelo plugin;

    public EloCommand(ballerelo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;
        EloManager eloManager = plugin.getEloManager();
        int elo = eloManager.getElo(player);
        String rank = eloManager.getRank(elo);

        player.sendMessage("§b[CSGO Ranks] Your ELO: " + elo + " | Rank: " + rank);
        return true;
    }
}