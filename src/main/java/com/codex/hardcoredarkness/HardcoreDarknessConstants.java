package com.codex.hardcoredarkness;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared constants for Hardcore Darkness.
 */
public final class HardcoreDarknessConstants {
    public static final String MOD_ID = "hardcore_darkness";
    public static final String MOD_NAME = "Hardcore Darkness";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Identifier HANDSHAKE_CHANNEL = Identifier.of(MOD_ID, "handshake");
    public static final int HANDSHAKE_PROTOCOL = 0;

    private HardcoreDarknessConstants() {
    }
}
