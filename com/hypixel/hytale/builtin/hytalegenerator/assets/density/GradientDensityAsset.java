/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.GradientDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class GradientDensityAsset
extends DensityAsset {
    public static final BuilderCodec<GradientDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(GradientDensityAsset.class, GradientDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Vector3d>("Axis", Vector3d.CODEC, false), (t, k) -> {
        t.axis = k;
    }, k -> k.axis).addValidator((v, r) -> {
        if (v.x == 0.0 && v.y == 0.0 && v.z == 0.0) {
            r.fail("Axis can't be zero.");
        }
    }).add()).append(new KeyedCodec<Double>("SampleRange", Codec.DOUBLE, false), (t, k) -> {
        t.sampleRange = k;
    }, t -> t.sampleRange).addValidator(Validators.greaterThan(0.0)).add()).build();
    private Vector3d axis = new Vector3d(0.0, 1.0, 0.0);
    private double sampleRange = 1.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new GradientDensity(this.buildFirstInput(argument), this.sampleRange, this.axis.clone());
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

