package com.example.crackedguard.util;

import github.scarsz.discordsrv.DiscordSRV;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Kept in its own class so referencing DiscordSRV/JDA classes only triggers
 * a NoClassDefFoundError when THIS class is actually loaded (i.e. when
 * sendRestartMessage is called), keeping DiscordSRV a true optional
 * soft-dependency.
 */
public final class DiscordHook {

    private DiscordHook() {
    }

    public static void sendRestartMessage(JavaPlugin plugin) {
        try {
            String message = plugin.getConfig().getString("discord.restart-message", "Server is Restarting");
            TextChannel channel = DiscordSRV.getPlugin().getMainTextChannel();
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else {
                plugin.getLogger().warning("DiscordSRV has no main channel configured; " +
                        "could not send the restart message.");
            }
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Failed to send Discord restart message", t);
        }
    }
}
