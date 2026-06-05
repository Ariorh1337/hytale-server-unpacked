package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentpredicates;

import com.hypixel.hytale.builtin.hytalegenerator.predicates.ConstantSetPredicate;
import com.hypixel.hytale.builtin.hytalegenerator.predicates.OrSetPredicate;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class OrContentPredicateAsset extends ContentPredicateAsset {
   @Nonnull
   public static final BuilderCodec<OrContentPredicateAsset> CODEC = BuilderCodec.builder(
         OrContentPredicateAsset.class, OrContentPredicateAsset::new, ContentPredicateAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("ContentPredicates", new ArrayCodec<>(ContentPredicateAsset.CODEC, ContentPredicateAsset[]::new), true),
         (asset, value) -> asset.contentPredicateAssets = value,
         asset -> asset.contentPredicateAssets
      )
      .add()
      .build();
   @Nonnull
   private ContentPredicateAsset[] contentPredicateAssets = new ContentPredicateAsset[0];

   @Nonnull
   @Override
   public Predicate<IntSet> build() {
      if (super.skip()) {
         return ConstantSetPredicate.TRUE;
      }

      List<Predicate<IntSet>> predicates = new ArrayList<>(this.contentPredicateAssets.length);

      for (ContentPredicateAsset contentPredicateAsset : this.contentPredicateAssets) {
         predicates.add(contentPredicateAsset.build());
      }

      return new OrSetPredicate(predicates);
   }

   @Override
   public void cleanUp() {
      for (ContentPredicateAsset contentPredicateAsset : this.contentPredicateAssets) {
         contentPredicateAsset.cleanUp();
      }
   }
}
