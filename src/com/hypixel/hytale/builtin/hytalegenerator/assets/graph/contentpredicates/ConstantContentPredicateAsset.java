package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentpredicates;

import com.hypixel.hytale.builtin.hytalegenerator.predicates.ConstantSetPredicate;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class ConstantContentPredicateAsset extends ContentPredicateAsset {
   public static final ConstantContentPredicateAsset INSTANCE = new ConstantContentPredicateAsset();
   @Nonnull
   public static final BuilderCodec<ConstantContentPredicateAsset> CODEC = BuilderCodec.builder(
         ConstantContentPredicateAsset.class, ConstantContentPredicateAsset::new, ContentPredicateAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Value", Codec.BOOLEAN, true), (asset, value) -> asset.value = value, asset -> asset.value)
      .add()
      .build();
   private boolean value = true;

   @Nonnull
   @Override
   public Predicate<IntSet> build() {
      if (super.skip()) {
         return ConstantSetPredicate.TRUE;
      } else {
         return this.value ? ConstantSetPredicate.TRUE : ConstantSetPredicate.FALSE;
      }
   }
}
