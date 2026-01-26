/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.RotatorDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class RotatorDensityAsset
extends DensityAsset {
    public static final BuilderCodec<RotatorDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RotatorDensityAsset.class, RotatorDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Vector3d>("NewYAxis", Vector3d.CODEC, true), (t, k) -> {
        t.newYAxis = k;
    }, t -> t.newYAxis).add()).append(new KeyedCodec<Double>("SpinAngle", Codec.DOUBLE, true), (t, k) -> {
        t.spinAngle = k;
    }, t -> t.spinAngle).add()).build();
    private Vector3d newYAxis = new Vector3d();
    private double spinAngle = 0.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new RotatorDensity(this.buildFirstInput(argument), this.newYAxis, this.spinAngle);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

