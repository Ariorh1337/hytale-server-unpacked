package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.internal.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTPublicKey;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.crypto.cmce.CMCEPublicKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.Composer;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPublicKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.bouncycastle.pqc.legacy.bike.BIKEPublicKeyParameters;
import org.bouncycastle.pqc.legacy.picnic.PicnicPublicKeyParameters;
import org.bouncycastle.pqc.legacy.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.legacy.sphincsplus.SPHINCSPlusPublicKeyParameters;

public class SubjectPublicKeyInfoFactory {
   private SubjectPublicKeyInfoFactory() {
   }

   public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(AsymmetricKeyParameter var0) throws IOException {
      if (var0 instanceof SPHINCSPublicKeyParameters) {
         SPHINCSPublicKeyParameters var29 = (SPHINCSPublicKeyParameters)var0;
         AlgorithmIdentifier var53 = new AlgorithmIdentifier(
            PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(Utils.sphincs256LookupTreeAlgID(var29.getTreeDigest()))
         );
         return new SubjectPublicKeyInfo(var53, var29.getKeyData());
      }

      if (var0 instanceof NHPublicKeyParameters) {
         NHPublicKeyParameters var28 = (NHPublicKeyParameters)var0;
         AlgorithmIdentifier var52 = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
         return new SubjectPublicKeyInfo(var52, var28.getPubData());
      }

      if (var0 instanceof LMSPublicKeyParameters) {
         LMSPublicKeyParameters var27 = (LMSPublicKeyParameters)var0;
         byte[] var51 = Composer.compose().u32str(1).bytes(var27).build();
         AlgorithmIdentifier var72 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
         return new SubjectPublicKeyInfo(var72, var51);
      }

      if (var0 instanceof HSSPublicKeyParameters) {
         HSSPublicKeyParameters var26 = (HSSPublicKeyParameters)var0;
         byte[] var50 = Composer.compose().u32str(var26.getL()).bytes(var26.getLMSPublicKey()).build();
         AlgorithmIdentifier var71 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
         return new SubjectPublicKeyInfo(var71, var50);
      }

      if (var0 instanceof SLHDSAPublicKeyParameters) {
         SLHDSAPublicKeyParameters var25 = (SLHDSAPublicKeyParameters)var0;
         byte[] var49 = var25.getEncoded();
         AlgorithmIdentifier var70 = new AlgorithmIdentifier(Utils.slhdsaOidLookup(var25.getParameters()));
         return new SubjectPublicKeyInfo(var70, var49);
      }

      if (var0 instanceof SPHINCSPlusPublicKeyParameters) {
         SPHINCSPlusPublicKeyParameters var24 = (SPHINCSPlusPublicKeyParameters)var0;
         byte[] var48 = var24.getEncoded();
         AlgorithmIdentifier var69 = new AlgorithmIdentifier(Utils.sphincsPlusOidLookup(var24.getParameters()));
         return new SubjectPublicKeyInfo(var69, var48);
      }

      if (var0 instanceof CMCEPublicKeyParameters) {
         CMCEPublicKeyParameters var23 = (CMCEPublicKeyParameters)var0;
         byte[] var47 = var23.getEncoded();
         AlgorithmIdentifier var68 = new AlgorithmIdentifier(Utils.mcElieceOidLookup(var23.getParameters()));
         return new SubjectPublicKeyInfo(var68, var47);
      }

      if (var0 instanceof XMSSPublicKeyParameters) {
         XMSSPublicKeyParameters var22 = (XMSSPublicKeyParameters)var0;
         byte[] var46 = var22.getPublicSeed();
         byte[] var67 = var22.getRoot();
         byte[] var74 = var22.getEncoded();
         if (var74.length > var46.length + var67.length) {
            AlgorithmIdentifier var77 = new AlgorithmIdentifier(IsaraObjectIdentifiers.id_alg_xmss);
            return new SubjectPublicKeyInfo(var77, new DEROctetString(var74));
         } else {
            AlgorithmIdentifier var76 = new AlgorithmIdentifier(
               PQCObjectIdentifiers.xmss, new XMSSKeyParams(var22.getParameters().getHeight(), Utils.xmssLookupTreeAlgID(var22.getTreeDigest()))
            );
            return new SubjectPublicKeyInfo(var76, new XMSSPublicKey(var46, var67));
         }
      } else if (var0 instanceof XMSSMTPublicKeyParameters) {
         XMSSMTPublicKeyParameters var21 = (XMSSMTPublicKeyParameters)var0;
         byte[] var45 = var21.getPublicSeed();
         byte[] var66 = var21.getRoot();
         byte[] var73 = var21.getEncoded();
         if (var73.length > var45.length + var66.length) {
            AlgorithmIdentifier var75 = new AlgorithmIdentifier(IsaraObjectIdentifiers.id_alg_xmssmt);
            return new SubjectPublicKeyInfo(var75, new DEROctetString(var73));
         } else {
            AlgorithmIdentifier var5 = new AlgorithmIdentifier(
               PQCObjectIdentifiers.xmss_mt,
               new XMSSMTKeyParams(var21.getParameters().getHeight(), var21.getParameters().getLayers(), Utils.xmssLookupTreeAlgID(var21.getTreeDigest()))
            );
            return new SubjectPublicKeyInfo(var5, new XMSSMTPublicKey(var21.getPublicSeed(), var21.getRoot()));
         }
      } else if (var0 instanceof FrodoPublicKeyParameters) {
         FrodoPublicKeyParameters var20 = (FrodoPublicKeyParameters)var0;
         byte[] var44 = var20.getEncoded();
         AlgorithmIdentifier var65 = new AlgorithmIdentifier(Utils.frodoOidLookup(var20.getParameters()));
         return new SubjectPublicKeyInfo(var65, new DEROctetString(var44));
      } else if (var0 instanceof SABERPublicKeyParameters) {
         SABERPublicKeyParameters var19 = (SABERPublicKeyParameters)var0;
         byte[] var43 = var19.getEncoded();
         AlgorithmIdentifier var64 = new AlgorithmIdentifier(Utils.saberOidLookup(var19.getParameters()));
         return new SubjectPublicKeyInfo(var64, new DERSequence(new DEROctetString(var43)));
      } else if (var0 instanceof PicnicPublicKeyParameters) {
         PicnicPublicKeyParameters var18 = (PicnicPublicKeyParameters)var0;
         byte[] var42 = var18.getEncoded();
         AlgorithmIdentifier var63 = new AlgorithmIdentifier(Utils.picnicOidLookup(var18.getParameters()));
         return new SubjectPublicKeyInfo(var63, new DEROctetString(var42));
      } else if (var0 instanceof NTRUPublicKeyParameters) {
         NTRUPublicKeyParameters var17 = (NTRUPublicKeyParameters)var0;
         byte[] var41 = var17.getEncoded();
         AlgorithmIdentifier var62 = new AlgorithmIdentifier(Utils.ntruOidLookup(var17.getParameters()));
         return new SubjectPublicKeyInfo(var62, var41);
      } else if (var0 instanceof FalconPublicKeyParameters) {
         FalconPublicKeyParameters var16 = (FalconPublicKeyParameters)var0;
         byte[] var40 = var16.getH();
         AlgorithmIdentifier var61 = new AlgorithmIdentifier(Utils.falconOidLookup(var16.getParameters()));
         byte[] var4 = new byte[var40.length + 1];
         var4[0] = (byte)(0 + var16.getParameters().getLogN());
         System.arraycopy(var40, 0, var4, 1, var40.length);
         return new SubjectPublicKeyInfo(var61, var4);
      } else if (var0 instanceof MLKEMPublicKeyParameters) {
         MLKEMPublicKeyParameters var15 = (MLKEMPublicKeyParameters)var0;
         AlgorithmIdentifier var39 = new AlgorithmIdentifier(Utils.mlkemOidLookup(var15.getParameters()));
         return new SubjectPublicKeyInfo(var39, var15.getEncoded());
      } else if (var0 instanceof NTRULPRimePublicKeyParameters) {
         NTRULPRimePublicKeyParameters var14 = (NTRULPRimePublicKeyParameters)var0;
         byte[] var38 = var14.getEncoded();
         AlgorithmIdentifier var60 = new AlgorithmIdentifier(Utils.ntrulprimeOidLookup(var14.getParameters()));
         return new SubjectPublicKeyInfo(var60, new DEROctetString(var38));
      } else if (var0 instanceof SNTRUPrimePublicKeyParameters) {
         SNTRUPrimePublicKeyParameters var13 = (SNTRUPrimePublicKeyParameters)var0;
         byte[] var37 = var13.getEncoded();
         AlgorithmIdentifier var59 = new AlgorithmIdentifier(Utils.sntruprimeOidLookup(var13.getParameters()));
         return new SubjectPublicKeyInfo(var59, new DEROctetString(var37));
      } else if (var0 instanceof DilithiumPublicKeyParameters) {
         DilithiumPublicKeyParameters var12 = (DilithiumPublicKeyParameters)var0;
         AlgorithmIdentifier var36 = new AlgorithmIdentifier(Utils.dilithiumOidLookup(var12.getParameters()));
         return new SubjectPublicKeyInfo(var36, var12.getEncoded());
      } else if (var0 instanceof MLDSAPublicKeyParameters) {
         MLDSAPublicKeyParameters var11 = (MLDSAPublicKeyParameters)var0;
         AlgorithmIdentifier var35 = new AlgorithmIdentifier(Utils.mldsaOidLookup(var11.getParameters()));
         return new SubjectPublicKeyInfo(var35, var11.getEncoded());
      } else if (var0 instanceof BIKEPublicKeyParameters) {
         BIKEPublicKeyParameters var10 = (BIKEPublicKeyParameters)var0;
         byte[] var34 = var10.getEncoded();
         AlgorithmIdentifier var58 = new AlgorithmIdentifier(Utils.bikeOidLookup(var10.getParameters()));
         return new SubjectPublicKeyInfo(var58, var34);
      } else if (var0 instanceof HQCPublicKeyParameters) {
         HQCPublicKeyParameters var9 = (HQCPublicKeyParameters)var0;
         byte[] var33 = var9.getEncoded();
         AlgorithmIdentifier var57 = new AlgorithmIdentifier(Utils.hqcOidLookup(var9.getParameters()));
         return new SubjectPublicKeyInfo(var57, var33);
      } else if (var0 instanceof RainbowPublicKeyParameters) {
         RainbowPublicKeyParameters var8 = (RainbowPublicKeyParameters)var0;
         byte[] var32 = var8.getEncoded();
         AlgorithmIdentifier var56 = new AlgorithmIdentifier(Utils.rainbowOidLookup(var8.getParameters()));
         return new SubjectPublicKeyInfo(var56, new DEROctetString(var32));
      } else if (var0 instanceof MayoPublicKeyParameters) {
         MayoPublicKeyParameters var7 = (MayoPublicKeyParameters)var0;
         byte[] var31 = var7.getEncoded();
         AlgorithmIdentifier var55 = new AlgorithmIdentifier(Utils.mayoOidLookup(var7.getParameters()));
         return new SubjectPublicKeyInfo(var55, new DEROctetString(var31));
      } else if (var0 instanceof SnovaPublicKeyParameters) {
         SnovaPublicKeyParameters var6 = (SnovaPublicKeyParameters)var0;
         byte[] var30 = var6.getEncoded();
         AlgorithmIdentifier var54 = new AlgorithmIdentifier(Utils.snovaOidLookup(var6.getParameters()));
         return new SubjectPublicKeyInfo(var54, new DEROctetString(var30));
      } else if (var0 instanceof NTRUPlusPublicKeyParameters) {
         NTRUPlusPublicKeyParameters var1 = (NTRUPlusPublicKeyParameters)var0;
         byte[] var2 = var1.getEncoded();
         AlgorithmIdentifier var3 = new AlgorithmIdentifier(Utils.ntruPlusOidLookup(var1.getParameters()));
         return new SubjectPublicKeyInfo(var3, new DEROctetString(var2));
      } else {
         throw new IOException("key parameters not recognized");
      }
   }
}
