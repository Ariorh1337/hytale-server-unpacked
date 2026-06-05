package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentpredicates.ConstantContentPredicateAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentpredicates.ContentPredicateAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.AllNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.ContentNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ContentNodeSelectorAsset extends NodeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<ContentNodeSelectorAsset> CODEC = BuilderCodec.builder(
         ContentNodeSelectorAsset.class, ContentNodeSelectorAsset::new, NodeSelectorAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("ContentPredicate", ContentPredicateAsset.CODEC, true),
         (asset, value) -> asset.contentPredicateAsset = value,
         asset -> asset.contentPredicateAsset
      )
      .add()
      .build();
   @Nonnull
   private ContentPredicateAsset contentPredicateAsset = ConstantContentPredicateAsset.INSTANCE;

   @Nonnull
   @Override
   public NodeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? AllNodeSelector.INSTANCE : new ContentNodeSelector(this.contentPredicateAsset.build());
   }

   @Override
   public void cleanUp() {
      this.contentPredicateAsset.cleanUp();
   }
}
