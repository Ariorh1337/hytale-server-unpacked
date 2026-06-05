package com.hypixel.hytale.builtin.hytalegenerator.predicates;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class NotSetPredicate implements Predicate<IntSet> {
   @Nonnull
   private final Predicate<IntSet> predicate;

   public NotSetPredicate(@Nonnull Predicate<IntSet> predicate) {
      this.predicate = predicate;
   }

   public boolean test(IntSet set) {
      return !this.predicate.test(set);
   }
}
