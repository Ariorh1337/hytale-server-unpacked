package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.passes;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.EmptyGraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.GraphPass;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class EmptyGraphPassAsset extends GraphPassAsset {
   public static final EmptyGraphPassAsset INSTANCE = new EmptyGraphPassAsset();
   @Nonnull
   public static final BuilderCodec<EmptyGraphPassAsset> CODEC = BuilderCodec.builder(
         EmptyGraphPassAsset.class, EmptyGraphPassAsset::new, GraphPassAsset.ABSTRACT_CODEC
      )
      .build();

   @Nonnull
   @Override
   public GraphPass build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return EmptyGraphPass.INSTANCE;
   }
}
