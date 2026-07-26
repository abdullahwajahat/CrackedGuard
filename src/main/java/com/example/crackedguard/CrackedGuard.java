package com.example.crackedguard;

import com.example.crackedguard.commands.ExceptionCommand;
import com.example.crackedguard.commands.NotifyDiscordCommand;
import com.example.crackedguard.listeners.CommandRestrictListener;
import com.example.crackedguard.listeners.PlayerJoinListener;
import com.example.crackedguard.util.DiscordHook;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CrackedGuard extends JavaPlugin {

    private static CrackedGuard instance;

    private boolean floodgateEnabled;
    private boolean discordSrvEnabled;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        floodgateEnabled = getServer().getPluginManager().getPlugin("floodgate") != null;
        discordSrvEnabled = getServer().getPluginManager().getPlugin("DiscordSRV") != null;

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandRestrictListener(this), this);

        getCommand("cgexcept").setExecutor(new ExceptionCommand(this));
        getCommand("cgnotify").setExecutor(new NotifyDiscordCommand(this));

        getLogger().info("CrackedGuard enabled. Floodgate present: " + floodgateEnabled
                + " | DiscordSRV present: " + discordSrvEnabled);
    }

    @Override
    public void onDisable() {
        // Announce the restart/shutdown to Discord via DiscordSRV, if available.
        if (discordSrvEnabled && getConfig().getBoolean("discord.enabled", true)) {
            DiscordHook.sendRestartMessage(this);
        }
    }

    public static CrackedGuard getInstance() {
        return instance;
    }

    public boolean isFloodgateEnabled() {
        return floodgateEnabled;
    }

    public boolean isDiscordSrvEnabled() {
        return discordSrvEnabled;
    }

    public List<String> getExceptions() {
        return getConfig().getStringList("exceptions").stream()
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());
    }

    public boolean isExempt(String playerName) {
        return getExceptions().contains(playerName.toLowerCase(Locale.ROOT));
    }

    public List<String> getRestrictedCommands() {
        return getConfig().getStringList("restricted-commands").stream()
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());
    }
}
