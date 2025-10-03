package com.codex.hardcoredarkness.lightmap;

import com.codex.hardcoredarkness.HardcoreDarknessState;
import com.codex.hardcoredarkness.compat.HardcoreDarknessApoliSupport;
import com.codex.hardcoredarkness.config.HardcoreDarknessConfigValues;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public final class HardcoreDarknessLightmapState {
    private static final int SIZE = 16;
    private static final int[][] COLOR_GRID = new int[SIZE][SIZE];
    private static final float[] BTW_MOON_BRIGHTNESS_BY_PHASE = new float[]{1.25F, 0.875F, 0.75F, 0.5F, 0F, 0.5F, 0.75F, 1.25F};
    private static final HardcoreDarknessApoliSupport APOLI_SUPPORT = new HardcoreDarknessApoliSupport();

    private static HardcoreDarknessConfigValues cachedConfig;
    private static double cachedNetherFog = 1.0D;
    private static double cachedEndFog = 1.0D;

    public static boolean enabled;

    private HardcoreDarknessLightmapState() {
    }

    public static double netherFogFactor() {
        refreshFogCache();
        return cachedNetherFog;
    }

    public static double endFogFactor() {
        refreshFogCache();
        return cachedEndFog;
    }

    public static int colorAt(int blockIndex, int skyIndex) {
        return COLOR_GRID[blockIndex][skyIndex];
    }

    public static int[][] colorGrid() {
        return COLOR_GRID;
    }

    public static void updateLuminance(float tickDelta, MinecraftClient client, GameRenderer renderer, float previousFlicker) {
        ClientWorld world = client.world;
        if (world == null || client.player == null) {
            enabled = false;
            clearGrid();
            return;
        }

        HardcoreDarknessConfigValues config = HardcoreDarknessState.config();
        if (!shouldDarken(world, config)
                || client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)
                || client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER) && client.player.getUnderwaterVisibility() > 0.0F
                || world.getLightningTicksLeft() > 0
                || APOLI_SUPPORT.hasNightVisionPower(client)) {
            enabled = false;
            clearGrid();
            return;
        }

        enabled = true;
        float skyFactorScale = computeSkyFactor(world, config);
        float ambientSky = world.getSkyBrightness(1.0F);
        DimensionType dimension = world.getDimension();
        boolean blockAmbient = !shouldDarken(world, config);

        float skyDarkness = Math.max(0.0F, renderer.getSkyDarkness(tickDelta));
        float gammaSetting = client.options.getGamma().getValue().floatValue();

        for (int skyIndex = 0; skyIndex < SIZE; skyIndex++) {
            float skyFactor = 1.0F - skyIndex / 15.0F;
            skyFactor = 1.0F - skyFactor * skyFactor * skyFactor * skyFactor;
            skyFactor *= skyFactorScale;

            float blend = skyFactor * 0.05F;
            float rawAmbient = ambientSky * skyFactor;
            float minAmbient = rawAmbient * (1.0F - blend) + blend;
            float skyBase = LightmapTextureManager.getBrightness(dimension, skyIndex) * minAmbient;

            blend = 0.35F * skyFactor;
            float skyRed = skyBase * (rawAmbient * (1.0F - blend) + blend);
            float skyGreen = skyBase * (rawAmbient * (1.0F - blend) + blend);
            float skyBlue = skyBase;

            if (skyDarkness > 0.0F) {
                skyRed = skyRed * (1.0F - skyDarkness) + skyRed * 0.7F * skyDarkness;
                skyGreen = skyGreen * (1.0F - skyDarkness) + skyGreen * 0.6F * skyDarkness;
                skyBlue = skyBlue * (1.0F - skyDarkness) + skyBlue * 0.6F * skyDarkness;
            }

            for (int blockIndex = 0; blockIndex < SIZE; blockIndex++) {
                float blockFactor = 1.0F;
                if (!blockAmbient) {
                    blockFactor = 1.0F - blockIndex / 15.0F;
                    blockFactor = 1.0F - blockFactor * blockFactor * blockFactor * blockFactor;
                }

                float blockBase = blockFactor * LightmapTextureManager.getBrightness(dimension, blockIndex)
                        * (previousFlicker * 0.1F + 1.5F);
                blend = 0.4F * blockFactor;
                float blockGreen = blockBase * ((blockBase * (1.0F - blend) + blend) * (1.0F - blend) + blend);
                float blockBlue = blockBase * (blockBase * blockBase * (1.0F - blend) + blend);

                float red = skyRed + blockBase;
                float green = skyGreen + blockGreen;
                float blue = skyBlue + blockBlue;

                float dominant = Math.max(skyFactor, blockFactor);
                blend = 0.03F * dominant;
                red = red * (0.99F - blend) + blend;
                green = green * (0.99F - blend) + blend;
                blue = blue * (0.99F - blend) + blend;

                RegistryKey<World> key = world.getRegistryKey();
                if (World.END.equals(key)) {
                    red = skyFactor * 0.22F + blockBase * 0.75F;
                    green = skyFactor * 0.28F + blockGreen * 0.75F;
                    blue = skyFactor * 0.25F + blockBlue * 0.75F;
                }

                red = MathHelper.clamp(red, 0.0F, 1.0F);
                green = MathHelper.clamp(green, 0.0F, 1.0F);
                blue = MathHelper.clamp(blue, 0.0F, 1.0F);

                float gamma = gammaSetting * dominant;
                float invRed = 1.0F - red;
                float invGreen = 1.0F - green;
                float invBlue = 1.0F - blue;
                invRed = 1.0F - invRed * invRed * invRed * invRed;
                invGreen = 1.0F - invGreen * invGreen * invGreen * invGreen;
                invBlue = 1.0F - invBlue * invBlue * invBlue * invBlue;
                red = red * (1.0F - gamma) + invRed * gamma;
                green = green * (1.0F - gamma) + invGreen * gamma;
                blue = blue * (1.0F - gamma) + invBlue * gamma;

                blend = 0.03F * dominant;
                red = red * (0.99F - blend) + blend;
                green = green * (0.99F - blend) + blend;
                blue = blue * (0.99F - blend) + blend;

                red = MathHelper.clamp(red, 0.0F, 1.0F);
                green = MathHelper.clamp(green, 0.0F, 1.0F);
                blue = MathHelper.clamp(blue, 0.0F, 1.0F);

                COLOR_GRID[blockIndex][skyIndex] = packColor(red, green, blue);
            }
        }
    }

    private static void refreshFogCache() {
        HardcoreDarknessConfigValues config = HardcoreDarknessState.config();
        if (config != cachedConfig) {
            cachedConfig = config;
            cachedNetherFog = config.darkNether() ? config.netherFogFactor() : 1.0D;
            cachedEndFog = config.darkEnd() ? config.endFogFactor() : 1.0D;
        }
    }

    private static boolean shouldDarken(ClientWorld world, HardcoreDarknessConfigValues config) {
        RegistryKey<World> key = world.getRegistryKey();
        if (World.OVERWORLD.equals(key)) {
            return config.darkOverworld();
        }
        if (World.NETHER.equals(key)) {
            return config.darkNether();
        }
        if (World.END.equals(key)) {
            return config.darkEnd();
        }
        if (world.getDimension().hasSkyLight()) {
            return config.darkDefault();
        }
        return config.darkSkyless();
    }

    private static float computeSkyFactor(ClientWorld world, HardcoreDarknessConfigValues config) {
        if (config.blockLightOnly() || !shouldDarken(world, config)) {
            return 1.0F;
        }

        // In 1.21.9+, the End has skylight (for sky flashes), but should still be dark
        RegistryKey<World> key = world.getRegistryKey();
        if (World.END.equals(key)) {
            return 0.0F;
        }

        DimensionType dimension = world.getDimension();
        if (!dimension.hasSkyLight()) {
            return 0.0F;
        }

        float angle = world.getSkyAngle(0.0F);
        if (angle <= 0.25F || angle >= 0.75F) {
            return 1.0F;
        }

        float weight = Math.max(0.0F, (Math.abs(angle - 0.5F) - 0.2F)) * 20.0F;
        float moonValue = config.ignoreMoonPhase() ? 0.0F : world.getMoonSize();
        float moonBrightness;
        switch (config.moonPhaseStyle()) {
            case DEFAULT -> moonBrightness = moonValue * moonValue;
            case GRADUAL -> moonBrightness = moonValue;
            case BTW -> {
                int phase = world.getMoonPhase();
                moonBrightness = BTW_MOON_BRIGHTNESS_BY_PHASE[MathHelper.clamp(phase, 0, BTW_MOON_BRIGHTNESS_BY_PHASE.length - 1)];
            }
            default -> moonBrightness = moonValue;
        }
        return MathHelper.lerp(weight * weight * weight, moonBrightness, 1.0F);
    }

    private static int packColor(float red, float green, float blue) {
        int r = Math.round(red * 255.0F);
        int g = Math.round(green * 255.0F);
        int b = Math.round(blue * 255.0F);
        return 0xFF000000 | r | (g << 8) | (b << 16);
    }

    private static void clearGrid() {
        for (int blockIndex = 0; blockIndex < SIZE; blockIndex++) {
            for (int skyIndex = 0; skyIndex < SIZE; skyIndex++) {
                COLOR_GRID[blockIndex][skyIndex] = 0xFF000000;
            }
        }
    }
}
