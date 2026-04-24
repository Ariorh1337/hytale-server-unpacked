package org.bouncycastle.crypto.hash2curve.impl;

import java.math.BigInteger;
import org.bouncycastle.crypto.hash2curve.H2cUtils;
import org.bouncycastle.crypto.hash2curve.MapToCurve;
import org.bouncycastle.crypto.hash2curve.SqrtRatioCalculator;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class SimplifiedShallueVanDeWoestijneMapToCurve implements MapToCurve {
   private final ECCurve curve;
   private final BigInteger z;
   private final SqrtRatioCalculator sqrtRatioCalculator;

   public SimplifiedShallueVanDeWoestijneMapToCurve(ECCurve var1, BigInteger var2) {
      this.curve = var1;
      this.z = var2;
      this.sqrtRatioCalculator = new GenericSqrtRatioCalculator(var1, var2);
   }

   @Override
   public ECPoint process(BigInteger var1) {
      BigInteger var2 = this.curve.getA().toBigInteger();
      BigInteger var3 = this.curve.getB().toBigInteger();
      BigInteger var4 = this.curve.getField().getCharacteristic();
      BigInteger var5 = var1.modPow(BigInteger.valueOf(2L), var4);
      var5 = this.z.multiply(var5).mod(var4);
      BigInteger var6 = var5.modPow(BigInteger.valueOf(2L), var4);
      var6 = var6.add(var5).mod(var4);
      BigInteger var7 = var6.add(BigInteger.ONE).mod(var4);
      var7 = var3.multiply(var7).mod(var4);
      BigInteger var8 = H2cUtils.cmov(this.z, var6.negate(), !var6.equals(BigInteger.ZERO));
      var8 = var2.multiply(var8).mod(var4);
      var6 = var7.modPow(BigInteger.valueOf(2L), var4);
      BigInteger var9 = var8.modPow(BigInteger.valueOf(2L), var4);
      BigInteger var10 = var2.multiply(var9).mod(var4);
      var6 = var6.add(var10).mod(var4);
      var6 = var6.multiply(var7).mod(var4);
      var9 = var9.multiply(var8).mod(var4);
      var10 = var3.multiply(var9).mod(var4);
      var6 = var6.add(var10).mod(var4);
      BigInteger var11 = var5.multiply(var7).mod(var4);
      SqrtRatio var12 = this.sqrtRatioCalculator.sqrtRatio(var6, var9);
      boolean var13 = var12.isQR();
      BigInteger var14 = var12.getRatio();
      BigInteger var15 = var5.multiply(var1).mod(var4);
      var15 = var15.multiply(var14).mod(var4);
      var11 = H2cUtils.cmov(var11, var7, var13);
      var15 = H2cUtils.cmov(var15, var14, var13);
      boolean var16 = H2cUtils.sgn0(var1, this.curve) == H2cUtils.sgn0(var15, this.curve);
      var15 = H2cUtils.cmov(var15.negate(), var15, var16).mod(var4);
      var11 = var11.multiply(var8.modPow(BigInteger.ONE.negate(), var4)).mod(var4);
      return this.curve.createPoint(var11, var15);
   }
}
