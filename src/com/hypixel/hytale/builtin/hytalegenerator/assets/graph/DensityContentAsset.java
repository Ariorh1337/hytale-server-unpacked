package com.hypixel.hytale.builtin.hytalegenerator.assets.graph;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class DensityContentAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, DensityContentAsset>> {
   @Nonnull
   public static final AssetBuilderCodec<String, DensityContentAsset> CODEC = AssetBuilderCodec.builder(
         DensityContentAsset.class,
         DensityContentAsset::new,
         Codec.STRING,
         (asset, value) -> asset.id = value,
         asset -> asset.id,
         (asset, value) -> asset.data = value,
         asset -> asset.data
      )
      .append(new KeyedCodec<>("Density", DensityAsset.CODEC, true), (asset, value) -> asset.densityAsset = value, asset -> asset.densityAsset)
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
   private DensityAsset densityAsset = DensityAsset.getFallbackAsset();
   private double range = 0.0;
   @Nonnull
   private String contentLayerName = "";

   @Nonnull
   public GraphSpace.DensityContent build(@Nonnull GraphGeneratorAsset.Argument argument) {
      Density density = this.densityAsset.build(DensityAsset.from(argument));
      return new GraphSpace.DensityContent(density, this.range);
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
      this.densityAsset.cleanUp();
   }
}
