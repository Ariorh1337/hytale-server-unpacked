package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.DeleteEdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EmptyEdgeAction;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class DeleteEdgeActionAsset extends EdgeActionAsset {
   @Nonnull
   public static final BuilderCodec<DeleteEdgeActionAsset> CODEC = BuilderCodec.builder(
         DeleteEdgeActionAsset.class, DeleteEdgeActionAsset::new, EdgeActionAsset.ABSTRACT_CODEC
      )
      .build();

   @Nonnull
   @Override
   public EdgeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? EmptyEdgeAction.INSTANCE : new DeleteEdgeAction();
   }
}
