package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentpredicates.ConstantContentPredicateAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentpredicates.ContentPredicateAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.ContentCopyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ContentCopyNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<ContentCopyNodeActionAsset> CODEC = BuilderCodec.builder(
         ContentCopyNodeActionAsset.class, ContentCopyNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("ContentPredicate", ContentPredicateAsset.CODEC, true),
         (asset, value) -> asset.contentPredicateAsset = value,
         asset -> asset.contentPredicateAsset
      )
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   @Nonnull
   private ContentPredicateAsset contentPredicateAsset = ConstantContentPredicateAsset.INSTANCE;
   @Nonnull
   private String seed = "";

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return EmptyNodeAction.INSTANCE;
      }

      int seedInt = argument.parentSeed.child(this.seed).createSupplier().get();
      return new ContentCopyNodeAction(this.contentPredicateAsset.build(), seedInt);
   }

   @Override
   public void cleanUp() {
      this.contentPredicateAsset.cleanUp();
   }
}
