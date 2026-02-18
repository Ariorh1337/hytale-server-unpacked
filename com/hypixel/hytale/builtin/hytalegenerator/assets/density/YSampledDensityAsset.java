/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.YSampledDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class YSampledDensityAsset
extends DensityAsset {
    @Nonnull
    public static final BuilderCodec<YSampledDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(YSampledDensityAsset.class, YSampledDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("SampleDistance", Codec.DOUBLE, true), (asset, value) -> {
        asset.sampleDistance = value;
    }, asset -> asset.sampleDistance).addValidator(Validators.greaterThan(0.0)).add()).append(new KeyedCodec<Double>("SampleOffset", Codec.DOUBLE, true), (asset, value) -> {
        asset.sampleOffset = value;
    }, asset -> asset.sampleOffset).add()).build();
    private double sampleDistance = 4.0;
    private double sampleOffset = 0.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.sampleDistance <= 0.0) {
            return new ConstantValueDensity(0.0);
        }
        return new YSampledDensity(this.buildFirstInput(argument), this.sampleDistance, this.sampleOffset);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

