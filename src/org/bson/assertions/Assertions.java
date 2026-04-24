package org.bson.assertions;

import javax.annotation.Nullable;

public final class Assertions {
   public static <T> T notNull(String name, T value) {
      if (value == null) {
         throw new IllegalArgumentException(name + " can not be null");
      } else {
         return value;
      }
   }

   public static void isTrue(String name, boolean condition) {
      if (!condition) {
         throw new IllegalStateException("state should be: " + name);
      }
   }

   public static void isTrueArgument(String name, boolean condition) {
      if (!condition) {
         throw new IllegalArgumentException("state should be: " + name);
      }
   }

   public static <T> T isTrueArgument(String name, T value, boolean condition) {
      if (!condition) {
         throw new IllegalArgumentException("state should be: " + name);
      } else {
         return value;
      }
   }

   public static AssertionError fail() throws AssertionError {
      throw new AssertionError();
   }

   public static AssertionError fail(String msg) throws AssertionError {
      throw new AssertionError(assertNotNull(msg));
   }

   public static AssertionError fail(String msg, Throwable cause) throws AssertionError {
      throw new AssertionError(assertNotNull(msg), assertNotNull(cause));
   }

   public static <T> T assertNotNull(@Nullable T value) throws AssertionError {
      if (value == null) {
         throw new AssertionError();
      } else {
         return value;
      }
   }

   public static void assertTrue(String name, boolean condition) {
      if (!condition) {
         throw new AssertionError("state should be: " + assertNotNull(name));
      }
   }

   public static <T> T convertToType(Class<T> clazz, Object value, String errorMessage) {
      if (!clazz.isAssignableFrom(value.getClass())) {
         throw new IllegalArgumentException(errorMessage);
      } else {
         return (T)value;
      }
   }

   private Assertions() {
   }
}
