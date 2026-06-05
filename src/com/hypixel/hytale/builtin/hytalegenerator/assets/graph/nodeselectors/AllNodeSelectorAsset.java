package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.AllNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class AllNodeSelectorAsset extends NodeSelectorAsset {
   public static final AllNodeSelectorAsset INSTANCE = new AllNodeSelectorAsset();
   @Nonnull
   public static final BuilderCodec<AllNodeSelectorAsset> CODEC = BuilderCodec.builder(
         AllNodeSelectorAsset.class, AllNodeSelectorAsset::new, NodeSelectorAsset.ABSTRACT_CODEC
      )
      .build();

   @Nonnull
   @Override
   public NodeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return AllNodeSelector.INSTANCE;
   }
}
