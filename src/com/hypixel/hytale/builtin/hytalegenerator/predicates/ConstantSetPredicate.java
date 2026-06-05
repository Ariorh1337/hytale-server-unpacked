package com.hypixel.hytale.builtin.hytalegenerator.predicates;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.Predicate;

public class ConstantSetPredicate implements Predicate<IntSet> {
   public static final ConstantSetPredicate TRUE = new ConstantSetPredicate(true);
   public static final ConstantSetPredicate FALSE = new ConstantSetPredicate(false);
   private final boolean value;

   private ConstantSetPredicate(boolean value) {
      this.value = value;
   }

   public boolean test(IntSet set) {
      return this.value;
   }
}
