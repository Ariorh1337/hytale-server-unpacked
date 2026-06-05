package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.delimiters.RangeDoubleAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.AllEdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.EdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.LengthEdgeSelector;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class LengthEdgeSelectorAsset extends EdgeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<LengthEdgeSelectorAsset> CODEC = BuilderCodec.builder(
         LengthEdgeSelectorAsset.class, LengthEdgeSelectorAsset::new, EdgeSelectorAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Delimiters", new ArrayCodec<>(RangeDoubleAsset.CODEC, RangeDoubleAsset[]::new), true),
         (asset, value) -> asset.delimiterAssets = value,
         asset -> asset.delimiterAssets
      )
      .add()
      .build();
   @Nonnull
   private RangeDoubleAsset[] delimiterAssets = new RangeDoubleAsset[0];

   @Nonnull
   @Override
   public EdgeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return AllEdgeSelector.INSTANCE;
      }

      List<RangeDouble> delimitersLengthSqr = new ArrayList<>(this.delimiterAssets.length);

      for (RangeDoubleAsset asset : this.delimiterAssets) {
         delimitersLengthSqr.add(asset.build());
      }

      return new LengthEdgeSelector(delimitersLengthSqr);
   }
}
