package org.bouncycastle.pqc.crypto.mlkem;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;

@Deprecated
public class MLKEMGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom random;

   public MLKEMGenerator(SecureRandom var1) {
      this.random = CryptoServicesRegistrar.getSecureRandom(var1);
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      byte[] var2 = new byte[32];
      this.random.nextBytes(var2);
      return internalGenerateEncapsulated((MLKEMPublicKeyParameters)var1, var2);
   }

   /** @deprecated */
   public SecretWithEncapsulation internalGenerateEncapsulated(AsymmetricKeyParameter var1, byte[] var2) {
      return internalGenerateEncapsulated((MLKEMPublicKeyParameters)var1, var2);
   }

   public static SecretWithEncapsulation internalGenerateEncapsulated(MLKEMPublicKeyParameters var0, byte[] var1) {
      if (var1.length != 32) {
         throw new IllegalArgumentException("'randBytes' has invalid length");
      }

      MLKEMEngine var2 = var0.getParameters().getEngine();
      byte[][] var3 = var2.kemEncrypt(var0, var1);
      return new SecretWithEncapsulationImpl(var3[0], var3[1]);
   }
}
