package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.AllEdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.EdgeSelector;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class AllEdgeSelectorAsset extends EdgeSelectorAsset {
   public static final AllEdgeSelectorAsset INSTANCE = new AllEdgeSelectorAsset();
   @Nonnull
   public static final BuilderCodec<AllEdgeSelectorAsset> CODEC = BuilderCodec.builder(
         AllEdgeSelectorAsset.class, AllEdgeSelectorAsset::new, EdgeSelectorAsset.ABSTRACT_CODEC
      )
      .build();

   @Nonnull
   @Override
   public EdgeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return AllEdgeSelector.INSTANCE;
   }
}
