package com.codex.hardcoredarkness.client.config;

import com.codex.hardcoredarkness.HardcoreDarknessConstants;
import com.codex.hardcoredarkness.HardcoreDarknessHandshake;
import com.codex.hardcoredarkness.HardcoreDarknessState;
import com.codex.hardcoredarkness.config.HardcoreDarknessConfigService;
import com.codex.hardcoredarkness.config.HardcoreDarknessConfigValues;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class HardcoreDarknessClothScreenFactory {
    private HardcoreDarknessClothScreenFactory() {
    }

    public static Screen create(Screen parent) {
        HardcoreDarknessConfigValues defaults = HardcoreDarknessConfigService.defaults();
        HardcoreDarknessConfigValues current = HardcoreDarknessState.config();

        AtomicBoolean darkOverworld = new AtomicBoolean(current.darkOverworld());
        AtomicBoolean darkNether = new AtomicBoolean(current.darkNether());
        AtomicBoolean darkEnd = new AtomicBoolean(current.darkEnd());
        AtomicBoolean darkDefault = new AtomicBoolean(current.darkDefault());
        AtomicBoolean darkSkyless = new AtomicBoolean(current.darkSkyless());
        AtomicBoolean blockLightOnly = new AtomicBoolean(current.blockLightOnly());
        AtomicBoolean ignoreMoonPhase = new AtomicBoolean(current.ignoreMoonPhase());
        AtomicBoolean requireMod = new AtomicBoolean(current.requireMod());
        AtomicReference<Double> netherFog = new AtomicReference<>(current.netherFogFactor());
        AtomicReference<Double> endFog = new AtomicReference<>(current.endFogFactor());
        AtomicReference<HardcoreDarknessConfigValues.HardcoreDarknessMoonPhaseStyle> moonPhaseStyle =
                new AtomicReference<>(current.moonPhaseStyle());

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("screen.hardcore_darkness.title"));

        builder.setSavingRunnable(() -> {
            HardcoreDarknessConfigValues updated = new HardcoreDarknessConfigValues(
                    darkOverworld.get(),
                    darkNether.get(),
                    darkEnd.get(),
                    darkDefault.get(),
                    darkSkyless.get(),
                    MathHelper.clamp(netherFog.get(), 0.0D, 1.0D),
                    MathHelper.clamp(endFog.get(), 0.0D, 1.0D),
                    blockLightOnly.get(),
                    ignoreMoonPhase.get(),
                    moonPhaseStyle.get(),
                    requireMod.get()
            );

            HardcoreDarknessState.apply(updated);
            if (updated.requireMod()) {
                HardcoreDarknessHandshake.registerServer();
            }
            HardcoreDarknessConstants.LOGGER.info("Hardcore Darkness settings saved");
        });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory toggles = builder.getOrCreateCategory(Text.translatable("category.hardcore_darkness.darkness"));
        toggles.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.hardcore_darkness.dark_overworld"), darkOverworld.get())
                .setDefaultValue(defaults.darkOverworld())
                .setTooltip(Text.translatable("tooltip.hardcore_darkness.dark_overworld"))
                .setSaveConsumer(darkOverworld::set)
                .build());
        toggles.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.hardcore_darkness.dark_nether"), darkNether.get())
                .setDefaultValue(defaults.darkNether())
                .setTooltip(Text.translatable("tooltip.hardcore_darkness.dark_nether"))
                .setSaveConsumer(darkNether::set)
                .build());
        toggles.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.hardcore_darkness.dark_end"), darkEnd.get())
                .setDefaultValue(defaults.darkEnd())
                .setTooltip(Text.translatable("tooltip.hardcore_darkness.dark_end"))
                .setSaveConsumer(darkEnd::set)
                .build());
        toggles.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.hardcore_darkness.dark_default"), darkDefault.get())
                .setDefaultValue(defaults.darkDefault())
                .setTooltip(Text.translatable("tooltip.hardcore_darkness.dark_default"))
                .setSaveConsumer(darkDefault::set)
                .build());
        toggles.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.hardcore_darkness.dark_skyless"), darkSkyless.get())
                .setDefaultValue(defaults.darkSkyless())
                .setTooltip(Text.translatable("tooltip.hardcore_darkness.dark_skyless"))
                .setSaveConsumer(darkSkyless::set)
                .build());
        toggles.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.hardcore_darkness.block_light_only"), blockLightOnly.get())
                .setDefaultValue(defaults.blockLightOnly())
                .setTooltip(Text.translatable("tooltip.hardcore_darkness.block_light_only"))
                .setSaveConsumer(blockLightOnly::set)
                .build());

        ConfigCategory sky = builder.getOrCreateCategory(Text.translatable("category.hardcore_darkness.sky"));
        sky.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.hardcore_darkness.ignore_moon_phase"), ignoreMoonPhase.get())
                .setDefaultValue(defaults.ignoreMoonPhase())
                .setTooltip(Text.translatable("tooltip.hardcore_darkness.ignore_moon_phase"))
                .setSaveConsumer(ignoreMoonPhase::set)
                .build());
        sky.addEntry(entryBuilder.startEnumSelector(Text.translatable("option.hardcore_darkness.moon_phase_style"),
                        HardcoreDarknessConfigValues.HardcoreDarknessMoonPhaseStyle.class, moonPhaseStyle.get())
                .setDefaultValue(defaults.moonPhaseStyle())
                .setTooltip(Text.translatable("tooltip.hardcore_darkness.moon_phase_style"))
                .setSaveConsumer(moonPhaseStyle::set)
                .build());

        ConfigCategory fog = builder.getOrCreateCategory(Text.translatable("category.hardcore_darkness.fog"));
        fog.addEntry(entryBuilder.startDoubleField(Text.translatable("option.hardcore_darkness.nether_fog_factor"), netherFog.get())
                .setDefaultValue(defaults.netherFogFactor())
                .setMin(0.0D)
                .setMax(1.0D)
                .setTooltip(Text.translatable("tooltip.hardcore_darkness.nether_fog_factor"))
                .setSaveConsumer(value -> netherFog.set(MathHelper.clamp(value, 0.0D, 1.0D)))
                .build());
        fog.addEntry(entryBuilder.startDoubleField(Text.translatable("option.hardcore_darkness.end_fog_factor"), endFog.get())
                .setDefaultValue(defaults.endFogFactor())
                .setMin(0.0D)
                .setMax(1.0D)
                .setTooltip(Text.translatable("tooltip.hardcore_darkness.end_fog_factor"))
                .setSaveConsumer(value -> endFog.set(MathHelper.clamp(value, 0.0D, 1.0D)))
                .build());

        ConfigCategory networking = builder.getOrCreateCategory(Text.translatable("category.hardcore_darkness.multiplayer"));
        networking.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.hardcore_darkness.require_mod"), requireMod.get())
                .setDefaultValue(defaults.requireMod())
                .setTooltip(Text.translatable("tooltip.hardcore_darkness.require_mod"))
                .setSaveConsumer(requireMod::set)
                .build());

        return builder.build();
    }
}
