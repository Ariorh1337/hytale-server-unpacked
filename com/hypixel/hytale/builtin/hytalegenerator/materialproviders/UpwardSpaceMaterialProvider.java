/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpwardSpaceMaterialProvider<V>
extends MaterialProvider<V> {
    @Nonnull
    private final MaterialProvider<V> materialProvider;
    private final int space;

    public UpwardSpaceMaterialProvider(@Nonnull MaterialProvider<V> materialProvider, int space) {
        this.materialProvider = materialProvider;
        this.space = space;
    }

    @Override
    @Nullable
    public V getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
        if (this.space != context.spaceAboveFloor) {
            return null;
        }
        return this.materialProvider.getVoxelTypeAt(context);
    }
}

