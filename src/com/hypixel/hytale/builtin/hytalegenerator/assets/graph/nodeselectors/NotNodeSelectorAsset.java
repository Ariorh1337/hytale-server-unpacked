package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.AllNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NotNodeSelector;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class NotNodeSelectorAsset extends NodeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<NotNodeSelectorAsset> CODEC = BuilderCodec.builder(
         NotNodeSelectorAsset.class, NotNodeSelectorAsset::new, NodeSelectorAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("NodeSelector", NodeSelectorAsset.CODEC, true), (asset, value) -> asset.nodeSelectorAsset = value, asset -> asset.nodeSelectorAsset
      )
      .add()
      .build();
   @Nonnull
   private NodeSelectorAsset nodeSelectorAsset = AllNodeSelectorAsset.INSTANCE;

   @Nonnull
   @Override
   public NodeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? AllNodeSelector.INSTANCE : new NotNodeSelector(this.nodeSelectorAsset.build(argument));
   }

   @Override
   public void cleanUp() {
      this.nodeSelectorAsset.cleanUp();
   }
}
