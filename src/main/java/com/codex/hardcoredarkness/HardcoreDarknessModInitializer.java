package com.codex.hardcoredarkness;

import com.codex.hardcoredarkness.config.HardcoreDarknessConfigService;
import net.fabricmc.api.ModInitializer;

public final class HardcoreDarknessModInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        HardcoreDarknessConfigService.reload();
        HardcoreDarknessState.replaceWithoutSaving(HardcoreDarknessConfigService.current());
        HardcoreDarknessConstants.LOGGER.info("Hardcore Darkness config loaded: requireMod={}", HardcoreDarknessState.config().requireMod());

        if (HardcoreDarknessState.config().requireMod()) {
            HardcoreDarknessHandshake.registerServer();
            HardcoreDarknessConstants.LOGGER.info("Server handshake enabled");
        }
    }
}
