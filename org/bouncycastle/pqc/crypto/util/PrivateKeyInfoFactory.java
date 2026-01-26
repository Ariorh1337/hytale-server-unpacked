/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
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
import org.bouncycastle.pqc.crypto.bike.BIKEPrivateKeyParameters;
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
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.Utils;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.util.Pack;

public class PrivateKeyInfoFactory {
    private PrivateKeyInfoFactory() {
    }

    public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        return PrivateKeyInfoFactory.createPrivateKeyInfo(asymmetricKeyParameter, null);
    }

    public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter asymmetricKeyParameter, ASN1Set aSN1Set) throws IOException {
        if (asymmetricKeyParameter instanceof SPHINCSPrivateKeyParameters) {
            SPHINCSPrivateKeyParameters sPHINCSPrivateKeyParameters = (SPHINCSPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(Utils.sphincs256LookupTreeAlgID(sPHINCSPrivateKeyParameters.getTreeDigest())));
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(sPHINCSPrivateKeyParameters.getKeyData()));
        }
        if (asymmetricKeyParameter instanceof NHPrivateKeyParameters) {
            NHPrivateKeyParameters nHPrivateKeyParameters = (NHPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
            short[] sArray = nHPrivateKeyParameters.getSecData();
            byte[] byArray = new byte[sArray.length * 2];
            for (int i = 0; i != sArray.length; ++i) {
                Pack.shortToLittleEndian(sArray[i], byArray, i * 2);
            }
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray));
        }
        if (asymmetricKeyParameter instanceof LMSPrivateKeyParameters) {
            LMSPrivateKeyParameters lMSPrivateKeyParameters = (LMSPrivateKeyParameters)asymmetricKeyParameter;
            byte[] byArray = Composer.compose().u32str(1).bytes(lMSPrivateKeyParameters).build();
            byte[] byArray2 = Composer.compose().u32str(1).bytes(lMSPrivateKeyParameters.getPublicKey()).build();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray), aSN1Set, byArray2);
        }
        if (asymmetricKeyParameter instanceof HSSPrivateKeyParameters) {
            HSSPrivateKeyParameters hSSPrivateKeyParameters = (HSSPrivateKeyParameters)asymmetricKeyParameter;
            byte[] byArray = Composer.compose().u32str(hSSPrivateKeyParameters.getL()).bytes(hSSPrivateKeyParameters).build();
            byte[] byArray3 = Composer.compose().u32str(hSSPrivateKeyParameters.getL()).bytes(hSSPrivateKeyParameters.getPublicKey().getLMSPublicKey()).build();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray), aSN1Set, byArray3);
        }
        if (asymmetricKeyParameter instanceof SPHINCSPlusPrivateKeyParameters) {
            SPHINCSPlusPrivateKeyParameters sPHINCSPlusPrivateKeyParameters = (SPHINCSPlusPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.sphincsPlusOidLookup(sPHINCSPlusPrivateKeyParameters.getParameters()));
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(sPHINCSPlusPrivateKeyParameters.getEncoded()), aSN1Set, sPHINCSPlusPrivateKeyParameters.getPublicKey());
        }
        if (asymmetricKeyParameter instanceof SLHDSAPrivateKeyParameters) {
            SLHDSAPrivateKeyParameters sLHDSAPrivateKeyParameters = (SLHDSAPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.slhdsaOidLookup(sLHDSAPrivateKeyParameters.getParameters()));
            return new PrivateKeyInfo(algorithmIdentifier, sLHDSAPrivateKeyParameters.getEncoded(), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof PicnicPrivateKeyParameters) {
            PicnicPrivateKeyParameters picnicPrivateKeyParameters = (PicnicPrivateKeyParameters)asymmetricKeyParameter;
            byte[] byArray = picnicPrivateKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.picnicOidLookup(picnicPrivateKeyParameters.getParameters()));
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof CMCEPrivateKeyParameters) {
            CMCEPrivateKeyParameters cMCEPrivateKeyParameters = (CMCEPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.mcElieceOidLookup(cMCEPrivateKeyParameters.getParameters()));
            CMCEPublicKey cMCEPublicKey = new CMCEPublicKey(cMCEPrivateKeyParameters.reconstructPublicKey());
            CMCEPrivateKey cMCEPrivateKey = new CMCEPrivateKey(0, cMCEPrivateKeyParameters.getDelta(), cMCEPrivateKeyParameters.getC(), cMCEPrivateKeyParameters.getG(), cMCEPrivateKeyParameters.getAlpha(), cMCEPrivateKeyParameters.getS(), cMCEPublicKey);
            return new PrivateKeyInfo(algorithmIdentifier, cMCEPrivateKey, aSN1Set);
        }
        if (asymmetricKeyParameter instanceof XMSSPrivateKeyParameters) {
            XMSSPrivateKeyParameters xMSSPrivateKeyParameters = (XMSSPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss, new XMSSKeyParams(xMSSPrivateKeyParameters.getParameters().getHeight(), Utils.xmssLookupTreeAlgID(xMSSPrivateKeyParameters.getTreeDigest())));
            return new PrivateKeyInfo(algorithmIdentifier, PrivateKeyInfoFactory.xmssCreateKeyStructure(xMSSPrivateKeyParameters), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof XMSSMTPrivateKeyParameters) {
            XMSSMTPrivateKeyParameters xMSSMTPrivateKeyParameters = (XMSSMTPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyParams(xMSSMTPrivateKeyParameters.getParameters().getHeight(), xMSSMTPrivateKeyParameters.getParameters().getLayers(), Utils.xmssLookupTreeAlgID(xMSSMTPrivateKeyParameters.getTreeDigest())));
            return new PrivateKeyInfo(algorithmIdentifier, PrivateKeyInfoFactory.xmssmtCreateKeyStructure(xMSSMTPrivateKeyParameters), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof FrodoPrivateKeyParameters) {
            FrodoPrivateKeyParameters frodoPrivateKeyParameters = (FrodoPrivateKeyParameters)asymmetricKeyParameter;
            byte[] byArray = frodoPrivateKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.frodoOidLookup(frodoPrivateKeyParameters.getParameters()));
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof SABERPrivateKeyParameters) {
            SABERPrivateKeyParameters sABERPrivateKeyParameters = (SABERPrivateKeyParameters)asymmetricKeyParameter;
            byte[] byArray = sABERPrivateKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.saberOidLookup(sABERPrivateKeyParameters.getParameters()));
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof NTRUPrivateKeyParameters) {
            NTRUPrivateKeyParameters nTRUPrivateKeyParameters = (NTRUPrivateKeyParameters)asymmetricKeyParameter;
            byte[] byArray = nTRUPrivateKeyParameters.getEncoded();
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.ntruOidLookup(nTRUPrivateKeyParameters.getParameters()));
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof FalconPrivateKeyParameters) {
            FalconPrivateKeyParameters falconPrivateKeyParameters = (FalconPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.falconOidLookup(falconPrivateKeyParameters.getParameters()));
            FalconPublicKey falconPublicKey = new FalconPublicKey(falconPrivateKeyParameters.getPublicKey());
            FalconPrivateKey falconPrivateKey = new FalconPrivateKey(0, falconPrivateKeyParameters.getSpolyf(), falconPrivateKeyParameters.getG(), falconPrivateKeyParameters.getSpolyF(), falconPublicKey);
            return new PrivateKeyInfo(algorithmIdentifier, falconPrivateKey, aSN1Set);
        }
        if (asymmetricKeyParameter instanceof MLKEMPrivateKeyParameters) {
            MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters = (MLKEMPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.mlkemOidLookup(mLKEMPrivateKeyParameters.getParameters()));
            if (mLKEMPrivateKeyParameters.getPreferredFormat() == 1) {
                return new PrivateKeyInfo(algorithmIdentifier, new DERTaggedObject(false, 0, (ASN1Encodable)new DEROctetString(mLKEMPrivateKeyParameters.getSeed())), aSN1Set);
            }
            if (mLKEMPrivateKeyParameters.getPreferredFormat() == 2) {
                return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(mLKEMPrivateKeyParameters.getEncoded()), aSN1Set);
            }
            return new PrivateKeyInfo(algorithmIdentifier, PrivateKeyInfoFactory.getBasicPQCEncoding(mLKEMPrivateKeyParameters.getSeed(), mLKEMPrivateKeyParameters.getEncoded()), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof NTRULPRimePrivateKeyParameters) {
            NTRULPRimePrivateKeyParameters nTRULPRimePrivateKeyParameters = (NTRULPRimePrivateKeyParameters)asymmetricKeyParameter;
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            aSN1EncodableVector.add(new DEROctetString(nTRULPRimePrivateKeyParameters.getEnca()));
            aSN1EncodableVector.add(new DEROctetString(nTRULPRimePrivateKeyParameters.getPk()));
            aSN1EncodableVector.add(new DEROctetString(nTRULPRimePrivateKeyParameters.getRho()));
            aSN1EncodableVector.add(new DEROctetString(nTRULPRimePrivateKeyParameters.getHash()));
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.ntrulprimeOidLookup(nTRULPRimePrivateKeyParameters.getParameters()));
            return new PrivateKeyInfo(algorithmIdentifier, new DERSequence(aSN1EncodableVector), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof SNTRUPrimePrivateKeyParameters) {
            SNTRUPrimePrivateKeyParameters sNTRUPrimePrivateKeyParameters = (SNTRUPrimePrivateKeyParameters)asymmetricKeyParameter;
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            aSN1EncodableVector.add(new DEROctetString(sNTRUPrimePrivateKeyParameters.getF()));
            aSN1EncodableVector.add(new DEROctetString(sNTRUPrimePrivateKeyParameters.getGinv()));
            aSN1EncodableVector.add(new DEROctetString(sNTRUPrimePrivateKeyParameters.getPk()));
            aSN1EncodableVector.add(new DEROctetString(sNTRUPrimePrivateKeyParameters.getRho()));
            aSN1EncodableVector.add(new DEROctetString(sNTRUPrimePrivateKeyParameters.getHash()));
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.sntruprimeOidLookup(sNTRUPrimePrivateKeyParameters.getParameters()));
            return new PrivateKeyInfo(algorithmIdentifier, new DERSequence(aSN1EncodableVector), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof MLDSAPrivateKeyParameters) {
            MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters = (MLDSAPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.mldsaOidLookup(mLDSAPrivateKeyParameters.getParameters()));
            if (mLDSAPrivateKeyParameters.getPreferredFormat() == 1) {
                return new PrivateKeyInfo(algorithmIdentifier, new DERTaggedObject(false, 0, (ASN1Encodable)new DEROctetString(mLDSAPrivateKeyParameters.getSeed())), aSN1Set);
            }
            if (mLDSAPrivateKeyParameters.getPreferredFormat() == 2) {
                return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(mLDSAPrivateKeyParameters.getEncoded()), aSN1Set);
            }
            return new PrivateKeyInfo(algorithmIdentifier, PrivateKeyInfoFactory.getBasicPQCEncoding(mLDSAPrivateKeyParameters.getSeed(), mLDSAPrivateKeyParameters.getEncoded()), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof DilithiumPrivateKeyParameters) {
            DilithiumPrivateKeyParameters dilithiumPrivateKeyParameters = (DilithiumPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.dilithiumOidLookup(dilithiumPrivateKeyParameters.getParameters()));
            DilithiumPublicKeyParameters dilithiumPublicKeyParameters = dilithiumPrivateKeyParameters.getPublicKeyParameters();
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(dilithiumPrivateKeyParameters.getEncoded()), aSN1Set, dilithiumPublicKeyParameters.getEncoded());
        }
        if (asymmetricKeyParameter instanceof BIKEPrivateKeyParameters) {
            BIKEPrivateKeyParameters bIKEPrivateKeyParameters = (BIKEPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.bikeOidLookup(bIKEPrivateKeyParameters.getParameters()));
            byte[] byArray = bIKEPrivateKeyParameters.getEncoded();
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof HQCPrivateKeyParameters) {
            HQCPrivateKeyParameters hQCPrivateKeyParameters = (HQCPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.hqcOidLookup(hQCPrivateKeyParameters.getParameters()));
            byte[] byArray = hQCPrivateKeyParameters.getEncoded();
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof RainbowPrivateKeyParameters) {
            RainbowPrivateKeyParameters rainbowPrivateKeyParameters = (RainbowPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.rainbowOidLookup(rainbowPrivateKeyParameters.getParameters()));
            byte[] byArray = rainbowPrivateKeyParameters.getEncoded();
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof MayoPrivateKeyParameters) {
            MayoPrivateKeyParameters mayoPrivateKeyParameters = (MayoPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.mayoOidLookup(mayoPrivateKeyParameters.getParameters()));
            byte[] byArray = mayoPrivateKeyParameters.getEncoded();
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray), aSN1Set);
        }
        if (asymmetricKeyParameter instanceof SnovaPrivateKeyParameters) {
            SnovaPrivateKeyParameters snovaPrivateKeyParameters = (SnovaPrivateKeyParameters)asymmetricKeyParameter;
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(Utils.snovaOidLookup(snovaPrivateKeyParameters.getParameters()));
            byte[] byArray = snovaPrivateKeyParameters.getEncoded();
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(byArray), aSN1Set);
        }
        throw new IOException("key parameters not recognized");
    }

    private static XMSSPrivateKey xmssCreateKeyStructure(XMSSPrivateKeyParameters xMSSPrivateKeyParameters) throws IOException {
        byte[] byArray = xMSSPrivateKeyParameters.getEncoded();
        int n = xMSSPrivateKeyParameters.getParameters().getTreeDigestSize();
        int n2 = xMSSPrivateKeyParameters.getParameters().getHeight();
        int n3 = 4;
        int n4 = n;
        int n5 = n;
        int n6 = n;
        int n7 = n;
        int n8 = 0;
        int n9 = (int)XMSSUtil.bytesToXBigEndian(byArray, n8, n3);
        if (!XMSSUtil.isIndexValid(n2, n9)) {
            throw new IllegalArgumentException("index out of bounds");
        }
        byte[] byArray2 = XMSSUtil.extractBytesAtOffset(byArray, n8 += n3, n4);
        byte[] byArray3 = XMSSUtil.extractBytesAtOffset(byArray, n8 += n4, n5);
        byte[] byArray4 = XMSSUtil.extractBytesAtOffset(byArray, n8 += n5, n6);
        byte[] byArray5 = XMSSUtil.extractBytesAtOffset(byArray, n8 += n6, n7);
        byte[] byArray6 = XMSSUtil.extractBytesAtOffset(byArray, n8 += n7, byArray.length - n8);
        BDS bDS = null;
        try {
            bDS = (BDS)XMSSUtil.deserialize(byArray6, BDS.class);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new IOException("cannot parse BDS: " + classNotFoundException.getMessage());
        }
        if (bDS.getMaxIndex() != (1 << n2) - 1) {
            return new XMSSPrivateKey(n9, byArray2, byArray3, byArray4, byArray5, byArray6, bDS.getMaxIndex());
        }
        return new XMSSPrivateKey(n9, byArray2, byArray3, byArray4, byArray5, byArray6);
    }

    private static ASN1Sequence getBasicPQCEncoding(byte[] byArray, byte[] byArray2) {
        return new DERSequence(new DEROctetString(byArray), new DEROctetString(byArray2));
    }

    private static XMSSMTPrivateKey xmssmtCreateKeyStructure(XMSSMTPrivateKeyParameters xMSSMTPrivateKeyParameters) throws IOException {
        byte[] byArray = xMSSMTPrivateKeyParameters.getEncoded();
        int n = xMSSMTPrivateKeyParameters.getParameters().getTreeDigestSize();
        int n2 = xMSSMTPrivateKeyParameters.getParameters().getHeight();
        int n3 = (n2 + 7) / 8;
        int n4 = n;
        int n5 = n;
        int n6 = n;
        int n7 = n;
        int n8 = 0;
        int n9 = (int)XMSSUtil.bytesToXBigEndian(byArray, n8, n3);
        if (!XMSSUtil.isIndexValid(n2, n9)) {
            throw new IllegalArgumentException("index out of bounds");
        }
        byte[] byArray2 = XMSSUtil.extractBytesAtOffset(byArray, n8 += n3, n4);
        byte[] byArray3 = XMSSUtil.extractBytesAtOffset(byArray, n8 += n4, n5);
        byte[] byArray4 = XMSSUtil.extractBytesAtOffset(byArray, n8 += n5, n6);
        byte[] byArray5 = XMSSUtil.extractBytesAtOffset(byArray, n8 += n6, n7);
        byte[] byArray6 = XMSSUtil.extractBytesAtOffset(byArray, n8 += n7, byArray.length - n8);
        BDSStateMap bDSStateMap = null;
        try {
            bDSStateMap = (BDSStateMap)XMSSUtil.deserialize(byArray6, BDSStateMap.class);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new IOException("cannot parse BDSStateMap: " + classNotFoundException.getMessage());
        }
        if (bDSStateMap.getMaxIndex() != (1L << n2) - 1L) {
            return new XMSSMTPrivateKey(n9, byArray2, byArray3, byArray4, byArray5, byArray6, bDSStateMap.getMaxIndex());
        }
        return new XMSSMTPrivateKey(n9, byArray2, byArray3, byArray4, byArray5, byArray6);
    }
}

