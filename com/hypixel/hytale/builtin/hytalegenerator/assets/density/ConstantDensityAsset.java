/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ConstantDensityAsset
extends DensityAsset {
    public static final BuilderCodec<ConstantDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ConstantDensityAsset.class, ConstantDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("Value", Codec.DOUBLE, true), (t, k) -> {
        t.value = k;
    }, k -> k.value).add()).build();
    private double value = 0.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new ConstantValueDensity(this.value);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

