package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors.AllNodeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors.NodeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.SelectorNodeAction;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class SelectorNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<SelectorNodeActionAsset> CODEC = BuilderCodec.builder(
         SelectorNodeActionAsset.class, SelectorNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("NodeSelector", NodeSelectorAsset.CODEC, true), (asset, value) -> asset.nodeSelectorAsset = value, asset -> asset.nodeSelectorAsset
      )
      .add()
      .append(new KeyedCodec<>("NodeAction", NodeActionAsset.CODEC, true), (asset, value) -> asset.nodeActionAsset = value, asset -> asset.nodeActionAsset)
      .add()
      .build();
   @Nonnull
   private NodeSelectorAsset nodeSelectorAsset = AllNodeSelectorAsset.INSTANCE;
   @Nonnull
   private NodeActionAsset nodeActionAsset = EmptyNodeActionAsset.INSTANCE;

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? EmptyNodeAction.INSTANCE : new SelectorNodeAction(this.nodeSelectorAsset.build(argument), this.nodeActionAsset.build(argument));
   }

   @Override
   public void cleanUp() {
      this.nodeSelectorAsset.cleanUp();
      this.nodeActionAsset.cleanUp();
   }
}
