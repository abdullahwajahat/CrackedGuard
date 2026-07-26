package com.example.crackedguard.commands;

import com.example.crackedguard.CrackedGuard;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Feature 6: commands to add/remove/list cracked players in the exception
 * list (players who are allowed OP and restricted commands anyway).
 *
 * Usage:
 *   /cgexcept add <player>
 *   /cgexcept remove <player>
 *   /cgexcept list
 */
public class ExceptionCommand implements CommandExecutor {

    private final CrackedGuard plugin;

    public ExceptionCommand(CrackedGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("crackedguard.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " <add|remove|list> [player]");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        List<String> exceptions = new ArrayList<>(plugin.getConfig().getStringList("exceptions"));

        switch (sub) {
            case "add": {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " add <player>");
                    return true;
                }
                String name = args[1].toLowerCase(Locale.ROOT);
                if (exceptions.contains(name)) {
                    sender.sendMessage(ChatColor.YELLOW + args[1] + " is already in the exception list.");
                    return true;
                }
                exceptions.add(name);
                plugin.getConfig().set("exceptions", exceptions);
                plugin.saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Added " + args[1] + " to the exception list.");
                break;
            }
            case "remove": {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " remove <player>");
                    return true;
                }
                String name = args[1].toLowerCase(Locale.ROOT);
                if (!exceptions.remove(name)) {
                    sender.sendMessage(ChatColor.YELLOW + args[1] + " was not in the exception list.");
                    return true;
                }
                plugin.getConfig().set("exceptions", exceptions);
                plugin.saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Removed " + args[1] + " from the exception list.");
                break;
            }
            case "list": {
                if (exceptions.isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "The exception list is empty.");
                } else {
                    sender.sendMessage(ChatColor.AQUA + "Exceptions: " + String.join(", ", exceptions));
                }
                break;
            }
            default:
                sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " <add|remove|list> [player]");
        }

        return true;
    }
}
