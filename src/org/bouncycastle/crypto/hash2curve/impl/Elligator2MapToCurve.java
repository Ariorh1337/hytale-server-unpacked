package org.bouncycastle.crypto.hash2curve.impl;

import java.math.BigInteger;
import org.bouncycastle.crypto.hash2curve.H2cUtils;
import org.bouncycastle.crypto.hash2curve.MapToCurve;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class Elligator2MapToCurve implements MapToCurve {
   private final ECCurve curve;
   private final BigInteger z;
   private final BigInteger K;
   private final BigInteger c1;
   private final BigInteger c2;
   private final BigInteger p;

   public Elligator2MapToCurve(ECCurve var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      this.curve = var1;
      this.z = var2;
      this.K = var4;
      this.p = var1.getField().getCharacteristic();
      BigInteger var5 = var4.modInverse(this.p);
      this.c1 = var3.multiply(var5).mod(this.p);
      BigInteger var6 = var5.multiply(var5).mod(this.p);
      this.c2 = var6;
   }

   @Override
   public ECPoint process(BigInteger var1) {
      BigInteger var2 = var1.multiply(var1).mod(this.p);
      var2 = this.z.multiply(var2).mod(this.p);
      BigInteger var3 = this.p.subtract(BigInteger.ONE);
      boolean var4 = var2.equals(var3);
      var2 = H2cUtils.cmov(var2, BigInteger.ZERO, var4);
      BigInteger var5 = var2.add(BigInteger.ONE).mod(this.p);
      var5 = H2cUtils.inv0(var5, this.p);
      var5 = var5.multiply(this.c1).negate().mod(this.p);
      BigInteger var6 = var5.add(this.c1).mod(this.p);
      var6 = var6.multiply(var5).mod(this.p);
      var6 = var6.add(this.c2).mod(this.p);
      var6 = var6.multiply(var5).mod(this.p);
      BigInteger var7 = var5.negate().subtract(this.c1).mod(this.p);
      BigInteger var8 = var2.multiply(var6).mod(this.p);
      boolean var9 = H2cUtils.isSquare(var6, this.p);
      BigInteger var10 = H2cUtils.cmov(var7, var5, var9);
      BigInteger var11 = H2cUtils.cmov(var8, var6, var9);
      BigInteger var12 = H2cUtils.sqrt(var11, this.p);
      boolean var13 = H2cUtils.sgn0(var12, this.curve) == 1;
      boolean var14 = var9 ^ var13;
      BigInteger var15 = var12.negate().mod(this.p);
      var12 = H2cUtils.cmov(var12, var15, var14);
      BigInteger var16 = var10.multiply(this.K).mod(this.p);
      BigInteger var17 = var12.multiply(this.K).mod(this.p);
      return this.curve.createPoint(var16, var17);
   }
}
