package org.bouncycastle.pqc.crypto.ntruplus;

import org.bouncycastle.util.Arrays;

public class NTRUPlusPrivateKeyParameters extends NTRUPlusKeyParameters {
   private final byte[] sk;

   public NTRUPlusPrivateKeyParameters(NTRUPlusParameters var1, byte[] var2) {
      super(true, var1);
      this.sk = Arrays.clone(var2);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.sk);
   }
}
