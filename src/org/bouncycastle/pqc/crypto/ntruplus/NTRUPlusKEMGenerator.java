package org.bouncycastle.pqc.crypto.ntruplus;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;

public class NTRUPlusKEMGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom sr;

   public NTRUPlusKEMGenerator(SecureRandom var1) {
      this.sr = var1;
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      NTRUPlusPublicKeyParameters var2 = (NTRUPlusPublicKeyParameters)var1;
      NTRUPlusParameters var3 = var2.getParameters();
      byte[] var4 = new byte[var3.getCiphertextBytes()];
      byte[] var5 = new byte[32];
      NTRUPlusEngine var6 = new NTRUPlusEngine(var3);
      byte[] var7 = new byte[var3.getN() >> 3];
      this.sr.nextBytes(var7);
      var6.crypto_kem_enc_derand(var4, 0, var5, 0, var2.getEncoded(), 0, var7, 0);
      return new SecretWithEncapsulationImpl(var5, var4);
   }
}
