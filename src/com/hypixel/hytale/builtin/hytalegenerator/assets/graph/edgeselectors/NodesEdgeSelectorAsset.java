package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors.AllNodeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors.NodeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.AllEdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.EdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.NodesEdgeSelector;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import javax.annotation.Nonnull;

public class NodesEdgeSelectorAsset extends EdgeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<NodesEdgeSelectorAsset> CODEC = BuilderCodec.builder(
         NodesEdgeSelectorAsset.class, NodesEdgeSelectorAsset::new, EdgeSelectorAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("NodeSelector", NodeSelectorAsset.CODEC, true), (asset, value) -> asset.nodeSelectorAsset = value, asset -> asset.nodeSelectorAsset
      )
      .add()
      .append(
         new KeyedCodec<>("Seed", new EnumCodec<>(NodesEdgeSelector.Operator.class), true), (asset, value) -> asset.operator = value, asset -> asset.operator
      )
      .add()
      .build();
   @Nonnull
   private NodeSelectorAsset nodeSelectorAsset = AllNodeSelectorAsset.INSTANCE;
   @Nonnull
   private NodesEdgeSelector.Operator operator = NodesEdgeSelector.Operator.AND;

   @Nonnull
   @Override
   public EdgeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? AllEdgeSelector.INSTANCE : new NodesEdgeSelector(this.nodeSelectorAsset.build(argument), this.operator);
   }

   @Override
   public void cleanUp() {
      this.nodeSelectorAsset.cleanUp();
   }
}
