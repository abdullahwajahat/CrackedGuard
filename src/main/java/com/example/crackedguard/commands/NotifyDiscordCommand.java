package com.example.crackedguard.commands;

import com.example.crackedguard.CrackedGuard;
import com.example.crackedguard.util.DiscordHook;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Feature 5: lets an admin manually trigger the "Server is Restarting"
 * DiscordSRV message (the same message is also sent automatically whenever
 * the plugin is disabled, i.e. on server stop/restart).
 */
public class NotifyDiscordCommand implements CommandExecutor {

    private final CrackedGuard plugin;

    public NotifyDiscordCommand(CrackedGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("crackedguard.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (!plugin.isDiscordSrvEnabled()) {
            sender.sendMessage(ChatColor.RED + "DiscordSRV is not installed/enabled on this server.");
            return true;
        }

        DiscordHook.sendRestartMessage(plugin);
        sender.sendMessage(ChatColor.GREEN + "Restart message sent to Discord.");
        return true;
    }
}
