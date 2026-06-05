package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.EmptyPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.ListPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class ListPositionProviderAsset extends PositionProviderAsset {
   public static final ListPositionProviderAsset INSTANCE = new ListPositionProviderAsset();
   @Nonnull
   public static final BuilderCodec<ListPositionProviderAsset> CODEC = BuilderCodec.builder(
         ListPositionProviderAsset.class, ListPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Positions", new ArrayCodec<>(ListPositionProviderAsset.PositionAsset.CODEC, ListPositionProviderAsset.PositionAsset[]::new), true),
         (asset, value) -> asset.positions = value,
         asset -> asset.positions
      )
      .add()
      .build();
   private ListPositionProviderAsset.PositionAsset[] positions = new ListPositionProviderAsset.PositionAsset[0];

   @Nonnull
   @Override
   public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
      if (super.skip()) {
         return EmptyPositionProvider.INSTANCE;
      }

      ArrayList<Vector3d> list = new ArrayList<>();

      for (ListPositionProviderAsset.PositionAsset asset : this.positions) {
         Vector3d position = new Vector3d(asset.x, asset.y, asset.z);
         list.add(position);
      }

      return new ListPositionProvider(list);
   }

   public static class PositionAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, ListPositionProviderAsset.PositionAsset>> {
      @Nonnull
      public static final AssetBuilderCodec<String, ListPositionProviderAsset.PositionAsset> CODEC = AssetBuilderCodec.builder(
            ListPositionProviderAsset.PositionAsset.class,
            ListPositionProviderAsset.PositionAsset::new,
            Codec.STRING,
            (asset, value) -> asset.id = value,
            asset -> asset.id,
            (asset, value) -> asset.data = value,
            asset -> asset.data
         )
         .append(new KeyedCodec<>("X", Codec.DOUBLE, true), (asset, value) -> asset.x = value, asset -> asset.x)
         .add()
         .append(new KeyedCodec<>("Y", Codec.DOUBLE, true), (asset, value) -> asset.y = value, asset -> asset.y)
         .add()
         .append(new KeyedCodec<>("Z", Codec.DOUBLE, true), (asset, value) -> asset.z = value, asset -> asset.z)
         .add()
         .build();
      private String id;
      private AssetExtraInfo.Data data;
      private double x;
      private double y;
      private double z;

      public String getId() {
         return this.id;
      }
   }
}
