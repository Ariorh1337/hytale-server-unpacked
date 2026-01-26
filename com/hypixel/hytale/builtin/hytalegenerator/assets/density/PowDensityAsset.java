/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.PowDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class PowDensityAsset
extends DensityAsset {
    public static final BuilderCodec<PowDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(PowDensityAsset.class, PowDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("Exponent", Codec.DOUBLE, true), (t, k) -> {
        t.exponent = k;
    }, t -> t.exponent).add()).build();
    private double exponent = 1.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new PowDensity(this.exponent, this.buildFirstInput(argument));
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

