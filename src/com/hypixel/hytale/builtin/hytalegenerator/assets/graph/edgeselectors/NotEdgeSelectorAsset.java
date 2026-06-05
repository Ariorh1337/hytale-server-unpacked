package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.AllEdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.EdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.NotEdgeSelector;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class NotEdgeSelectorAsset extends EdgeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<NotEdgeSelectorAsset> CODEC = BuilderCodec.builder(
         NotEdgeSelectorAsset.class, NotEdgeSelectorAsset::new, EdgeSelectorAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("EdgeSelector", EdgeSelectorAsset.CODEC, true), (asset, value) -> asset.edgeSelectorAsset = value, asset -> asset.edgeSelectorAsset
      )
      .add()
      .build();
   @Nonnull
   private EdgeSelectorAsset edgeSelectorAsset = AllEdgeSelectorAsset.INSTANCE;

   @Nonnull
   @Override
   public EdgeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? AllEdgeSelector.INSTANCE : new NotEdgeSelector(this.edgeSelectorAsset.build(argument));
   }

   @Override
   public void cleanUp() {
      this.edgeSelectorAsset.cleanUp();
   }
}
