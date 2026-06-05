package com.hypixel.hytale.builtin.hytalegenerator.assets.bounds;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class DecimalBounds3dAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, DecimalBounds3dAsset>> {
   public static final DecimalBounds3dAsset ZERO_INSTANCE = new DecimalBounds3dAsset();
   @Nonnull
   public static final AssetBuilderCodec<String, DecimalBounds3dAsset> CODEC = AssetBuilderCodec.builder(
         DecimalBounds3dAsset.class,
         DecimalBounds3dAsset::new,
         Codec.STRING,
         (asset, value) -> asset.id = value,
         asset -> asset.id,
         (asset, value) -> asset.data = value,
         asset -> asset.data
      )
      .append(new KeyedCodec<>("PointA", Vector3dUtil.CODEC, true), (asset, value) -> asset.pointA = value, asset -> asset.pointA)
      .add()
      .append(new KeyedCodec<>("PointB", Vector3dUtil.CODEC, true), (asset, value) -> asset.pointB = value, asset -> asset.pointB)
      .add()
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   private Vector3d pointA = new Vector3d();
   private Vector3d pointB = new Vector3d();

   @Nonnull
   public Bounds3d build() {
      Bounds3d bounds = new Bounds3d(this.pointA, this.pointB);
      bounds.correct();
      return bounds;
   }

   public String getId() {
      return this.id;
   }
}
