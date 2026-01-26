/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.material.SolidMaterial;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import javax.annotation.Nonnull;

public class AllStoneMaterialProvider
extends MaterialProvider<SolidMaterial> {
    private final MaterialCache materialCache;

    public AllStoneMaterialProvider(@Nonnull MaterialCache materialCache) {
        this.materialCache = materialCache;
    }

    @Override
    public SolidMaterial getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
        return context.depthIntoFloor > 0 ? this.materialCache.ROCK_STONE : this.materialCache.EMPTY_AIR;
    }
}

