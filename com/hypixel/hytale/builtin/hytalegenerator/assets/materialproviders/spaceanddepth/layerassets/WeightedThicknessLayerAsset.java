/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.layerassets;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.ConstantMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.layerassets.LayerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.SpaceAndDepthMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.layers.WeightedThicknessLayer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class WeightedThicknessLayerAsset
extends LayerAsset {
    public static final BuilderCodec<WeightedThicknessLayerAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WeightedThicknessLayerAsset.class, WeightedThicknessLayerAsset::new, LayerAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("PossibleThicknesses", new ArrayCodec(WeightedThicknessAsset.CODEC, WeightedThicknessAsset[]::new), true), (t, k) -> {
        t.possibleThicknessAssets = k;
    }, k -> k.possibleThicknessAssets).addValidator(Validators.nonNullArrayElements()).add()).append(new KeyedCodec("Material", MaterialProviderAsset.CODEC, true), (t, k) -> {
        t.materialProviderAsset = k;
    }, k -> k.materialProviderAsset).add()).append(new KeyedCodec<String>("Seed", Codec.STRING, true), (t, k) -> {
        t.seed = k;
    }, k -> k.seed).add()).build();
    private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();
    private String seed = "";
    private WeightedThicknessAsset[] possibleThicknessAssets = new WeightedThicknessAsset[0];

    @Override
    @Nonnull
    public SpaceAndDepthMaterialProvider.Layer<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
        WeightedMap<Integer> pool = new WeightedMap<Integer>();
        for (WeightedThicknessAsset asset : this.possibleThicknessAssets) {
            pool.add(asset.thickness, asset.weight);
        }
        return new WeightedThicknessLayer<Material>(pool, this.materialProviderAsset.build(argument), argument.parentSeed);
    }

    @Override
    public void cleanUp() {
        this.materialProviderAsset.cleanUp();
    }

    public static class WeightedThicknessAsset
    implements JsonAssetWithMap<String, DefaultAssetMap<String, WeightedThicknessAsset>> {
        public static final AssetBuilderCodec<String, WeightedThicknessAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(WeightedThicknessAsset.class, WeightedThicknessAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec<Double>("Weight", Codec.DOUBLE, true), (t, y) -> {
            t.weight = y;
        }, t -> t.weight).add()).append(new KeyedCodec<Integer>("Thickness", Codec.INTEGER, true), (t, out) -> {
            t.thickness = out;
        }, t -> t.thickness).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private double weight;
        private int thickness;

        @Override
        public String getId() {
            return this.id;
        }
    }
}

