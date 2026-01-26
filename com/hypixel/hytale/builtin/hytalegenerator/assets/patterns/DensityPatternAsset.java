/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.PatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.FieldFunctionPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import javax.annotation.Nonnull;

public class DensityPatternAsset
extends PatternAsset {
    public static final BuilderCodec<DensityPatternAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DensityPatternAsset.class, DensityPatternAsset::new, PatternAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Delimiters", new ArrayCodec(DelimiterAsset.CODEC, DelimiterAsset[]::new), true), (t, k) -> {
        t.delimiterAssets = k;
    }, k -> k.delimiterAssets).add()).append(new KeyedCodec("FieldFunction", DensityAsset.CODEC, true), (t, k) -> {
        t.densityAsset = k;
    }, k -> k.densityAsset).add()).build();
    private DelimiterAsset[] delimiterAssets = new DelimiterAsset[0];
    private DensityAsset densityAsset = new ConstantDensityAsset();

    @Override
    @Nonnull
    public Pattern build(@Nonnull PatternAsset.Argument argument) {
        if (super.isSkipped()) {
            return Pattern.noPattern();
        }
        Density field = this.densityAsset.build(DensityAsset.from(argument));
        FieldFunctionPattern out = new FieldFunctionPattern(field);
        for (DelimiterAsset asset : this.delimiterAssets) {
            out.addDelimiter(asset.min, asset.max);
        }
        return out;
    }

    @Override
    public void cleanUp() {
        this.densityAsset.cleanUp();
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
        private double min;
        private double max;

        @Override
        public String getId() {
            return this.id;
        }
    }
}

