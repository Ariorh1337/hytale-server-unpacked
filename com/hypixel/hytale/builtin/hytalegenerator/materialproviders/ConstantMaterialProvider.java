/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConstantMaterialProvider<V>
extends MaterialProvider<V> {
    @Nullable
    private final V material;

    public ConstantMaterialProvider(@Nullable V material) {
        this.material = material;
    }

    @Override
    @Nullable
    public V getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
        return this.material;
    }
}

