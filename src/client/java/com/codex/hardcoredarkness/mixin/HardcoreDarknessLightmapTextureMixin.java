package com.codex.hardcoredarkness.mixin;

import com.codex.hardcoredarkness.lightmap.HardcoreDarknessLightmapAccess;
import com.codex.hardcoredarkness.lightmap.HardcoreDarknessLightmapUploader;
import com.mojang.blaze3d.textures.GpuTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
@Environment(EnvType.CLIENT)
public abstract class HardcoreDarknessLightmapTextureMixin implements HardcoreDarknessLightmapAccess {
    @Shadow @Final private GpuTexture glTexture;
    @Shadow private boolean dirty;
    @Shadow private float flickerIntensity;

    @Override
    public boolean hardcoreDarkness$isDirty() {
        return this.dirty;
    }

    @Override
    public float hardcoreDarkness$previousFlicker() {
        return this.flickerIntensity;
    }

    @Override
    public GpuTexture hardcoreDarkness$gpuTexture() {
        return this.glTexture;
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void hardcoreDarkness$injectUpload(float tickProgress, CallbackInfo ci) {
        HardcoreDarknessLightmapUploader.apply((LightmapTextureManager)(Object)this);
    }
}
