/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.SmoothMinDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class SmoothMinDensityAsset
extends DensityAsset {
    public static final BuilderCodec<SmoothMinDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(SmoothMinDensityAsset.class, SmoothMinDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("Range", Codec.DOUBLE, true), (t, k) -> {
        t.range = k;
    }, k -> k.range).addValidator(Validators.greaterThanOrEqual(0.0)).add()).build();
    private double range = 1.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new SmoothMinDensity(this.range, this.buildFirstInput(argument), this.buildSecondInput(argument));
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

