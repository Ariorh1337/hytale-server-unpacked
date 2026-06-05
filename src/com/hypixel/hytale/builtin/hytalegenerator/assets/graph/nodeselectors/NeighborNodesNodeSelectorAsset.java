package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.AllNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NeighborNodesNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class NeighborNodesNodeSelectorAsset extends NodeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<NeighborNodesNodeSelectorAsset> CODEC = BuilderCodec.builder(
         NeighborNodesNodeSelectorAsset.class, NeighborNodesNodeSelectorAsset::new, NodeSelectorAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("NodeSelector", NodeSelectorAsset.CODEC, true), (asset, value) -> asset.nodeSelectorAsset = value, asset -> asset.nodeSelectorAsset
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
   private NodeSelectorAsset nodeSelectorAsset = AllNodeSelectorAsset.INSTANCE;
   private int selectedNeighborsThreshold = -1;

   @Nonnull
   @Override
   public NodeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? AllNodeSelector.INSTANCE : new NeighborNodesNodeSelector(this.nodeSelectorAsset.build(argument), this.selectedNeighborsThreshold);
   }

   @Override
   public void cleanUp() {
      this.nodeSelectorAsset.cleanUp();
   }
}
