package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.CMCEPrivateKey;
import org.bouncycastle.pqc.asn1.CMCEPublicKey;
import org.bouncycastle.pqc.asn1.FalconPrivateKey;
import org.bouncycastle.pqc.asn1.FalconPublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTPrivateKey;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.crypto.cmce.CMCEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.Composer;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.pqc.legacy.bike.BIKEPrivateKeyParameters;
import org.bouncycastle.pqc.legacy.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.legacy.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.legacy.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.util.Pack;

public class PrivateKeyInfoFactory {
   private PrivateKeyInfoFactory() {
   }

   public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter var0) throws IOException {
      return createPrivateKeyInfo(var0, null);
   }

   public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter var0, ASN1Set var1) throws IOException {
      if (var0 instanceof SPHINCSPrivateKeyParameters) {
         SPHINCSPrivateKeyParameters var30 = (SPHINCSPrivateKeyParameters)var0;
         AlgorithmIdentifier var54 = new AlgorithmIdentifier(
            PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(Utils.sphincs256LookupTreeAlgID(var30.getTreeDigest()))
         );
         return new PrivateKeyInfo(var54, new DEROctetString(var30.getKeyData()));
      }

      if (!(var0 instanceof NHPrivateKeyParameters)) {
         if (var0 instanceof LMSPrivateKeyParameters) {
            LMSPrivateKeyParameters var29 = (LMSPrivateKeyParameters)var0;
            byte[] var53 = Composer.compose().u32str(1).bytes(var29).build();
            byte[] var71 = Composer.compose().u32str(1).bytes(var29.getPublicKey()).build();
            AlgorithmIdentifier var75 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
            return new PrivateKeyInfo(var75, new DEROctetString(var53), var1, var71);
         }

         if (var0 instanceof HSSPrivateKeyParameters) {
            HSSPrivateKeyParameters var28 = (HSSPrivateKeyParameters)var0;
            byte[] var52 = Composer.compose().u32str(var28.getL()).bytes(var28).build();
            byte[] var70 = Composer.compose().u32str(var28.getL()).bytes(var28.getPublicKey().getLMSPublicKey()).build();
            AlgorithmIdentifier var74 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
            return new PrivateKeyInfo(var74, new DEROctetString(var52), var1, var70);
         }

         if (var0 instanceof SPHINCSPlusPrivateKeyParameters) {
            SPHINCSPlusPrivateKeyParameters var27 = (SPHINCSPlusPrivateKeyParameters)var0;
            AlgorithmIdentifier var51 = new AlgorithmIdentifier(Utils.sphincsPlusOidLookup(var27.getParameters()));
            return new PrivateKeyInfo(var51, new DEROctetString(var27.getEncoded()), var1, var27.getPublicKey());
         }

         if (var0 instanceof SLHDSAPrivateKeyParameters) {
            SLHDSAPrivateKeyParameters var26 = (SLHDSAPrivateKeyParameters)var0;
            AlgorithmIdentifier var50 = new AlgorithmIdentifier(Utils.slhdsaOidLookup(var26.getParameters()));
            return new PrivateKeyInfo(var50, var26.getEncoded(), var1);
         }

         if (var0 instanceof PicnicPrivateKeyParameters) {
            PicnicPrivateKeyParameters var25 = (PicnicPrivateKeyParameters)var0;
            byte[] var49 = var25.getEncoded();
            AlgorithmIdentifier var69 = new AlgorithmIdentifier(Utils.picnicOidLookup(var25.getParameters()));
            return new PrivateKeyInfo(var69, new DEROctetString(var49), var1);
         }

         if (var0 instanceof CMCEPrivateKeyParameters) {
            CMCEPrivateKeyParameters var24 = (CMCEPrivateKeyParameters)var0;
            AlgorithmIdentifier var48 = new AlgorithmIdentifier(Utils.mcElieceOidLookup(var24.getParameters()));
            CMCEPublicKey var68 = new CMCEPublicKey(var24.reconstructPublicKey());
            CMCEPrivateKey var73 = new CMCEPrivateKey(0, var24.getDelta(), var24.getC(), var24.getG(), var24.getAlpha(), var24.getS(), var68);
            return new PrivateKeyInfo(var48, var73, var1);
         }

         if (var0 instanceof XMSSPrivateKeyParameters) {
            XMSSPrivateKeyParameters var23 = (XMSSPrivateKeyParameters)var0;
            AlgorithmIdentifier var47 = new AlgorithmIdentifier(
               PQCObjectIdentifiers.xmss, new XMSSKeyParams(var23.getParameters().getHeight(), Utils.xmssLookupTreeAlgID(var23.getTreeDigest()))
            );
            return new PrivateKeyInfo(var47, xmssCreateKeyStructure(var23), var1);
         }

         if (var0 instanceof XMSSMTPrivateKeyParameters) {
            XMSSMTPrivateKeyParameters var22 = (XMSSMTPrivateKeyParameters)var0;
            AlgorithmIdentifier var46 = new AlgorithmIdentifier(
               PQCObjectIdentifiers.xmss_mt,
               new XMSSMTKeyParams(var22.getParameters().getHeight(), var22.getParameters().getLayers(), Utils.xmssLookupTreeAlgID(var22.getTreeDigest()))
            );
            return new PrivateKeyInfo(var46, xmssmtCreateKeyStructure(var22), var1);
         }

         if (var0 instanceof FrodoPrivateKeyParameters) {
            FrodoPrivateKeyParameters var21 = (FrodoPrivateKeyParameters)var0;
            byte[] var45 = var21.getEncoded();
            AlgorithmIdentifier var67 = new AlgorithmIdentifier(Utils.frodoOidLookup(var21.getParameters()));
            return new PrivateKeyInfo(var67, new DEROctetString(var45), var1);
         }

         if (var0 instanceof SABERPrivateKeyParameters) {
            SABERPrivateKeyParameters var20 = (SABERPrivateKeyParameters)var0;
            byte[] var44 = var20.getEncoded();
            AlgorithmIdentifier var66 = new AlgorithmIdentifier(Utils.saberOidLookup(var20.getParameters()));
            return new PrivateKeyInfo(var66, new DEROctetString(var44), var1);
         }

         if (var0 instanceof NTRUPrivateKeyParameters) {
            NTRUPrivateKeyParameters var19 = (NTRUPrivateKeyParameters)var0;
            byte[] var43 = var19.getEncoded();
            AlgorithmIdentifier var65 = new AlgorithmIdentifier(Utils.ntruOidLookup(var19.getParameters()));
            return new PrivateKeyInfo(var65, new DEROctetString(var43), var1);
         }

         if (var0 instanceof FalconPrivateKeyParameters) {
            FalconPrivateKeyParameters var18 = (FalconPrivateKeyParameters)var0;
            AlgorithmIdentifier var42 = new AlgorithmIdentifier(Utils.falconOidLookup(var18.getParameters()));
            FalconPublicKey var64 = new FalconPublicKey(var18.getPublicKey());
            FalconPrivateKey var72 = new FalconPrivateKey(0, var18.getSpolyf(), var18.getG(), var18.getSpolyF(), var64);
            return new PrivateKeyInfo(var42, var72, var1);
         }

         if (var0 instanceof MLKEMPrivateKeyParameters) {
            MLKEMPrivateKeyParameters var17 = (MLKEMPrivateKeyParameters)var0;
            AlgorithmIdentifier var41 = new AlgorithmIdentifier(Utils.mlkemOidLookup(var17.getParameters()));
            if (var17.getPreferredFormat() == 1) {
               return new PrivateKeyInfo(var41, new DERTaggedObject(false, 0, new DEROctetString(var17.getSeed())), var1);
            } else {
               return var17.getPreferredFormat() == 2
                  ? new PrivateKeyInfo(var41, new DEROctetString(var17.getEncoded()), var1)
                  : new PrivateKeyInfo(var41, getBasicPQCEncoding(var17.getSeed(), var17.getEncoded()), var1);
            }
         } else {
            if (var0 instanceof NTRULPRimePrivateKeyParameters) {
               NTRULPRimePrivateKeyParameters var16 = (NTRULPRimePrivateKeyParameters)var0;
               ASN1EncodableVector var40 = new ASN1EncodableVector();
               var40.add(new DEROctetString(var16.getEnca()));
               var40.add(new DEROctetString(var16.getPk()));
               var40.add(new DEROctetString(var16.getRho()));
               var40.add(new DEROctetString(var16.getHash()));
               AlgorithmIdentifier var63 = new AlgorithmIdentifier(Utils.ntrulprimeOidLookup(var16.getParameters()));
               return new PrivateKeyInfo(var63, new DERSequence(var40), var1);
            }

            if (var0 instanceof SNTRUPrimePrivateKeyParameters) {
               SNTRUPrimePrivateKeyParameters var15 = (SNTRUPrimePrivateKeyParameters)var0;
               ASN1EncodableVector var39 = new ASN1EncodableVector();
               var39.add(new DEROctetString(var15.getF()));
               var39.add(new DEROctetString(var15.getGinv()));
               var39.add(new DEROctetString(var15.getPk()));
               var39.add(new DEROctetString(var15.getRho()));
               var39.add(new DEROctetString(var15.getHash()));
               AlgorithmIdentifier var62 = new AlgorithmIdentifier(Utils.sntruprimeOidLookup(var15.getParameters()));
               return new PrivateKeyInfo(var62, new DERSequence(var39), var1);
            }

            if (var0 instanceof MLDSAPrivateKeyParameters) {
               MLDSAPrivateKeyParameters var14 = (MLDSAPrivateKeyParameters)var0;
               AlgorithmIdentifier var38 = new AlgorithmIdentifier(Utils.mldsaOidLookup(var14.getParameters()));
               if (var14.getPreferredFormat() == 1) {
                  return new PrivateKeyInfo(var38, new DERTaggedObject(false, 0, new DEROctetString(var14.getSeed())), var1);
               } else {
                  return var14.getPreferredFormat() == 2
                     ? new PrivateKeyInfo(var38, new DEROctetString(var14.getEncoded()), var1)
                     : new PrivateKeyInfo(var38, getBasicPQCEncoding(var14.getSeed(), var14.getEncoded()), var1);
               }
            } else if (var0 instanceof DilithiumPrivateKeyParameters) {
               DilithiumPrivateKeyParameters var13 = (DilithiumPrivateKeyParameters)var0;
               AlgorithmIdentifier var37 = new AlgorithmIdentifier(Utils.dilithiumOidLookup(var13.getParameters()));
               DilithiumPublicKeyParameters var61 = var13.getPublicKeyParameters();
               return new PrivateKeyInfo(var37, new DEROctetString(var13.getEncoded()), var1, var61.getEncoded());
            } else if (var0 instanceof BIKEPrivateKeyParameters) {
               BIKEPrivateKeyParameters var12 = (BIKEPrivateKeyParameters)var0;
               AlgorithmIdentifier var36 = new AlgorithmIdentifier(Utils.bikeOidLookup(var12.getParameters()));
               byte[] var60 = var12.getEncoded();
               return new PrivateKeyInfo(var36, new DEROctetString(var60), var1);
            } else if (var0 instanceof HQCPrivateKeyParameters) {
               HQCPrivateKeyParameters var11 = (HQCPrivateKeyParameters)var0;
               AlgorithmIdentifier var35 = new AlgorithmIdentifier(Utils.hqcOidLookup(var11.getParameters()));
               byte[] var59 = var11.getEncoded();
               return new PrivateKeyInfo(var35, new DEROctetString(var59), var1);
            } else if (var0 instanceof RainbowPrivateKeyParameters) {
               RainbowPrivateKeyParameters var10 = (RainbowPrivateKeyParameters)var0;
               AlgorithmIdentifier var34 = new AlgorithmIdentifier(Utils.rainbowOidLookup(var10.getParameters()));
               byte[] var58 = var10.getEncoded();
               return new PrivateKeyInfo(var34, new DEROctetString(var58), var1);
            } else if (var0 instanceof MayoPrivateKeyParameters) {
               MayoPrivateKeyParameters var9 = (MayoPrivateKeyParameters)var0;
               AlgorithmIdentifier var33 = new AlgorithmIdentifier(Utils.mayoOidLookup(var9.getParameters()));
               byte[] var57 = var9.getEncoded();
               return new PrivateKeyInfo(var33, new DEROctetString(var57), var1);
            } else if (var0 instanceof SnovaPrivateKeyParameters) {
               SnovaPrivateKeyParameters var8 = (SnovaPrivateKeyParameters)var0;
               AlgorithmIdentifier var32 = new AlgorithmIdentifier(Utils.snovaOidLookup(var8.getParameters()));
               byte[] var56 = var8.getEncoded();
               return new PrivateKeyInfo(var32, new DEROctetString(var56), var1);
            } else if (var0 instanceof NTRUPlusPrivateKeyParameters) {
               NTRUPlusPrivateKeyParameters var7 = (NTRUPlusPrivateKeyParameters)var0;
               AlgorithmIdentifier var31 = new AlgorithmIdentifier(Utils.ntruPlusOidLookup(var7.getParameters()));
               byte[] var55 = var7.getEncoded();
               return new PrivateKeyInfo(var31, new DEROctetString(var55), var1);
            } else {
               throw new IOException("key parameters not recognized");
            }
         }
      } else {
         NHPrivateKeyParameters var2 = (NHPrivateKeyParameters)var0;
         AlgorithmIdentifier var3 = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
         short[] var4 = var2.getSecData();
         byte[] var5 = new byte[var4.length * 2];

         for (int var6 = 0; var6 != var4.length; var6++) {
            Pack.shortToLittleEndian(var4[var6], var5, var6 * 2);
         }

         return new PrivateKeyInfo(var3, new DEROctetString(var5));
      }
   }

   private static XMSSPrivateKey xmssCreateKeyStructure(XMSSPrivateKeyParameters var0) throws IOException {
      byte[] var1 = var0.getEncoded();
      int var2 = var0.getParameters().getTreeDigestSize();
      int var3 = var0.getParameters().getHeight();
      byte var4 = 4;
      int var5 = var2;
      int var6 = var2;
      int var7 = var2;
      int var8 = var2;
      int var9 = 0;
      int var10 = (int)XMSSUtil.bytesToXBigEndian(var1, var9, var4);
      if (!XMSSUtil.isIndexValid(var3, var10)) {
         throw new IllegalArgumentException("index out of bounds");
      }

      var9 += var4;
      byte[] var11 = XMSSUtil.extractBytesAtOffset(var1, var9, var5);
      var9 += var5;
      byte[] var12 = XMSSUtil.extractBytesAtOffset(var1, var9, var6);
      var9 += var6;
      byte[] var13 = XMSSUtil.extractBytesAtOffset(var1, var9, var7);
      var9 += var7;
      byte[] var14 = XMSSUtil.extractBytesAtOffset(var1, var9, var8);
      var9 += var8;
      byte[] var15 = XMSSUtil.extractBytesAtOffset(var1, var9, var1.length - var9);
      BDS var16 = null;

      try {
         var16 = (BDS)XMSSUtil.deserialize(var15, BDS.class);
      } catch (ClassNotFoundException var18) {
         throw new IOException("cannot parse BDS: " + var18.getMessage());
      }

      return var16.getMaxIndex() != (1 << var3) - 1
         ? new XMSSPrivateKey(var10, var11, var12, var13, var14, var15, var16.getMaxIndex())
         : new XMSSPrivateKey(var10, var11, var12, var13, var14, var15);
   }

   private static ASN1Sequence getBasicPQCEncoding(byte[] var0, byte[] var1) {
      return new DERSequence(new DEROctetString(var0), new DEROctetString(var1));
   }

   private static XMSSMTPrivateKey xmssmtCreateKeyStructure(XMSSMTPrivateKeyParameters var0) throws IOException {
      byte[] var1 = var0.getEncoded();
      int var2 = var0.getParameters().getTreeDigestSize();
      int var3 = var0.getParameters().getHeight();
      int var4 = (var3 + 7) / 8;
      int var5 = var2;
      int var6 = var2;
      int var7 = var2;
      int var8 = var2;
      int var9 = 0;
      int var10 = (int)XMSSUtil.bytesToXBigEndian(var1, var9, var4);
      if (!XMSSUtil.isIndexValid(var3, var10)) {
         throw new IllegalArgumentException("index out of bounds");
      }

      var9 += var4;
      byte[] var11 = XMSSUtil.extractBytesAtOffset(var1, var9, var5);
      var9 += var5;
      byte[] var12 = XMSSUtil.extractBytesAtOffset(var1, var9, var6);
      var9 += var6;
      byte[] var13 = XMSSUtil.extractBytesAtOffset(var1, var9, var7);
      var9 += var7;
      byte[] var14 = XMSSUtil.extractBytesAtOffset(var1, var9, var8);
      var9 += var8;
      byte[] var15 = XMSSUtil.extractBytesAtOffset(var1, var9, var1.length - var9);
      BDSStateMap var16 = null;

      try {
         var16 = (BDSStateMap)XMSSUtil.deserialize(var15, BDSStateMap.class);
      } catch (ClassNotFoundException var18) {
         throw new IOException("cannot parse BDSStateMap: " + var18.getMessage());
      }

      return var16.getMaxIndex() != (1L << var3) - 1L
         ? new XMSSMTPrivateKey(var10, var11, var12, var13, var14, var15, var16.getMaxIndex())
         : new XMSSMTPrivateKey(var10, var11, var12, var13, var14, var15);
   }
}
