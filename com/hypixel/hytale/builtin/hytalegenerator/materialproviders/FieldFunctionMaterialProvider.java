/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FieldFunctionMaterialProvider<V>
extends MaterialProvider<V> {
    @Nonnull
    private final Density density;
    @Nonnull
    private final FieldDelimiter<V>[] fieldDelimiters;
    @Nonnull
    private final Density.Context rDensityContext;

    public FieldFunctionMaterialProvider(@Nonnull Density density, @Nonnull List<FieldDelimiter<V>> delimiters) {
        this.density = density;
        this.fieldDelimiters = new FieldDelimiter[delimiters.size()];
        for (FieldDelimiter<V> field : delimiters) {
            if (field != null) continue;
            throw new IllegalArgumentException("delimiters contain null value");
        }
        for (int i = 0; i < delimiters.size(); ++i) {
            this.fieldDelimiters[i] = delimiters.get(i);
        }
        this.rDensityContext = new Density.Context();
    }

    @Override
    @Nullable
    public V getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
        this.rDensityContext.assign(context);
        double densityValue = this.density.process(this.rDensityContext);
        for (FieldDelimiter<V> delimiter : this.fieldDelimiters) {
            if (!delimiter.isInside(densityValue)) continue;
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

