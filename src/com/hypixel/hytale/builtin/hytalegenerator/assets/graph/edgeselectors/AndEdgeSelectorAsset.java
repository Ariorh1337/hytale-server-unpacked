package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.AllEdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.AndEdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.EdgeSelector;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class AndEdgeSelectorAsset extends EdgeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<AndEdgeSelectorAsset> CODEC = BuilderCodec.builder(
         AndEdgeSelectorAsset.class, AndEdgeSelectorAsset::new, EdgeSelectorAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("EdgeSelectors", new ArrayCodec<>(EdgeSelectorAsset.CODEC, EdgeSelectorAsset[]::new), true),
         (asset, value) -> asset.edgeSelectorAssets = value,
         asset -> asset.edgeSelectorAssets
      )
      .add()
      .build();
   @Nonnull
   private EdgeSelectorAsset[] edgeSelectorAssets = new EdgeSelectorAsset[0];

   @Nonnull
   @Override
   public EdgeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return AllEdgeSelector.INSTANCE;
      }

      List<EdgeSelector> edgeSelectors = new ArrayList<>(this.edgeSelectorAssets.length);

      for (EdgeSelectorAsset edgeSelectorAsset : this.edgeSelectorAssets) {
         edgeSelectors.add(edgeSelectorAsset.build(argument));
      }

      return new AndEdgeSelector(edgeSelectors);
   }

   @Override
   public void cleanUp() {
      for (EdgeSelectorAsset edgeSelectorAsset : this.edgeSelectorAssets) {
         edgeSelectorAsset.cleanUp();
      }
   }
}
