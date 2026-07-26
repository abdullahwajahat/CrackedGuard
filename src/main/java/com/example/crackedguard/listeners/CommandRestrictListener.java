package com.example.crackedguard.listeners;

import com.example.crackedguard.CrackedGuard;
import com.example.crackedguard.util.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Locale;

public class CommandRestrictListener implements Listener {

    private final CrackedGuard plugin;

    public CommandRestrictListener(CrackedGuard plugin) {
        this.plugin = plugin;
    }

    /**
     * Feature 4: if a cracked player runs a command not intended for normal
     * players, kick them with a reason — unless they're in the exception list.
     */
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        boolean cracked = !PlayerUtils.isBedrock(player) && PlayerUtils.isCracked(player);
        if (!cracked || plugin.isExempt(player.getName())) {
            return;
        }

        String baseCommand = extractBaseCommand(event.getMessage());

        if (plugin.getRestrictedCommands().contains(baseCommand)) {
            event.setCancelled(true);
            String kickMsg = plugin.getConfig().getString("kick-messages.command-kick",
                    "&cThat command is not available to cracked accounts.");
            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', kickMsg));
        }
    }

    /**
     * Extracts the base command name from a raw chat command, lower-cased,
     * stripping the leading "/" and any "plugin:" alias prefix.
     * e.g. "/Essentials:GameMode creative" -> "gamemode"
     */
    private String extractBaseCommand(String rawMessage) {
        String withoutSlash = rawMessage.startsWith("/") ? rawMessage.substring(1) : rawMessage;
        String firstToken = withoutSlash.split(" ", 2)[0];

        int colon = firstToken.indexOf(':');
        String base = colon >= 0 ? firstToken.substring(colon + 1) : firstToken;

        return base.toLowerCase(Locale.ROOT);
    }
}
