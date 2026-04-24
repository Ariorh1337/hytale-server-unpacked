package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Pack;

class Utils {
   static void fromLongArrayToByteArray(byte[] var0, int var1, int var2, long[] var3) {
      int var4 = var2 >> 3;
      Pack.longToLittleEndian(var3, 0, var4, var0, var1);
      int var5 = var2 & 7;
      if (var5 != 0) {
         Pack.longToLittleEndian_Low(var3[var4], var0, var1 + var2 - var5, var5);
      }
   }

   static void fromByteArrayToLongArray(long[] var0, byte[] var1, int var2, int var3) {
      int var4 = var3 >> 3;
      Pack.littleEndianToLong(var1, var2, var0, 0, var4);
      int var5 = var3 & 7;
      if (var5 != 0) {
         var0[var4] = Pack.littleEndianToLong_Low(var1, var2 + var3 - var5, var5);
      }
   }

   static void fromByte32ArrayToLongArray(long[] var0, int[] var1) {
      for (byte var2 = 0; var2 != var1.length; var2 += 2) {
         var0[var2 / 2] = var1[var2] & 4294967295L;
         var0[var2 / 2] = var0[var2 / 2] | (long)var1[var2 + 1] << 32;
      }
   }

   static void fromLongArrayToByte32Array(int[] var0, long[] var1) {
      for (int var2 = 0; var2 != var1.length; var2++) {
         var0[2 * var2] = (int)var1[var2];
         var0[2 * var2 + 1] = (int)(var1[var2] >> 32);
      }
   }

   static int getByteSizeFromBitSize(int var0) {
      return (var0 + 7) / 8;
   }

   static int getByte64SizeFromBitSize(int var0) {
      return (var0 + 63) / 64;
   }

   static int toUnsigned8bits(int var0) {
      return var0 & 0xFF;
   }

   static int toUnsigned16Bits(int var0) {
      return var0 & 65535;
   }
}
