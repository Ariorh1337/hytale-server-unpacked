/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import javax.annotation.Nonnull;

public class SolidityMaterialProvider<V>
extends MaterialProvider<V> {
    @Nonnull
    private final MaterialProvider<V> solidMaterialProvider;
    @Nonnull
    private final MaterialProvider<V> emptyMaterialProvider;

    public SolidityMaterialProvider(@Nonnull MaterialProvider<V> solidMaterialProvider, @Nonnull MaterialProvider<V> emptyMaterialProvider) {
        this.solidMaterialProvider = solidMaterialProvider;
        this.emptyMaterialProvider = emptyMaterialProvider;
    }

    @Override
    public V getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
        if (context.depthIntoFloor <= 0) {
            return this.emptyMaterialProvider.getVoxelTypeAt(context);
        }
        return this.solidMaterialProvider.getVoxelTypeAt(context);
    }
}

