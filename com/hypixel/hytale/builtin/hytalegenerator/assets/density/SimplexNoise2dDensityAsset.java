/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.CacheDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.MultiCacheDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.Noise2dDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.YOverrideDensity;
import com.hypixel.hytale.builtin.hytalegenerator.fields.noise.SimplexNoiseField;
import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class SimplexNoise2dDensityAsset
extends DensityAsset {
    public static final BuilderCodec<SimplexNoise2dDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SimplexNoise2dDensityAsset.class, SimplexNoise2dDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("Lacunarity", Codec.DOUBLE, true), (asset, lacunarity) -> {
        asset.lacunarity = lacunarity;
    }, asset -> asset.lacunarity).addValidator(Validators.greaterThan(0.0)).add()).append(new KeyedCodec<Double>("Persistence", Codec.DOUBLE, true), (asset, persistence) -> {
        asset.persistence = persistence;
    }, asset -> asset.persistence).addValidator(Validators.greaterThan(0.0)).add()).append(new KeyedCodec<Double>("Scale", Codec.DOUBLE, true), (asset, scale) -> {
        asset.scale = scale;
    }, asset -> asset.scale).addValidator(Validators.greaterThan(0.0)).add()).append(new KeyedCodec<Integer>("Octaves", Codec.INTEGER, true), (asset, octaves) -> {
        asset.octaves = octaves;
    }, asset -> asset.octaves).addValidator(Validators.greaterThan(0)).add()).append(new KeyedCodec<String>("Seed", Codec.STRING, true), (asset, seed) -> {
        asset.seedKey = seed;
    }, asset -> asset.seedKey).add()).build();
    private double lacunarity = 1.0;
    private double persistence = 1.0;
    private double scale = 1.0;
    private int octaves = 1;
    private String seedKey = "A";

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        SeedBox childSeed = argument.parentSeed.child(this.seedKey);
        SimplexNoiseField noise = SimplexNoiseField.builder().withAmplitudeMultiplier(this.persistence).withFrequencyMultiplier(this.lacunarity).withScale(this.scale).withSeed(childSeed.createSupplier().get().intValue()).withNumberOfOctaves(this.octaves).build();
        Noise2dDensity noiseDensity = new Noise2dDensity(noise);
        MultiCacheDensity cacheDensity = new MultiCacheDensity(noiseDensity, argument.workerIndexer.getWorkerCount(), CacheDensityAsset.DEFAULT_CAPACITY);
        return new YOverrideDensity(cacheDensity, 0.0);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

