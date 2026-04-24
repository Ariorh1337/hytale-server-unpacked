package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.MLDSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.params.SLHDSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class PrivateKeyInfoFactory {
   private static Set cryptoProOids = new HashSet(5);

   private PrivateKeyInfoFactory() {
   }

   public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter var0) throws IOException {
      return createPrivateKeyInfo(var0, null);
   }

   public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter var0, ASN1Set var1) throws IOException {
      if (var0 instanceof RSAKeyParameters) {
         RSAPrivateCrtKeyParameters var18 = (RSAPrivateCrtKeyParameters)var0;
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE),
            new RSAPrivateKey(
               var18.getModulus(), var18.getPublicExponent(), var18.getExponent(), var18.getP(), var18.getQ(), var18.getDP(), var18.getDQ(), var18.getQInv()
            ),
            var1
         );
      }

      if (var0 instanceof DSAPrivateKeyParameters) {
         DSAPrivateKeyParameters var17 = (DSAPrivateKeyParameters)var0;
         DSAParameters var22 = var17.getParameters();
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(var22.getP(), var22.getQ(), var22.getG())),
            new ASN1Integer(var17.getX()),
            var1
         );
      }

      if (var0 instanceof ECPrivateKeyParameters) {
         ECPrivateKeyParameters var16 = (ECPrivateKeyParameters)var0;
         ECDomainParameters var21 = var16.getParameters();
         X962Parameters var4;
         int var5;
         if (var21 == null) {
            var4 = new X962Parameters(DERNull.INSTANCE);
            var5 = var16.getD().bitLength();
         } else {
            if (var21 instanceof ECGOST3410Parameters) {
               GOST3410PublicKeyAlgParameters var24 = new GOST3410PublicKeyAlgParameters(
                  ((ECGOST3410Parameters)var21).getPublicKeyParamSet(),
                  ((ECGOST3410Parameters)var21).getDigestParamSet(),
                  ((ECGOST3410Parameters)var21).getEncryptionParamSet()
               );
               ASN1ObjectIdentifier var8;
               int var25;
               if (cryptoProOids.contains(var24.getPublicKeyParamSet())) {
                  var25 = 32;
                  var8 = CryptoProObjectIdentifiers.gostR3410_2001;
               } else {
                  boolean var9 = var16.getD().bitLength() > 256;
                  var8 = var9 ? RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512 : RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
                  var25 = var9 ? 64 : 32;
               }

               byte[] var26 = new byte[var25];
               extractBytes(var26, var25, 0, var16.getD());
               return new PrivateKeyInfo(new AlgorithmIdentifier(var8, var24), new DEROctetString(var26));
            }

            if (var21 instanceof ECNamedDomainParameters) {
               var4 = new X962Parameters(((ECNamedDomainParameters)var21).getName());
               var5 = var21.getN().bitLength();
            } else {
               X9ECParameters var6 = new X9ECParameters(var21.getCurve(), new X9ECPoint(var21.getG(), false), var21.getN(), var21.getH(), var21.getSeed());
               var4 = new X962Parameters(var6);
               var5 = var21.getN().bitLength();
            }
         }

         ECPoint var23 = new FixedPointCombMultiplier().multiply(var21.getG(), var16.getD());
         DERBitString var7 = new DERBitString(var23.getEncoded(false));
         return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, var4), new ECPrivateKey(var5, var16.getD(), var7, var4), var1);
      } else if (var0 instanceof MLDSAPrivateKeyParameters) {
         MLDSAPrivateKeyParameters var15 = (MLDSAPrivateKeyParameters)var0;
         AlgorithmIdentifier var20 = new AlgorithmIdentifier(Utils.mldsaOidLookup(var15.getParameters()));
         if (var15.getPreferredFormat() == 1) {
            return new PrivateKeyInfo(var20, new DERTaggedObject(false, 0, new DEROctetString(var15.getSeed())), var1);
         } else {
            return var15.getPreferredFormat() == 2
               ? new PrivateKeyInfo(var20, new DEROctetString(var15.getEncoded()), var1)
               : new PrivateKeyInfo(var20, getBasicPQCEncoding(var15.getSeed(), var15.getEncoded()), var1);
         }
      } else if (var0 instanceof MLKEMPrivateKeyParameters) {
         MLKEMPrivateKeyParameters var14 = (MLKEMPrivateKeyParameters)var0;
         AlgorithmIdentifier var19 = new AlgorithmIdentifier(Utils.mlkemOidLookup(var14.getParameters()));
         if (var14.getPreferredFormat() == 1) {
            return new PrivateKeyInfo(var19, new DERTaggedObject(false, 0, new DEROctetString(var14.getSeed())), var1);
         } else {
            return var14.getPreferredFormat() == 2
               ? new PrivateKeyInfo(var19, new DEROctetString(var14.getEncoded()), var1)
               : new PrivateKeyInfo(var19, getBasicPQCEncoding(var14.getSeed(), var14.getEncoded()), var1);
         }
      } else if (var0 instanceof SLHDSAPrivateKeyParameters) {
         SLHDSAPrivateKeyParameters var13 = (SLHDSAPrivateKeyParameters)var0;
         AlgorithmIdentifier var3 = new AlgorithmIdentifier(Utils.slhdsaOidLookup(var13.getParameters()));
         return new PrivateKeyInfo(var3, var13.getEncoded(), var1);
      } else if (var0 instanceof X448PrivateKeyParameters) {
         X448PrivateKeyParameters var12 = (X448PrivateKeyParameters)var0;
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(EdECObjectIdentifiers.id_X448), new DEROctetString(var12.getEncoded()), var1, var12.generatePublicKey().getEncoded()
         );
      } else if (var0 instanceof X25519PrivateKeyParameters) {
         X25519PrivateKeyParameters var11 = (X25519PrivateKeyParameters)var0;
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(EdECObjectIdentifiers.id_X25519), new DEROctetString(var11.getEncoded()), var1, var11.generatePublicKey().getEncoded()
         );
      } else if (var0 instanceof Ed448PrivateKeyParameters) {
         Ed448PrivateKeyParameters var10 = (Ed448PrivateKeyParameters)var0;
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448), new DEROctetString(var10.getEncoded()), var1, var10.generatePublicKey().getEncoded()
         );
      } else if (var0 instanceof Ed25519PrivateKeyParameters) {
         Ed25519PrivateKeyParameters var2 = (Ed25519PrivateKeyParameters)var0;
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), new DEROctetString(var2.getEncoded()), var1, var2.generatePublicKey().getEncoded()
         );
      } else {
         throw new IOException("key parameters not recognized");
      }
   }

   private static ASN1Sequence getBasicPQCEncoding(byte[] var0, byte[] var1) {
      return new DERSequence(new DEROctetString(var0), new DEROctetString(var1));
   }

   private static void extractBytes(byte[] var0, int var1, int var2, BigInteger var3) {
      byte[] var4 = var3.toByteArray();
      if (var4.length < var1) {
         byte[] var5 = new byte[var1];
         System.arraycopy(var4, 0, var5, var5.length - var4.length, var4.length);
         var4 = var5;
      }

      for (int var6 = 0; var6 != var1; var6++) {
         var0[var2 + var6] = var4[var4.length - 1 - var6];
      }
   }

   static {
      cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_A);
      cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_B);
      cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_C);
      cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchA);
      cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchB);
   }
}
