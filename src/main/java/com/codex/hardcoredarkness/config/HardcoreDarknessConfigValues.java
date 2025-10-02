package com.codex.hardcoredarkness.config;

public record HardcoreDarknessConfigValues(
        boolean darkOverworld,
        boolean darkNether,
        boolean darkEnd,
        boolean darkDefault,
        boolean darkSkyless,
        double netherFogFactor,
        double endFogFactor,
        boolean blockLightOnly,
        boolean ignoreMoonPhase,
        HardcoreDarknessMoonPhaseStyle moonPhaseStyle) {

    public HardcoreDarknessConfigValues {
        netherFogFactor = clampFog(netherFogFactor);
        endFogFactor = clampFog(endFogFactor);
    }

    private static double clampFog(double value) {
        if (value < 0.0D) {
            return 0.0D;
        }
        return Math.min(1.0D, value);
    }

    public enum HardcoreDarknessMoonPhaseStyle {
        DEFAULT,
        GRADUAL,
        BTW
    }
}
