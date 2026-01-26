/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.YValueDensity;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class YValueDensityAsset
extends DensityAsset {
    public static final BuilderCodec<YValueDensityAsset> CODEC = BuilderCodec.builder(YValueDensityAsset.class, YValueDensityAsset::new, DensityAsset.ABSTRACT_CODEC).build();

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new YValueDensity();
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

