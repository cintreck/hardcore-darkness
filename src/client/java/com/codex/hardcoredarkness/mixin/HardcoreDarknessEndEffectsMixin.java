package com.codex.hardcoredarkness.mixin;

import com.codex.hardcoredarkness.lightmap.HardcoreDarknessLightmapState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionEffects.End.class)
@Environment(EnvType.CLIENT)
public abstract class HardcoreDarknessEndEffectsMixin {
    private static final double MIN_COMPONENT = 0.029999999329447746D;

    @Inject(method = "adjustFogColor", at = @At("RETURN"), cancellable = true)
    private void hardcoreDarkness$dimEndFog(Vec3d color, float sunHeight, CallbackInfoReturnable<Vec3d> cir) {
        double factor = HardcoreDarknessLightmapState.endFogFactor();
        if (factor != 1.0D) {
            Vec3d result = cir.getReturnValue();
            cir.setReturnValue(new Vec3d(
                    Math.max(MIN_COMPONENT, result.x * factor),
                    Math.max(MIN_COMPONENT, result.y * factor),
                    Math.max(MIN_COMPONENT, result.z * factor)
            ));
        }
    }
}
