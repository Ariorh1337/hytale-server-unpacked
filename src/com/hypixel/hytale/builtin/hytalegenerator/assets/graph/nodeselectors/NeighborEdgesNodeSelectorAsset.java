package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors.AllEdgeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors.EdgeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.AllNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NeighborEdgesNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class NeighborEdgesNodeSelectorAsset extends NodeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<NeighborEdgesNodeSelectorAsset> CODEC = BuilderCodec.builder(
         NeighborEdgesNodeSelectorAsset.class, NeighborEdgesNodeSelectorAsset::new, NodeSelectorAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("EdgeSelector", EdgeSelectorAsset.CODEC, true), (asset, value) -> asset.edgeSelectorAsset = value, asset -> asset.edgeSelectorAsset
      )
      .add()
      .append(
         new KeyedCodec<>("SelectedNeighborsThreshold", Codec.INTEGER, true),
         (asset, value) -> asset.selectedNeighborsThreshold = value,
         asset -> asset.selectedNeighborsThreshold
      )
      .add()
      .build();
   @Nonnull
   private EdgeSelectorAsset edgeSelectorAsset = AllEdgeSelectorAsset.INSTANCE;
   private int selectedNeighborsThreshold = -1;

   @Nonnull
   @Override
   public NodeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? AllNodeSelector.INSTANCE : new NeighborEdgesNodeSelector(this.edgeSelectorAsset.build(argument), this.selectedNeighborsThreshold);
   }

   @Override
   public void cleanUp() {
      this.edgeSelectorAsset.cleanUp();
   }
}
