package com.codex.hardcoredarkness;

import com.codex.hardcoredarkness.config.HardcoreDarknessConfigService;
import com.codex.hardcoredarkness.config.HardcoreDarknessConfigValues;

public final class HardcoreDarknessState {
    private static volatile HardcoreDarknessConfigValues config = HardcoreDarknessConfigService.current();

    private HardcoreDarknessState() {
    }

    public static HardcoreDarknessConfigValues config() {
        return config;
    }

    public static synchronized void refreshFromDisk() {
        config = HardcoreDarknessConfigService.current();
    }

    public static synchronized void apply(HardcoreDarknessConfigValues values) {
        config = HardcoreDarknessConfigService.overwrite(values);
    }

    public static synchronized void replaceWithoutSaving(HardcoreDarknessConfigValues values) {
        config = values;
    }
}
