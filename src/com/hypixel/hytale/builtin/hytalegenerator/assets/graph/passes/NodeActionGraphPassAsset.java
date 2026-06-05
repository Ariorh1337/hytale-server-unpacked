package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.passes;

import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions.EmptyNodeActionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions.NodeActionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.EmptyGraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.GraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.NodeActionGraphPass;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class NodeActionGraphPassAsset extends GraphPassAsset implements Cleanable {
   @Nonnull
   public static final BuilderCodec<NodeActionGraphPassAsset> CODEC = BuilderCodec.builder(
         NodeActionGraphPassAsset.class, NodeActionGraphPassAsset::new, GraphPassAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("NodeAction", NodeActionAsset.CODEC, true), (asset, value) -> asset.nodeActionAsset = value, asset -> asset.nodeActionAsset)
      .add()
      .append(new KeyedCodec<>("StatsLabel", Codec.STRING, true), (asset, value) -> asset.statsLabel = value, asset -> asset.statsLabel)
      .add()
      .build();
   @Nonnull
   private NodeActionAsset nodeActionAsset = EmptyNodeActionAsset.INSTANCE;
   @Nonnull
   private String statsLabel = "";

   @Nonnull
   @Override
   public GraphPass build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? EmptyGraphPass.INSTANCE : new NodeActionGraphPass(this.nodeActionAsset.build(argument), this.statsLabel);
   }

   @Override
   public void cleanUp() {
      this.nodeActionAsset.cleanUp();
   }
}
