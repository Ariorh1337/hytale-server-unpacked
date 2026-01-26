/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.environmentproviders;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.delimiters.RangeDoubleAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.environmentproviders.EnvironmentProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.delimiters.DelimiterDouble;
import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.environmentproviders.DensityDelimitedEnvironmentProvider;
import com.hypixel.hytale.builtin.hytalegenerator.environmentproviders.EnvironmentProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class DensityDelimitedEnvironmentProviderAsset
extends EnvironmentProviderAsset {
    public static final BuilderCodec<DensityDelimitedEnvironmentProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DensityDelimitedEnvironmentProviderAsset.class, DensityDelimitedEnvironmentProviderAsset::new, EnvironmentProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Delimiters", new ArrayCodec(DelimiterAsset.CODEC, DelimiterAsset[]::new), true), (t, k) -> {
        t.delimiterAssets = k;
    }, k -> k.delimiterAssets).add()).append(new KeyedCodec("Density", DensityAsset.CODEC, true), (t, value) -> {
        t.densityAsset = value;
    }, t -> t.densityAsset).add()).build();
    private DelimiterAsset[] delimiterAssets = new DelimiterAsset[0];
    private DensityAsset densityAsset = DensityAsset.getFallbackAsset();

    @Override
    @Nonnull
    public EnvironmentProvider build(@Nonnull EnvironmentProviderAsset.Argument argument) {
        if (super.isSkipped()) {
            return EnvironmentProvider.noEnvironmentProvider();
        }
        ArrayList<DelimiterDouble<EnvironmentProvider>> delimiters = new ArrayList<DelimiterDouble<EnvironmentProvider>>(this.delimiterAssets.length);
        for (DelimiterAsset delimiterAsset : this.delimiterAssets) {
            delimiters.add(delimiterAsset.build(argument));
        }
        Density density = this.densityAsset.build(DensityAsset.from(argument));
        return new DensityDelimitedEnvironmentProvider(delimiters, density);
    }

    @Override
    public void cleanUp() {
        this.densityAsset.cleanUp();
        for (DelimiterAsset delimiterAsset : this.delimiterAssets) {
            delimiterAsset.cleanUp();
        }
    }

    public static class DelimiterAsset
    implements Cleanable,
    JsonAssetWithMap<String, DefaultAssetMap<String, DelimiterAsset>> {
        public static final AssetBuilderCodec<String, DelimiterAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(DelimiterAsset.class, DelimiterAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec("Range", RangeDoubleAsset.CODEC, true), (t, value) -> {
            t.rangeAsset = value;
        }, t -> t.rangeAsset).add()).append(new KeyedCodec("Environment", EnvironmentProviderAsset.CODEC, true), (t, value) -> {
            t.environmentProviderAsset = value;
        }, t -> t.environmentProviderAsset).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private RangeDoubleAsset rangeAsset = new RangeDoubleAsset();
        private EnvironmentProviderAsset environmentProviderAsset = EnvironmentProviderAsset.getFallbackAsset();

        @Nonnull
        public DelimiterDouble<EnvironmentProvider> build(@Nonnull EnvironmentProviderAsset.Argument argument) {
            RangeDouble range = this.rangeAsset.build();
            EnvironmentProvider environmentProvider = this.environmentProviderAsset.build(argument);
            return new DelimiterDouble<EnvironmentProvider>(range, environmentProvider);
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public void cleanUp() {
            this.environmentProviderAsset.cleanUp();
        }
    }
}

