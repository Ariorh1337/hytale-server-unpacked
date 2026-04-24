package org.bouncycastle.pqc.crypto.ntruplus;

import org.bouncycastle.util.Arrays;

public class NTRUPlusPublicKeyParameters extends NTRUPlusKeyParameters {
   private final byte[] p;

   public NTRUPlusPublicKeyParameters(NTRUPlusParameters var1, byte[] var2) {
      super(false, var1);
      this.p = Arrays.clone(var2);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.p);
   }
}
