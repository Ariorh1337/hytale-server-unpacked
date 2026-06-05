package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions.EmptyNodeActionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions.NodeActionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EmptyEdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.NodesEdgeAction;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class NodesEdgeActionAsset extends EdgeActionAsset {
   @Nonnull
   public static final BuilderCodec<NodesEdgeActionAsset> CODEC = BuilderCodec.builder(
         NodesEdgeActionAsset.class, NodesEdgeActionAsset::new, EdgeActionAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("NodeAction", NodeActionAsset.CODEC, true), (asset, value) -> asset.nodeActionAsset = value, asset -> asset.nodeActionAsset)
      .add()
      .build();
   @Nonnull
   private NodeActionAsset nodeActionAsset = EmptyNodeActionAsset.INSTANCE;

   @Nonnull
   @Override
   public EdgeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? EmptyEdgeAction.INSTANCE : new NodesEdgeAction(this.nodeActionAsset.build(argument));
   }

   @Override
   public void cleanUp() {
      this.nodeActionAsset.cleanUp();
   }
}
