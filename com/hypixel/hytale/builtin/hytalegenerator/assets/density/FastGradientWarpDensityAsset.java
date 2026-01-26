/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.FastGradientWarpDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class FastGradientWarpDensityAsset
extends DensityAsset {
    public static final BuilderCodec<FastGradientWarpDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FastGradientWarpDensityAsset.class, FastGradientWarpDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Float>("WarpScale", Codec.FLOAT, false), (t, k) -> {
        t.warpScale = k.floatValue();
    }, t -> Float.valueOf(t.warpScale)).addValidator(Validators.greaterThan(Float.valueOf(0.0f))).add()).append(new KeyedCodec<Integer>("WarpOctaves", Codec.INTEGER, false), (t, k) -> {
        t.warpOctaves = k;
    }, t -> t.warpOctaves).addValidator(Validators.greaterThan(0)).add()).append(new KeyedCodec<Float>("WarpLacunarity", Codec.FLOAT, false), (t, k) -> {
        t.warpLacunarity = k.floatValue();
    }, t -> Float.valueOf(t.warpLacunarity)).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f))).add()).append(new KeyedCodec<Float>("WarpPersistence", Codec.FLOAT, false), (t, k) -> {
        t.warpPersistence = k.floatValue();
    }, t -> Float.valueOf(t.warpPersistence)).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f))).add()).append(new KeyedCodec<Float>("WarpFactor", Codec.FLOAT, false), (t, k) -> {
        t.warpFactor = k.floatValue();
    }, t -> Float.valueOf(t.warpFactor)).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f))).add()).append(new KeyedCodec<String>("Seed", Codec.STRING, false), (t, k) -> {
        t.seed = k;
    }, t -> t.seed).add()).build();
    private float warpLacunarity = 2.0f;
    private float warpPersistence = 0.5f;
    private int warpOctaves = 1;
    private float warpScale = 1.0f;
    private float warpFactor = 1.0f;
    private String seed = "A";

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new FastGradientWarpDensity(this.buildFirstInput(argument), this.warpLacunarity, this.warpPersistence, this.warpOctaves, 1.0f / this.warpScale, this.warpFactor, argument.parentSeed.child(this.seed).createSupplier().get());
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

