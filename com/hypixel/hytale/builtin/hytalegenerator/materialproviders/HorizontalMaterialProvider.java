/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HorizontalMaterialProvider<V>
extends MaterialProvider<V> {
    @Nonnull
    private final MaterialProvider<V> materialProvider;
    private double topY;
    private double bottomY;

    public HorizontalMaterialProvider(@Nonnull MaterialProvider<V> materialProvider, double topY, double bottomY) {
        this.materialProvider = materialProvider;
        this.topY = topY;
        this.bottomY = bottomY;
    }

    @Override
    @Nullable
    public V getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
        if ((double)context.position.y >= this.topY || (double)context.position.y < this.bottomY) {
            return null;
        }
        return this.materialProvider.getVoxelTypeAt(context);
    }
}

