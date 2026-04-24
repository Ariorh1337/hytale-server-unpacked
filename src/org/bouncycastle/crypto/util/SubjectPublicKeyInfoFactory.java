package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.params.MLDSAPublicKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.SLHDSAPublicKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.pqc.crypto.lms.Composer;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class SubjectPublicKeyInfoFactory {
   private static final byte tag_OctetString = 4;
   private static Set cryptoProOids = new HashSet(5);

   private SubjectPublicKeyInfoFactory() {
   }

   public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(AsymmetricKeyParameter var0) throws IOException {
      if (var0 instanceof RSAKeyParameters) {
         RSAKeyParameters var23 = (RSAKeyParameters)var0;
         return new SubjectPublicKeyInfo(
            new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new RSAPublicKey(var23.getModulus(), var23.getExponent())
         );
      }

      if (var0 instanceof MLDSAPublicKeyParameters) {
         MLDSAPublicKeyParameters var22 = (MLDSAPublicKeyParameters)var0;
         AlgorithmIdentifier var29 = new AlgorithmIdentifier(Utils.mldsaOidLookup(var22.getParameters()));
         return new SubjectPublicKeyInfo(var29, var22.getEncoded());
      }

      if (var0 instanceof MLKEMPublicKeyParameters) {
         MLKEMPublicKeyParameters var21 = (MLKEMPublicKeyParameters)var0;
         AlgorithmIdentifier var28 = new AlgorithmIdentifier(Utils.mlkemOidLookup(var21.getParameters()));
         return new SubjectPublicKeyInfo(var28, var21.getEncoded());
      }

      if (var0 instanceof SLHDSAPublicKeyParameters) {
         SLHDSAPublicKeyParameters var20 = (SLHDSAPublicKeyParameters)var0;
         byte[] var27 = var20.getEncoded();
         AlgorithmIdentifier var34 = new AlgorithmIdentifier(Utils.slhdsaOidLookup(var20.getParameters()));
         return new SubjectPublicKeyInfo(var34, var27);
      }

      if (var0 instanceof DSAPublicKeyParameters) {
         DSAPublicKeyParameters var19 = (DSAPublicKeyParameters)var0;
         DSAParameter var26 = null;
         DSAParameters var33 = var19.getParameters();
         if (var33 != null) {
            var26 = new DSAParameter(var33.getP(), var33.getQ(), var33.getG());
         }

         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, var26), new ASN1Integer(var19.getY()));
      } else if (var0 instanceof ECPublicKeyParameters) {
         ECPublicKeyParameters var18 = (ECPublicKeyParameters)var0;
         ECDomainParameters var25 = var18.getParameters();
         X962Parameters var31;
         if (var25 == null) {
            var31 = new X962Parameters(DERNull.INSTANCE);
         } else {
            if (var25 instanceof ECGOST3410Parameters) {
               ECGOST3410Parameters var36 = (ECGOST3410Parameters)var25;
               BigInteger var5 = var18.getQ().getAffineXCoord().toBigInteger();
               BigInteger var6 = var18.getQ().getAffineYCoord().toBigInteger();
               GOST3410PublicKeyAlgParameters var32 = new GOST3410PublicKeyAlgParameters(var36.getPublicKeyParamSet(), var36.getDigestParamSet());
               short var7;
               byte var8;
               ASN1ObjectIdentifier var9;
               if (cryptoProOids.contains(var36.getPublicKeyParamSet())) {
                  var7 = 64;
                  var8 = 32;
                  var9 = CryptoProObjectIdentifiers.gostR3410_2001;
               } else {
                  boolean var10 = var5.bitLength() > 256;
                  if (var10) {
                     var7 = 128;
                     var8 = 64;
                     var9 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512;
                  } else {
                     var7 = 64;
                     var8 = 32;
                     var9 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
                  }
               }

               byte[] var37 = new byte[var7];
               extractBytes(var37, var7 / 2, 0, var5);
               extractBytes(var37, var7 / 2, var8, var6);

               try {
                  return new SubjectPublicKeyInfo(new AlgorithmIdentifier(var9, var32), new DEROctetString(var37));
               } catch (IOException var12) {
                  return null;
               }
            }

            if (var25 instanceof ECNamedDomainParameters) {
               var31 = new X962Parameters(((ECNamedDomainParameters)var25).getName());
            } else {
               X9ECParameters var4 = new X9ECParameters(var25.getCurve(), new X9ECPoint(var25.getG(), false), var25.getN(), var25.getH(), var25.getSeed());
               var31 = new X962Parameters(var4);
            }
         }

         byte[] var35 = var18.getQ().getEncoded(false);
         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, var31), var35);
      } else if (var0 instanceof X448PublicKeyParameters) {
         X448PublicKeyParameters var17 = (X448PublicKeyParameters)var0;
         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_X448), var17.getEncoded());
      } else if (var0 instanceof X25519PublicKeyParameters) {
         X25519PublicKeyParameters var16 = (X25519PublicKeyParameters)var0;
         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_X25519), var16.getEncoded());
      } else if (var0 instanceof Ed448PublicKeyParameters) {
         Ed448PublicKeyParameters var15 = (Ed448PublicKeyParameters)var0;
         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448), var15.getEncoded());
      } else if (var0 instanceof Ed25519PublicKeyParameters) {
         Ed25519PublicKeyParameters var14 = (Ed25519PublicKeyParameters)var0;
         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), var14.getEncoded());
      } else if (var0 instanceof HSSPublicKeyParameters) {
         HSSPublicKeyParameters var13 = (HSSPublicKeyParameters)var0;
         byte[] var24 = Composer.compose().u32str(var13.getL()).bytes(var13.getLMSPublicKey()).build();
         AlgorithmIdentifier var30 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
         return new SubjectPublicKeyInfo(var30, Arrays.concatenate(new byte[]{4, (byte)var24.length}, var24));
      } else if (var0 instanceof LMSPublicKeyParameters) {
         LMSPublicKeyParameters var1 = (LMSPublicKeyParameters)var0;
         byte[] var2 = Composer.compose().u32str(1).bytes(var1).build();
         AlgorithmIdentifier var3 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
         return new SubjectPublicKeyInfo(var3, Arrays.concatenate(new byte[]{4, (byte)var2.length}, var2));
      } else {
         throw new IOException("key parameters not recognized");
      }
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
