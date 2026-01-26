/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DownwardDepthMaterialProvider<V>
extends MaterialProvider<V> {
    @Nonnull
    private final MaterialProvider<V> materialProvider;
    private final int depth;

    public DownwardDepthMaterialProvider(@Nonnull MaterialProvider<V> materialProvider, int depth) {
        this.materialProvider = materialProvider;
        this.depth = depth;
    }

    @Override
    @Nullable
    public V getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
        if (this.depth != context.depthIntoFloor) {
            return null;
        }
        return this.materialProvider.getVoxelTypeAt(context);
    }
}

