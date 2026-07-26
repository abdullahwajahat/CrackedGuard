package com.example.crackedguard.listeners;

import com.example.crackedguard.CrackedGuard;
import com.example.crackedguard.util.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinListener implements Listener {

    private final CrackedGuard plugin;

    public PlayerJoinListener(CrackedGuard plugin) {
        this.plugin = plugin;
    }

    /**
     * Feature 3: if a cracked player has OP, kick them at login (unless exempt).
     * Done at PlayerLoginEvent rather than PlayerJoinEvent so the kick happens
     * before the player actually spawns into the world.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return; // something else already denied this login
        }

        Player player = event.getPlayer();
        boolean cracked = !PlayerUtils.isBedrock(player) && PlayerUtils.isCracked(player);

        if (cracked && player.isOp() && !plugin.isExempt(player.getName())) {
            String msg = plugin.getConfig().getString("kick-messages.op-kick",
                    "&cCracked accounts are not allowed to have OP.");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    /**
     * Features 1 & 2: prefix cracked players with "+" and Bedrock players with ".".
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (PlayerUtils.isBedrock(player)) {
            applyPrefix(player, ".");
        } else if (PlayerUtils.isCracked(player)) {
            applyPrefix(player, "+");
        }
    }

    private void applyPrefix(Player player, String prefix) {
        String name = prefix + player.getName();
        player.setDisplayName(name);
        player.setPlayerListName(name);
    }
}
