package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.WeightedNodeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class WeightedNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<WeightedNodeActionAsset> CODEC = BuilderCodec.builder(
         WeightedNodeActionAsset.class, WeightedNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Entries", new ArrayCodec<>(WeightedNodeActionAsset.EntryAsset.CODEC, WeightedNodeActionAsset.EntryAsset[]::new), true),
         (asset, value) -> asset.entryAssets = value,
         asset -> asset.entryAssets
      )
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   @Nonnull
   private WeightedNodeActionAsset.EntryAsset[] entryAssets = new WeightedNodeActionAsset.EntryAsset[0];
   @Nonnull
   private String seed = "";

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return EmptyNodeAction.INSTANCE;
      }

      if (this.entryAssets.length == 0) {
         return EmptyNodeAction.INSTANCE;
      }

      WeightedMap<NodeAction> weightedMap = new WeightedMap<>();

      for (WeightedNodeActionAsset.EntryAsset entry : this.entryAssets) {
         assert entry.weight > -0.0;
         NodeAction nodeAction = entry.nodeActionAsset.build(argument);
         weightedMap.add(nodeAction, entry.weight);
      }

      return new WeightedNodeAction(weightedMap, argument.parentSeed.child(this.seed).createSupplier().get());
   }

   @Override
   public void cleanUp() {
      for (WeightedNodeActionAsset.EntryAsset entryAsset : this.entryAssets) {
         entryAsset.nodeActionAsset.cleanUp();
      }
   }

   public static class EntryAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, WeightedNodeActionAsset.EntryAsset>> {
      @Nonnull
      public static final AssetBuilderCodec<String, WeightedNodeActionAsset.EntryAsset> CODEC = AssetBuilderCodec.builder(
            WeightedNodeActionAsset.EntryAsset.class,
            WeightedNodeActionAsset.EntryAsset::new,
            Codec.STRING,
            (asset, value) -> asset.id = value,
            asset -> asset.id,
            (asset, value) -> asset.data = value,
            asset -> asset.data
         )
         .append(new KeyedCodec<>("Weight", Codec.DOUBLE, true), (asset, value) -> asset.weight = value, asset -> asset.weight)
         .addValidator(Validators.greaterThanOrEqual(0.0))
         .add()
         .append(new KeyedCodec<>("NodeAction", NodeActionAsset.CODEC, true), (asset, value) -> asset.nodeActionAsset = value, asset -> asset.nodeActionAsset)
         .add()
         .build();
      private String id;
      private AssetExtraInfo.Data data;
      private double weight = 1.0;
      @Nonnull
      private NodeActionAsset nodeActionAsset = EmptyNodeActionAsset.INSTANCE;

      public String getId() {
         return this.id;
      }

      @Override
      public void cleanUp() {
         this.nodeActionAsset.cleanUp();
      }
   }
}
