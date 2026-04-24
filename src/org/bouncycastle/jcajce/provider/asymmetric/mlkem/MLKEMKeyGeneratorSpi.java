package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.kems.MLKEMExtractor;
import org.bouncycastle.crypto.kems.MLKEMGenerator;
import org.bouncycastle.crypto.params.MLKEMParameters;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.provider.asymmetric.util.KdfUtil;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.util.Arrays;

public class MLKEMKeyGeneratorSpi extends KeyGeneratorSpi {
   private final MLKEMParameters mlkemParameters;
   private KEMGenerateSpec genSpec;
   private SecureRandom random;
   private KEMExtractSpec extSpec;

   public MLKEMKeyGeneratorSpi() {
      this(null);
   }

   protected MLKEMKeyGeneratorSpi(MLKEMParameters var1) {
      this.mlkemParameters = var1;
   }

   @Override
   protected void engineInit(SecureRandom var1) {
      throw new UnsupportedOperationException("Operation not supported");
   }

   @Override
   protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      this.random = var2;
      if (var1 instanceof KEMGenerateSpec) {
         this.genSpec = (KEMGenerateSpec)var1;
         this.extSpec = null;
         if (this.mlkemParameters != null) {
            String var3 = MLKEMParameterSpec.fromName(this.mlkemParameters.getName()).getName();
            if (!var3.equals(this.genSpec.getPublicKey().getAlgorithm())) {
               throw new InvalidAlgorithmParameterException("key generator locked to " + var3);
            }
         }
      } else {
         if (!(var1 instanceof KEMExtractSpec)) {
            throw new InvalidAlgorithmParameterException("unknown spec");
         }

         this.genSpec = null;
         this.extSpec = (KEMExtractSpec)var1;
         if (this.mlkemParameters != null) {
            String var4 = MLKEMParameterSpec.fromName(this.mlkemParameters.getName()).getName();
            if (!var4.equals(this.extSpec.getPrivateKey().getAlgorithm())) {
               throw new InvalidAlgorithmParameterException("key generator locked to " + var4);
            }
         }
      }
   }

   @Override
   protected void engineInit(int var1, SecureRandom var2) {
      throw new UnsupportedOperationException("Operation not supported");
   }

   @Override
   protected SecretKey engineGenerateKey() {
      if (this.genSpec != null) {
         BCMLKEMPublicKey var23 = (BCMLKEMPublicKey)this.genSpec.getPublicKey();
         MLKEMGenerator var24 = new MLKEMGenerator(this.random);
         SecretWithEncapsulation var25 = var24.generateEncapsulated(var23.getKeyParams());
         byte[] var26 = var25.getSecret();
         byte[] var27 = KdfUtil.makeKeyBytes(this.genSpec, var26);

         try {
            SecretKeySpec var28 = new SecretKeySpec(var27, this.genSpec.getKeyAlgorithmName());
            return new SecretKeyWithEncapsulation(var28, var25.getEncapsulation());
         } finally {
            try {
               var25.destroy();
            } catch (DestroyFailedException var20) {
            }
         }
      } else {
         BCMLKEMPrivateKey var1 = (BCMLKEMPrivateKey)this.extSpec.getPrivateKey();
         MLKEMExtractor var2 = new MLKEMExtractor(var1.getKeyParams());
         byte[] var3 = this.extSpec.getEncapsulation();
         byte[] var4 = var2.extractSecret(var3);
         byte[] var5 = KdfUtil.makeKeyBytes(this.extSpec, var4);

         try {
            SecretKeySpec var6 = new SecretKeySpec(var5, this.extSpec.getKeyAlgorithmName());
            return new SecretKeyWithEncapsulation(var6, var3);
         } finally {
            Arrays.clear(var5);
         }
      }
   }

   public static class MLKEM1024 extends MLKEMKeyGeneratorSpi {
      public MLKEM1024() {
         super(MLKEMParameters.ml_kem_1024);
      }
   }

   public static class MLKEM512 extends MLKEMKeyGeneratorSpi {
      public MLKEM512() {
         super(MLKEMParameters.ml_kem_512);
      }
   }

   public static class MLKEM768 extends MLKEMKeyGeneratorSpi {
      public MLKEM768() {
         super(MLKEMParameters.ml_kem_768);
      }
   }
}
