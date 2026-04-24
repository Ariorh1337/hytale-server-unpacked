package org.bouncycastle.pqc.crypto.ntruplus;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;

public class NTRUPlusKEMExtractor implements EncapsulatedSecretExtractor {
   private final NTRUPlusPrivateKeyParameters privateKey;
   private final NTRUPlusEngine engine;

   public NTRUPlusKEMExtractor(NTRUPlusPrivateKeyParameters var1) {
      if (var1 == null) {
         throw new NullPointerException("'privateKey' cannot be null");
      }

      this.privateKey = var1;
      this.engine = new NTRUPlusEngine(var1.getParameters());
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      byte[] var2 = new byte[32];
      this.engine.crypto_kem_dec(var2, 0, var1, 0, this.privateKey.getEncoded(), 0);
      return var2;
   }

   @Override
   public int getEncapsulationLength() {
      return this.privateKey.getParameters().getCiphertextBytes();
   }
}
