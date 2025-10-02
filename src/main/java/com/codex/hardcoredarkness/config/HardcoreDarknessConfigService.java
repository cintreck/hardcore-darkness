package com.codex.hardcoredarkness.config;

public final class HardcoreDarknessConfigService {
    private static final HardcoreDarknessConfigValues DEFAULTS = new HardcoreDarknessConfigValues(
            true,
            true,
            true,
            true,
            true,
            0.5D,
            0.0D,
            false,
            false,
            HardcoreDarknessConfigValues.HardcoreDarknessMoonPhaseStyle.DEFAULT
    );

    private static volatile HardcoreDarknessConfigValues current = HardcoreDarknessConfigLoader.loadOrCreate(DEFAULTS);

    private HardcoreDarknessConfigService() {
    }

    public static HardcoreDarknessConfigValues current() {
        return current;
    }

    public static synchronized void reload() {
        current = HardcoreDarknessConfigLoader.loadOrCreate(DEFAULTS);
    }

    public static synchronized HardcoreDarknessConfigValues overwrite(HardcoreDarknessConfigValues values) {
        current = HardcoreDarknessConfigLoader.overwrite(values);
        return current;
    }

    public static HardcoreDarknessConfigValues defaults() {
        return DEFAULTS;
    }
}
