package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;

public class SLHDSAPublicKeyParameters extends SLHDSAKeyParameters {
   private final byte[] pkSeed;
   private final byte[] pkRoot;

   public SLHDSAPublicKeyParameters(SLHDSAParameters var1, byte[] var2) {
      super(false, var1);
      int var3 = var1.getN();
      if (var2.length != 2 * var3) {
         throw new IllegalArgumentException("public key encoding does not match parameters");
      }

      this.pkSeed = Arrays.copyOfRange(var2, 0, var3);
      this.pkRoot = Arrays.copyOfRange(var2, var3, 2 * var3);
   }

   public byte[] getSeed() {
      return Arrays.clone(this.pkSeed);
   }

   public byte[] getRoot() {
      return Arrays.clone(this.pkRoot);
   }

   public byte[] getEncoded() {
      return Arrays.concatenate(this.pkSeed, this.pkRoot);
   }
}
