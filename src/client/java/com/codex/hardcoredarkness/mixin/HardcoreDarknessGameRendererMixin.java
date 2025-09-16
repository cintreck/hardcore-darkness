package com.codex.hardcoredarkness.mixin;

import com.codex.hardcoredarkness.lightmap.HardcoreDarknessLightmapAccess;
import com.codex.hardcoredarkness.lightmap.HardcoreDarknessLightmapState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class HardcoreDarknessGameRendererMixin {
    @Shadow private MinecraftClient client;
    @Shadow private LightmapTextureManager lightmapTextureManager;

    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void hardcoreDarkness$prepareLightmap(RenderTickCounter renderTickCounter, CallbackInfo ci) {
        HardcoreDarknessLightmapAccess access = (HardcoreDarknessLightmapAccess) this.lightmapTextureManager;
        if (access.hardcoreDarkness$isDirty()) {
            float tickProgress = renderTickCounter.getTickProgress(true);
            HardcoreDarknessLightmapState.updateLuminance(tickProgress, this.client, (GameRenderer)(Object)this, access.hardcoreDarkness$previousFlicker());
        }
    }
}
