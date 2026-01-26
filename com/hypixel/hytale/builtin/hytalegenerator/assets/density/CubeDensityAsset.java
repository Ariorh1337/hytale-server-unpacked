/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.CubeDensity;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class CubeDensityAsset
extends DensityAsset {
    public static final BuilderCodec<CubeDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(CubeDensityAsset.class, CubeDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec("Curve", CurveAsset.CODEC, false), (t, k) -> {
        t.densityCurveAsset = k;
    }, k -> k.densityCurveAsset).add()).build();
    private CurveAsset densityCurveAsset = new ConstantCurveAsset();

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped() || this.densityCurveAsset == null) {
            return new ConstantValueDensity(0.0);
        }
        return new CubeDensity(this.densityCurveAsset.build());
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
        this.densityCurveAsset.cleanUp();
    }
}

