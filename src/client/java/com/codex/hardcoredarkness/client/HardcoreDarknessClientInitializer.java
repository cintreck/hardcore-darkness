package com.codex.hardcoredarkness.client;

import com.codex.hardcoredarkness.HardcoreDarknessState;
import com.codex.hardcoredarkness.config.HardcoreDarknessConfigService;
import net.fabricmc.api.ClientModInitializer;

public final class HardcoreDarknessClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HardcoreDarknessConfigService.reload();
        HardcoreDarknessState.replaceWithoutSaving(HardcoreDarknessConfigService.current());
    }
}
