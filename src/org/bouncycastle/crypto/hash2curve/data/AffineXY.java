package org.bouncycastle.crypto.hash2curve.data;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public final class AffineXY {
   private final BigInteger x;
   private final BigInteger y;

   public AffineXY(BigInteger var1, BigInteger var2) {
      this.x = var1;
      this.y = var2;
   }

   public AffineXY(ECPoint var1) {
      this(var1, true);
   }

   public AffineXY(ECPoint var1, boolean var2) {
      if (var1.isInfinity()) {
         throw new IllegalArgumentException("Cannot extract affine coordinates from point at infinity");
      }

      if (var2) {
         var1 = var1.normalize();
      }

      this.x = var1.getAffineXCoord().toBigInteger();
      this.y = var1.getAffineYCoord().toBigInteger();
   }

   public ECPoint toPoint(ECCurve var1) {
      return var1.createPoint(this.getX(), this.getY()).normalize();
   }

   public BigInteger getX() {
      return this.x;
   }

   public BigInteger getY() {
      return this.y;
   }
}
