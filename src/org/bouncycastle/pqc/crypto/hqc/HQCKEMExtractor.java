package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.util.Arrays;

public class HQCKEMExtractor implements EncapsulatedSecretExtractor {
   private final HQCPrivateKeyParameters privateKey;
   private final HQCEngine engine;

   public HQCKEMExtractor(HQCPrivateKeyParameters var1) {
      if (var1 == null) {
         throw new NullPointerException("'privateKey' cannot be null");
      }

      this.privateKey = var1;
      this.engine = var1.getParameters().getEngine();
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      byte[] var2 = new byte[64];
      byte[] var3 = this.privateKey.getPrivateKey();
      this.engine.decaps(var2, var1, var3);
      return Arrays.copyOfRange(var2, 0, 32);
   }

   @Override
   public int getEncapsulationLength() {
      return this.engine.getCipherTextBytes();
   }
}
