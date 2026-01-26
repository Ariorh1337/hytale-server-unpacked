/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.material.SolidMaterial;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import javax.annotation.Nonnull;

public class GrassTopMaterialProvider
extends MaterialProvider<SolidMaterial> {
    private final SolidMaterial grass;
    private final SolidMaterial dirt;
    private final SolidMaterial stone;
    private final SolidMaterial empty;

    public GrassTopMaterialProvider(@Nonnull SolidMaterial grass, @Nonnull SolidMaterial dirt, @Nonnull SolidMaterial stone, @Nonnull SolidMaterial empty) {
        this.grass = grass;
        this.dirt = dirt;
        this.stone = stone;
        this.empty = empty;
    }

    @Override
    public SolidMaterial getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
        if (context.depthIntoFloor == 1) {
            return this.grass;
        }
        if (context.depthIntoFloor > 1 && context.depthIntoFloor <= 3) {
            return this.dirt;
        }
        if (context.depthIntoFloor > 3) {
            return this.stone;
        }
        return this.empty;
    }
}

