/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.AxisDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class AxisDensityAsset
extends DensityAsset {
    public static final BuilderCodec<AxisDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(AxisDensityAsset.class, AxisDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec("Curve", CurveAsset.CODEC, true), (t, k) -> {
        t.distanceCurveAsset = k;
    }, k -> k.distanceCurveAsset).add()).append(new KeyedCodec<Boolean>("IsAnchored", Codec.BOOLEAN, false), (t, k) -> {
        t.isAnchored = k;
    }, k -> k.isAnchored).add()).append(new KeyedCodec<Vector3d>("Axis", Vector3d.CODEC, false), (t, k) -> {
        t.axis = k;
    }, k -> k.axis).addValidator((v, r) -> {
        if (v.length() == 0.0) {
            r.fail("Axis can't be a zero vector.");
        }
    }).add()).build();
    private CurveAsset distanceCurveAsset = new ConstantCurveAsset();
    private Vector3d axis = new Vector3d(0.0, 1.0, 0.0);
    private boolean isAnchored = false;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped() || this.distanceCurveAsset == null) {
            return new ConstantValueDensity(0.0);
        }
        return new AxisDensity(this.distanceCurveAsset.build(), this.axis, this.isAnchored);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
        this.distanceCurveAsset.cleanUp();
    }
}

