package com.hypixel.hytale.builtin.hytalegenerator.predicates;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class AndSetPredicate implements Predicate<IntSet> {
   @Nonnull
   private final List<Predicate<IntSet>> predicates;

   public AndSetPredicate(@Nonnull List<Predicate<IntSet>> predicates) {
      this.predicates = predicates;
   }

   public boolean test(@Nonnull IntSet set) {
      for (Predicate<IntSet> predicate : this.predicates) {
         if (!predicate.test(set)) {
            return false;
         }
      }

      return true;
   }
}
