package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.passes;

import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions.EdgeActionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions.EmptyEdgeActionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.EdgeActionGraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.EmptyGraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.GraphPass;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class EdgeActionGraphPassAsset extends GraphPassAsset implements Cleanable {
   @Nonnull
   public static final BuilderCodec<EdgeActionGraphPassAsset> CODEC = BuilderCodec.builder(
         EdgeActionGraphPassAsset.class, EdgeActionGraphPassAsset::new, GraphPassAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("EdgeAction", EdgeActionAsset.CODEC, true), (asset, value) -> asset.edgeActionAsset = value, asset -> asset.edgeActionAsset)
      .add()
      .append(new KeyedCodec<>("StatsLabel", Codec.STRING, true), (asset, value) -> asset.statsLabel = value, asset -> asset.statsLabel)
      .add()
      .build();
   @Nonnull
   private EdgeActionAsset edgeActionAsset = EmptyEdgeActionAsset.INSTANCE;
   @Nonnull
   private String statsLabel = "";

   @Nonnull
   @Override
   public GraphPass build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? EmptyGraphPass.INSTANCE : new EdgeActionGraphPass(this.edgeActionAsset.build(argument), this.statsLabel);
   }

   @Override
   public void cleanUp() {
      this.edgeActionAsset.cleanUp();
   }
}
