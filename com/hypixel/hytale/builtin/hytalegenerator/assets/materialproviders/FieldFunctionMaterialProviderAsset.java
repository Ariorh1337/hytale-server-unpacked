/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.ConstantMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.FieldFunctionMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class FieldFunctionMaterialProviderAsset
extends MaterialProviderAsset {
    public static final BuilderCodec<FieldFunctionMaterialProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FieldFunctionMaterialProviderAsset.class, FieldFunctionMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec("FieldFunction", DensityAsset.CODEC, true), (t, k) -> {
        t.densityAsset = k;
    }, t -> t.densityAsset).add()).append(new KeyedCodec<T[]>("Delimiters", new ArrayCodec(DelimiterAsset.CODEC, DelimiterAsset[]::new), true), (t, k) -> {
        t.delimiterAssets = k;
    }, k -> k.delimiterAssets).add()).build();
    private DensityAsset densityAsset = new ConstantDensityAsset();
    private DelimiterAsset[] delimiterAssets = new DelimiterAsset[0];

    @Override
    @Nonnull
    public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
        if (super.skip()) {
            return MaterialProvider.noMaterialProvider();
        }
        Density functionTree = this.densityAsset.build(DensityAsset.from(argument));
        ArrayList delimitersList = new ArrayList(this.delimiterAssets.length);
        for (DelimiterAsset delimiterAsset : this.delimiterAssets) {
            MaterialProvider<Material> materialProvider = delimiterAsset.materialProviderAsset.build(argument);
            FieldFunctionMaterialProvider.FieldDelimiter<Material> delimiter = new FieldFunctionMaterialProvider.FieldDelimiter<Material>(materialProvider, delimiterAsset.from, delimiterAsset.to);
            delimitersList.add(delimiter);
        }
        return new FieldFunctionMaterialProvider<Material>(functionTree, delimitersList);
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
        public static final AssetBuilderCodec<String, DelimiterAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(DelimiterAsset.class, DelimiterAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec<Double>("From", Codec.DOUBLE, true), (t, y) -> {
            t.from = y;
        }, t -> t.from).add()).append(new KeyedCodec<Double>("To", Codec.DOUBLE, true), (t, out) -> {
            t.to = out;
        }, t -> t.to).add()).append(new KeyedCodec("Material", MaterialProviderAsset.CODEC, true), (t, out) -> {
            t.materialProviderAsset = out;
        }, t -> t.materialProviderAsset).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private double from = 0.0;
        private double to = 0.0;
        private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public void cleanUp() {
            this.materialProviderAsset.cleanUp();
        }
    }
}

