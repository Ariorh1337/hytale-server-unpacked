package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;

public class DESParameters extends KeyParameter {
   public static final int DES_KEY_LENGTH = 8;
   private static final int N_DES_WEAK_KEYS = 16;
   private static byte[] DES_weak_keys = new byte[]{
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      31,
      31,
      31,
      31,
      14,
      14,
      14,
      14,
      -32,
      -32,
      -32,
      -32,
      -15,
      -15,
      -15,
      -15,
      -2,
      -2,
      -2,
      -2,
      -2,
      -2,
      -2,
      -2,
      1,
      -2,
      1,
      -2,
      1,
      -2,
      1,
      -2,
      31,
      -32,
      31,
      -32,
      14,
      -15,
      14,
      -15,
      1,
      -32,
      1,
      -32,
      1,
      -15,
      1,
      -15,
      31,
      -2,
      31,
      -2,
      14,
      -2,
      14,
      -2,
      1,
      31,
      1,
      31,
      1,
      14,
      1,
      14,
      -32,
      -2,
      -32,
      -2,
      -15,
      -2,
      -15,
      -2,
      -2,
      1,
      -2,
      1,
      -2,
      1,
      -2,
      1,
      -32,
      31,
      -32,
      31,
      -15,
      14,
      -15,
      14,
      -32,
      1,
      -32,
      1,
      -15,
      1,
      -15,
      1,
      -2,
      31,
      -2,
      31,
      -2,
      14,
      -2,
      14,
      31,
      1,
      31,
      1,
      14,
      1,
      14,
      1,
      -2,
      -32,
      -2,
      -32,
      -2,
      -15,
      -2,
      -15
   };

   public DESParameters(byte[] var1) {
      super(var1);
      if (isWeakKey(var1, 0)) {
         throw new IllegalArgumentException("attempt to create weak DES key");
      }
   }

   public static boolean isWeakKey(byte[] var0, int var1) {
      if (var1 > var0.length - 8) {
         throw new IllegalArgumentException("key material too short.");
      }

      for (int var2 = 0; var2 < 16; var2++) {
         if (Arrays.constantTimeAreEqual(8, var0, var1, DES_weak_keys, var2 * 8)) {
            return true;
         }
      }

      return false;
   }

   public static void setOddParity(byte[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         byte var2 = var0[var1];
         var0[var1] = (byte)(var2 & 254 | (var2 >> 1 ^ var2 >> 2 ^ var2 >> 3 ^ var2 >> 4 ^ var2 >> 5 ^ var2 >> 6 ^ var2 >> 7 ^ 1) & 1);
      }
   }
}
