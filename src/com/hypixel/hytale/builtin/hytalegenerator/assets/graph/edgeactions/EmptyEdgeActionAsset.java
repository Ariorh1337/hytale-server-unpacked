package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EmptyEdgeAction;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class EmptyEdgeActionAsset extends EdgeActionAsset {
   public static final EmptyEdgeActionAsset INSTANCE = new EmptyEdgeActionAsset();
   @Nonnull
   public static final BuilderCodec<EmptyEdgeActionAsset> CODEC = BuilderCodec.builder(
         EmptyEdgeActionAsset.class, EmptyEdgeActionAsset::new, EdgeActionAsset.ABSTRACT_CODEC
      )
      .build();

   @Nonnull
   @Override
   public EdgeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return EmptyEdgeAction.INSTANCE;
   }
}
