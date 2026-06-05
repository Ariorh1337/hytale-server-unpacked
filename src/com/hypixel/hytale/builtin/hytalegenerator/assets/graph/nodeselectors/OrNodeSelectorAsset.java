package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.AllNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.OrNodeSelector;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class OrNodeSelectorAsset extends NodeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<OrNodeSelectorAsset> CODEC = BuilderCodec.builder(
         OrNodeSelectorAsset.class, OrNodeSelectorAsset::new, NodeSelectorAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("NodeSelectors", new ArrayCodec<>(NodeSelectorAsset.CODEC, NodeSelectorAsset[]::new), true),
         (asset, value) -> asset.nodeSelectorAssets = value,
         asset -> asset.nodeSelectorAssets
      )
      .add()
      .build();
   @Nonnull
   private NodeSelectorAsset[] nodeSelectorAssets = new NodeSelectorAsset[0];

   @Nonnull
   @Override
   public NodeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return AllNodeSelector.INSTANCE;
      }

      List<NodeSelector> edgeSelectors = new ArrayList<>(this.nodeSelectorAssets.length);

      for (NodeSelectorAsset nodeSelectorAsset : this.nodeSelectorAssets) {
         edgeSelectors.add(nodeSelectorAsset.build(argument));
      }

      return new OrNodeSelector(edgeSelectors);
   }

   @Override
   public void cleanUp() {
      for (NodeSelectorAsset nodeSelectorAsset : this.nodeSelectorAssets) {
         nodeSelectorAsset.cleanUp();
      }
   }
}
