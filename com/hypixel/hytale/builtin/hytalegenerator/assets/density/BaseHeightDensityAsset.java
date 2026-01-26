/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.BaseHeightDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiDouble2DoubleFunction;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.BaseHeightReference;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;

public class BaseHeightDensityAsset
extends DensityAsset {
    public static final BuilderCodec<BaseHeightDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BaseHeightDensityAsset.class, BaseHeightDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<String>("BaseHeightName", Codec.STRING, false), (t, k) -> {
        t.baseHeightName = k;
    }, t -> t.baseHeightName).add()).append(new KeyedCodec<Boolean>("Distance", Codec.BOOLEAN, false), (t, k) -> {
        t.isDistance = k;
    }, t -> t.isDistance).add()).build();
    private String baseHeightName = "";
    private boolean isDistance = false;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        BaseHeightReference heightDataLayer = argument.referenceBundle.getLayerWithName(this.baseHeightName, BaseHeightReference.class);
        if (heightDataLayer == null) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atConfig()).log("Couldn't find height data layer with name \"" + this.baseHeightName + "\", using a zero-constant Density node.");
            return new ConstantValueDensity(0.0);
        }
        BiDouble2DoubleFunction yFunction = heightDataLayer.getHeightFunction();
        return new BaseHeightDensity(yFunction, this.isDistance);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

