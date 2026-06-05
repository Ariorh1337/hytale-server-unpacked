package com.hypixel.hytale.builtin.hytalegenerator.assets.curves.manual;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import javax.annotation.Nonnull;
import org.joml.Vector2d;

public class PointInOutAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, PointInOutAsset>> {
   @Nonnull
   public static final AssetBuilderCodec<String, PointInOutAsset> CODEC = AssetBuilderCodec.builder(
         PointInOutAsset.class,
         PointInOutAsset::new,
         Codec.STRING,
         (asset, value) -> asset.id = value,
         asset -> asset.id,
         (asset, value) -> asset.data = value,
         asset -> asset.data
      )
      .append(new KeyedCodec<>("In", Codec.DOUBLE, true), (asset, value) -> asset.y = value, asset -> asset.y)
      .add()
      .append(new KeyedCodec<>("Out", Codec.DOUBLE, true), (asset, value) -> asset.out = value, asset -> asset.out)
      .add()
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   private double y = 0.0;
   private double out = 0.0;

   private PointInOutAsset() {
   }

   @Nonnull
   public Vector2d build() {
      return new Vector2d(this.y, this.out);
   }

   public double getY() {
      return this.y;
   }

   public double getOut() {
      return this.out;
   }

   public String getId() {
      return this.id;
   }
}
