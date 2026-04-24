package org.bouncycastle.crypto.hash2curve.impl;

import java.math.BigInteger;
import org.bouncycastle.crypto.hash2curve.H2cUtils;
import org.bouncycastle.crypto.hash2curve.SqrtRatioCalculator;
import org.bouncycastle.math.ec.ECCurve;

public class GenericSqrtRatioCalculator implements SqrtRatioCalculator {
   private final BigInteger q;
   private final int c1;
   private final BigInteger c2;
   private final BigInteger c3;
   private final BigInteger c4;
   private final BigInteger c5;
   private final BigInteger c6;
   private final BigInteger c7;

   public GenericSqrtRatioCalculator(ECCurve var1, BigInteger var2) {
      this.q = var1.getField().getCharacteristic();
      this.c1 = this.calculateC1();
      this.c2 = this.q.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2L).pow(this.c1));
      this.c3 = this.c2.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2L));
      this.c4 = BigInteger.valueOf(2L).pow(this.c1).subtract(BigInteger.ONE);
      this.c5 = BigInteger.valueOf(2L).pow(this.c1 - 1);
      this.c6 = var2.modPow(this.c2, this.q);
      this.c7 = var2.modPow(this.c2.add(BigInteger.ONE).divide(BigInteger.valueOf(2L)), this.q);
   }

   private int calculateC1() {
      BigInteger var1 = this.q.subtract(BigInteger.ONE);

      int var2;
      for (var2 = 0; var1.mod(BigInteger.valueOf(2L)).equals(BigInteger.ZERO); var2++) {
         var1 = var1.divide(BigInteger.valueOf(2L));
      }

      return var2;
   }

   @Override
   public SqrtRatio sqrtRatio(BigInteger var1, BigInteger var2) {
      BigInteger var3 = this.c6;
      BigInteger var4 = var2.modPow(this.c4, this.q);
      BigInteger var5 = var4.modPow(BigInteger.valueOf(2L), this.q);
      var5 = var5.multiply(var2).mod(this.q);
      BigInteger var6 = var1.multiply(var5).mod(this.q);
      var6 = var6.modPow(this.c3, this.q);
      var6 = var6.multiply(var4).mod(this.q);
      var4 = var6.multiply(var2).mod(this.q);
      var5 = var6.multiply(var1).mod(this.q);
      BigInteger var7 = var5.multiply(var4).mod(this.q);
      var6 = var7.modPow(this.c5, this.q);
      boolean var8 = var6.equals(BigInteger.ONE);
      var4 = var5.multiply(this.c7).mod(this.q);
      var6 = var7.multiply(var3).mod(this.q);
      var5 = H2cUtils.cmov(var4, var5, var8);
      var7 = H2cUtils.cmov(var6, var7, var8);

      for (int var9 = this.c1; var9 >= 2; var9--) {
         var6 = BigInteger.valueOf(var9 - 2);
         var6 = BigInteger.valueOf(2L).pow(var6.intValue());
         var6 = var7.modPow(var6, this.q);
         boolean var10 = var6.equals(BigInteger.ONE);
         var4 = var5.multiply(var3).mod(this.q);
         var3 = var3.multiply(var3).mod(this.q);
         var6 = var7.multiply(var3).mod(this.q);
         var5 = H2cUtils.cmov(var4, var5, var10);
         var7 = H2cUtils.cmov(var6, var7, var10);
      }

      return new SqrtRatio(var8, var5);
   }
}
