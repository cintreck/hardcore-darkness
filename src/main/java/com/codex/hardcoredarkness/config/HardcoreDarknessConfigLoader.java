package com.codex.hardcoredarkness.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HardcoreDarknessConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger("hardcore-darkness-config");
    private static final String FILE_NAME = "hardcore_darkness.toml";

    private HardcoreDarknessConfigLoader() {
    }

    public static HardcoreDarknessConfigValues loadOrCreate(HardcoreDarknessConfigValues defaults) {
        Path path = configPath();
        if (!Files.exists(path)) {
            writeConfig(path, defaults);
            return defaults;
        }

        try (CommentedFileConfig file = openFile(path)) {
            file.load();
            HardcoreDarknessConfigValues loaded = fromToml(file, defaults);
            writeToFile(file, loaded);
            return loaded;
        } catch (Throwable throwable) {
            LOGGER.error("Failed to load {}: {}", FILE_NAME, throwable.toString(), throwable);
            writeConfig(path, defaults);
            return defaults;
        }
    }

    public static HardcoreDarknessConfigValues overwrite(HardcoreDarknessConfigValues values) {
        Path path = configPath();
        writeConfig(path, values);
        return values;
    }

    private static HardcoreDarknessConfigValues fromToml(CommentedConfig config, HardcoreDarknessConfigValues defaults) {
        boolean darkOverworld = readBoolean(config, "dark_overworld", defaults.darkOverworld());
        boolean darkNether = readBoolean(config, "dark_nether", defaults.darkNether());
        boolean darkEnd = readBoolean(config, "dark_end", defaults.darkEnd());
        boolean darkDefault = readBoolean(config, "dark_default", defaults.darkDefault());
        boolean darkSkyless = readBoolean(config, "dark_skyless", defaults.darkSkyless());
        double netherFogFactor = readDouble(config, "dark_nether_fog_factor", defaults.netherFogFactor());
        double endFogFactor = readDouble(config, "dark_end_fog_factor", defaults.endFogFactor());
        boolean blockLightOnly = readBoolean(config, "block_light_only", defaults.blockLightOnly());
        boolean ignoreMoonPhase = readBoolean(config, "ignore_moon_phase", defaults.ignoreMoonPhase());
        HardcoreDarknessConfigValues.HardcoreDarknessMoonPhaseStyle moonPhaseStyle = readMoonPhase(config, defaults.moonPhaseStyle());
        boolean requireMod = readBoolean(config, "require_server_mod", defaults.requireMod());

        return new HardcoreDarknessConfigValues(
                darkOverworld,
                darkNether,
                darkEnd,
                darkDefault,
                darkSkyless,
                netherFogFactor,
                endFogFactor,
                blockLightOnly,
                ignoreMoonPhase,
                moonPhaseStyle,
                requireMod
        );
    }

    private static boolean readBoolean(CommentedConfig config, String key, boolean fallback) {
        Object value = config.get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value != null) {
            LOGGER.warn("{} contains non-boolean value for '{}', using {}", FILE_NAME, key, fallback);
        }
        return fallback;
    }

    private static double readDouble(CommentedConfig config, String key, double fallback) {
        Object value = config.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value != null) {
            LOGGER.warn("{} contains non-numeric value for '{}', using {}", FILE_NAME, key, fallback);
        }
        return fallback;
    }

    private static HardcoreDarknessConfigValues.HardcoreDarknessMoonPhaseStyle readMoonPhase(
            CommentedConfig config,
            HardcoreDarknessConfigValues.HardcoreDarknessMoonPhaseStyle fallback
    ) {
        Object value = config.get("moon_phase_style");
        if (value instanceof String raw) {
            String normalized = raw.trim().toUpperCase();
            try {
                return HardcoreDarknessConfigValues.HardcoreDarknessMoonPhaseStyle.valueOf(normalized);
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("{} has invalid moon_phase_style '{}', using {}", FILE_NAME, raw, fallback);
            }
        } else if (value != null) {
            LOGGER.warn("{} contains non-string value for 'moon_phase_style', using {}", FILE_NAME, fallback);
        }
        return fallback;
    }

    private static void writeConfig(Path path, HardcoreDarknessConfigValues values) {
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException ioException) {
            LOGGER.error("Failed to create config directory for {}", FILE_NAME, ioException);
            return;
        }

        try (CommentedFileConfig file = openFile(path)) {
            file.load();
            writeToFile(file, values);
        } catch (Throwable throwable) {
            LOGGER.error("Failed to write {}: {}", FILE_NAME, throwable.toString(), throwable);
        }
    }

    private static CommentedFileConfig openFile(Path path) {
        return CommentedFileConfig.builder(path)
                .preserveInsertionOrder()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
    }

    private static void writeToFile(CommentedFileConfig file, HardcoreDarknessConfigValues values) {
        file.set("#", "Hardcore Darkness configuration");
        file.set("#1", "Toggles for each dimension and fog adjustments");

        file.set("dark_overworld", values.darkOverworld());
        file.setComment("dark_overworld", "If true, darken overworld nights");

        file.set("dark_nether", values.darkNether());
        file.setComment("dark_nether", "If true, darken the Nether");

        file.set("dark_end", values.darkEnd());
        file.setComment("dark_end", "If true, darken the End");

        file.set("dark_default", values.darkDefault());
        file.setComment("dark_default", "Apply darkness to sky-lit custom dimensions");

        file.set("dark_skyless", values.darkSkyless());
        file.setComment("dark_skyless", "Apply darkness to skyless custom dimensions");

        file.set("dark_nether_fog_factor", values.netherFogFactor());
        file.setComment("dark_nether_fog_factor", "Multiplier for Nether fog color (0=black, 1=vanilla)");

        file.set("dark_end_fog_factor", values.endFogFactor());
        file.setComment("dark_end_fog_factor", "Multiplier for End fog color");

        file.set("block_light_only", values.blockLightOnly());
        file.setComment("block_light_only", "If true, only darken block light and leave sky alone");

        file.set("ignore_moon_phase", values.ignoreMoonPhase());
        file.setComment("ignore_moon_phase", "If true, ignore moon phase when darkening the sky");

        file.set("moon_phase_style", values.moonPhaseStyle().name());
        file.setComment("moon_phase_style", "DEFAULT, GRADUAL, or BTW blending curves");

        file.set("require_server_mod", values.requireMod());
        file.setComment("require_server_mod", "If true, disconnect when connecting to servers without the mod");

        file.save();
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }
}
