package com.hypixel.hytale.sneakythrow.function;

import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Throwable> extends Function<T, R> {
   @Override
   default R apply(T t) {
      try {
         return this.applyNow(t);
      } catch (Throwable e) {
         throw SneakyThrow.sneakyThrow(e);
      }
   }

   R applyNow(T var1) throws E;
}
