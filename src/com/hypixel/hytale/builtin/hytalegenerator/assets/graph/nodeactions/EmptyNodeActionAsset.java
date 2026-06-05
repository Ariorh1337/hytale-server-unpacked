package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class EmptyNodeActionAsset extends NodeActionAsset {
   public static final EmptyNodeActionAsset INSTANCE = new EmptyNodeActionAsset();
   @Nonnull
   public static final BuilderCodec<EmptyNodeActionAsset> CODEC = BuilderCodec.builder(
         EmptyNodeActionAsset.class, EmptyNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .build();

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return EmptyNodeAction.INSTANCE;
   }
}
