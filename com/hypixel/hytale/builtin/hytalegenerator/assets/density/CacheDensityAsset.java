/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.CacheDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.MultiCacheDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class CacheDensityAsset
extends DensityAsset {
    public static final BuilderCodec<CacheDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(CacheDensityAsset.class, CacheDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Integer>("Capacity", Codec.INTEGER, true), (asset, value) -> {
        asset.capacity = value;
    }, asset -> asset.capacity).addValidator(Validators.greaterThanOrEqual(0)).add()).build();
    public static int DEFAULT_CAPACITY = 3;
    private int capacity = DEFAULT_CAPACITY;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.capacity <= 0) {
            return this.build(argument);
        }
        if (this.capacity == 1) {
            return new CacheDensity(this.buildFirstInput(argument), argument.workerIndexer.getWorkerCount());
        }
        return new MultiCacheDensity(this.buildFirstInput(argument), argument.workerIndexer.getWorkerCount(), this.capacity);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

