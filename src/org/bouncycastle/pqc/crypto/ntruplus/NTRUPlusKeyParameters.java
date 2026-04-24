package org.bouncycastle.pqc.crypto.ntruplus;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class NTRUPlusKeyParameters extends AsymmetricKeyParameter {
   private final NTRUPlusParameters params;

   public NTRUPlusKeyParameters(boolean var1, NTRUPlusParameters var2) {
      super(var1);
      this.params = var2;
   }

   public NTRUPlusParameters getParameters() {
      return this.params;
   }
}
