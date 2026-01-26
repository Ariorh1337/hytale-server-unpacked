/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.ListPositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiDouble2DoubleFunction;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.BaseHeightPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.BaseHeightReference;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;

public class BaseHeightPositionProviderAsset
extends PositionProviderAsset {
    public static final BuilderCodec<BaseHeightPositionProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BaseHeightPositionProviderAsset.class, BaseHeightPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("MinYRead", Codec.DOUBLE, false), (asset, v) -> {
        asset.minYRead = v;
    }, asset -> asset.minYRead).add()).append(new KeyedCodec<Double>("MaxYRead", Codec.DOUBLE, false), (asset, v) -> {
        asset.maxYRead = v;
    }, asset -> asset.maxYRead).add()).append(new KeyedCodec<String>("BedName", Codec.STRING, false), (asset, v) -> {
        asset.bedName = v;
    }, asset -> asset.bedName).add()).append(new KeyedCodec("Positions", PositionProviderAsset.CODEC, true), (asset, v) -> {
        asset.positionProviderAsset = v;
    }, asset -> asset.positionProviderAsset).add()).build();
    private double minYRead = -1.0;
    private double maxYRead = 1.0;
    private String bedName = "";
    private PositionProviderAsset positionProviderAsset = new ListPositionProviderAsset();

    @Override
    @Nonnull
    public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
        if (super.skip()) {
            return PositionProvider.noPositionProvider();
        }
        PositionProvider positionProvider = this.positionProviderAsset.build(argument);
        BaseHeightReference heightDataLayer = argument.referenceBundle.getLayerWithName(this.bedName, BaseHeightReference.class);
        if (heightDataLayer == null) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atConfig()).log("Couldn't height data layer with name \"" + this.bedName + "\", the positions will not be offset by the bed.");
            return new BaseHeightPositionProvider((x, z) -> 0.0, positionProvider, this.minYRead, this.maxYRead);
        }
        BiDouble2DoubleFunction heightFunction = heightDataLayer.getHeightFunction();
        return new BaseHeightPositionProvider(heightFunction, positionProvider, this.minYRead, this.maxYRead);
    }

    @Override
    public void cleanUp() {
        this.positionProviderAsset.cleanUp();
    }
}

