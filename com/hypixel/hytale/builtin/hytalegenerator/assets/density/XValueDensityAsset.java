/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.XValueDensity;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class XValueDensityAsset
extends DensityAsset {
    public static final BuilderCodec<XValueDensityAsset> CODEC = BuilderCodec.builder(XValueDensityAsset.class, XValueDensityAsset::new, DensityAsset.ABSTRACT_CODEC).build();

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new XValueDensity();
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

