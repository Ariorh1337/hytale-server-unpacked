/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ScaleDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ScaleDensityAsset
extends DensityAsset {
    public static final BuilderCodec<ScaleDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ScaleDensityAsset.class, ScaleDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("ScaleX", Codec.DOUBLE, false), (t, k) -> {
        t.scaleX = k;
    }, k -> k.scaleX).add()).append(new KeyedCodec<Double>("ScaleY", Codec.DOUBLE, false), (t, k) -> {
        t.scaleY = k;
    }, k -> k.scaleY).add()).append(new KeyedCodec<Double>("ScaleZ", Codec.DOUBLE, false), (t, k) -> {
        t.scaleZ = k;
    }, k -> k.scaleZ).add()).build();
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private double scaleZ = 1.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new ScaleDensity(this.scaleX, this.scaleY, this.scaleZ, this.buildFirstInput(argument));
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

