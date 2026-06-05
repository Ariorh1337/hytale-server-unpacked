package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentsuppliers;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers.ConstantContentSupplier;
import com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers.ContentSupplier;
import com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers.WeightedContentSupplier;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class WeightedContentSupplierAsset extends ContentSupplierAsset {
   @Nonnull
   public static final BuilderCodec<WeightedContentSupplierAsset> CODEC = BuilderCodec.builder(
         WeightedContentSupplierAsset.class, WeightedContentSupplierAsset::new, ContentSupplierAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Entries", new ArrayCodec<>(WeightedContentSupplierAsset.EntryAsset.CODEC, WeightedContentSupplierAsset.EntryAsset[]::new), true),
         (asset, value) -> asset.entryAssets = value,
         asset -> asset.entryAssets
      )
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   @Nonnull
   private WeightedContentSupplierAsset.EntryAsset[] entryAssets = new WeightedContentSupplierAsset.EntryAsset[0];
   @Nonnull
   private String seed = "";

   @Nonnull
   @Override
   public ContentSupplier build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (!super.skip() && this.entryAssets.length != 0) {
         WeightedMap<ContentSupplier> weightedMap = new WeightedMap<>(this.entryAssets.length);

         for (WeightedContentSupplierAsset.EntryAsset entryAsset : this.entryAssets) {
            weightedMap.add(entryAsset.contentSupplierAsset.build(argument), entryAsset.weight);
         }

         return new WeightedContentSupplier(weightedMap, argument.parentSeed.child(this.seed).createSupplier().get());
      } else {
         return ConstantContentSupplier.INSTANCE;
      }
   }

   @Override
   public void cleanUp() {
      for (WeightedContentSupplierAsset.EntryAsset entryAsset : this.entryAssets) {
         entryAsset.cleanUp();
      }
   }

   public static class EntryAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, WeightedContentSupplierAsset.EntryAsset>> {
      @Nonnull
      public static final AssetBuilderCodec<String, WeightedContentSupplierAsset.EntryAsset> CODEC = AssetBuilderCodec.builder(
            WeightedContentSupplierAsset.EntryAsset.class,
            WeightedContentSupplierAsset.EntryAsset::new,
            Codec.STRING,
            (asset, value) -> asset.id = value,
            asset -> asset.id,
            (asset, value) -> asset.data = value,
            asset -> asset.data
         )
         .append(new KeyedCodec<>("Weight", Codec.DOUBLE, true), (asset, value) -> asset.weight = value, asset -> asset.weight)
         .addValidator(Validators.greaterThan(0.0))
         .add()
         .append(
            new KeyedCodec<>("ContentSupplier", ContentSupplierAsset.CODEC, true),
            (asset, value) -> asset.contentSupplierAsset = value,
            asset -> asset.contentSupplierAsset
         )
         .add()
         .build();
      private String id;
      private AssetExtraInfo.Data data;
      private double weight = 1.0;
      @Nonnull
      private ContentSupplierAsset contentSupplierAsset = ConstantContentSupplierAsset.INSTANCE;

      public String getId() {
         return this.id;
      }

      @Override
      public void cleanUp() {
         this.contentSupplierAsset.cleanUp();
      }
   }
}
