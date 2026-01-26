/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.WeightedMaterialProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class WeightedMaterialProviderAsset
extends MaterialProviderAsset {
    public static final BuilderCodec<WeightedMaterialProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WeightedMaterialProviderAsset.class, WeightedMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("WeightedMaterials", new ArrayCodec(WeightedMaterialAsset.CODEC, WeightedMaterialAsset[]::new), true), (t, k) -> {
        t.weighedMapEntries = k;
    }, k -> k.weighedMapEntries).add()).append(new KeyedCodec<Double>("SkipChance", Codec.DOUBLE, true), (t, k) -> {
        t.skipChance = k;
    }, k -> k.skipChance).add()).append(new KeyedCodec<String>("Seed", Codec.STRING, true), (t, k) -> {
        t.seed = k;
    }, k -> k.seed).add()).build();
    private WeightedMaterialAsset[] weighedMapEntries = new WeightedMaterialAsset[0];
    private double skipChance = 0.0;
    private String seed = "";

    @Override
    @Nonnull
    public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
        if (super.skip()) {
            return MaterialProvider.noMaterialProvider();
        }
        WeightedMap weightMap = new WeightedMap();
        for (WeightedMaterialAsset entry : this.weighedMapEntries) {
            weightMap.add(entry.materialProviderAsset.build(argument), entry.weight);
        }
        return new WeightedMaterialProvider<Material>(weightMap, argument.parentSeed.child(this.seed), this.skipChance);
    }

    @Override
    public void cleanUp() {
        for (WeightedMaterialAsset weightedMaterialAsset : this.weighedMapEntries) {
            weightedMaterialAsset.cleanUp();
        }
    }

    public static class WeightedMaterialAsset
    implements Cleanable,
    JsonAssetWithMap<String, DefaultAssetMap<String, WeightedMaterialAsset>> {
        public static final AssetBuilderCodec<String, WeightedMaterialAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(WeightedMaterialAsset.class, WeightedMaterialAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec<Double>("Weight", Codec.DOUBLE, true), (t, y) -> {
            t.weight = y;
        }, t -> t.weight).addValidator(Validators.greaterThanOrEqual(0.0)).add()).append(new KeyedCodec("Material", MaterialProviderAsset.CODEC, true), (t, out) -> {
            t.materialProviderAsset = out;
        }, t -> t.materialProviderAsset).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private double weight = 1.0;
        private MaterialProviderAsset materialProviderAsset;

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

