/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.CurveMapperDensity;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class CurveMapperDensityAsset
extends DensityAsset {
    public static final BuilderCodec<CurveMapperDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(CurveMapperDensityAsset.class, CurveMapperDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec("Curve", CurveAsset.CODEC, true), (t, k) -> {
        t.curveAsset = k;
    }, k -> k.curveAsset).add()).build();
    private CurveAsset curveAsset = new ConstantCurveAsset();

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new CurveMapperDensity(this.curveAsset.build(), this.buildFirstInput(argument));
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
        this.curveAsset.cleanUp();
    }
}

