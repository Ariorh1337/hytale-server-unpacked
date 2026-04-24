package org.bouncycastle.crypto.hash2curve.impl;

import java.math.BigInteger;
import org.bouncycastle.crypto.hash2curve.CurveProcessor;
import org.bouncycastle.crypto.hash2curve.data.AffineXY;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class MontgomeryCurveProcessor implements CurveProcessor {
   private final ECCurve curve;
   private final BigInteger p;
   private final BigInteger hEff;
   private final int J;
   private final int K;

   public MontgomeryCurveProcessor(ECCurve var1, int var2, int var3, int var4) {
      this.J = var2;
      this.K = var3;
      this.curve = var1;
      this.p = var1.getField().getCharacteristic();
      this.hEff = BigInteger.valueOf(var4);
   }

   @Override
   public ECPoint add(ECPoint var1, ECPoint var2) {
      ECPoint var3 = this.Fmtow(var1).toPoint(this.curve);
      ECPoint var4 = this.Fmtow(var2).toPoint(this.curve);
      ECPoint var5 = var3.add(var4).normalize();
      return this.Fwtom(var5).toPoint(this.curve);
   }

   @Override
   public ECPoint clearCofactor(ECPoint var1) {
      if (var1.isInfinity()) {
         return var1;
      }

      ECPoint var2 = this.Fmtow(var1).toPoint(this.curve);
      return var2.multiply(this.hEff).normalize();
   }

   @Override
   public AffineXY mapToAffineXY(ECPoint var1) {
      return this.Fwtom(var1.normalize());
   }

   private AffineXY Fmtow(BigInteger var1, BigInteger var2) {
      BigInteger var3 = BigInteger.valueOf(3L).modInverse(this.p);
      BigInteger var4 = BigInteger.valueOf(this.J).mod(this.p).multiply(BigInteger.valueOf(this.K).mod(this.p).modInverse(this.p)).mod(this.p);
      BigInteger var5 = var1.mod(this.p).add(var4.multiply(var3).mod(this.p)).mod(this.p);
      BigInteger var6 = BigInteger.valueOf(this.K).mod(this.p).modInverse(this.p);
      BigInteger var7 = var2.mod(this.p).multiply(var6).mod(this.p);
      return new AffineXY(var5, var7);
   }

   private AffineXY Fwtom(BigInteger var1, BigInteger var2) {
      BigInteger var3 = BigInteger.valueOf(3L).modInverse(this.p);
      BigInteger var4 = BigInteger.valueOf(this.J).mod(this.p).multiply(BigInteger.valueOf(this.K).mod(this.p).modInverse(this.p)).mod(this.p);
      BigInteger var5 = var1.mod(this.p).subtract(var4.multiply(var3).mod(this.p)).mod(this.p);
      BigInteger var6 = var2.mod(this.p).multiply(BigInteger.valueOf(this.K).mod(this.p)).mod(this.p);
      return new AffineXY(var5, var6);
   }

   private AffineXY Fmtow(ECPoint var1) {
      if (var1.isInfinity()) {
         return new AffineXY(BigInteger.ZERO, BigInteger.ZERO);
      }

      ECPoint var2 = var1.normalize();
      return this.Fmtow(var2.getAffineXCoord().toBigInteger(), var2.getAffineYCoord().toBigInteger());
   }

   private AffineXY Fwtom(ECPoint var1) {
      if (var1.isInfinity()) {
         return new AffineXY(BigInteger.ZERO, BigInteger.ZERO);
      }

      ECPoint var2 = var1.normalize();
      return this.Fwtom(var2.getAffineXCoord().toBigInteger(), var2.getAffineYCoord().toBigInteger());
   }
}
