/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.AnchorDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class AnchorDensityAsset
extends DensityAsset {
    public static final BuilderCodec<AnchorDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(AnchorDensityAsset.class, AnchorDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Boolean>("Reversed", Codec.BOOLEAN, false), (t, k) -> {
        t.isReversed = k;
    }, k -> k.isReversed).add()).build();
    private boolean isReversed = false;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new AnchorDensity(this.buildFirstInput(argument), this.isReversed);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

