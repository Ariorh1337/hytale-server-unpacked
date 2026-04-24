package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.CMCEPrivateKey;
import org.bouncycastle.pqc.asn1.FalconPrivateKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.SPHINCSPLUSPrivateKey;
import org.bouncycastle.pqc.asn1.SPHINCSPLUSPublicKey;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTPrivateKey;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusParameters;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.pqc.legacy.bike.BIKEParameters;
import org.bouncycastle.pqc.legacy.bike.BIKEPrivateKeyParameters;
import org.bouncycastle.pqc.legacy.picnic.PicnicParameters;
import org.bouncycastle.pqc.legacy.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.legacy.rainbow.RainbowParameters;
import org.bouncycastle.pqc.legacy.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.legacy.sphincsplus.SPHINCSPlusParameters;
import org.bouncycastle.pqc.legacy.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

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
         throw new IllegalArgumentException("keyInfo array null");
      }

      AlgorithmIdentifier var1 = var0.getPrivateKeyAlgorithm();
      ASN1ObjectIdentifier var2 = var1.getAlgorithm();
      if (var2.equals(PQCObjectIdentifiers.sphincs256)) {
         return new SPHINCSPrivateKeyParameters(
            ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets(),
            Utils.sphincs256LookupTreeAlgName(SPHINCS256KeyParams.getInstance(var1.getParameters()))
         );
      }

      if (var2.equals(PQCObjectIdentifiers.newHope)) {
         return new NHPrivateKeyParameters(convert(ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets()));
      }

      if (var2.equals(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig)) {
         ASN1OctetString var33 = parseOctetString(var0.getPrivateKey(), 64);
         byte[] var55 = var33.getOctets();
         ASN1BitString var63 = var0.getPublicKeyData();
         if (var63 != null) {
            byte[] var71 = var63.getOctets();
            return HSSPrivateKeyParameters.getInstance(Arrays.copyOfRange(var55, 4, var55.length), var71);
         } else {
            return HSSPrivateKeyParameters.getInstance(Arrays.copyOfRange(var55, 4, var55.length));
         }
      } else if (var2.on(BCObjectIdentifiers.sphincsPlus) || var2.on(BCObjectIdentifiers.sphincsPlus_interop)) {
         SPHINCSPlusParameters var32 = Utils.sphincsPlusParamsLookup(var2);
         ASN1Encodable var54 = var0.parsePrivateKey();
         if (var54 instanceof ASN1Sequence) {
            SPHINCSPLUSPrivateKey var62 = SPHINCSPLUSPrivateKey.getInstance(var54);
            SPHINCSPLUSPublicKey var70 = var62.getPublicKey();
            return new SPHINCSPlusPrivateKeyParameters(var32, var62.getSkseed(), var62.getSkprf(), var70.getPkseed(), var70.getPkroot());
         } else {
            return new SPHINCSPlusPrivateKeyParameters(var32, ASN1OctetString.getInstance(var54).getOctets());
         }
      } else {
         if (Utils.slhdsaParams.containsKey(var2)) {
            SLHDSAParameters var31 = Utils.slhdsaParamsLookup(var2);
            ASN1OctetString var53 = parseOctetString(var0.getPrivateKey(), var31.getN() * 4);
            return new SLHDSAPrivateKeyParameters(var31, var53.getOctets());
         }

         if (var2.on(BCObjectIdentifiers.picnic)) {
            byte[] var30 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            PicnicParameters var52 = Utils.picnicParamsLookup(var2);
            return new PicnicPrivateKeyParameters(var52, var30);
         }

         if (var2.on(BCObjectIdentifiers.pqc_kem_mceliece)) {
            CMCEPrivateKey var29 = CMCEPrivateKey.getInstance(var0.parsePrivateKey());
            CMCEParameters var51 = Utils.mcElieceParamsLookup(var2);
            return new CMCEPrivateKeyParameters(var51, var29.getDelta(), var29.getC(), var29.getG(), var29.getAlpha(), var29.getS());
         }

         if (var2.on(BCObjectIdentifiers.pqc_kem_frodo)) {
            byte[] var28 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            FrodoParameters var50 = Utils.frodoParamsLookup(var2);
            return new FrodoPrivateKeyParameters(var50, var28);
         }

         if (var2.on(BCObjectIdentifiers.pqc_kem_saber)) {
            byte[] var27 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            SABERParameters var49 = Utils.saberParamsLookup(var2);
            return new SABERPrivateKeyParameters(var49, var27);
         }

         if (var2.on(BCObjectIdentifiers.pqc_kem_ntru)) {
            byte[] var26 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            NTRUParameters var48 = Utils.ntruParamsLookup(var2);
            return new NTRUPrivateKeyParameters(var48, var26);
         }

         if (var2.equals(NISTObjectIdentifiers.id_alg_ml_kem_512)
            || var2.equals(NISTObjectIdentifiers.id_alg_ml_kem_768)
            || var2.equals(NISTObjectIdentifiers.id_alg_ml_kem_1024)) {
            ASN1Primitive var25 = parsePrimitiveString(var0.getPrivateKey(), 64);
            MLKEMParameters var47 = Utils.mlkemParamsLookup(var2);
            MLKEMPublicKeyParameters var61 = null;
            if (var0.getPublicKeyData() != null) {
               var61 = PublicKeyFactory.MLKEMConverter.getPublicKeyParams(var47, var0.getPublicKeyData());
            }

            if (var25 instanceof ASN1OctetString) {
               return new MLKEMPrivateKeyParameters(var47, ((ASN1OctetString)var25).getOctets(), var61);
            }

            if (var25 instanceof ASN1Sequence) {
               ASN1Sequence var69 = (ASN1Sequence)var25;
               byte[] var76 = ASN1OctetString.getInstance(var69.getObjectAt(0)).getOctets();
               byte[] var77 = ASN1OctetString.getInstance(var69.getObjectAt(1)).getOctets();
               MLKEMPrivateKeyParameters var78 = new MLKEMPrivateKeyParameters(var47, var76, var61);
               if (!Arrays.constantTimeAreEqual(var78.getEncoded(), var77)) {
                  throw new IllegalArgumentException("inconsistent " + var47.getName() + " private key");
               } else {
                  return var78;
               }
            } else {
               throw new IllegalArgumentException("invalid " + var47.getName() + " private key");
            }
         } else {
            if (var2.on(BCObjectIdentifiers.pqc_kem_ntrulprime)) {
               ASN1Sequence var24 = ASN1Sequence.getInstance(var0.parsePrivateKey());
               NTRULPRimeParameters var46 = Utils.ntrulprimeParamsLookup(var2);
               return new NTRULPRimePrivateKeyParameters(
                  var46,
                  ASN1OctetString.getInstance(var24.getObjectAt(0)).getOctets(),
                  ASN1OctetString.getInstance(var24.getObjectAt(1)).getOctets(),
                  ASN1OctetString.getInstance(var24.getObjectAt(2)).getOctets(),
                  ASN1OctetString.getInstance(var24.getObjectAt(3)).getOctets()
               );
            }

            if (var2.on(BCObjectIdentifiers.pqc_kem_sntruprime)) {
               ASN1Sequence var23 = ASN1Sequence.getInstance(var0.parsePrivateKey());
               SNTRUPrimeParameters var45 = Utils.sntruprimeParamsLookup(var2);
               return new SNTRUPrimePrivateKeyParameters(
                  var45,
                  ASN1OctetString.getInstance(var23.getObjectAt(0)).getOctets(),
                  ASN1OctetString.getInstance(var23.getObjectAt(1)).getOctets(),
                  ASN1OctetString.getInstance(var23.getObjectAt(2)).getOctets(),
                  ASN1OctetString.getInstance(var23.getObjectAt(3)).getOctets(),
                  ASN1OctetString.getInstance(var23.getObjectAt(4)).getOctets()
               );
            }

            if (Utils.mldsaParams.containsKey(var2)) {
               ASN1Primitive var22 = parsePrimitiveString(var0.getPrivateKey(), 32);
               MLDSAParameters var44 = Utils.mldsaParamsLookup(var2);
               MLDSAPublicKeyParameters var60 = null;
               if (var0.getPublicKeyData() != null) {
                  var60 = PublicKeyFactory.MLDSAConverter.getPublicKeyParams(var44, var0.getPublicKeyData());
               }

               if (var22 instanceof ASN1OctetString) {
                  return new MLDSAPrivateKeyParameters(var44, ((ASN1OctetString)var22).getOctets(), var60);
               }

               if (var22 instanceof ASN1Sequence) {
                  ASN1Sequence var68 = (ASN1Sequence)var22;
                  byte[] var75 = ASN1OctetString.getInstance(var68.getObjectAt(0)).getOctets();
                  byte[] var8 = ASN1OctetString.getInstance(var68.getObjectAt(1)).getOctets();
                  MLDSAPrivateKeyParameters var9 = new MLDSAPrivateKeyParameters(var44, var75, var60);
                  if (!Arrays.constantTimeAreEqual(var9.getEncoded(), var8)) {
                     throw new IllegalArgumentException("inconsistent " + var44.getName() + " private key");
                  } else {
                     return var9;
                  }
               } else {
                  throw new IllegalArgumentException("invalid " + var44.getName() + " private key");
               }
            } else if (var2.equals(BCObjectIdentifiers.dilithium2)
               || var2.equals(BCObjectIdentifiers.dilithium3)
               || var2.equals(BCObjectIdentifiers.dilithium5)) {
               ASN1Encodable var21 = var0.parsePrivateKey();
               DilithiumParameters var43 = Utils.dilithiumParamsLookup(var2);
               if (var21 instanceof ASN1Sequence) {
                  ASN1Sequence var59 = ASN1Sequence.getInstance(var21);
                  int var67 = ASN1Integer.getInstance(var59.getObjectAt(0)).intValueExact();
                  if (var67 != 0) {
                     throw new IOException("unknown private key version: " + var67);
                  } else if (var0.getPublicKeyData() != null) {
                     DilithiumPublicKeyParameters var74 = PublicKeyFactory.DilithiumConverter.getPublicKeyParams(var43, var0.getPublicKeyData());
                     return new DilithiumPrivateKeyParameters(
                        var43,
                        ASN1BitString.getInstance(var59.getObjectAt(1)).getOctets(),
                        ASN1BitString.getInstance(var59.getObjectAt(2)).getOctets(),
                        ASN1BitString.getInstance(var59.getObjectAt(3)).getOctets(),
                        ASN1BitString.getInstance(var59.getObjectAt(4)).getOctets(),
                        ASN1BitString.getInstance(var59.getObjectAt(5)).getOctets(),
                        ASN1BitString.getInstance(var59.getObjectAt(6)).getOctets(),
                        var74.getT1()
                     );
                  } else {
                     return new DilithiumPrivateKeyParameters(
                        var43,
                        ASN1BitString.getInstance(var59.getObjectAt(1)).getOctets(),
                        ASN1BitString.getInstance(var59.getObjectAt(2)).getOctets(),
                        ASN1BitString.getInstance(var59.getObjectAt(3)).getOctets(),
                        ASN1BitString.getInstance(var59.getObjectAt(4)).getOctets(),
                        ASN1BitString.getInstance(var59.getObjectAt(5)).getOctets(),
                        ASN1BitString.getInstance(var59.getObjectAt(6)).getOctets(),
                        null
                     );
                  }
               } else if (var21 instanceof DEROctetString) {
                  byte[] var58 = ASN1OctetString.getInstance(var21).getOctets();
                  if (var0.getPublicKeyData() != null) {
                     DilithiumPublicKeyParameters var66 = PublicKeyFactory.DilithiumConverter.getPublicKeyParams(var43, var0.getPublicKeyData());
                     return new DilithiumPrivateKeyParameters(var43, var58, var66);
                  } else {
                     return new DilithiumPrivateKeyParameters(var43, var58, null);
                  }
               } else {
                  throw new IOException("not supported");
               }
            } else {
               if (var2.equals(BCObjectIdentifiers.falcon_512) || var2.equals(BCObjectIdentifiers.falcon_1024)) {
                  FalconPrivateKey var20 = FalconPrivateKey.getInstance(var0.parsePrivateKey());
                  FalconParameters var42 = Utils.falconParamsLookup(var2);
                  return new FalconPrivateKeyParameters(var42, var20.getf(), var20.getG(), var20.getF(), var20.getPublicKey().getH());
               }

               if (var2.equals(BCObjectIdentifiers.old_falcon_512) || var2.equals(BCObjectIdentifiers.old_falcon_1024)) {
                  FalconPrivateKey var19 = FalconPrivateKey.getInstance(var0.parsePrivateKey());
                  FalconParameters var41 = Utils.falconParamsLookup(var2);
                  return new FalconPrivateKeyParameters(var41, var19.getf(), var19.getG(), var19.getF(), var19.getPublicKey().getH());
               }

               if (var2.on(BCObjectIdentifiers.pqc_kem_bike)) {
                  byte[] var18 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
                  BIKEParameters var40 = Utils.bikeParamsLookup(var2);
                  byte[] var57 = Arrays.copyOfRange(var18, 0, var40.getRByte());
                  byte[] var65 = Arrays.copyOfRange(var18, var40.getRByte(), 2 * var40.getRByte());
                  byte[] var73 = Arrays.copyOfRange(var18, 2 * var40.getRByte(), var18.length);
                  return new BIKEPrivateKeyParameters(var40, var57, var65, var73);
               }

               if (var2.on(BCObjectIdentifiers.pqc_kem_hqc)) {
                  byte[] var17 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
                  HQCParameters var39 = Utils.hqcParamsLookup(var2);
                  return new HQCPrivateKeyParameters(var39, var17);
               }

               if (var2.on(BCObjectIdentifiers.rainbow)) {
                  byte[] var16 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
                  RainbowParameters var38 = Utils.rainbowParamsLookup(var2);
                  return new RainbowPrivateKeyParameters(var38, var16);
               }

               if (var2.equals(PQCObjectIdentifiers.xmss)) {
                  XMSSKeyParams var15 = XMSSKeyParams.getInstance(var1.getParameters());
                  ASN1ObjectIdentifier var37 = var15.getTreeDigest().getAlgorithm();
                  XMSSPrivateKey var56 = XMSSPrivateKey.getInstance(var0.parsePrivateKey());

                  try {
                     XMSSPrivateKeyParameters.Builder var64 = new XMSSPrivateKeyParameters.Builder(
                           new XMSSParameters(var15.getHeight(), Utils.getDigest(var37))
                        )
                        .withIndex(var56.getIndex())
                        .withSecretKeySeed(var56.getSecretKeySeed())
                        .withSecretKeyPRF(var56.getSecretKeyPRF())
                        .withPublicSeed(var56.getPublicSeed())
                        .withRoot(var56.getRoot());
                     if (var56.getVersion() != 0) {
                        var64.withMaxIndex(var56.getMaxIndex());
                     }

                     if (var56.getBdsState() != null) {
                        BDS var72 = (BDS)XMSSUtil.deserialize(var56.getBdsState(), BDS.class);
                        var64.withBDSState(var72.withWOTSDigest(var37));
                     }

                     return var64.build();
                  } catch (ClassNotFoundException var10) {
                     throw new IOException("ClassNotFoundException processing BDS state: " + var10.getMessage());
                  }
               } else if (var2.equals(PQCObjectIdentifiers.xmss_mt)) {
                  XMSSMTKeyParams var14 = XMSSMTKeyParams.getInstance(var1.getParameters());
                  ASN1ObjectIdentifier var36 = var14.getTreeDigest().getAlgorithm();

                  try {
                     XMSSMTPrivateKey var5 = XMSSMTPrivateKey.getInstance(var0.parsePrivateKey());
                     XMSSMTPrivateKeyParameters.Builder var6 = new XMSSMTPrivateKeyParameters.Builder(
                           new XMSSMTParameters(var14.getHeight(), var14.getLayers(), Utils.getDigest(var36))
                        )
                        .withIndex(var5.getIndex())
                        .withSecretKeySeed(var5.getSecretKeySeed())
                        .withSecretKeyPRF(var5.getSecretKeyPRF())
                        .withPublicSeed(var5.getPublicSeed())
                        .withRoot(var5.getRoot());
                     if (var5.getVersion() != 0) {
                        var6.withMaxIndex(var5.getMaxIndex());
                     }

                     if (var5.getBdsState() != null) {
                        BDSStateMap var7 = (BDSStateMap)XMSSUtil.deserialize(var5.getBdsState(), BDSStateMap.class);
                        var6.withBDSState(var7.withWOTSDigest(var36));
                     }

                     return var6.build();
                  } catch (ClassNotFoundException var11) {
                     throw new IOException("ClassNotFoundException processing BDS state: " + var11.getMessage());
                  }
               } else if (var2.on(BCObjectIdentifiers.mayo)) {
                  byte[] var13 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
                  MayoParameters var35 = Utils.mayoParamsLookup(var2);
                  return new MayoPrivateKeyParameters(var35, var13);
               } else if (var2.on(BCObjectIdentifiers.snova)) {
                  byte[] var12 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
                  SnovaParameters var34 = Utils.snovaParamsLookup(var2);
                  return new SnovaPrivateKeyParameters(var34, var12);
               } else if (var2.on(BCObjectIdentifiers.pqc_kem_ntruplus)) {
                  byte[] var3 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
                  NTRUPlusParameters var4 = Utils.ntruPlusParamsLookup(var2);
                  return new NTRUPlusPrivateKeyParameters(var4, var3);
               } else {
                  throw new RuntimeException("algorithm identifier in private key not recognised");
               }
            }
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

   private static short[] convert(byte[] var0) {
      short[] var1 = new short[var0.length / 2];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = Pack.littleEndianToShort(var0, var2 * 2);
      }

      return var1;
   }
}
