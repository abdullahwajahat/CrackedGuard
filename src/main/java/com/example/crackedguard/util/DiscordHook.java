package com.example.crackedguard.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Talks to DiscordSRV purely via reflection at runtime. This intentionally
 * avoids a compile-time dependency on DiscordSRV/JDA (those artifacts are
 * only available via JitPack, which has to build DiscordSRV from source and
 * is version-fragile). If DiscordSRV isn't installed, or its API shape
 * differs from what we expect, this fails quietly and just logs a warning —
 * it never breaks the rest of the plugin.
 */
public final class DiscordHook {

    private DiscordHook() {
    }

    public static void sendRestartMessage(JavaPlugin plugin) {
        try {
            Plugin discordSrv = plugin.getServer().getPluginManager().getPlugin("DiscordSRV");
            if (discordSrv == null || !discordSrv.isEnabled()) {
                return;
            }

            String message = plugin.getConfig().getString("discord.restart-message", "Server is Restarting");

            // DiscordSRV exposes its main text channel as an instance method
            // directly on the plugin object: discordSrv.getMainTextChannel()
            Method getMainTextChannel = discordSrv.getClass().getMethod("getMainTextChannel");
            Object channel = getMainTextChannel.invoke(discordSrv);

            if (channel == null) {
                plugin.getLogger().warning("DiscordSRV has no main channel configured; " +
                        "could not send the restart message.");
                return;
            }

            // channel is a net.dv8tion.jda.api.entities.TextChannel (or MessageChannel).
            // sendMessage(CharSequence) returns a MessageAction/MessageCreateAction; call .queue() on it.
            Method sendMessage = channel.getClass().getMethod("sendMessage", CharSequence.class);
            Object action = sendMessage.invoke(channel, message);

            Method queue = action.getClass().getMethod("queue");
            queue.invoke(action);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Failed to send Discord restart message", t);
        }
    }
}
