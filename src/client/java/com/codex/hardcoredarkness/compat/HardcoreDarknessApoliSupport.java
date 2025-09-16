package com.codex.hardcoredarkness.compat;

import com.codex.hardcoredarkness.HardcoreDarknessConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;

import java.lang.reflect.Method;

public final class HardcoreDarknessApoliSupport {
    private static final String COMPONENT_CLASS = "io.github.apace100.apoli.component.PowerHolderComponent";
    private static final String POWER_TYPE_CLASS = "io.github.apace100.apoli.power.type.NightVisionPowerType";

    public boolean hasNightVisionPower(MinecraftClient client) {
        if (!FabricLoader.getInstance().isModLoaded("apoli")) {
            return false;
        }

        LivingEntity player = client.player;
        if (player == null) {
            return false;
        }

        try {
            Class<?> component = Class.forName(COMPONENT_CLASS);
            Class<?> powerType = Class.forName(POWER_TYPE_CLASS);
            Method hasPowerType = component.getMethod("hasPowerType", LivingEntity.class, Class.class);
            Object result = hasPowerType.invoke(null, player, powerType);
            return result instanceof Boolean bool && bool;
        } catch (ReflectiveOperationException | LinkageError exception) {
            HardcoreDarknessConstants.LOGGER.debug("Apoli night vision detection unavailable: {}", exception.toString());
            return false;
        }
    }
}
