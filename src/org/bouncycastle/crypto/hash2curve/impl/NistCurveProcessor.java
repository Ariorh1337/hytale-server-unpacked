package org.bouncycastle.crypto.hash2curve.impl;

import java.math.BigInteger;
import org.bouncycastle.crypto.hash2curve.CurveProcessor;
import org.bouncycastle.crypto.hash2curve.data.AffineXY;
import org.bouncycastle.math.ec.ECPoint;

public class NistCurveProcessor implements CurveProcessor {
   @Override
   public ECPoint add(ECPoint var1, ECPoint var2) {
      return var1.add(var2);
   }

   @Override
   public ECPoint clearCofactor(ECPoint var1) {
      return var1.multiply(BigInteger.ONE);
   }

   @Override
   public AffineXY mapToAffineXY(ECPoint var1) {
      return new AffineXY(var1);
   }
}
