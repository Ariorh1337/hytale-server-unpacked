package org.bouncycastle.crypto.hash2curve;

import java.math.BigInteger;

public enum HashToCurveProfile {
   P256_XMD_SHA_256(BigInteger.valueOf(-10L), 48, 128, 1, null, null),
   P384_XMD_SHA_384(BigInteger.valueOf(-12L), 72, 192, 1, null, null),
   P521_XMD_SHA_512(BigInteger.valueOf(-4L), 98, 256, 1, null, null),
   CURVE25519W_XMD_SHA_512_ELL2(BigInteger.valueOf(2L), 48, 128, 8, 486662, 1);

   private final BigInteger Z;
   private final int L;
   private final int k;
   private final int h;
   private final Integer mJ;
   private final Integer mK;

   HashToCurveProfile(final BigInteger nullxx, final int nullxxx, final int nullxxxx, int nullxxxxx, Integer nullxxxxxx, Integer nullxxxxxxx) {
      this.Z = nullxx;
      this.L = nullxxx;
      this.k = nullxxxx;
      this.h = nullxxxxx;
      this.mJ = nullxxxxxx;
      this.mK = nullxxxxxxx;
   }

   public int getK() {
      return this.k;
   }

   public int getL() {
      return this.L;
   }

   public BigInteger getZ() {
      return this.Z;
   }

   public int getH() {
      return this.h;
   }

   public Integer getmJ() {
      return this.mJ;
   }

   public Integer getmK() {
      return this.mK;
   }
}
