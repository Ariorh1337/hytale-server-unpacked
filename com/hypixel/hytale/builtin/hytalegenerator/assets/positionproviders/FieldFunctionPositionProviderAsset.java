/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.ListPositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.FieldFunctionPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import javax.annotation.Nonnull;

public class FieldFunctionPositionProviderAsset
extends PositionProviderAsset {
    private static final DelimiterAsset[] EMPTY_DELIMITER_ASSETS = new DelimiterAsset[0];
    public static final BuilderCodec<FieldFunctionPositionProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FieldFunctionPositionProviderAsset.class, FieldFunctionPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Delimiters", new ArrayCodec(DelimiterAsset.CODEC, DelimiterAsset[]::new), true), (asset, v) -> {
        asset.delimiterAssets = v;
    }, asset -> asset.delimiterAssets).add()).append(new KeyedCodec("FieldFunction", DensityAsset.CODEC, true), (asset, v) -> {
        asset.densityAsset = v;
    }, asset -> asset.densityAsset).add()).append(new KeyedCodec("Positions", PositionProviderAsset.CODEC, true), (asset, v) -> {
        asset.positionProviderAsset = v;
    }, asset -> asset.positionProviderAsset).add()).build();
    private DelimiterAsset[] delimiterAssets = EMPTY_DELIMITER_ASSETS;
    private DensityAsset densityAsset = new ConstantDensityAsset();
    private PositionProviderAsset positionProviderAsset = new ListPositionProviderAsset();

    @Override
    @Nonnull
    public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
        if (super.skip()) {
            return PositionProvider.noPositionProvider();
        }
        Density functionTree = this.densityAsset.build(DensityAsset.from(argument));
        PositionProvider positionProvider = this.positionProviderAsset.build(argument);
        FieldFunctionPositionProvider out = new FieldFunctionPositionProvider(functionTree, positionProvider);
        for (DelimiterAsset asset : this.delimiterAssets) {
            out.addDelimiter(asset.min, asset.max);
        }
        return out;
    }

    @Override
    public void cleanUp() {
        this.densityAsset.cleanUp();
        this.positionProviderAsset.cleanUp();
    }

    public static class DelimiterAsset
    implements JsonAssetWithMap<String, DefaultAssetMap<String, DelimiterAsset>> {
        public static final AssetBuilderCodec<String, DelimiterAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(DelimiterAsset.class, DelimiterAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec<Double>("Min", Codec.DOUBLE, true), (t, v) -> {
            t.min = v;
        }, t -> t.min).add()).append(new KeyedCodec<Double>("Max", Codec.DOUBLE, true), (t, v) -> {
            t.max = v;
        }, t -> t.max).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private double min = 0.0;
        private double max = 0.0;

        @Override
        public String getId() {
            return this.id;
        }
    }
}

