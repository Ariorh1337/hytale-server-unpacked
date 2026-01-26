/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.SmoothCeilingDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class SmoothCeilingDensityAsset
extends DensityAsset {
    public static final BuilderCodec<SmoothCeilingDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SmoothCeilingDensityAsset.class, SmoothCeilingDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("Limit", Codec.DOUBLE, true), (t, k) -> {
        t.limit = k;
    }, k -> k.limit).add()).append(new KeyedCodec<Double>("SmoothRange", Codec.DOUBLE, true), (t, k) -> {
        t.smoothRange = k;
    }, k -> k.smoothRange).addValidator(Validators.greaterThanOrEqual(0.0)).add()).build();
    private double smoothRange = 1.0;
    private double limit = 0.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new SmoothCeilingDensity(this.limit, this.smoothRange, this.buildFirstInput(argument));
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

