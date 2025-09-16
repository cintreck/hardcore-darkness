package com.codex.hardcoredarkness.mixin;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
@Environment(EnvType.CLIENT)
public abstract class HardcoreDarknessLightmapTextureAllocationMixin {
    @Shadow @Final @Mutable private GpuTexture glTexture;
    @Shadow @Final @Mutable private GpuTextureView glTextureView;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void hardcoreDarkness$replaceTexture(GameRenderer renderer, MinecraftClient client, CallbackInfo ci) {
        GpuDevice device = RenderSystem.getDevice();
        GpuTexture replacement = device.createTexture(
                "Hardcore Darkness Light Texture",
                GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT | GpuTexture.USAGE_COPY_DST,
                TextureFormat.RGBA8,
                16,
                16,
                1,
                1
        );
        replacement.setTextureFilter(FilterMode.LINEAR, false);
        device.createCommandEncoder().clearColorTexture(replacement, -1);

        GpuTextureView replacementView = device.createTextureView(replacement);

        this.glTexture.close();
        this.glTextureView.close();
        this.glTexture = replacement;
        this.glTextureView = replacementView;
    }
}
