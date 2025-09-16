package com.codex.hardcoredarkness.lightmap;

import com.mojang.blaze3d.textures.GpuTexture;

public interface HardcoreDarknessLightmapAccess {
    boolean hardcoreDarkness$isDirty();

    float hardcoreDarkness$previousFlicker();

    GpuTexture hardcoreDarkness$gpuTexture();
}
