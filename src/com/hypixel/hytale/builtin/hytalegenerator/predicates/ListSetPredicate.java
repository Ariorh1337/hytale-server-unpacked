package com.hypixel.hytale.builtin.hytalegenerator.predicates;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class ListSetPredicate implements Predicate<IntSet> {
   @Nonnull
   private final IntList list;

   public ListSetPredicate(@Nonnull IntList list) {
      this.list = new IntArrayList(list);
   }

   public boolean test(IntSet set) {
      for (int e : this.list) {
         if (set.contains(e)) {
            return true;
         }
      }

      return false;
   }
}
