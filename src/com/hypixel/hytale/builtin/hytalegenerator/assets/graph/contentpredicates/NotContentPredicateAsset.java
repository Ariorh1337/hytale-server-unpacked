package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentpredicates;

import com.hypixel.hytale.builtin.hytalegenerator.predicates.ConstantSetPredicate;
import com.hypixel.hytale.builtin.hytalegenerator.predicates.NotSetPredicate;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class NotContentPredicateAsset extends ContentPredicateAsset {
   @Nonnull
   public static final BuilderCodec<NotContentPredicateAsset> CODEC = BuilderCodec.builder(
         NotContentPredicateAsset.class, NotContentPredicateAsset::new, ContentPredicateAsset.ABSTRACT_CODEC
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
   public Predicate<IntSet> build() {
      return super.skip() ? ConstantSetPredicate.TRUE : new NotSetPredicate(this.contentPredicateAsset.build());
   }

   @Override
   public void cleanUp() {
      this.contentPredicateAsset.cleanUp();
   }
}
