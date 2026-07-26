package com.example.crackedguard.util;

import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

/**
 * Kept in its own class so that referencing Floodgate classes only triggers
 * a NoClassDefFoundError when THIS class is actually loaded (i.e. when
 * isBedrockPlayer is called), not when PlayerUtils/the plugin loads.
 * This is what lets Floodgate stay a true optional soft-dependency.
 */
final class FloodgateHook {

    private FloodgateHook() {
    }

    static boolean isBedrockPlayer(UUID uuid) {
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }
}
