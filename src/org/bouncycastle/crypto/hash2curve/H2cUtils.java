package org.bouncycastle.crypto.hash2curve;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Arrays;

public class H2cUtils {
   public static BigInteger cmov(BigInteger var0, BigInteger var1, boolean var2) {
      return var2 ? var1 : var0;
   }

   public static boolean isSquare(BigInteger var0, BigInteger var1) {
      BigInteger var2 = var0.modPow(var1.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2L)), var1);
      return var2.equals(BigInteger.ONE) || var2.equals(BigInteger.ZERO);
   }

   public static BigInteger sqrt(BigInteger var0, BigInteger var1) {
      int var2 = var1.subtract(BigInteger.ONE).getLowestSetBit();
      BigInteger var3 = var1.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2L).pow(var2));
      BigInteger var4 = var3.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2L));
      BigInteger var5 = getFirstNonSquare(var1);
      BigInteger var6 = var5.modPow(var3, var1);
      BigInteger var7 = var0.modPow(var4, var1);
      BigInteger var8 = var7.multiply(var7).multiply(var0).mod(var1);
      var7 = var7.multiply(var0).mod(var1);
      BigInteger var9 = var8;
      BigInteger var10 = var6;

      for (int var11 = var2; var11 >= 2; var11--) {
         for (int var12 = 1; var12 <= var11 - 2; var12++) {
            var9 = var9.multiply(var9).mod(var1);
         }

         boolean var16 = var9.equals(BigInteger.ONE);
         BigInteger var13 = var7.multiply(var10).mod(var1);
         var7 = cmov(var13, var7, var16);
         var10 = var10.multiply(var10).mod(var1);
         BigInteger var14 = var8.multiply(var10).mod(var1);
         var8 = cmov(var14, var8, var16);
         var9 = var8;
      }

      return var7;
   }

   public static int sgn0(BigInteger var0, ECCurve var1) {
      if (var1.getField().getDimension() == 1) {
         return var0.intValue() & 1;
      } else {
         throw new IllegalArgumentException("Extension fields must be 1 for supported elliptic curves");
      }
   }

   public static BigInteger inv0(BigInteger var0, BigInteger var1) {
      return var0.modInverse(var1);
   }

   public static byte[] i2osp(int var0, int var1) {
      byte[] var2 = new BigInteger(String.valueOf(var0)).toByteArray();
      byte[] var3 = (byte[])var2.clone();
      if (var3.length > 1 && var3[0] == 0) {
         var3 = Arrays.copyOfRange(var3, 1, var3.length);
      }

      if (var3.length > var1) {
         throw new IllegalArgumentException("Value require more bytes than the assigned length size");
      }

      if (var3.length < var1) {
         for (int var4 = var3.length; var4 < var1; var4++) {
            var3 = Arrays.concatenate(new byte[]{0}, var3);
         }
      }

      return var3;
   }

   public static BigInteger os2ip(byte[] var0) {
      return new BigInteger(Arrays.concatenate(new byte[]{0}, var0));
   }

   public static byte[] xor(byte[] var0, byte[] var1) {
      requireNonNull(var0, "XOR argument must not be null");
      requireNonNull(var1, "XOR argument must not be null");
      if (var0.length != var1.length) {
         throw new IllegalArgumentException("XOR operation on parameters of different lengths");
      }

      byte[] var2 = new byte[var0.length];

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2[var3] = (byte)(var0[var3] ^ var1[var3]);
      }

      return var2;
   }

   private static BigInteger getFirstNonSquare(BigInteger var0) {
      BigInteger var1 = new BigInteger("1000");
      BigInteger var2 = BigInteger.ONE;

      while (isSquare(var2, var0)) {
         var2 = var2.add(BigInteger.ONE);
         if (var2.compareTo(var1) > 0) {
            throw new RuntimeException("Illegal Field. No non square value can be found");
         }
      }

      return var2;
   }

   private static void requireNonNull(Object var0, String var1) {
      if (var0 == null) {
         throw new NullPointerException(var1);
      }
   }
}
