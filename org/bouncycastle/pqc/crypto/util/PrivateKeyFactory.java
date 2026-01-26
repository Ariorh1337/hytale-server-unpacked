/*
 * Decompiled with CFR 0.152.
 */
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
import org.bouncycastle.pqc.crypto.bike.BIKEParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEPrivateKeyParameters;
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
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.Utils;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class PrivateKeyFactory {
    public static AsymmetricKeyParameter createKey(byte[] byArray) throws IOException {
        if (byArray == null) {
            throw new IllegalArgumentException("privateKeyInfoData array null");
        }
        if (byArray.length == 0) {
            throw new IllegalArgumentException("privateKeyInfoData array empty");
        }
        return PrivateKeyFactory.createKey(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(byArray)));
    }

    public static AsymmetricKeyParameter createKey(InputStream inputStream) throws IOException {
        return PrivateKeyFactory.createKey(PrivateKeyInfo.getInstance(new ASN1InputStream(inputStream).readObject()));
    }

    public static AsymmetricKeyParameter createKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        if (privateKeyInfo == null) {
            throw new IllegalArgumentException("keyInfo array null");
        }
        AlgorithmIdentifier algorithmIdentifier = privateKeyInfo.getPrivateKeyAlgorithm();
        ASN1ObjectIdentifier aSN1ObjectIdentifier = algorithmIdentifier.getAlgorithm();
        if (aSN1ObjectIdentifier.equals(PQCObjectIdentifiers.sphincs256)) {
            return new SPHINCSPrivateKeyParameters(ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets(), Utils.sphincs256LookupTreeAlgName(SPHINCS256KeyParams.getInstance(algorithmIdentifier.getParameters())));
        }
        if (aSN1ObjectIdentifier.equals(PQCObjectIdentifiers.newHope)) {
            return new NHPrivateKeyParameters(PrivateKeyFactory.convert(ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets()));
        }
        if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig)) {
            ASN1OctetString aSN1OctetString = PrivateKeyFactory.parseOctetString(privateKeyInfo.getPrivateKey(), 64);
            byte[] byArray = aSN1OctetString.getOctets();
            ASN1BitString aSN1BitString = privateKeyInfo.getPublicKeyData();
            if (aSN1BitString != null) {
                byte[] byArray2 = aSN1BitString.getOctets();
                return HSSPrivateKeyParameters.getInstance(Arrays.copyOfRange(byArray, 4, byArray.length), byArray2);
            }
            return HSSPrivateKeyParameters.getInstance(Arrays.copyOfRange(byArray, 4, byArray.length));
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.sphincsPlus) || aSN1ObjectIdentifier.on(BCObjectIdentifiers.sphincsPlus_interop)) {
            SPHINCSPlusParameters sPHINCSPlusParameters = Utils.sphincsPlusParamsLookup(aSN1ObjectIdentifier);
            ASN1Encodable aSN1Encodable = privateKeyInfo.parsePrivateKey();
            if (aSN1Encodable instanceof ASN1Sequence) {
                SPHINCSPLUSPrivateKey sPHINCSPLUSPrivateKey = SPHINCSPLUSPrivateKey.getInstance(aSN1Encodable);
                SPHINCSPLUSPublicKey sPHINCSPLUSPublicKey = sPHINCSPLUSPrivateKey.getPublicKey();
                return new SPHINCSPlusPrivateKeyParameters(sPHINCSPlusParameters, sPHINCSPLUSPrivateKey.getSkseed(), sPHINCSPLUSPrivateKey.getSkprf(), sPHINCSPLUSPublicKey.getPkseed(), sPHINCSPLUSPublicKey.getPkroot());
            }
            return new SPHINCSPlusPrivateKeyParameters(sPHINCSPlusParameters, ASN1OctetString.getInstance(aSN1Encodable).getOctets());
        }
        if (Utils.slhdsaParams.containsKey(aSN1ObjectIdentifier)) {
            SLHDSAParameters sLHDSAParameters = Utils.slhdsaParamsLookup(aSN1ObjectIdentifier);
            ASN1OctetString aSN1OctetString = PrivateKeyFactory.parseOctetString(privateKeyInfo.getPrivateKey(), sLHDSAParameters.getN() * 4);
            return new SLHDSAPrivateKeyParameters(sLHDSAParameters, aSN1OctetString.getOctets());
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.picnic)) {
            byte[] byArray = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
            PicnicParameters picnicParameters = Utils.picnicParamsLookup(aSN1ObjectIdentifier);
            return new PicnicPrivateKeyParameters(picnicParameters, byArray);
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.pqc_kem_mceliece)) {
            CMCEPrivateKey cMCEPrivateKey = CMCEPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
            CMCEParameters cMCEParameters = Utils.mcElieceParamsLookup(aSN1ObjectIdentifier);
            return new CMCEPrivateKeyParameters(cMCEParameters, cMCEPrivateKey.getDelta(), cMCEPrivateKey.getC(), cMCEPrivateKey.getG(), cMCEPrivateKey.getAlpha(), cMCEPrivateKey.getS());
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.pqc_kem_frodo)) {
            byte[] byArray = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
            FrodoParameters frodoParameters = Utils.frodoParamsLookup(aSN1ObjectIdentifier);
            return new FrodoPrivateKeyParameters(frodoParameters, byArray);
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.pqc_kem_saber)) {
            byte[] byArray = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
            SABERParameters sABERParameters = Utils.saberParamsLookup(aSN1ObjectIdentifier);
            return new SABERPrivateKeyParameters(sABERParameters, byArray);
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.pqc_kem_ntru)) {
            byte[] byArray = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
            NTRUParameters nTRUParameters = Utils.ntruParamsLookup(aSN1ObjectIdentifier);
            return new NTRUPrivateKeyParameters(nTRUParameters, byArray);
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_alg_ml_kem_512) || aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_alg_ml_kem_768) || aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_alg_ml_kem_1024)) {
            ASN1Primitive aSN1Primitive = PrivateKeyFactory.parsePrimitiveString(privateKeyInfo.getPrivateKey(), 64);
            MLKEMParameters mLKEMParameters = Utils.mlkemParamsLookup(aSN1ObjectIdentifier);
            MLKEMPublicKeyParameters mLKEMPublicKeyParameters = null;
            if (privateKeyInfo.getPublicKeyData() != null) {
                mLKEMPublicKeyParameters = PublicKeyFactory.MLKEMConverter.getPublicKeyParams(mLKEMParameters, privateKeyInfo.getPublicKeyData());
            }
            if (aSN1Primitive instanceof ASN1OctetString) {
                return new MLKEMPrivateKeyParameters(mLKEMParameters, ((ASN1OctetString)aSN1Primitive).getOctets(), mLKEMPublicKeyParameters);
            }
            if (aSN1Primitive instanceof ASN1Sequence) {
                ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1Primitive;
                byte[] byArray = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets();
                byte[] byArray3 = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets();
                MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters = new MLKEMPrivateKeyParameters(mLKEMParameters, byArray, mLKEMPublicKeyParameters);
                if (!Arrays.constantTimeAreEqual(mLKEMPrivateKeyParameters.getEncoded(), byArray3)) {
                    throw new IllegalArgumentException("inconsistent " + mLKEMParameters.getName() + " private key");
                }
                return mLKEMPrivateKeyParameters;
            }
            throw new IllegalArgumentException("invalid " + mLKEMParameters.getName() + " private key");
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.pqc_kem_ntrulprime)) {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(privateKeyInfo.parsePrivateKey());
            NTRULPRimeParameters nTRULPRimeParameters = Utils.ntrulprimeParamsLookup(aSN1ObjectIdentifier);
            return new NTRULPRimePrivateKeyParameters(nTRULPRimeParameters, ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets(), ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets(), ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets(), ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(3)).getOctets());
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.pqc_kem_sntruprime)) {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(privateKeyInfo.parsePrivateKey());
            SNTRUPrimeParameters sNTRUPrimeParameters = Utils.sntruprimeParamsLookup(aSN1ObjectIdentifier);
            return new SNTRUPrimePrivateKeyParameters(sNTRUPrimeParameters, ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets(), ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets(), ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets(), ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(3)).getOctets(), ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(4)).getOctets());
        }
        if (Utils.mldsaParams.containsKey(aSN1ObjectIdentifier)) {
            ASN1Primitive aSN1Primitive = PrivateKeyFactory.parsePrimitiveString(privateKeyInfo.getPrivateKey(), 32);
            MLDSAParameters mLDSAParameters = Utils.mldsaParamsLookup(aSN1ObjectIdentifier);
            MLDSAPublicKeyParameters mLDSAPublicKeyParameters = null;
            if (privateKeyInfo.getPublicKeyData() != null) {
                mLDSAPublicKeyParameters = PublicKeyFactory.MLDSAConverter.getPublicKeyParams(mLDSAParameters, privateKeyInfo.getPublicKeyData());
            }
            if (aSN1Primitive instanceof ASN1OctetString) {
                return new MLDSAPrivateKeyParameters(mLDSAParameters, ((ASN1OctetString)aSN1Primitive).getOctets(), mLDSAPublicKeyParameters);
            }
            if (aSN1Primitive instanceof ASN1Sequence) {
                ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1Primitive;
                byte[] byArray = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets();
                byte[] byArray4 = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets();
                MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters = new MLDSAPrivateKeyParameters(mLDSAParameters, byArray, mLDSAPublicKeyParameters);
                if (!Arrays.constantTimeAreEqual(mLDSAPrivateKeyParameters.getEncoded(), byArray4)) {
                    throw new IllegalArgumentException("inconsistent " + mLDSAParameters.getName() + " private key");
                }
                return mLDSAPrivateKeyParameters;
            }
            throw new IllegalArgumentException("invalid " + mLDSAParameters.getName() + " private key");
        }
        if (aSN1ObjectIdentifier.equals(BCObjectIdentifiers.dilithium2) || aSN1ObjectIdentifier.equals(BCObjectIdentifiers.dilithium3) || aSN1ObjectIdentifier.equals(BCObjectIdentifiers.dilithium5)) {
            ASN1Encodable aSN1Encodable = privateKeyInfo.parsePrivateKey();
            DilithiumParameters dilithiumParameters = Utils.dilithiumParamsLookup(aSN1ObjectIdentifier);
            if (aSN1Encodable instanceof ASN1Sequence) {
                ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1Encodable);
                int n = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).intValueExact();
                if (n != 0) {
                    throw new IOException("unknown private key version: " + n);
                }
                if (privateKeyInfo.getPublicKeyData() != null) {
                    DilithiumPublicKeyParameters dilithiumPublicKeyParameters = PublicKeyFactory.DilithiumConverter.getPublicKeyParams(dilithiumParameters, privateKeyInfo.getPublicKeyData());
                    return new DilithiumPrivateKeyParameters(dilithiumParameters, ASN1BitString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets(), ASN1BitString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets(), ASN1BitString.getInstance(aSN1Sequence.getObjectAt(3)).getOctets(), ASN1BitString.getInstance(aSN1Sequence.getObjectAt(4)).getOctets(), ASN1BitString.getInstance(aSN1Sequence.getObjectAt(5)).getOctets(), ASN1BitString.getInstance(aSN1Sequence.getObjectAt(6)).getOctets(), dilithiumPublicKeyParameters.getT1());
                }
                return new DilithiumPrivateKeyParameters(dilithiumParameters, ASN1BitString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets(), ASN1BitString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets(), ASN1BitString.getInstance(aSN1Sequence.getObjectAt(3)).getOctets(), ASN1BitString.getInstance(aSN1Sequence.getObjectAt(4)).getOctets(), ASN1BitString.getInstance(aSN1Sequence.getObjectAt(5)).getOctets(), ASN1BitString.getInstance(aSN1Sequence.getObjectAt(6)).getOctets(), null);
            }
            if (aSN1Encodable instanceof DEROctetString) {
                byte[] byArray = ASN1OctetString.getInstance(aSN1Encodable).getOctets();
                if (privateKeyInfo.getPublicKeyData() != null) {
                    DilithiumPublicKeyParameters dilithiumPublicKeyParameters = PublicKeyFactory.DilithiumConverter.getPublicKeyParams(dilithiumParameters, privateKeyInfo.getPublicKeyData());
                    return new DilithiumPrivateKeyParameters(dilithiumParameters, byArray, dilithiumPublicKeyParameters);
                }
                return new DilithiumPrivateKeyParameters(dilithiumParameters, byArray, null);
            }
            throw new IOException("not supported");
        }
        if (aSN1ObjectIdentifier.equals(BCObjectIdentifiers.falcon_512) || aSN1ObjectIdentifier.equals(BCObjectIdentifiers.falcon_1024)) {
            FalconPrivateKey falconPrivateKey = FalconPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
            FalconParameters falconParameters = Utils.falconParamsLookup(aSN1ObjectIdentifier);
            return new FalconPrivateKeyParameters(falconParameters, falconPrivateKey.getf(), falconPrivateKey.getG(), falconPrivateKey.getF(), falconPrivateKey.getPublicKey().getH());
        }
        if (aSN1ObjectIdentifier.equals(BCObjectIdentifiers.old_falcon_512) || aSN1ObjectIdentifier.equals(BCObjectIdentifiers.old_falcon_1024)) {
            FalconPrivateKey falconPrivateKey = FalconPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
            FalconParameters falconParameters = Utils.falconParamsLookup(aSN1ObjectIdentifier);
            return new FalconPrivateKeyParameters(falconParameters, falconPrivateKey.getf(), falconPrivateKey.getG(), falconPrivateKey.getF(), falconPrivateKey.getPublicKey().getH());
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.pqc_kem_bike)) {
            byte[] byArray = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
            BIKEParameters bIKEParameters = Utils.bikeParamsLookup(aSN1ObjectIdentifier);
            byte[] byArray5 = Arrays.copyOfRange(byArray, 0, bIKEParameters.getRByte());
            byte[] byArray6 = Arrays.copyOfRange(byArray, bIKEParameters.getRByte(), 2 * bIKEParameters.getRByte());
            byte[] byArray7 = Arrays.copyOfRange(byArray, 2 * bIKEParameters.getRByte(), byArray.length);
            return new BIKEPrivateKeyParameters(bIKEParameters, byArray5, byArray6, byArray7);
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.pqc_kem_hqc)) {
            byte[] byArray = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
            HQCParameters hQCParameters = Utils.hqcParamsLookup(aSN1ObjectIdentifier);
            return new HQCPrivateKeyParameters(hQCParameters, byArray);
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.rainbow)) {
            byte[] byArray = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
            RainbowParameters rainbowParameters = Utils.rainbowParamsLookup(aSN1ObjectIdentifier);
            return new RainbowPrivateKeyParameters(rainbowParameters, byArray);
        }
        if (aSN1ObjectIdentifier.equals(PQCObjectIdentifiers.xmss)) {
            XMSSKeyParams xMSSKeyParams = XMSSKeyParams.getInstance(algorithmIdentifier.getParameters());
            ASN1ObjectIdentifier aSN1ObjectIdentifier2 = xMSSKeyParams.getTreeDigest().getAlgorithm();
            XMSSPrivateKey xMSSPrivateKey = XMSSPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
            try {
                XMSSPrivateKeyParameters.Builder builder = new XMSSPrivateKeyParameters.Builder(new XMSSParameters(xMSSKeyParams.getHeight(), Utils.getDigest(aSN1ObjectIdentifier2))).withIndex(xMSSPrivateKey.getIndex()).withSecretKeySeed(xMSSPrivateKey.getSecretKeySeed()).withSecretKeyPRF(xMSSPrivateKey.getSecretKeyPRF()).withPublicSeed(xMSSPrivateKey.getPublicSeed()).withRoot(xMSSPrivateKey.getRoot());
                if (xMSSPrivateKey.getVersion() != 0) {
                    builder.withMaxIndex(xMSSPrivateKey.getMaxIndex());
                }
                if (xMSSPrivateKey.getBdsState() != null) {
                    BDS bDS = (BDS)XMSSUtil.deserialize(xMSSPrivateKey.getBdsState(), BDS.class);
                    builder.withBDSState(bDS.withWOTSDigest(aSN1ObjectIdentifier2));
                }
                return builder.build();
            }
            catch (ClassNotFoundException classNotFoundException) {
                throw new IOException("ClassNotFoundException processing BDS state: " + classNotFoundException.getMessage());
            }
        }
        if (aSN1ObjectIdentifier.equals(PQCObjectIdentifiers.xmss_mt)) {
            XMSSMTKeyParams xMSSMTKeyParams = XMSSMTKeyParams.getInstance(algorithmIdentifier.getParameters());
            ASN1ObjectIdentifier aSN1ObjectIdentifier3 = xMSSMTKeyParams.getTreeDigest().getAlgorithm();
            try {
                XMSSMTPrivateKey xMSSMTPrivateKey = XMSSMTPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
                XMSSMTPrivateKeyParameters.Builder builder = new XMSSMTPrivateKeyParameters.Builder(new XMSSMTParameters(xMSSMTKeyParams.getHeight(), xMSSMTKeyParams.getLayers(), Utils.getDigest(aSN1ObjectIdentifier3))).withIndex(xMSSMTPrivateKey.getIndex()).withSecretKeySeed(xMSSMTPrivateKey.getSecretKeySeed()).withSecretKeyPRF(xMSSMTPrivateKey.getSecretKeyPRF()).withPublicSeed(xMSSMTPrivateKey.getPublicSeed()).withRoot(xMSSMTPrivateKey.getRoot());
                if (xMSSMTPrivateKey.getVersion() != 0) {
                    builder.withMaxIndex(xMSSMTPrivateKey.getMaxIndex());
                }
                if (xMSSMTPrivateKey.getBdsState() != null) {
                    BDSStateMap bDSStateMap = (BDSStateMap)XMSSUtil.deserialize(xMSSMTPrivateKey.getBdsState(), BDSStateMap.class);
                    builder.withBDSState(bDSStateMap.withWOTSDigest(aSN1ObjectIdentifier3));
                }
                return builder.build();
            }
            catch (ClassNotFoundException classNotFoundException) {
                throw new IOException("ClassNotFoundException processing BDS state: " + classNotFoundException.getMessage());
            }
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.mayo)) {
            byte[] byArray = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
            MayoParameters mayoParameters = Utils.mayoParamsLookup(aSN1ObjectIdentifier);
            return new MayoPrivateKeyParameters(mayoParameters, byArray);
        }
        if (aSN1ObjectIdentifier.on(BCObjectIdentifiers.snova)) {
            byte[] byArray = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
            SnovaParameters snovaParameters = Utils.snovaParamsLookup(aSN1ObjectIdentifier);
            return new SnovaPrivateKeyParameters(snovaParameters, byArray);
        }
        throw new RuntimeException("algorithm identifier in private key not recognised");
    }

    private static ASN1OctetString parseOctetString(ASN1OctetString aSN1OctetString, int n) throws IOException {
        byte[] byArray = aSN1OctetString.getOctets();
        if (byArray.length == n) {
            return aSN1OctetString;
        }
        ASN1OctetString aSN1OctetString2 = Utils.parseOctetData(byArray);
        if (aSN1OctetString2 != null) {
            return ASN1OctetString.getInstance(aSN1OctetString2);
        }
        return aSN1OctetString;
    }

    private static ASN1Primitive parsePrimitiveString(ASN1OctetString aSN1OctetString, int n) throws IOException {
        byte[] byArray = aSN1OctetString.getOctets();
        if (byArray.length == n) {
            return aSN1OctetString;
        }
        ASN1Primitive aSN1Primitive = Utils.parseData(byArray);
        if (aSN1Primitive instanceof ASN1OctetString) {
            return ASN1OctetString.getInstance(aSN1Primitive);
        }
        if (aSN1Primitive instanceof ASN1Sequence) {
            return ASN1Sequence.getInstance(aSN1Primitive);
        }
        return aSN1OctetString;
    }

    private static short[] convert(byte[] byArray) {
        short[] sArray = new short[byArray.length / 2];
        for (int i = 0; i != sArray.length; ++i) {
            sArray[i] = Pack.littleEndianToShort(byArray, i * 2);
        }
        return sArray;
    }
}

