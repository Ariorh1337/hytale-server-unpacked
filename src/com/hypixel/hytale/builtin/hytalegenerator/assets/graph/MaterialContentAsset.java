package com.hypixel.hytale.builtin.hytalegenerator.assets.graph;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.ConstantMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class MaterialContentAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, MaterialContentAsset>> {
   @Nonnull
   public static final AssetBuilderCodec<String, MaterialContentAsset> CODEC = AssetBuilderCodec.builder(
         MaterialContentAsset.class,
         MaterialContentAsset::new,
         Codec.STRING,
         (asset, value) -> asset.id = value,
         asset -> asset.id,
         (asset, value) -> asset.data = value,
         asset -> asset.data
      )
      .append(
         new KeyedCodec<>("MaterialProvider", MaterialProviderAsset.CODEC, true),
         (asset, value) -> asset.materialProviderAsset = value,
         asset -> asset.materialProviderAsset
      )
      .add()
      .<Double>append(new KeyedCodec<>("Range", Codec.DOUBLE, true), (asset, value) -> asset.range = value, asset -> asset.range)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .append(new KeyedCodec<>("ContentLayer", Codec.STRING, true), (asset, value) -> asset.contentLayerName = value, asset -> asset.contentLayerName)
      .add()
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   @Nonnull
   private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();
   private double range = 0.0;
   @Nonnull
   private String contentLayerName = "";

   @Nonnull
   public GraphSpace.MaterialContent build(@Nonnull GraphGeneratorAsset.Argument argument) {
      assert argument.materialCache != null;
      MaterialProvider<Material> materialProvider = this.materialProviderAsset.build(MaterialProviderAsset.argumentFrom(argument));
      return new GraphSpace.MaterialContent(materialProvider, this.range);
   }

   @Nonnull
   public String getContentLayerName() {
      return this.contentLayerName;
   }

   @Nonnull
   public String getId() {
      return "";
   }

   @Override
   public void cleanUp() {
      this.materialProviderAsset.cleanUp();
   }
}
