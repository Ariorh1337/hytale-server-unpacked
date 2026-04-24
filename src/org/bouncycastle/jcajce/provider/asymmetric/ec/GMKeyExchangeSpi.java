package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.agreement.SM2KeyExchange;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.SM2KeyExchangePrivateParameters;
import org.bouncycastle.crypto.params.SM2KeyExchangePublicParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.jcajce.spec.SM2KeyExchangeSpec;
import org.bouncycastle.util.Arrays;

public class GMKeyExchangeSpi extends BaseAgreementSpi {
   private final String kaAlgorithm;
   private final SM2KeyExchange engine;
   private SM2KeyExchangeSpec spec;
   private byte[] result;

   protected GMKeyExchangeSpi(String var1) {
      super(var1, null);
      this.kaAlgorithm = var1;
      this.engine = new SM2KeyExchange();
   }

   @Override
   protected Key engineDoPhase(Key var1, boolean var2) throws InvalidKeyException, IllegalStateException {
      if (this.spec == null) {
         throw new IllegalStateException(this.kaAlgorithm + " not initialised.");
      }

      if (!var2) {
         throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties.");
      }

      if (!(var1 instanceof BCECPublicKey)) {
         throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(BCECPublicKey.class) + " for doPhase");
      }

      ECPublicKeyParameters var3 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter((PublicKey)var1);
      ECPublicKeyParameters var4 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(this.spec.getOtherPartyEphemeralKey());
      ParametersWithID var5 = new ParametersWithID(new SM2KeyExchangePublicParameters(var3, var4), this.spec.getOtherPartyId());
      this.result = this.engine.calculateKey(128, var5);
      return null;
   }

   @Override
   protected void doInitFromKey(Key var1, AlgorithmParameterSpec var2, SecureRandom var3) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (var2 != null && !(var2 instanceof SM2KeyExchangeSpec)) {
         throw new InvalidAlgorithmParameterException("No algorithm parameters supported");
      }

      if (!(var1 instanceof PrivateKey)) {
         throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(BCECPrivateKey.class) + " for initialisation");
      }

      this.spec = (SM2KeyExchangeSpec)var2;
      ECPrivateKeyParameters var4 = (ECPrivateKeyParameters)ECUtils.generatePrivateKeyParameter((PrivateKey)var1);
      ECPrivateKeyParameters var5 = (ECPrivateKeyParameters)ECUtils.generatePrivateKeyParameter(this.spec.getEphemeralPrivateKey());
      ParametersWithID var6 = new ParametersWithID(new SM2KeyExchangePrivateParameters(this.spec.isInitiator(), var4, var5), this.spec.getId());
      this.engine.init(var6);
   }

   private static String getSimpleName(Class var0) {
      String var1 = var0.getName();
      return var1.substring(var1.lastIndexOf(46) + 1);
   }

   @Override
   protected byte[] doCalcSecret() {
      return Arrays.clone(this.result);
   }

   public static class SM2 extends GMKeyExchangeSpi {
      public SM2() {
         super("SM2");
      }
   }
}
