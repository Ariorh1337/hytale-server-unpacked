package org.bouncycastle.pqc.jcajce.provider.ntruplus;

import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusKEMExtractor;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusKEMGenerator;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusParameters;
import org.bouncycastle.pqc.jcajce.spec.NTRUPlusParameterSpec;
import org.bouncycastle.util.Arrays;

public class NTRUPlusKeyGeneratorSpi extends KeyGeneratorSpi {
   private KEMGenerateSpec genSpec;
   private SecureRandom random;
   private KEMExtractSpec extSpec;
   private NTRUPlusParameters ntruplusParameters;

   public NTRUPlusKeyGeneratorSpi() {
      this(null);
   }

   public NTRUPlusKeyGeneratorSpi(NTRUPlusParameters var1) {
      this.ntruplusParameters = var1;
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
         if (this.ntruplusParameters != null) {
            String var3 = NTRUPlusParameterSpec.fromName(this.ntruplusParameters.getName()).getName();
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
         if (this.ntruplusParameters != null) {
            String var4 = NTRUPlusParameterSpec.fromName(this.ntruplusParameters.getName()).getName();
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
         BCNTRUPlusPublicKey var7 = (BCNTRUPlusPublicKey)this.genSpec.getPublicKey();
         NTRUPlusKEMGenerator var8 = new NTRUPlusKEMGenerator(this.random);
         SecretWithEncapsulation var9 = var8.generateEncapsulated(var7.getKeyParams());
         SecretKeyWithEncapsulation var10 = new SecretKeyWithEncapsulation(
            new SecretKeySpec(var9.getSecret(), this.genSpec.getKeyAlgorithmName()), var9.getEncapsulation()
         );

         try {
            var9.destroy();
            return var10;
         } catch (DestroyFailedException var6) {
            throw new IllegalStateException("key cleanup failed");
         }
      } else {
         BCNTRUPlusPrivateKey var1 = (BCNTRUPlusPrivateKey)this.extSpec.getPrivateKey();
         NTRUPlusKEMExtractor var2 = new NTRUPlusKEMExtractor(var1.getKeyParams());
         byte[] var3 = this.extSpec.getEncapsulation();
         byte[] var4 = var2.extractSecret(var3);
         SecretKeyWithEncapsulation var5 = new SecretKeyWithEncapsulation(new SecretKeySpec(var4, this.extSpec.getKeyAlgorithmName()), var3);
         Arrays.clear(var4);
         return var5;
      }
   }

   public static class NTRUPlus1152 extends NTRUPlusKeyGeneratorSpi {
      public NTRUPlus1152() {
         super(NTRUPlusParameters.ntruplus_kem_1152);
      }
   }

   public static class NTRUPlus768 extends NTRUPlusKeyGeneratorSpi {
      public NTRUPlus768() {
         super(NTRUPlusParameters.ntruplus_kem_768);
      }
   }

   public static class NTRUPlus864 extends NTRUPlusKeyGeneratorSpi {
      public NTRUPlus864() {
         super(NTRUPlusParameters.ntruplus_kem_864);
      }
   }
}
