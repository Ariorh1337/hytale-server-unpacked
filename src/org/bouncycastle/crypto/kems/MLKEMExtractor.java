package org.bouncycastle.crypto.kems;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.kems.mlkem.MLKEMEngine;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;

public class MLKEMExtractor implements EncapsulatedSecretExtractor {
   private final MLKEMPrivateKeyParameters privateKey;
   private final MLKEMEngine engine;

   public MLKEMExtractor(MLKEMPrivateKeyParameters var1) {
      if (var1 == null) {
         throw new NullPointerException("'privateKey' cannot be null");
      }

      this.privateKey = var1;
      this.engine = MLKEMEngine.getInstance(var1.getParameters());
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      if (var1.length != this.getEncapsulationLength()) {
         throw new IllegalArgumentException("encapsulation wrong length");
      } else {
         return this.engine.kemDecrypt(this.privateKey, var1);
      }
   }

   @Override
   public int getEncapsulationLength() {
      return this.engine.getCipherTextBytes();
   }
}
