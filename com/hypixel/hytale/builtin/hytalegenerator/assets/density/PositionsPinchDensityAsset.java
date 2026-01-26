/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.ListPositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.PositionsHorizontalPinchDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.PositionsPinchDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class PositionsPinchDensityAsset
extends DensityAsset {
    public static final BuilderCodec<PositionsPinchDensityAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PositionsPinchDensityAsset.class, PositionsPinchDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec("Positions", PositionProviderAsset.CODEC, true), (asset, v) -> {
        asset.positionProviderAsset = v;
    }, asset -> asset.positionProviderAsset).add()).append(new KeyedCodec("PinchCurve", CurveAsset.CODEC, true), (asset, v) -> {
        asset.pinchCurveAsset = v;
    }, asset -> asset.pinchCurveAsset).add()).append(new KeyedCodec<Double>("MaxDistance", Codec.DOUBLE, true), (asset, v) -> {
        asset.maxDistance = v;
    }, asset -> asset.maxDistance).addValidator(Validators.greaterThanOrEqual(0.0)).add()).append(new KeyedCodec<Boolean>("NormalizeDistance", Codec.BOOLEAN, true), (asset, v) -> {
        asset.normalizeDistance = v;
    }, asset -> asset.normalizeDistance).add()).append(new KeyedCodec<Boolean>("HorizontalPinch", Codec.BOOLEAN, false), (asset, v) -> {
        asset.isHorizontal = v;
    }, asset -> asset.isHorizontal).add()).append(new KeyedCodec<Double>("PositionsMinY", Codec.DOUBLE, false), (asset, v) -> {
        asset.positionsMinY = v;
    }, asset -> asset.positionsMinY).add()).append(new KeyedCodec<Double>("PositionsMaxY", Codec.DOUBLE, false), (asset, v) -> {
        asset.positionsMaxY = v;
    }, asset -> asset.positionsMaxY).add()).build();
    private PositionProviderAsset positionProviderAsset = new ListPositionProviderAsset();
    private CurveAsset pinchCurveAsset = new ConstantCurveAsset();
    private double maxDistance = 0.0;
    private boolean normalizeDistance = false;
    private boolean isHorizontal = false;
    private double positionsMinY = 0.0;
    private double positionsMaxY = 1.0E-6;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        if (this.isHorizontal) {
            PositionsHorizontalPinchDensity node = new PositionsHorizontalPinchDensity(this.buildFirstInput(argument), this.positionProviderAsset.build(new PositionProviderAsset.Argument(argument.parentSeed, argument.referenceBundle, argument.workerIndexer)), this.pinchCurveAsset.build(), this.maxDistance, this.normalizeDistance, this.positionsMinY, this.positionsMaxY, argument.workerIndexer.getWorkerCount());
            return node;
        }
        return new PositionsPinchDensity(this.buildFirstInput(argument), this.positionProviderAsset.build(new PositionProviderAsset.Argument(argument.parentSeed, argument.referenceBundle, argument.workerIndexer)), this.pinchCurveAsset.build(), this.maxDistance, this.normalizeDistance);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
        this.positionProviderAsset.cleanUp();
        this.pinchCurveAsset.cleanUp();
    }
}

