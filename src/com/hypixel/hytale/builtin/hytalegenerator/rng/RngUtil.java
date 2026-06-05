package com.hypixel.hytale.builtin.hytalegenerator.rng;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;

public class RngUtil {
   public static <T> void pickElements(@Nonnull Random random, int count, @Nonnull List<T> list_mutated, @Nonnull List<T> picked_out) {
      assert picked_out.isEmpty() && count >= 0;
      if (!list_mutated.isEmpty()) {
         count = Math.min(count, list_mutated.size());
         int lastIndex = list_mutated.size() - 1;

         for (int i = 0; i < count; i++) {
            int pickedIndex = random.nextInt(lastIndex + 1);
            picked_out.add(list_mutated.get(pickedIndex));
            list_mutated.set(pickedIndex, list_mutated.get(lastIndex));
            lastIndex--;
         }
      }
   }

   public static void pickElements(@Nonnull Random random, int count, @Nonnull IntArrayList list_mutated, @Nonnull IntArrayList picked_out) {
      assert picked_out.isEmpty() && count >= 0;
      if (!list_mutated.isEmpty()) {
         count = Math.min(count, list_mutated.size());
         int lastIndex = list_mutated.size() - 1;

         for (int i = 0; i < count; i++) {
            int pickedIndex = random.nextInt(lastIndex + 1);
            picked_out.add(list_mutated.getInt(pickedIndex));
            list_mutated.set(pickedIndex, list_mutated.getInt(lastIndex));
            lastIndex--;
         }
      }
   }

   public static void pickIndices(@Nonnull Random random, int count, int size, @Nonnull IntArrayList indices_out) {
      assert count <= size;
      if (count > 0) {
         if (count == size) {
            for (int i = 0; i < size; i++) {
               indices_out.add(i);
            }
         } else {
            IntArrayList allIndices = new IntArrayList(size);

            for (int i = 0; i < size; i++) {
               allIndices.add(i);
            }

            pickElements(random, count, allIndices, indices_out);
         }
      }
   }
}
