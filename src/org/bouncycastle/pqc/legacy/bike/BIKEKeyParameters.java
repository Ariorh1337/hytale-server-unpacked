package org.bouncycastle.pqc.legacy.bike;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class BIKEKeyParameters extends AsymmetricKeyParameter {
   private BIKEParameters params;

   public BIKEKeyParameters(boolean var1, BIKEParameters var2) {
      super(var1);
      this.params = var2;
   }

   public BIKEParameters getParameters() {
      return this.params;
   }
}
