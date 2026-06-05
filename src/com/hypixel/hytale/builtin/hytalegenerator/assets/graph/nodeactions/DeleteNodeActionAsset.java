package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.DeleteNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class DeleteNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<DeleteNodeActionAsset> CODEC = BuilderCodec.builder(
         DeleteNodeActionAsset.class, DeleteNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .build();

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? EmptyNodeAction.INSTANCE : new DeleteNodeAction();
   }
}
