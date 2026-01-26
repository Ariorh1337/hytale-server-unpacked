/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.GradientWarpDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class GradientWarpDensityAsset
extends DensityAsset {
    public static final BuilderCodec<GradientWarpDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(GradientWarpDensityAsset.class, GradientWarpDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("SampleRange", Codec.DOUBLE, false), (t, k) -> {
        t.sampleRange = k;
    }, t -> t.sampleRange).addValidator(Validators.greaterThan(0.0)).add()).append(new KeyedCodec<Double>("WarpFactor", Codec.DOUBLE, false), (t, k) -> {
        t.warpFactor = k;
    }, t -> t.warpFactor).add()).append(new KeyedCodec<Boolean>("2D", Codec.BOOLEAN, false), (t, k) -> {
        t.is2d = k;
    }, t -> t.is2d).add()).append(new KeyedCodec<Double>("YFor2D", Codec.DOUBLE, false), (t, k) -> {
        t.y2d = k;
    }, t -> t.y2d).add()).build();
    private double sampleRange = 1.0;
    private double warpFactor = 1.0;
    private boolean is2d = false;
    private double y2d = 0.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        GradientWarpDensity node = new GradientWarpDensity(this.buildFirstInput(argument), this.buildSecondInput(argument), this.sampleRange, this.warpFactor);
        return node;
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

