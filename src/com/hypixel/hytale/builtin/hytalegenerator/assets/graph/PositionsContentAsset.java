package com.hypixel.hytale.builtin.hytalegenerator.assets.graph;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.ListPositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class PositionsContentAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, PositionsContentAsset>> {
   @Nonnull
   public static final AssetBuilderCodec<String, PositionsContentAsset> CODEC = AssetBuilderCodec.builder(
         PositionsContentAsset.class,
         PositionsContentAsset::new,
         Codec.STRING,
         (asset, value) -> asset.id = value,
         asset -> asset.id,
         (asset, value) -> asset.data = value,
         asset -> asset.data
      )
      .append(new KeyedCodec<>("Positions", PositionProviderAsset.CODEC, true), (asset, value) -> asset.positionsAsset = value, asset -> asset.positionsAsset)
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
   private PositionProviderAsset positionsAsset = ListPositionProviderAsset.INSTANCE;
   private double range = 0.0;
   @Nonnull
   private String contentLayerName = "";

   @Nonnull
   public GraphSpace.PositionsContent build(@Nonnull GraphGeneratorAsset.Argument argument) {
      PositionProvider positions = this.positionsAsset.build(new PositionProviderAsset.Argument(argument));
      return new GraphSpace.PositionsContent(positions, this.range);
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
      this.positionsAsset.cleanUp();
   }
}
