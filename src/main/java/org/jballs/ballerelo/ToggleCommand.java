package org.jballs.ballerelo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class ToggleCommand implements CommandExecutor {

    private final ballerelo plugin;

    public ToggleCommand(ballerelo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + "Only players can use this command.");
            return true;
        }


        Player player = (Player) sender;
        plugin.getScoreboardManager().toggleScoreboard(player);
        return true;
    }
}
