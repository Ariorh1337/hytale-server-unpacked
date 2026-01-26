/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TerrainDensityMaterialProvider<V>
extends MaterialProvider<V> {
    @Nonnull
    private final FieldDelimiter<V>[] fieldDelimiters;

    public TerrainDensityMaterialProvider(@Nonnull List<FieldDelimiter<V>> delimiters) {
        this.fieldDelimiters = new FieldDelimiter[delimiters.size()];
        for (FieldDelimiter<V> field : delimiters) {
            if (field != null) continue;
            throw new IllegalArgumentException("delimiters contain null value");
        }
        for (int i = 0; i < delimiters.size(); ++i) {
            this.fieldDelimiters[i] = delimiters.get(i);
        }
    }

    @Override
    @Nullable
    public V getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
        for (FieldDelimiter<V> delimiter : this.fieldDelimiters) {
            if (!delimiter.isInside(context.density)) continue;
            return delimiter.materialProvider.getVoxelTypeAt(context);
        }
        return null;
    }

    public static class FieldDelimiter<V> {
        double top;
        double bottom;
        MaterialProvider<V> materialProvider;

        public FieldDelimiter(@Nonnull MaterialProvider<V> materialProvider, double bottom, double top) {
            this.bottom = bottom;
            this.top = top;
            this.materialProvider = materialProvider;
        }

        boolean isInside(double fieldValue) {
            return fieldValue < this.top && fieldValue >= this.bottom;
        }
    }
}

