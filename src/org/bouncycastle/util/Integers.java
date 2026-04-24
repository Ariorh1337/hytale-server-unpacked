package org.bouncycastle.util;

public class Integers {
   public static final int BYTES = 4;
   public static final int SIZE = 32;

   public static int bitCount(int var0) {
      return Integer.bitCount(var0);
   }

   public static int bitLength(int var0) {
      return 32 - numberOfLeadingZeros(var0);
   }

   public static int compare(int var0, int var1) {
      return var0 < var1 ? -1 : (var0 == var1 ? 0 : 1);
   }

   public static int compareUnsigned(int var0, int var1) {
      return compare(var0 + Integer.MIN_VALUE, var1 + Integer.MIN_VALUE);
   }

   public static int highestOneBit(int var0) {
      return Integer.highestOneBit(var0);
   }

   public static int lowestOneBit(int var0) {
      return Integer.lowestOneBit(var0);
   }

   public static int numberOfLeadingZeros(int var0) {
      return Integer.numberOfLeadingZeros(var0);
   }

   public static int numberOfTrailingZeros(int var0) {
      return Integer.numberOfTrailingZeros(var0);
   }

   public static int reverse(int var0) {
      return Integer.reverse(var0);
   }

   public static int reverseBytes(int var0) {
      return Integer.reverseBytes(var0);
   }

   public static int rotateLeft(int var0, int var1) {
      return Integer.rotateLeft(var0, var1);
   }

   public static int rotateRight(int var0, int var1) {
      return Integer.rotateRight(var0, var1);
   }

   public static Integer valueOf(int var0) {
      return var0;
   }
}
