/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ShellDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class ShellDensityAsset
extends DensityAsset {
    public static final BuilderCodec<ShellDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ShellDensityAsset.class, ShellDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Vector3d>("Axis", Vector3d.CODEC, true), (t, k) -> {
        t.axis = k;
    }, k -> k.axis).add()).append(new KeyedCodec<Boolean>("Mirror", Codec.BOOLEAN, false), (t, k) -> {
        t.isMirrored = k;
    }, k -> k.isMirrored).add()).append(new KeyedCodec("AngleCurve", CurveAsset.CODEC, true), (t, k) -> {
        t.angleCurveAsset = k;
    }, k -> k.angleCurveAsset).add()).append(new KeyedCodec("DistanceCurve", CurveAsset.CODEC, true), (t, k) -> {
        t.distanceCurveAsset = k;
    }, k -> k.distanceCurveAsset).add()).build();
    private Vector3d axis = new Vector3d(0.0, 0.0, 0.0);
    private boolean isMirrored = false;
    private CurveAsset angleCurveAsset = new ConstantCurveAsset();
    private CurveAsset distanceCurveAsset = new ConstantCurveAsset();

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped() || this.angleCurveAsset == null || this.distanceCurveAsset == null) {
            return new ConstantValueDensity(0.0);
        }
        return new ShellDensity(this.angleCurveAsset.build(), this.distanceCurveAsset.build(), this.axis, this.isMirrored);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
        this.angleCurveAsset.cleanUp();
        this.distanceCurveAsset.cleanUp();
    }
}

