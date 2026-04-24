package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLDSAParameters;
import org.bouncycastle.crypto.params.MLDSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLDSAPublicKeyParameters;
import org.bouncycastle.crypto.params.MLKEMParameters;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.params.SLHDSAParameters;
import org.bouncycastle.crypto.params.SLHDSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.ElGamalParameter;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class PrivateKeyFactory {
   public static AsymmetricKeyParameter createKey(byte[] var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("privateKeyInfoData array null");
      } else if (var0.length == 0) {
         throw new IllegalArgumentException("privateKeyInfoData array empty");
      } else {
         return createKey(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(var0)));
      }
   }

   public static AsymmetricKeyParameter createKey(InputStream var0) throws IOException {
      return createKey(PrivateKeyInfo.getInstance(new ASN1InputStream(var0).readObject()));
   }

   public static AsymmetricKeyParameter createKey(PrivateKeyInfo var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("keyInfo argument null");
      }

      AlgorithmIdentifier var1 = var0.getPrivateKeyAlgorithm();
      ASN1ObjectIdentifier var2 = var1.getAlgorithm();
      if (var2.equals(PKCSObjectIdentifiers.rsaEncryption) || var2.equals(PKCSObjectIdentifiers.id_RSASSA_PSS) || var2.equals(X509ObjectIdentifiers.id_ea_rsa)) {
         RSAPrivateKey var19 = RSAPrivateKey.getInstance(var0.parsePrivateKey());
         return new RSAPrivateCrtKeyParameters(
            var19.getModulus(),
            var19.getPublicExponent(),
            var19.getPrivateExponent(),
            var19.getPrime1(),
            var19.getPrime2(),
            var19.getExponent1(),
            var19.getExponent2(),
            var19.getCoefficient()
         );
      }

      if (var2.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
         DHParameter var18 = DHParameter.getInstance(var1.getParameters());
         ASN1Integer var26 = (ASN1Integer)var0.parsePrivateKey();
         BigInteger var32 = var18.getL();
         int var40 = var32 == null ? 0 : var32.intValue();
         DHParameters var43 = new DHParameters(var18.getP(), var18.getG(), null, var40);
         return new DHPrivateKeyParameters(var26.getValue(), var43);
      }

      if (var2.equals(OIWObjectIdentifiers.elGamalAlgorithm)) {
         ElGamalParameter var17 = ElGamalParameter.getInstance(var1.getParameters());
         ASN1Integer var25 = (ASN1Integer)var0.parsePrivateKey();
         return new ElGamalPrivateKeyParameters(var25.getValue(), new ElGamalParameters(var17.getP(), var17.getG()));
      }

      if (var2.equals(X9ObjectIdentifiers.id_dsa)) {
         ASN1Integer var16 = (ASN1Integer)var0.parsePrivateKey();
         ASN1Encodable var24 = var1.getParameters();
         DSAParameters var31 = null;
         if (var24 != null) {
            DSAParameter var39 = DSAParameter.getInstance(var24.toASN1Primitive());
            var31 = new DSAParameters(var39.getP(), var39.getQ(), var39.getG());
         }

         return new DSAPrivateKeyParameters(var16.getValue(), var31);
      } else if (var2.equals(X9ObjectIdentifiers.id_ecPublicKey)) {
         ECPrivateKey var15 = ECPrivateKey.getInstance(var0.parsePrivateKey());
         X962Parameters var23 = X962Parameters.getInstance(var1.getParameters().toASN1Primitive());
         ECDomainParameters var30;
         if (var23.isNamedCurve()) {
            ASN1ObjectIdentifier var36 = ASN1ObjectIdentifier.getInstance(var23.getParameters());
            var30 = ECNamedDomainParameters.lookup(var36);
         } else {
            X9ECParameters var37 = X9ECParameters.getInstance(var23.getParameters());
            var30 = new ECDomainParameters(var37);
         }

         BigInteger var38 = var15.getKey();
         return new ECPrivateKeyParameters(var38, var30);
      } else {
         if (var2.equals(EdECObjectIdentifiers.id_X25519)) {
            return 32 == var0.getPrivateKeyLength()
               ? new X25519PrivateKeyParameters(var0.getPrivateKey().getOctets())
               : new X25519PrivateKeyParameters(getRawKey(var0));
         }

         if (var2.equals(EdECObjectIdentifiers.id_X448)) {
            return 56 == var0.getPrivateKeyLength()
               ? new X448PrivateKeyParameters(var0.getPrivateKey().getOctets())
               : new X448PrivateKeyParameters(getRawKey(var0));
         }

         if (var2.equals(EdECObjectIdentifiers.id_Ed25519)) {
            return new Ed25519PrivateKeyParameters(getRawKey(var0));
         }

         if (var2.equals(EdECObjectIdentifiers.id_Ed448)) {
            return new Ed448PrivateKeyParameters(getRawKey(var0));
         }

         if (Utils.mldsaParams.containsKey(var2)) {
            ASN1Primitive var14 = parsePrimitiveString(var0.getPrivateKey(), 32);
            MLDSAParameters var22 = Utils.mldsaParamsLookup(var2);
            MLDSAPublicKeyParameters var29 = null;
            if (var0.getPublicKeyData() != null) {
               var29 = PublicKeyFactory.MLDSAConverter.getPublicKeyParams(var22, var0.getPublicKeyData());
            }

            if (var14 instanceof ASN1OctetString) {
               return new MLDSAPrivateKeyParameters(var22, ((ASN1OctetString)var14).getOctets(), var29);
            }

            if (var14 instanceof ASN1Sequence) {
               ASN1Sequence var35 = (ASN1Sequence)var14;
               byte[] var42 = ASN1OctetString.getInstance(var35.getObjectAt(0)).getOctets();
               byte[] var46 = ASN1OctetString.getInstance(var35.getObjectAt(1)).getOctets();
               MLDSAPrivateKeyParameters var51 = new MLDSAPrivateKeyParameters(var22, var42, var29);
               if (!Arrays.constantTimeAreEqual(var51.getEncoded(), var46)) {
                  throw new IllegalArgumentException("inconsistent " + var22.getName() + " private key");
               } else {
                  return var51;
               }
            } else {
               throw new IllegalArgumentException("invalid " + var22.getName() + " private key");
            }
         } else if (var2.equals(NISTObjectIdentifiers.id_alg_ml_kem_512)
            || var2.equals(NISTObjectIdentifiers.id_alg_ml_kem_768)
            || var2.equals(NISTObjectIdentifiers.id_alg_ml_kem_1024)) {
            ASN1Primitive var13 = parsePrimitiveString(var0.getPrivateKey(), 64);
            MLKEMParameters var21 = Utils.mlkemParamsLookup(var2);
            MLKEMPublicKeyParameters var28 = null;
            if (var0.getPublicKeyData() != null) {
               var28 = PublicKeyFactory.MLKEMConverter.getPublicKeyParams(var21, var0.getPublicKeyData());
            }

            if (var13 instanceof ASN1OctetString) {
               return new MLKEMPrivateKeyParameters(var21, ((ASN1OctetString)var13).getOctets(), var28);
            }

            if (var13 instanceof ASN1Sequence) {
               ASN1Sequence var34 = (ASN1Sequence)var13;
               byte[] var41 = ASN1OctetString.getInstance(var34.getObjectAt(0)).getOctets();
               byte[] var45 = ASN1OctetString.getInstance(var34.getObjectAt(1)).getOctets();
               MLKEMPrivateKeyParameters var50 = new MLKEMPrivateKeyParameters(var21, var41, var28);
               if (!Arrays.constantTimeAreEqual(var50.getEncoded(), var45)) {
                  throw new IllegalArgumentException("inconsistent " + var21.getName() + " private key");
               } else {
                  return var50;
               }
            } else {
               throw new IllegalArgumentException("invalid " + var21.getName() + " private key");
            }
         } else {
            if (Utils.slhdsaParams.containsKey(var2)) {
               SLHDSAParameters var12 = Utils.slhdsaParamsLookup(var2);
               ASN1OctetString var20 = parseOctetString(var0.getPrivateKey(), var12.getN() * 4);
               return new SLHDSAPrivateKeyParameters(var12, var20.getOctets());
            }

            if (!var2.equals(CryptoProObjectIdentifiers.gostR3410_2001)
               && !var2.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512)
               && !var2.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256)) {
               throw new RuntimeException("algorithm identifier in private key not recognised");
            }

            ASN1Encodable var3 = var1.getParameters();
            GOST3410PublicKeyAlgParameters var4 = GOST3410PublicKeyAlgParameters.getInstance(var3);
            ECDomainParameters var5 = null;
            BigInteger var6 = null;
            ASN1Primitive var7 = var3.toASN1Primitive();
            if (var7 instanceof ASN1Sequence && (ASN1Sequence.getInstance(var7).size() == 2 || ASN1Sequence.getInstance(var7).size() == 3)) {
               X9ECParameters var44 = ECGOST3410NamedCurves.getByOIDX9(var4.getPublicKeyParamSet());
               var5 = new ECGOST3410Parameters(
                  new ECNamedDomainParameters(var4.getPublicKeyParamSet(), var44),
                  var4.getPublicKeyParamSet(),
                  var4.getDigestParamSet(),
                  var4.getEncryptionParamSet()
               );
               int var49 = var0.getPrivateKeyLength();
               if (var49 != 32 && var49 != 64) {
                  ASN1Encodable var54 = var0.parsePrivateKey();
                  if (var54 instanceof ASN1Integer) {
                     var6 = ASN1Integer.getInstance(var54).getPositiveValue();
                  } else {
                     byte[] var11 = Arrays.reverse(ASN1OctetString.getInstance(var54).getOctets());
                     var6 = new BigInteger(1, var11);
                  }
               } else {
                  var6 = new BigInteger(1, Arrays.reverse(var0.getPrivateKey().getOctets()));
               }
            } else {
               X962Parameters var8 = X962Parameters.getInstance(var1.getParameters());
               if (var8.isNamedCurve()) {
                  ASN1ObjectIdentifier var9 = ASN1ObjectIdentifier.getInstance(var8.getParameters());
                  X9ECParameters var10 = ECNamedCurveTable.getByOID(var9);
                  var5 = new ECGOST3410Parameters(
                     new ECNamedDomainParameters(var9, var10), var4.getPublicKeyParamSet(), var4.getDigestParamSet(), var4.getEncryptionParamSet()
                  );
               } else if (var8.isImplicitlyCA()) {
                  var5 = null;
               } else {
                  X9ECParameters var47 = X9ECParameters.getInstance(var8.getParameters());
                  var5 = new ECGOST3410Parameters(
                     new ECNamedDomainParameters(var2, var47), var4.getPublicKeyParamSet(), var4.getDigestParamSet(), var4.getEncryptionParamSet()
                  );
               }

               ASN1Encodable var48 = var0.parsePrivateKey();
               if (var48 instanceof ASN1Integer) {
                  ASN1Integer var52 = ASN1Integer.getInstance(var48);
                  var6 = var52.getValue();
               } else {
                  ECPrivateKey var53 = ECPrivateKey.getInstance(var48);
                  var6 = var53.getKey();
               }
            }

            return new ECPrivateKeyParameters(
               var6, new ECGOST3410Parameters(var5, var4.getPublicKeyParamSet(), var4.getDigestParamSet(), var4.getEncryptionParamSet())
            );
         }
      }
   }

   private static ASN1OctetString parseOctetString(ASN1OctetString var0, int var1) throws IOException {
      byte[] var2 = var0.getOctets();
      if (var2.length == var1) {
         return var0;
      }

      ASN1OctetString var3 = Utils.parseOctetData(var2);
      return var3 != null ? ASN1OctetString.getInstance(var3) : var0;
   }

   private static ASN1Primitive parsePrimitiveString(ASN1OctetString var0, int var1) throws IOException {
      byte[] var2 = var0.getOctets();
      if (var2.length == var1) {
         return var0;
      } else {
         ASN1Primitive var3 = Utils.parseData(var2);
         if (var3 instanceof ASN1OctetString) {
            return ASN1OctetString.getInstance(var3);
         } else {
            return var3 instanceof ASN1Sequence ? ASN1Sequence.getInstance(var3) : var0;
         }
      }
   }

   private static byte[] getRawKey(PrivateKeyInfo var0) throws IOException {
      return ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
   }
}
