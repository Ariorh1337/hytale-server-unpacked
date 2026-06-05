package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors.AllEdgeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors.EdgeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EmptyEdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.SelectorEdgeAction;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class SelectorEdgeActionAsset extends EdgeActionAsset {
   @Nonnull
   public static final BuilderCodec<SelectorEdgeActionAsset> CODEC = BuilderCodec.builder(
         SelectorEdgeActionAsset.class, SelectorEdgeActionAsset::new, EdgeActionAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("EdgeSelector", EdgeSelectorAsset.CODEC, true), (asset, value) -> asset.edgeSelectorAsset = value, asset -> asset.edgeSelectorAsset
      )
      .add()
      .append(new KeyedCodec<>("EdgeAction", EdgeActionAsset.CODEC, true), (asset, value) -> asset.edgeActionAsset = value, asset -> asset.edgeActionAsset)
      .add()
      .build();
   @Nonnull
   private EdgeSelectorAsset edgeSelectorAsset = AllEdgeSelectorAsset.INSTANCE;
   @Nonnull
   private EdgeActionAsset edgeActionAsset = EmptyEdgeActionAsset.INSTANCE;

   @Nonnull
   @Override
   public EdgeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? EmptyEdgeAction.INSTANCE : new SelectorEdgeAction(this.edgeSelectorAsset.build(argument), this.edgeActionAsset.build(argument));
   }

   @Override
   public void cleanUp() {
      this.edgeSelectorAsset.cleanUp();
      this.edgeActionAsset.cleanUp();
   }
}
