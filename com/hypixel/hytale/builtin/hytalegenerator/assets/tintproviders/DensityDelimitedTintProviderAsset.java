/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.tintproviders;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.delimiters.RangeDoubleAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.tintproviders.TintProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.delimiters.DelimiterDouble;
import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.DensityDelimitedTintProvider;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.TintProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class DensityDelimitedTintProviderAsset
extends TintProviderAsset {
    public static final BuilderCodec<DensityDelimitedTintProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DensityDelimitedTintProviderAsset.class, DensityDelimitedTintProviderAsset::new, TintProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Delimiters", new ArrayCodec(DelimiterAsset.CODEC, DelimiterAsset[]::new), true), (t, k) -> {
        t.delimiterAssets = k;
    }, k -> k.delimiterAssets).add()).append(new KeyedCodec("Density", DensityAsset.CODEC, true), (t, value) -> {
        t.densityAsset = value;
    }, t -> t.densityAsset).add()).build();
    private DelimiterAsset[] delimiterAssets = new DelimiterAsset[0];
    private DensityAsset densityAsset = DensityAsset.getFallbackAsset();

    @Override
    @Nonnull
    public TintProvider build(@Nonnull TintProviderAsset.Argument argument) {
        if (super.isSkipped()) {
            return TintProvider.noTintProvider();
        }
        ArrayList<DelimiterDouble<TintProvider>> delimiters = new ArrayList<DelimiterDouble<TintProvider>>(this.delimiterAssets.length);
        for (DelimiterAsset delimiterAsset : this.delimiterAssets) {
            delimiters.add(delimiterAsset.build(argument));
        }
        Density density = this.densityAsset.build(DensityAsset.from(argument));
        return new DensityDelimitedTintProvider(delimiters, density);
    }

    public static class DelimiterAsset
    implements JsonAssetWithMap<String, DefaultAssetMap<String, DelimiterAsset>> {
        public static final AssetBuilderCodec<String, DelimiterAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(DelimiterAsset.class, DelimiterAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec("Range", RangeDoubleAsset.CODEC, true), (t, value) -> {
            t.rangeAsset = value;
        }, t -> t.rangeAsset).add()).append(new KeyedCodec("Tint", TintProviderAsset.CODEC, true), (t, value) -> {
            t.tintProviderAsset = value;
        }, t -> t.tintProviderAsset).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private RangeDoubleAsset rangeAsset = new RangeDoubleAsset();
        private TintProviderAsset tintProviderAsset = TintProviderAsset.getFallbackAsset();

        @Nonnull
        public DelimiterDouble<TintProvider> build(@Nonnull TintProviderAsset.Argument argument) {
            RangeDouble range = this.rangeAsset.build();
            TintProvider environmentProvider = this.tintProviderAsset.build(argument);
            return new DelimiterDouble<TintProvider>(range, environmentProvider);
        }

        @Override
        public String getId() {
            return this.id;
        }
    }
}

