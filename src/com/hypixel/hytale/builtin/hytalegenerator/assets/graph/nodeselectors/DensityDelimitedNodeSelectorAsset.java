package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.delimiters.RangeDoubleAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.AllNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.DensityDelimitedNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class DensityDelimitedNodeSelectorAsset extends NodeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<DensityDelimitedNodeSelectorAsset> CODEC = BuilderCodec.builder(
         DensityDelimitedNodeSelectorAsset.class, DensityDelimitedNodeSelectorAsset::new, NodeSelectorAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Density", DensityAsset.CODEC, true), (asset, value) -> asset.densityAsset = value, asset -> asset.densityAsset)
      .add()
      .append(
         new KeyedCodec<>("Delimiters", new ArrayCodec<>(RangeDoubleAsset.CODEC, RangeDoubleAsset[]::new), true),
         (asset, value) -> asset.delimiterAssets = value,
         asset -> asset.delimiterAssets
      )
      .add()
      .build();
   @Nonnull
   private DensityAsset densityAsset = DensityAsset.getFallbackAsset();
   @Nonnull
   private RangeDoubleAsset[] delimiterAssets = new RangeDoubleAsset[0];

   @Nonnull
   @Override
   public NodeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return AllNodeSelector.INSTANCE;
      }

      List<RangeDouble> delimiters = new ArrayList<>(this.delimiterAssets.length);

      for (RangeDoubleAsset delimiterAsset : this.delimiterAssets) {
         delimiters.add(delimiterAsset.build());
      }

      return new DensityDelimitedNodeSelector(this.densityAsset.build(new DensityAsset.Argument(argument)), delimiters);
   }

   @Override
   public void cleanUp() {
      this.densityAsset.cleanUp();
   }
}
