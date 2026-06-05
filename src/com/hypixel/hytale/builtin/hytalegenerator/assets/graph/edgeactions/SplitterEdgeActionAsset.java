package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphContentAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.SplitterEdgeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class SplitterEdgeActionAsset extends EdgeActionAsset {
   public static final SplitterEdgeActionAsset INSTANCE = new SplitterEdgeActionAsset();
   @Nonnull
   public static final BuilderCodec<SplitterEdgeActionAsset> CODEC = BuilderCodec.builder(
         SplitterEdgeActionAsset.class, SplitterEdgeActionAsset::new, EdgeActionAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("NodeCount", Codec.INTEGER, true), (asset, value) -> asset.nodeCount = value, asset -> asset.nodeCount)
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .append(new KeyedCodec<>("Content", GraphContentAsset.CODEC, true), (asset, value) -> asset.contentAsset = value, asset -> asset.contentAsset)
      .add()
      .build();
   private int nodeCount = 0;
   @Nonnull
   private GraphContentAsset contentAsset = GraphContentAsset.INSTANCE;

   @Nonnull
   @Override
   public EdgeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      assert this.nodeCount >= 0;
      return new SplitterEdgeAction(this.contentAsset.build(argument), this.nodeCount);
   }

   @Override
   public void cleanUp() {
      this.contentAsset.cleanUp();
   }
}
