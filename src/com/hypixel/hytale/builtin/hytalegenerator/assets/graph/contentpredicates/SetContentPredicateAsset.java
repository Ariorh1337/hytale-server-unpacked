package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentpredicates;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.predicates.ConstantSetPredicate;
import com.hypixel.hytale.builtin.hytalegenerator.predicates.ListSetPredicate;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class SetContentPredicateAsset extends ContentPredicateAsset {
   @Nonnull
   public static final BuilderCodec<SetContentPredicateAsset> CODEC = BuilderCodec.builder(
         SetContentPredicateAsset.class, SetContentPredicateAsset::new, ContentPredicateAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("ContentTags", new ArrayCodec<>(Codec.STRING, String[]::new), true), (asset, value) -> asset.values = value, asset -> asset.values
      )
      .add()
      .build();
   @Nonnull
   private String[] values = new String[0];

   @Nonnull
   @Override
   public Predicate<IntSet> build() {
      if (super.skip()) {
         return ConstantSetPredicate.TRUE;
      }

      IntList set = new IntArrayList(this.values.length);

      for (String value : this.values) {
         set.add(GraphSpace.Content.toIntId(value));
      }

      return new ListSetPredicate(set);
   }
}
