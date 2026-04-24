package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.DSTU4145KeyPairGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.DSTU4145Parameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.spec.DSTU4145ParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class KeyPairGeneratorSpi extends KeyPairGenerator {
   Object ecParams = null;
   ECKeyPairGenerator engine = new DSTU4145KeyPairGenerator();
   String algorithm = "DSTU4145";
   ECKeyGenerationParameters param;
   SecureRandom random = null;
   boolean initialised = false;

   public KeyPairGeneratorSpi() {
      super("DSTU4145");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      this.random = var2;
      if (this.ecParams != null) {
         try {
            this.initialize((ECGenParameterSpec)this.ecParams, var2);
         } catch (InvalidAlgorithmParameterException var4) {
            throw new InvalidParameterException("key size not configurable.");
         }
      } else {
         throw new InvalidParameterException("unknown key size.");
      }
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (var1 instanceof ECParameterSpec) {
         ECParameterSpec var11 = (ECParameterSpec)var1;
         this.ecParams = var1;
         this.param = new ECKeyGenerationParameters(new ECDomainParameters(var11.getCurve(), var11.getG(), var11.getN(), var11.getH()), var2);
         this.engine.init(this.param);
         this.initialised = true;
      } else if (var1 instanceof java.security.spec.ECParameterSpec) {
         java.security.spec.ECParameterSpec var10 = (java.security.spec.ECParameterSpec)var1;
         this.ecParams = var1;
         ECCurve var12 = EC5Util.convertCurve(var10.getCurve());
         ECPoint var13 = EC5Util.convertPoint(var12, var10.getGenerator());
         if (var10 instanceof DSTU4145ParameterSpec) {
            DSTU4145ParameterSpec var14 = (DSTU4145ParameterSpec)var10;
            this.param = new ECKeyGenerationParameters(
               new DSTU4145Parameters(new ECDomainParameters(var12, var13, var10.getOrder(), BigInteger.valueOf(var10.getCofactor())), var14.getDKE()), var2
            );
         } else {
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(var12, var13, var10.getOrder(), BigInteger.valueOf(var10.getCofactor())), var2);
         }

         this.engine.init(this.param);
         this.initialised = true;
      } else if (!(var1 instanceof ECGenParameterSpec) && !(var1 instanceof ECNamedCurveGenParameterSpec)) {
         if (var1 != null || BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null) {
            if (var1 == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null) {
               throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
            }

            throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec: " + var1.getClass().getName());
         }

         ECParameterSpec var9 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
         this.ecParams = var1;
         this.param = new ECKeyGenerationParameters(new ECDomainParameters(var9.getCurve(), var9.getG(), var9.getN(), var9.getH()), var2);
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         String var3;
         if (var1 instanceof ECGenParameterSpec) {
            var3 = ((ECGenParameterSpec)var1).getName();
         } else {
            var3 = ((ECNamedCurveGenParameterSpec)var1).getName();
         }

         ASN1ObjectIdentifier var4 = ASN1ObjectIdentifier.tryFromID(var3);
         if (var4 == null) {
            throw new InvalidAlgorithmParameterException("non-OID curve name not supported: " + var3);
         }

         ECDomainParameters var5 = DSTU4145NamedCurves.getByOID(var4);
         if (var5 == null) {
            throw new InvalidAlgorithmParameterException("unknown curve name: " + var3);
         }

         this.ecParams = new ECNamedCurveSpec(var3, var5.getCurve(), var5.getG(), var5.getN(), var5.getH(), var5.getSeed());
         java.security.spec.ECParameterSpec var6 = (java.security.spec.ECParameterSpec)this.ecParams;
         ECCurve var7 = EC5Util.convertCurve(var6.getCurve());
         ECPoint var8 = EC5Util.convertPoint(var7, var6.getGenerator());
         this.param = new ECKeyGenerationParameters(new ECDomainParameters(var7, var8, var6.getOrder(), BigInteger.valueOf(var6.getCofactor())), var2);
         this.engine.init(this.param);
         this.initialised = true;
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         throw new IllegalStateException("DSTU Key Pair Generator not initialised");
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      ECPublicKeyParameters var2 = (ECPublicKeyParameters)var1.getPublic();
      ECPrivateKeyParameters var3 = (ECPrivateKeyParameters)var1.getPrivate();
      if (this.ecParams instanceof ECParameterSpec) {
         ECParameterSpec var6 = (ECParameterSpec)this.ecParams;
         BCDSTU4145PublicKey var7 = new BCDSTU4145PublicKey(this.algorithm, var2, var6);
         return new KeyPair(var7, new BCDSTU4145PrivateKey(this.algorithm, var3, var7, var6));
      }

      if (this.ecParams == null) {
         return new KeyPair(new BCDSTU4145PublicKey(this.algorithm, var2), new BCDSTU4145PrivateKey(this.algorithm, var3));
      }

      java.security.spec.ECParameterSpec var4 = (java.security.spec.ECParameterSpec)this.ecParams;
      BCDSTU4145PublicKey var5 = new BCDSTU4145PublicKey(this.algorithm, var2, var4);
      return new KeyPair(var5, new BCDSTU4145PrivateKey(this.algorithm, var3, var5, var4));
   }
}
