package org.bouncycastle.pqc.jcajce.provider.ntruplus;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusParameters;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.NTRUPlusParameterSpec;
import org.bouncycastle.util.Strings;

public class NTRUPlusKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   private final NTRUPlusParameters ntruplusParameters;
   NTRUPlusKeyGenerationParameters param;
   NTRUPlusKeyPairGenerator engine = new NTRUPlusKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public NTRUPlusKeyPairGeneratorSpi() {
      super("NTRUPLUS");
      this.ntruplusParameters = null;
   }

   protected NTRUPlusKeyPairGeneratorSpi(NTRUPlusParameters var1) {
      super(var1.getName());
      this.ntruplusParameters = var1;
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null && parameters.containsKey(var3)) {
         NTRUPlusParameters var4 = (NTRUPlusParameters)parameters.get(var3);
         this.param = new NTRUPlusKeyGenerationParameters(var2, var4);
         if (this.ntruplusParameters != null && !var4.getName().equals(this.ntruplusParameters.getName())) {
            throw new InvalidAlgorithmParameterException("key pair generator locked to " + Strings.toUpperCase(this.ntruplusParameters.getName()));
         }

         this.engine.init(this.param);
         this.initialised = true;
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof NTRUPlusParameterSpec) {
         NTRUPlusParameterSpec var1 = (NTRUPlusParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         if (this.ntruplusParameters != null) {
            this.param = new NTRUPlusKeyGenerationParameters(this.random, this.ntruplusParameters);
         } else {
            this.param = new NTRUPlusKeyGenerationParameters(this.random, NTRUPlusParameters.ntruplus_kem_768);
         }

         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      NTRUPlusPublicKeyParameters var2 = (NTRUPlusPublicKeyParameters)var1.getPublic();
      NTRUPlusPrivateKeyParameters var3 = (NTRUPlusPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCNTRUPlusPublicKey(var2), new BCNTRUPlusPrivateKey(var3));
   }

   static {
      parameters.put(NTRUPlusParameterSpec.ntruplus_768.getName(), NTRUPlusParameters.ntruplus_kem_768);
      parameters.put(NTRUPlusParameterSpec.ntruplus_864.getName(), NTRUPlusParameters.ntruplus_kem_864);
      parameters.put(NTRUPlusParameterSpec.ntruplus_1152.getName(), NTRUPlusParameters.ntruplus_kem_1152);
   }

   public static class NTRUPlus1152 extends NTRUPlusKeyPairGeneratorSpi {
      public NTRUPlus1152() {
         super(NTRUPlusParameters.ntruplus_kem_864);
      }
   }

   public static class NTRUPlus768 extends NTRUPlusKeyPairGeneratorSpi {
      public NTRUPlus768() {
         super(NTRUPlusParameters.ntruplus_kem_768);
      }
   }

   public static class NTRUPlus864 extends NTRUPlusKeyPairGeneratorSpi {
      public NTRUPlus864() {
         super(NTRUPlusParameters.ntruplus_kem_864);
      }
   }
}
