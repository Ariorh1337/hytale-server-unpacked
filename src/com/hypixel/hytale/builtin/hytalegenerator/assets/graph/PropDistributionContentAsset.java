package com.hypixel.hytale.builtin.hytalegenerator.assets.graph;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.propdistribution.ConstantPropDistributionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.propdistribution.PropDistributionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.PropDistribution;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class PropDistributionContentAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, PropDistributionContentAsset>> {
   @Nonnull
   public static final AssetBuilderCodec<String, PropDistributionContentAsset> CODEC = AssetBuilderCodec.builder(
         PropDistributionContentAsset.class,
         PropDistributionContentAsset::new,
         Codec.STRING,
         (asset, value) -> asset.id = value,
         asset -> asset.id,
         (asset, value) -> asset.data = value,
         asset -> asset.data
      )
      .append(
         new KeyedCodec<>("PropDistribution", PropDistributionAsset.CODEC, true),
         (asset, value) -> asset.propDistributionAsset = value,
         asset -> asset.propDistributionAsset
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
   private PropDistributionAsset propDistributionAsset = ConstantPropDistributionAsset.INSTANCE;
   private double range = 0.0;
   @Nonnull
   private String contentLayerName = "";

   @Nonnull
   public GraphSpace.PropDistributionContent build(@Nonnull GraphGeneratorAsset.Argument argument) {
      assert argument.materialCache != null;
      PropDistribution propDistribution = this.propDistributionAsset.build(new PropDistributionAsset.Argument(argument));
      return new GraphSpace.PropDistributionContent(propDistribution, this.range);
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
      this.propDistributionAsset.cleanUp();
   }
}
