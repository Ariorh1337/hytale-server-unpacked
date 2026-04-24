package org.bouncycastle.pqc.crypto.ntruplus;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class NTRUPlusKeyGenerationParameters extends KeyGenerationParameters {
   private final NTRUPlusParameters params;

   public NTRUPlusKeyGenerationParameters(SecureRandom var1, NTRUPlusParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public NTRUPlusParameters getParameters() {
      return this.params;
   }
}
