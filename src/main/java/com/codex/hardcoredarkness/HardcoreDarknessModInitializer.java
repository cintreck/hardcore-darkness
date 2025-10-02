package com.codex.hardcoredarkness;

import com.codex.hardcoredarkness.config.HardcoreDarknessConfigService;
import net.fabricmc.api.ModInitializer;

public final class HardcoreDarknessModInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        HardcoreDarknessConfigService.reload();
        HardcoreDarknessState.replaceWithoutSaving(HardcoreDarknessConfigService.current());
        HardcoreDarknessConstants.LOGGER.info("Hardcore Darkness config loaded");
    }
}
