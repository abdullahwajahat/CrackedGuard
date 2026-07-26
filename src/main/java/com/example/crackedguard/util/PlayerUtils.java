package com.example.crackedguard.util;

import org.bukkit.entity.Player;

import java.util.UUID;

public final class PlayerUtils {

    private PlayerUtils() {
    }

    /**
     * Determines whether a player is using a "cracked" (offline-mode style) UUID.
     * <p>
     * Mojang-authenticated (premium) accounts are always issued a version 4
     * (random) UUID by Mojang. Offline-mode UUIDs, generated with
     * {@code UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes())},
     * are always version 3 (name-based). This lets us tell cracked players
     * apart from premium ones regardless of the server's own online-mode
     * setting (useful behind a proxy that allows both).
     */
    public static boolean isCracked(Player player) {
        UUID uuid = player.getUniqueId();
        return uuid.version() == 3;
    }

    /**
     * Determines whether a player connected via Bedrock/Pocket Edition,
     * using the Floodgate API if it is installed. Returns false safely
     * (and never throws) if Floodgate is not present on the server.
     */
    public static boolean isBedrock(Player player) {
        try {
            return FloodgateHook.isBedrockPlayer(player.getUniqueId());
        } catch (Throwable t) {
            return false;
        }
    }
}
