package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.layerassets;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.ConstantMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.SpaceAndDepthMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.layers.WeightedThicknessLayer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class WeightedThicknessLayerAsset extends LayerAsset {
   @Nonnull
   public static final BuilderCodec<WeightedThicknessLayerAsset> CODEC = BuilderCodec.builder(
         WeightedThicknessLayerAsset.class, WeightedThicknessLayerAsset::new, LayerAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>(
            "PossibleThicknesses",
            new ArrayCodec<>(WeightedThicknessLayerAsset.WeightedThicknessAsset.CODEC, WeightedThicknessLayerAsset.WeightedThicknessAsset[]::new),
            true
         ),
         (asset, value) -> asset.possibleThicknessAssets = value,
         asset -> asset.possibleThicknessAssets
      )
      .addValidator(Validators.nonNullArrayElements())
      .add()
      .append(
         new KeyedCodec<>("Material", MaterialProviderAsset.CODEC, true),
         (asset, value) -> asset.materialProviderAsset = value,
         asset -> asset.materialProviderAsset
      )
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();
   private String seed = "";
   private WeightedThicknessLayerAsset.WeightedThicknessAsset[] possibleThicknessAssets = new WeightedThicknessLayerAsset.WeightedThicknessAsset[0];

   @Nonnull
   @Override
   public SpaceAndDepthMaterialProvider.Layer<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
      WeightedMap<Integer> pool = new WeightedMap<>();

      for (WeightedThicknessLayerAsset.WeightedThicknessAsset asset : this.possibleThicknessAssets) {
         pool.add(asset.thickness, asset.weight);
      }

      return new WeightedThicknessLayer<>(pool, this.materialProviderAsset.build(argument), argument.parentSeed);
   }

   @Override
   public void cleanUp() {
      this.materialProviderAsset.cleanUp();
   }

   public static class WeightedThicknessAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, WeightedThicknessLayerAsset.WeightedThicknessAsset>> {
      @Nonnull
      public static final AssetBuilderCodec<String, WeightedThicknessLayerAsset.WeightedThicknessAsset> CODEC = AssetBuilderCodec.builder(
            WeightedThicknessLayerAsset.WeightedThicknessAsset.class,
            WeightedThicknessLayerAsset.WeightedThicknessAsset::new,
            Codec.STRING,
            (asset, value) -> asset.id = value,
            asset -> asset.id,
            (asset, value) -> asset.data = value,
            asset -> asset.data
         )
         .append(new KeyedCodec<>("Weight", Codec.DOUBLE, true), (asset, value) -> asset.weight = value, asset -> asset.weight)
         .add()
         .append(new KeyedCodec<>("Thickness", Codec.INTEGER, true), (asset, value) -> asset.thickness = value, asset -> asset.thickness)
         .add()
         .build();
      private String id;
      private AssetExtraInfo.Data data;
      private double weight;
      private int thickness;

      public String getId() {
         return this.id;
      }
   }
}
