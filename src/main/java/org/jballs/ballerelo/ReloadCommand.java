package org.jballs.ballerelo;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final ballerelo plugin;

    public ReloadCommand(ballerelo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ballerelo.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
            return true;
        }

        plugin.reloadPluginConfig();
        sender.sendMessage(ChatColor.GREEN + "ballerelo configuration reloaded!");
        return true;
    }
}
