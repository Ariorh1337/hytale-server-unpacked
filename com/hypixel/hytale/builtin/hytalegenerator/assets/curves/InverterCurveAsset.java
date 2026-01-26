/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.curves;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import javax.annotation.Nonnull;

public class InverterCurveAsset
extends CurveAsset {
    public static final BuilderCodec<InverterCurveAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(InverterCurveAsset.class, InverterCurveAsset::new, CurveAsset.ABSTRACT_CODEC).append(new KeyedCodec("Curve", CurveAsset.CODEC, true), (t, k) -> {
        t.curveAsset = k;
    }, k -> k.curveAsset).add()).build();
    private CurveAsset curveAsset = new ConstantCurveAsset();

    @Override
    @Nonnull
    public Double2DoubleFunction build() {
        if (this.curveAsset == null) {
            return in -> 0.0;
        }
        Double2DoubleFunction inputCurve = this.curveAsset.build();
        return in -> -inputCurve.applyAsDouble(in);
    }

    @Override
    public void cleanUp() {
        this.curveAsset.cleanUp();
    }
}

