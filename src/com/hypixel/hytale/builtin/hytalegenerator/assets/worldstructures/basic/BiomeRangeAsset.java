package com.hypixel.hytale.builtin.hytalegenerator.assets.worldstructures.basic;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.biomes.BiomeAsset;
import com.hypixel.hytale.builtin.hytalegenerator.rangemaps.DoubleRange;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BiomeRangeAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, BiomeRangeAsset>> {
   @Nonnull
   public static final AssetBuilderCodec<String, BiomeRangeAsset> CODEC = AssetBuilderCodec.builder(
         BiomeRangeAsset.class,
         BiomeRangeAsset::new,
         Codec.STRING,
         (asset, value) -> asset.id = value,
         asset -> asset.id,
         (asset, value) -> asset.data = value,
         asset -> asset.data
      )
      .append(
         new KeyedCodec<>("Biome", new ContainedAssetCodec<>(BiomeAsset.class, BiomeAsset.CODEC), true),
         (asset, value) -> asset.biomeAssetId = value,
         asset -> asset.biomeAssetId
      )
      .addValidatorLate(() -> BiomeAsset.VALIDATOR_CACHE.getValidator().late())
      .add()
      .append(new KeyedCodec<>("Min", Codec.DOUBLE, true), (asset, value) -> asset.min = value, asset -> asset.min)
      .add()
      .append(new KeyedCodec<>("Max", Codec.DOUBLE, true), (asset, value) -> asset.max = value, asset -> asset.max)
      .add()
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   private double min = -1.0;
   private double max = 1.0;
   private String biomeAssetId = "";

   private BiomeRangeAsset() {
   }

   @Nonnull
   public DoubleRange getRange() {
      return DoubleRange.inclusive(this.min, this.max);
   }

   @Nullable
   public BiomeAsset getBiomeAsset() {
      return (BiomeAsset)((DefaultAssetMap)BiomeAsset.getAssetStore().getAssetMap()).getAsset(this.biomeAssetId);
   }

   public String getBiomeAssetId() {
      return this.biomeAssetId;
   }

   public String getId() {
      return this.id;
   }
}
