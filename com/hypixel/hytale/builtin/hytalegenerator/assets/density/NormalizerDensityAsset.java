/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.NormalizerDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class NormalizerDensityAsset
extends DensityAsset {
    public static final BuilderCodec<NormalizerDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(NormalizerDensityAsset.class, NormalizerDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("FromMin", Codec.DOUBLE, true), (t, k) -> {
        t.fromMin = k;
    }, k -> k.fromMin).add()).append(new KeyedCodec<Double>("FromMax", Codec.DOUBLE, true), (t, k) -> {
        t.fromMax = k;
    }, k -> k.fromMax).add()).append(new KeyedCodec<Double>("ToMin", Codec.DOUBLE, true), (t, k) -> {
        t.toMin = k;
    }, k -> k.toMin).add()).append(new KeyedCodec<Double>("ToMax", Codec.DOUBLE, true), (t, k) -> {
        t.toMax = k;
    }, k -> k.toMax).add()).build();
    private double fromMin = 0.0;
    private double fromMax = 1.0;
    private double toMin = 0.0;
    private double toMax = 1.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new NormalizerDensity(this.fromMin, this.fromMax, this.toMin, this.toMax, this.buildFirstInput(argument));
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

