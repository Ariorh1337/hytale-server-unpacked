/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.CacheDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.MultiCacheDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.YOverrideDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class Cache2dDensityAsset_Deprecated
extends DensityAsset {
    public static final BuilderCodec<Cache2dDensityAsset_Deprecated> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(Cache2dDensityAsset_Deprecated.class, Cache2dDensityAsset_Deprecated::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("Y", Codec.DOUBLE, false), (t, k) -> {
        t.y = k;
    }, t -> t.y).add()).build();
    private double y = 0.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        DensityAsset inputAsset = this.firstInput();
        if (inputAsset == null || this.isSkipped() || inputAsset.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        Density input = this.buildFirstInput(argument);
        if (input == null) {
            return new ConstantValueDensity(0.0);
        }
        MultiCacheDensity cacheDensity = new MultiCacheDensity(input, argument.workerIndexer.getWorkerCount(), CacheDensityAsset.DEFAULT_CAPACITY);
        return new YOverrideDensity(cacheDensity, this.y);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

