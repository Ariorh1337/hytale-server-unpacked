/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.DistanceDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.RotatorDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ScaleDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class EllipsoidDensityAsset
extends DensityAsset {
    public static final BuilderCodec<EllipsoidDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(EllipsoidDensityAsset.class, EllipsoidDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec("Curve", CurveAsset.CODEC, true), (t, k) -> {
        t.densityCurveAsset = k;
    }, k -> k.densityCurveAsset).add()).append(new KeyedCodec<Vector3d>("Scale", Vector3d.CODEC, false), (t, k) -> {
        t.scaleVector = k;
    }, k -> k.scaleVector).addValidator((v, r) -> {
        if (v.x == 0.0 || v.y == 0.0 || v.z == 0.0) {
            r.fail("scale vector contains 0.0");
        }
    }).add()).append(new KeyedCodec<Vector3d>("NewYAxis", Vector3d.CODEC, false), (t, k) -> {
        if (k.length() != 0.0) {
            t.newYAxis = k;
        }
    }, k -> k.newYAxis).add()).append(new KeyedCodec<Double>("Spin", Codec.DOUBLE, false), (t, k) -> {
        t.spinAngle = k;
    }, k -> k.spinAngle).add()).build();
    private CurveAsset densityCurveAsset = new ConstantCurveAsset();
    private Vector3d scaleVector = new Vector3d(1.0, 1.0, 1.0);
    @Nonnull
    private Vector3d newYAxis = new Vector3d(0.0, 1.0, 0.0);
    private double spinAngle = 0.0;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped() || this.densityCurveAsset == null) {
            return new ConstantValueDensity(0.0);
        }
        DistanceDensity sphere = new DistanceDensity(this.densityCurveAsset.build());
        ScaleDensity scale = new ScaleDensity(this.scaleVector.x, this.scaleVector.y, this.scaleVector.z, sphere);
        return new RotatorDensity(scale, this.newYAxis, this.spinAngle);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
        this.densityCurveAsset.cleanUp();
    }
}

