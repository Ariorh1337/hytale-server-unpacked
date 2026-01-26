/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ZOverrideDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ZOverrideDensityAsset
extends DensityAsset {
    public static final BuilderCodec<ZOverrideDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ZOverrideDensityAsset.class, ZOverrideDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("Value", Codec.DOUBLE, true), (t, k) -> {
        t.value = k;
    }, t -> t.value).add()).build();
    private double value = 0.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        Density input = this.buildFirstInput(argument);
        if (input == null) {
            return new ConstantValueDensity(0.0);
        }
        return new ZOverrideDensity(input, this.value);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

