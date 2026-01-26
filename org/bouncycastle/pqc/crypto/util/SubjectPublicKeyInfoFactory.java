/*
 * Decompiled with CFR 0.152.
 */
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
import org.bouncycastle.pqc.crypto.bike.BIKEPublicKeyParameters;
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
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPublicKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.Utils;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;

public class SubjectPublicKeyInfoFactory {
    private SubjectPublicKeyInfoFactory() {
    }

    public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        if (asymmetricKeyParameter instanceof SPHINCSPublicKeyParameters) {
            SPHINCSPublicKeyParameters sPHINCSPublicKeyParameters = (SPHINCSPublicKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(Utils.sphincs256LookupTreeAlgID(sPHINCSPublicKeyParameters.getTreeDigest())));
            return new SubjectPublicKeyInfo(algorithmIdentifier, sPHINCSPublicKeyParameters.getKeyData());
        }
        if (asymmetricKeyParameter instanceof NHPublicKeyParameters) {
            NHPublicKeyParameters nHPublicKeyParameters = (NHPublicKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
            return new SubjectPublicKeyInfo(algorithmIdentifier, nHPublicKeyParameters.getPubData());
        }
        if (asymmetricKeyParameter instanceof LMSPublicKeyParameters) {
            LMSPublicKeyParameters lMSPublicKeyParameters = (LMSPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = Composer.compose().u32str(1).bytes(lMSPublicKeyParameters).build();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
            return new SubjectPublicKeyInfo(algorithmIdentifier, byArray);
        }
        if (asymmetricKeyParameter instanceof HSSPublicKeyParameters) {
            HSSPublicKeyParameters hSSPublicKeyParameters = (HSSPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = Composer.compose().u32str(hSSPublicKeyParameters.getL()).bytes(hSSPublicKeyParameters.getLMSPublicKey()).build();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
            return new SubjectPublicKeyInfo(algorithmIdentifier, byArray);
        }
        if (asymmetricKeyParameter instanceof SLHDSAPublicKeyParameters) {
            SLHDSAPublicKeyParameters sLHDSAPublicKeyParameters = (SLHDSAPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = sLHDSAPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.slhdsaOidLookup(sLHDSAPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, byArray);
        }
        if (asymmetricKeyParameter instanceof SPHINCSPlusPublicKeyParameters) {
            SPHINCSPlusPublicKeyParameters sPHINCSPlusPublicKeyParameters = (SPHINCSPlusPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = sPHINCSPlusPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.sphincsPlusOidLookup(sPHINCSPlusPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, byArray);
        }
        if (asymmetricKeyParameter instanceof CMCEPublicKeyParameters) {
            CMCEPublicKeyParameters cMCEPublicKeyParameters = (CMCEPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = cMCEPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.mcElieceOidLookup(cMCEPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, byArray);
        }
        if (asymmetricKeyParameter instanceof XMSSPublicKeyParameters) {
            XMSSPublicKeyParameters xMSSPublicKeyParameters = (XMSSPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = xMSSPublicKeyParameters.getPublicSeed();
            byte[] byArray2 = xMSSPublicKeyParameters.getRoot();
            byte[] byArray3 = xMSSPublicKeyParameters.getEncoded();
            if (byArray3.length > byArray.length + byArray2.length) {
                AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(IsaraObjectIdentifiers.id_alg_xmss);
                return new SubjectPublicKeyInfo(algorithmIdentifier, new DEROctetString(byArray3));
            }
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss, new XMSSKeyParams(xMSSPublicKeyParameters.getParameters().getHeight(), Utils.xmssLookupTreeAlgID(xMSSPublicKeyParameters.getTreeDigest())));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new XMSSPublicKey(byArray, byArray2));
        }
        if (asymmetricKeyParameter instanceof XMSSMTPublicKeyParameters) {
            XMSSMTPublicKeyParameters xMSSMTPublicKeyParameters = (XMSSMTPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = xMSSMTPublicKeyParameters.getPublicSeed();
            byte[] byArray4 = xMSSMTPublicKeyParameters.getRoot();
            byte[] byArray5 = xMSSMTPublicKeyParameters.getEncoded();
            if (byArray5.length > byArray.length + byArray4.length) {
                AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(IsaraObjectIdentifiers.id_alg_xmssmt);
                return new SubjectPublicKeyInfo(algorithmIdentifier, new DEROctetString(byArray5));
            }
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyParams(xMSSMTPublicKeyParameters.getParameters().getHeight(), xMSSMTPublicKeyParameters.getParameters().getLayers(), Utils.xmssLookupTreeAlgID(xMSSMTPublicKeyParameters.getTreeDigest())));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new XMSSMTPublicKey(xMSSMTPublicKeyParameters.getPublicSeed(), xMSSMTPublicKeyParameters.getRoot()));
        }
        if (asymmetricKeyParameter instanceof FrodoPublicKeyParameters) {
            FrodoPublicKeyParameters frodoPublicKeyParameters = (FrodoPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = frodoPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.frodoOidLookup(frodoPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new DEROctetString(byArray));
        }
        if (asymmetricKeyParameter instanceof SABERPublicKeyParameters) {
            SABERPublicKeyParameters sABERPublicKeyParameters = (SABERPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = sABERPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.saberOidLookup(sABERPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new DERSequence(new DEROctetString(byArray)));
        }
        if (asymmetricKeyParameter instanceof PicnicPublicKeyParameters) {
            PicnicPublicKeyParameters picnicPublicKeyParameters = (PicnicPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = picnicPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.picnicOidLookup(picnicPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new DEROctetString(byArray));
        }
        if (asymmetricKeyParameter instanceof NTRUPublicKeyParameters) {
            NTRUPublicKeyParameters nTRUPublicKeyParameters = (NTRUPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = nTRUPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.ntruOidLookup(nTRUPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, byArray);
        }
        if (asymmetricKeyParameter instanceof FalconPublicKeyParameters) {
            FalconPublicKeyParameters falconPublicKeyParameters = (FalconPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = falconPublicKeyParameters.getH();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.falconOidLookup(falconPublicKeyParameters.getParameters()));
            byte[] byArray6 = new byte[byArray.length + 1];
            byArray6[0] = (byte)(0 + falconPublicKeyParameters.getParameters().getLogN());
            System.arraycopy(byArray, 0, byArray6, 1, byArray.length);
            return new SubjectPublicKeyInfo(algorithmIdentifier, byArray6);
        }
        if (asymmetricKeyParameter instanceof MLKEMPublicKeyParameters) {
            MLKEMPublicKeyParameters mLKEMPublicKeyParameters = (MLKEMPublicKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.mlkemOidLookup(mLKEMPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, mLKEMPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof NTRULPRimePublicKeyParameters) {
            NTRULPRimePublicKeyParameters nTRULPRimePublicKeyParameters = (NTRULPRimePublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = nTRULPRimePublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.ntrulprimeOidLookup(nTRULPRimePublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new DEROctetString(byArray));
        }
        if (asymmetricKeyParameter instanceof SNTRUPrimePublicKeyParameters) {
            SNTRUPrimePublicKeyParameters sNTRUPrimePublicKeyParameters = (SNTRUPrimePublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = sNTRUPrimePublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.sntruprimeOidLookup(sNTRUPrimePublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new DEROctetString(byArray));
        }
        if (asymmetricKeyParameter instanceof DilithiumPublicKeyParameters) {
            DilithiumPublicKeyParameters dilithiumPublicKeyParameters = (DilithiumPublicKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.dilithiumOidLookup(dilithiumPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, dilithiumPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof MLDSAPublicKeyParameters) {
            MLDSAPublicKeyParameters mLDSAPublicKeyParameters = (MLDSAPublicKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.mldsaOidLookup(mLDSAPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, mLDSAPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof BIKEPublicKeyParameters) {
            BIKEPublicKeyParameters bIKEPublicKeyParameters = (BIKEPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = bIKEPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.bikeOidLookup(bIKEPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, byArray);
        }
        if (asymmetricKeyParameter instanceof HQCPublicKeyParameters) {
            HQCPublicKeyParameters hQCPublicKeyParameters = (HQCPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = hQCPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.hqcOidLookup(hQCPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, byArray);
        }
        if (asymmetricKeyParameter instanceof RainbowPublicKeyParameters) {
            RainbowPublicKeyParameters rainbowPublicKeyParameters = (RainbowPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = rainbowPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.rainbowOidLookup(rainbowPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new DEROctetString(byArray));
        }
        if (asymmetricKeyParameter instanceof MayoPublicKeyParameters) {
            MayoPublicKeyParameters mayoPublicKeyParameters = (MayoPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = mayoPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.mayoOidLookup(mayoPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new DEROctetString(byArray));
        }
        if (asymmetricKeyParameter instanceof SnovaPublicKeyParameters) {
            SnovaPublicKeyParameters snovaPublicKeyParameters = (SnovaPublicKeyParameters)asymmetricKeyParameter;
            byte[] byArray = snovaPublicKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.snovaOidLookup(snovaPublicKeyParameters.getParameters()));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new DEROctetString(byArray));
        }
        throw new IOException("key parameters not recognized");
    }
}

