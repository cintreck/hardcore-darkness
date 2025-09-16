package com.codex.hardcoredarkness.lightmap;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;

@Environment(EnvType.CLIENT)
public final class HardcoreDarknessLightmapUploader {
    private static final NativeImage LIGHTMAP_IMAGE = new NativeImage(NativeImage.Format.RGBA, 16, 16, false);

    private HardcoreDarknessLightmapUploader() {
    }

    public static void apply(LightmapTextureManager manager) {
        if (!HardcoreDarknessLightmapState.enabled) {
            return;
        }

        int[][] colors = HardcoreDarknessLightmapState.colorGrid();
        for (int block = 0; block < 16; block++) {
            for (int sky = 0; sky < 16; sky++) {
                LIGHTMAP_IMAGE.setColor(block, sky, colors[block][sky]);
            }
        }

        RenderSystem.getDevice()
                .createCommandEncoder()
                .writeToTexture(((HardcoreDarknessLightmapAccess) manager).hardcoreDarkness$gpuTexture(), LIGHTMAP_IMAGE);
    }
}
