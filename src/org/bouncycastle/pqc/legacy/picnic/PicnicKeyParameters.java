package org.bouncycastle.pqc.legacy.picnic;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class PicnicKeyParameters extends AsymmetricKeyParameter {
   final PicnicParameters parameters;

   public PicnicKeyParameters(boolean var1, PicnicParameters var2) {
      super(var1);
      this.parameters = var2;
   }

   public PicnicParameters getParameters() {
      return this.parameters;
   }
}
