package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.delimiters.RangeDoubleAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.ConstantVectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.VectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.AllEdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.AngleEdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.EdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class AngleEdgeSelectorAsset extends EdgeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<AngleEdgeSelectorAsset> CODEC = BuilderCodec.builder(
         AngleEdgeSelectorAsset.class, AngleEdgeSelectorAsset::new, EdgeSelectorAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Reference", VectorProviderAsset.CODEC, true),
         (asset, value) -> asset.referenceVectorProviderAsset = value,
         asset -> asset.referenceVectorProviderAsset
      )
      .add()
      .append(
         new KeyedCodec<>("Delimiters", new ArrayCodec<>(RangeDoubleAsset.CODEC, RangeDoubleAsset[]::new), true),
         (asset, value) -> asset.delimiterAssets = value,
         asset -> asset.delimiterAssets
      )
      .add()
      .build();
   @Nonnull
   private VectorProviderAsset referenceVectorProviderAsset = ConstantVectorProviderAsset.INSTANCE;
   @Nonnull
   private RangeDoubleAsset[] delimiterAssets = new RangeDoubleAsset[0];

   @Nonnull
   @Override
   public EdgeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return AllEdgeSelector.INSTANCE;
      }

      VectorProvider referenceVectorProvider = this.referenceVectorProviderAsset.build(new VectorProviderAsset.Argument(argument));
      List<RangeDouble> delimiters = new ArrayList<>(this.delimiterAssets.length);

      for (RangeDoubleAsset asset : this.delimiterAssets) {
         delimiters.add(asset.build());
      }

      return new AngleEdgeSelector(referenceVectorProvider, delimiters);
   }

   @Override
   public void cleanUp() {
      this.referenceVectorProviderAsset.cleanUp();
   }
}
