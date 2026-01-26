/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.MaxDensity;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class MaxDensityAsset
extends DensityAsset {
    public static final BuilderCodec<MaxDensityAsset> CODEC = BuilderCodec.builder(MaxDensityAsset.class, MaxDensityAsset::new, DensityAsset.ABSTRACT_CODEC).build();

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new MaxDensity(this.buildInputs(argument, true));
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

