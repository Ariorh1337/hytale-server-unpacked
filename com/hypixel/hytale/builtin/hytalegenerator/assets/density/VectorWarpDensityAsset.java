/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.VectorWarpDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class VectorWarpDensityAsset
extends DensityAsset {
    public static final BuilderCodec<VectorWarpDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(VectorWarpDensityAsset.class, VectorWarpDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("WarpFactor", Codec.DOUBLE, true), (t, k) -> {
        t.warpFactor = k;
    }, t -> t.warpFactor).add()).append(new KeyedCodec<Vector3d>("WarpVector", Vector3d.CODEC, true), (t, k) -> {
        t.warpVector = k;
    }, t -> t.warpVector).add()).build();
    private double warpFactor = 1.0;
    private Vector3d warpVector = new Vector3d();

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new VectorWarpDensity(this.buildFirstInput(argument), this.buildSecondInput(argument), this.warpFactor, this.warpVector);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

