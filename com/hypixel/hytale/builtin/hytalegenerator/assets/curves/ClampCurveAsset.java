/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.curves;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Calculator;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import javax.annotation.Nonnull;

public class ClampCurveAsset
extends CurveAsset {
    public static final BuilderCodec<ClampCurveAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ClampCurveAsset.class, ClampCurveAsset::new, CurveAsset.ABSTRACT_CODEC).append(new KeyedCodec("Curve", CurveAsset.CODEC, false), (t, k) -> {
        t.curveAsset = k;
    }, k -> k.curveAsset).add()).append(new KeyedCodec<Double>("WallA", Codec.DOUBLE, false), (t, k) -> {
        t.wallA = k;
    }, k -> k.wallA).add()).append(new KeyedCodec<Double>("WallB", Codec.DOUBLE, false), (t, k) -> {
        t.wallB = k;
    }, k -> k.wallB).add()).build();
    private CurveAsset curveAsset = new ConstantCurveAsset();
    private double wallA = 1.0;
    private double wallB = -1.0;

    @Override
    @Nonnull
    public Double2DoubleFunction build() {
        double defaultValue = (this.wallA + this.wallB) / 2.0;
        if (this.curveAsset == null) {
            return in -> defaultValue;
        }
        Double2DoubleFunction inputCurve = this.curveAsset.build();
        return in -> {
            double value = inputCurve.applyAsDouble(in);
            value = Calculator.clamp(this.wallA, value, this.wallB);
            return value;
        };
    }

    @Override
    public void cleanUp() {
        this.curveAsset.cleanUp();
    }
}

