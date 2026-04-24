package org.bouncycastle.crypto.hash2curve.impl;

import java.math.BigInteger;

public class SqrtRatio {
   private final boolean isQR;
   private final BigInteger ratio;

   protected SqrtRatio(boolean var1, BigInteger var2) {
      this.isQR = var1;
      this.ratio = var2;
   }

   public boolean isQR() {
      return this.isQR;
   }

   public BigInteger getRatio() {
      return this.ratio;
   }
}
