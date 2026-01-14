package com.hypixel.hytale.sneakythrow.supplier;

import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.util.function.IntSupplier;

@FunctionalInterface
public interface ThrowableIntSupplier<E extends Throwable> extends IntSupplier {
   @Override
   default int getAsInt() {
      try {
         return this.getAsIntNow();
      } catch (Throwable e) {
         throw SneakyThrow.sneakyThrow(e);
      }
   }

   int getAsIntNow() throws E;
}
