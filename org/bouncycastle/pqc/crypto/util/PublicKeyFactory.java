/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.internal.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.pqc.asn1.CMCEPublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.crypto.bike.BIKEParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEPublicKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPublicKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPublicKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.Utils;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class PublicKeyFactory {
    private static Map converters = new HashMap();

    public static AsymmetricKeyParameter createKey(byte[] byArray) throws IOException {
        if (byArray == null) {
            throw new IllegalArgumentException("keyInfoData array null");
        }
        if (byArray.length == 0) {
            throw new IllegalArgumentException("keyInfoData array empty");
        }
        return PublicKeyFactory.createKey(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(byArray)));
    }

    public static AsymmetricKeyParameter createKey(InputStream inputStream) throws IOException {
        return PublicKeyFactory.createKey(SubjectPublicKeyInfo.getInstance(new ASN1InputStream(inputStream).readObject()));
    }

    public static AsymmetricKeyParameter createKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        if (subjectPublicKeyInfo == null) {
            throw new IllegalArgumentException("keyInfo argument null");
        }
        return PublicKeyFactory.createKey(subjectPublicKeyInfo, null);
    }

    public static AsymmetricKeyParameter createKey(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
        if (subjectPublicKeyInfo == null) {
            throw new IllegalArgumentException("keyInfo argument null");
        }
        AlgorithmIdentifier algorithmIdentifier = subjectPublicKeyInfo.getAlgorithm();
        SubjectPublicKeyInfoConverter subjectPublicKeyInfoConverter = (SubjectPublicKeyInfoConverter)converters.get(algorithmIdentifier.getAlgorithm());
        if (subjectPublicKeyInfoConverter != null) {
            return subjectPublicKeyInfoConverter.getPublicKeyParameters(subjectPublicKeyInfo, object);
        }
        throw new IOException("algorithm identifier in public key not recognised: " + algorithmIdentifier.getAlgorithm());
    }

    static {
        converters.put(PQCObjectIdentifiers.sphincs256, new SPHINCSConverter());
        converters.put(PQCObjectIdentifiers.newHope, new NHConverter());
        converters.put(PQCObjectIdentifiers.xmss, new XMSSConverter());
        converters.put(PQCObjectIdentifiers.xmss_mt, new XMSSMTConverter());
        converters.put(IsaraObjectIdentifiers.id_alg_xmss, new XMSSConverter());
        converters.put(IsaraObjectIdentifiers.id_alg_xmssmt, new XMSSMTConverter());
        converters.put(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, new LMSConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_128s_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_128f_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_128s_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_128f_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_128s_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_128f_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_192s_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_192f_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_192s_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_192f_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_192s_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_192f_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_256s_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_256f_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_256s_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_256f_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_256s_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_256f_r3, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_128s_r3_simple, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_128f_r3_simple, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_192s_r3_simple, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_192f_r3_simple, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_256s_r3_simple, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_haraka_256f_r3_simple, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_128s, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_128f, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_128s, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_128f, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_192s, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_192f, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_192s, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_192f, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_256s, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_sha2_256f, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_256s, new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.sphincsPlus_shake_256f, new SPHINCSPlusConverter());
        converters.put(new ASN1ObjectIdentifier("1.3.9999.6.4.10"), new SPHINCSPlusConverter());
        converters.put(BCObjectIdentifiers.mceliece348864_r3, new CMCEConverter());
        converters.put(BCObjectIdentifiers.mceliece348864f_r3, new CMCEConverter());
        converters.put(BCObjectIdentifiers.mceliece460896_r3, new CMCEConverter());
        converters.put(BCObjectIdentifiers.mceliece460896f_r3, new CMCEConverter());
        converters.put(BCObjectIdentifiers.mceliece6688128_r3, new CMCEConverter());
        converters.put(BCObjectIdentifiers.mceliece6688128f_r3, new CMCEConverter());
        converters.put(BCObjectIdentifiers.mceliece6960119_r3, new CMCEConverter());
        converters.put(BCObjectIdentifiers.mceliece6960119f_r3, new CMCEConverter());
        converters.put(BCObjectIdentifiers.mceliece8192128_r3, new CMCEConverter());
        converters.put(BCObjectIdentifiers.mceliece8192128f_r3, new CMCEConverter());
        converters.put(BCObjectIdentifiers.frodokem640aes, new FrodoConverter());
        converters.put(BCObjectIdentifiers.frodokem640shake, new FrodoConverter());
        converters.put(BCObjectIdentifiers.frodokem976aes, new FrodoConverter());
        converters.put(BCObjectIdentifiers.frodokem976shake, new FrodoConverter());
        converters.put(BCObjectIdentifiers.frodokem1344aes, new FrodoConverter());
        converters.put(BCObjectIdentifiers.frodokem1344shake, new FrodoConverter());
        converters.put(BCObjectIdentifiers.lightsaberkem128r3, new SABERConverter());
        converters.put(BCObjectIdentifiers.saberkem128r3, new SABERConverter());
        converters.put(BCObjectIdentifiers.firesaberkem128r3, new SABERConverter());
        converters.put(BCObjectIdentifiers.lightsaberkem192r3, new SABERConverter());
        converters.put(BCObjectIdentifiers.saberkem192r3, new SABERConverter());
        converters.put(BCObjectIdentifiers.firesaberkem192r3, new SABERConverter());
        converters.put(BCObjectIdentifiers.lightsaberkem256r3, new SABERConverter());
        converters.put(BCObjectIdentifiers.saberkem256r3, new SABERConverter());
        converters.put(BCObjectIdentifiers.firesaberkem256r3, new SABERConverter());
        converters.put(BCObjectIdentifiers.ulightsaberkemr3, new SABERConverter());
        converters.put(BCObjectIdentifiers.usaberkemr3, new SABERConverter());
        converters.put(BCObjectIdentifiers.ufiresaberkemr3, new SABERConverter());
        converters.put(BCObjectIdentifiers.lightsaberkem90sr3, new SABERConverter());
        converters.put(BCObjectIdentifiers.saberkem90sr3, new SABERConverter());
        converters.put(BCObjectIdentifiers.firesaberkem90sr3, new SABERConverter());
        converters.put(BCObjectIdentifiers.ulightsaberkem90sr3, new SABERConverter());
        converters.put(BCObjectIdentifiers.usaberkem90sr3, new SABERConverter());
        converters.put(BCObjectIdentifiers.ufiresaberkem90sr3, new SABERConverter());
        converters.put(BCObjectIdentifiers.picnicl1fs, new PicnicConverter());
        converters.put(BCObjectIdentifiers.picnicl1ur, new PicnicConverter());
        converters.put(BCObjectIdentifiers.picnicl3fs, new PicnicConverter());
        converters.put(BCObjectIdentifiers.picnicl3ur, new PicnicConverter());
        converters.put(BCObjectIdentifiers.picnicl5fs, new PicnicConverter());
        converters.put(BCObjectIdentifiers.picnicl5ur, new PicnicConverter());
        converters.put(BCObjectIdentifiers.picnic3l1, new PicnicConverter());
        converters.put(BCObjectIdentifiers.picnic3l3, new PicnicConverter());
        converters.put(BCObjectIdentifiers.picnic3l5, new PicnicConverter());
        converters.put(BCObjectIdentifiers.picnicl1full, new PicnicConverter());
        converters.put(BCObjectIdentifiers.picnicl3full, new PicnicConverter());
        converters.put(BCObjectIdentifiers.picnicl5full, new PicnicConverter());
        converters.put(BCObjectIdentifiers.ntruhps2048509, new NtruConverter());
        converters.put(BCObjectIdentifiers.ntruhps2048677, new NtruConverter());
        converters.put(BCObjectIdentifiers.ntruhps4096821, new NtruConverter());
        converters.put(BCObjectIdentifiers.ntruhps40961229, new NtruConverter());
        converters.put(BCObjectIdentifiers.ntruhrss701, new NtruConverter());
        converters.put(BCObjectIdentifiers.ntruhrss1373, new NtruConverter());
        converters.put(BCObjectIdentifiers.falcon_512, new FalconConverter());
        converters.put(BCObjectIdentifiers.falcon_1024, new FalconConverter());
        converters.put(BCObjectIdentifiers.old_falcon_512, new FalconConverter());
        converters.put(BCObjectIdentifiers.old_falcon_1024, new FalconConverter());
        converters.put(NISTObjectIdentifiers.id_alg_ml_kem_512, new MLKEMConverter());
        converters.put(NISTObjectIdentifiers.id_alg_ml_kem_768, new MLKEMConverter());
        converters.put(NISTObjectIdentifiers.id_alg_ml_kem_1024, new MLKEMConverter());
        converters.put(BCObjectIdentifiers.kyber512_aes, new MLKEMConverter());
        converters.put(BCObjectIdentifiers.kyber768_aes, new MLKEMConverter());
        converters.put(BCObjectIdentifiers.kyber1024_aes, new MLKEMConverter());
        converters.put(BCObjectIdentifiers.ntrulpr653, new NTRULPrimeConverter());
        converters.put(BCObjectIdentifiers.ntrulpr761, new NTRULPrimeConverter());
        converters.put(BCObjectIdentifiers.ntrulpr857, new NTRULPrimeConverter());
        converters.put(BCObjectIdentifiers.ntrulpr953, new NTRULPrimeConverter());
        converters.put(BCObjectIdentifiers.ntrulpr1013, new NTRULPrimeConverter());
        converters.put(BCObjectIdentifiers.ntrulpr1277, new NTRULPrimeConverter());
        converters.put(BCObjectIdentifiers.sntrup653, new SNTRUPrimeConverter());
        converters.put(BCObjectIdentifiers.sntrup761, new SNTRUPrimeConverter());
        converters.put(BCObjectIdentifiers.sntrup857, new SNTRUPrimeConverter());
        converters.put(BCObjectIdentifiers.sntrup953, new SNTRUPrimeConverter());
        converters.put(BCObjectIdentifiers.sntrup1013, new SNTRUPrimeConverter());
        converters.put(BCObjectIdentifiers.sntrup1277, new SNTRUPrimeConverter());
        converters.put(NISTObjectIdentifiers.id_ml_dsa_44, new MLDSAConverter());
        converters.put(NISTObjectIdentifiers.id_ml_dsa_65, new MLDSAConverter());
        converters.put(NISTObjectIdentifiers.id_ml_dsa_87, new MLDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512, new MLDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512, new MLDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512, new MLDSAConverter());
        converters.put(BCObjectIdentifiers.dilithium2, new DilithiumConverter());
        converters.put(BCObjectIdentifiers.dilithium3, new DilithiumConverter());
        converters.put(BCObjectIdentifiers.dilithium5, new DilithiumConverter());
        converters.put(BCObjectIdentifiers.dilithium2_aes, new DilithiumConverter());
        converters.put(BCObjectIdentifiers.dilithium3_aes, new DilithiumConverter());
        converters.put(BCObjectIdentifiers.dilithium5_aes, new DilithiumConverter());
        converters.put(BCObjectIdentifiers.bike128, new BIKEConverter());
        converters.put(BCObjectIdentifiers.bike192, new BIKEConverter());
        converters.put(BCObjectIdentifiers.bike256, new BIKEConverter());
        converters.put(BCObjectIdentifiers.hqc128, new HQCConverter());
        converters.put(BCObjectIdentifiers.hqc192, new HQCConverter());
        converters.put(BCObjectIdentifiers.hqc256, new HQCConverter());
        converters.put(BCObjectIdentifiers.rainbow_III_classic, new RainbowConverter());
        converters.put(BCObjectIdentifiers.rainbow_III_circumzenithal, new RainbowConverter());
        converters.put(BCObjectIdentifiers.rainbow_III_compressed, new RainbowConverter());
        converters.put(BCObjectIdentifiers.rainbow_V_classic, new RainbowConverter());
        converters.put(BCObjectIdentifiers.rainbow_V_circumzenithal, new RainbowConverter());
        converters.put(BCObjectIdentifiers.rainbow_V_compressed, new RainbowConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_128s, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_128f, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_192s, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_192f, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_256s, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_256f, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_128s, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_128f, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_192s, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_192f, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_256s, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_256f, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256, new SLHDSAConverter());
        converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256, new SLHDSAConverter());
        converters.put(BCObjectIdentifiers.mayo1, new MayoConverter());
        converters.put(BCObjectIdentifiers.mayo2, new MayoConverter());
        converters.put(BCObjectIdentifiers.mayo3, new MayoConverter());
        converters.put(BCObjectIdentifiers.mayo5, new MayoConverter());
        converters.put(BCObjectIdentifiers.snova_24_5_4_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_24_5_4_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_24_5_4_shake_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_24_5_4_shake_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_24_5_5_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_24_5_5_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_24_5_5_shake_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_24_5_5_shake_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_25_8_3_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_25_8_3_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_25_8_3_shake_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_25_8_3_shake_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_29_6_5_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_29_6_5_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_29_6_5_shake_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_29_6_5_shake_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_37_8_4_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_37_8_4_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_37_8_4_shake_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_37_8_4_shake_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_37_17_2_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_37_17_2_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_37_17_2_shake_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_37_17_2_shake_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_49_11_3_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_49_11_3_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_49_11_3_shake_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_49_11_3_shake_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_56_25_2_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_56_25_2_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_56_25_2_shake_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_56_25_2_shake_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_60_10_4_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_60_10_4_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_60_10_4_shake_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_60_10_4_shake_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_66_15_3_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_66_15_3_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_66_15_3_shake_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_66_15_3_shake_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_75_33_2_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_75_33_2_ssk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_75_33_2_shake_esk, new SnovaConverter());
        converters.put(BCObjectIdentifiers.snova_75_33_2_shake_ssk, new SnovaConverter());
    }

    private static class BIKEConverter
    extends SubjectPublicKeyInfoConverter {
        private BIKEConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            try {
                byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
                BIKEParameters bIKEParameters = Utils.bikeParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
                return new BIKEPublicKeyParameters(bIKEParameters, byArray);
            }
            catch (Exception exception) {
                byte[] byArray = subjectPublicKeyInfo.getPublicKeyData().getOctets();
                BIKEParameters bIKEParameters = Utils.bikeParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
                return new BIKEPublicKeyParameters(bIKEParameters, byArray);
            }
        }
    }

    private static class CMCEConverter
    extends SubjectPublicKeyInfoConverter {
        private CMCEConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            try {
                byte[] byArray = CMCEPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey()).getT();
                CMCEParameters cMCEParameters = Utils.mcElieceParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
                return new CMCEPublicKeyParameters(cMCEParameters, byArray);
            }
            catch (Exception exception) {
                byte[] byArray = subjectPublicKeyInfo.getPublicKeyData().getOctets();
                CMCEParameters cMCEParameters = Utils.mcElieceParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
                return new CMCEPublicKeyParameters(cMCEParameters, byArray);
            }
        }
    }

    static class DilithiumConverter
    extends SubjectPublicKeyInfoConverter {
        DilithiumConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            DilithiumParameters dilithiumParameters = Utils.dilithiumParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return DilithiumConverter.getPublicKeyParams(dilithiumParameters, subjectPublicKeyInfo.getPublicKeyData());
        }

        static DilithiumPublicKeyParameters getPublicKeyParams(DilithiumParameters dilithiumParameters, ASN1BitString aSN1BitString) {
            try {
                ASN1Primitive aSN1Primitive = ASN1Primitive.fromByteArray(aSN1BitString.getOctets());
                if (aSN1Primitive instanceof ASN1Sequence) {
                    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1Primitive);
                    return new DilithiumPublicKeyParameters(dilithiumParameters, ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets(), ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
                }
                byte[] byArray = ASN1OctetString.getInstance(aSN1Primitive).getOctets();
                return new DilithiumPublicKeyParameters(dilithiumParameters, byArray);
            }
            catch (Exception exception) {
                return new DilithiumPublicKeyParameters(dilithiumParameters, aSN1BitString.getOctets());
            }
        }
    }

    private static class FalconConverter
    extends SubjectPublicKeyInfoConverter {
        private FalconConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            byte[] byArray = subjectPublicKeyInfo.getPublicKeyData().getOctets();
            FalconParameters falconParameters = Utils.falconParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return new FalconPublicKeyParameters(falconParameters, Arrays.copyOfRange(byArray, 1, byArray.length));
        }
    }

    private static class FrodoConverter
    extends SubjectPublicKeyInfoConverter {
        private FrodoConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
            FrodoParameters frodoParameters = Utils.frodoParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return new FrodoPublicKeyParameters(frodoParameters, byArray);
        }
    }

    private static class HQCConverter
    extends SubjectPublicKeyInfoConverter {
        private HQCConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            try {
                byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
                HQCParameters hQCParameters = Utils.hqcParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
                return new HQCPublicKeyParameters(hQCParameters, byArray);
            }
            catch (Exception exception) {
                byte[] byArray = subjectPublicKeyInfo.getPublicKeyData().getOctets();
                HQCParameters hQCParameters = Utils.hqcParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
                return new HQCPublicKeyParameters(hQCParameters, byArray);
            }
        }
    }

    private static class LMSConverter
    extends SubjectPublicKeyInfoConverter {
        private LMSConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            byte[] byArray = subjectPublicKeyInfo.getPublicKeyData().getOctets();
            ASN1OctetString aSN1OctetString = (ASN1OctetString)Utils.parseData(byArray);
            if (aSN1OctetString != null) {
                return this.getLmsKeyParameters(aSN1OctetString.getOctets());
            }
            return this.getLmsKeyParameters(byArray);
        }

        private LMSKeyParameters getLmsKeyParameters(byte[] byArray) throws IOException {
            return HSSPublicKeyParameters.getInstance(byArray);
        }
    }

    static class MLDSAConverter
    extends SubjectPublicKeyInfoConverter {
        MLDSAConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            MLDSAParameters mLDSAParameters = Utils.mldsaParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return MLDSAConverter.getPublicKeyParams(mLDSAParameters, subjectPublicKeyInfo.getPublicKeyData());
        }

        static MLDSAPublicKeyParameters getPublicKeyParams(MLDSAParameters mLDSAParameters, ASN1BitString aSN1BitString) {
            try {
                ASN1Primitive aSN1Primitive = ASN1Primitive.fromByteArray(aSN1BitString.getOctets());
                if (aSN1Primitive instanceof ASN1Sequence) {
                    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1Primitive);
                    return new MLDSAPublicKeyParameters(mLDSAParameters, ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets(), ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
                }
                byte[] byArray = ASN1OctetString.getInstance(aSN1Primitive).getOctets();
                return new MLDSAPublicKeyParameters(mLDSAParameters, byArray);
            }
            catch (Exception exception) {
                return new MLDSAPublicKeyParameters(mLDSAParameters, aSN1BitString.getOctets());
            }
        }
    }

    static class MLKEMConverter
    extends SubjectPublicKeyInfoConverter {
        MLKEMConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            MLKEMParameters mLKEMParameters = Utils.mlkemParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return new MLKEMPublicKeyParameters(mLKEMParameters, subjectPublicKeyInfo.getPublicKeyData().getOctets());
        }

        static MLKEMPublicKeyParameters getPublicKeyParams(MLKEMParameters mLKEMParameters, ASN1BitString aSN1BitString) {
            try {
                ASN1Primitive aSN1Primitive = ASN1Primitive.fromByteArray(aSN1BitString.getOctets());
                if (aSN1Primitive instanceof ASN1Sequence) {
                    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1Primitive);
                    return new MLKEMPublicKeyParameters(mLKEMParameters, ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets(), ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
                }
                byte[] byArray = ASN1OctetString.getInstance(aSN1Primitive).getOctets();
                return new MLKEMPublicKeyParameters(mLKEMParameters, byArray);
            }
            catch (Exception exception) {
                return new MLKEMPublicKeyParameters(mLKEMParameters, aSN1BitString.getOctets());
            }
        }
    }

    private static class MayoConverter
    extends SubjectPublicKeyInfoConverter {
        private MayoConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
            MayoParameters mayoParameters = Utils.mayoParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return new MayoPublicKeyParameters(mayoParameters, byArray);
        }
    }

    private static class NHConverter
    extends SubjectPublicKeyInfoConverter {
        private NHConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            return new NHPublicKeyParameters(subjectPublicKeyInfo.getPublicKeyData().getBytes());
        }
    }

    private static class NTRULPrimeConverter
    extends SubjectPublicKeyInfoConverter {
        private NTRULPrimeConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
            NTRULPRimeParameters nTRULPRimeParameters = Utils.ntrulprimeParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return new NTRULPRimePublicKeyParameters(nTRULPRimeParameters, byArray);
        }
    }

    private static class NtruConverter
    extends SubjectPublicKeyInfoConverter {
        private NtruConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            byte[] byArray = subjectPublicKeyInfo.getPublicKeyData().getOctets();
            ASN1OctetString aSN1OctetString = Utils.parseOctetData(byArray);
            if (aSN1OctetString != null) {
                return this.getNtruPublicKeyParameters(subjectPublicKeyInfo, aSN1OctetString.getOctets());
            }
            return this.getNtruPublicKeyParameters(subjectPublicKeyInfo, byArray);
        }

        private NTRUPublicKeyParameters getNtruPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, byte[] byArray) {
            NTRUParameters nTRUParameters = Utils.ntruParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return new NTRUPublicKeyParameters(nTRUParameters, byArray);
        }
    }

    private static class PicnicConverter
    extends SubjectPublicKeyInfoConverter {
        private PicnicConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
            PicnicParameters picnicParameters = Utils.picnicParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return new PicnicPublicKeyParameters(picnicParameters, byArray);
        }
    }

    private static class RainbowConverter
    extends SubjectPublicKeyInfoConverter {
        private RainbowConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
            RainbowParameters rainbowParameters = Utils.rainbowParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return new RainbowPublicKeyParameters(rainbowParameters, byArray);
        }
    }

    private static class SABERConverter
    extends SubjectPublicKeyInfoConverter {
        private SABERConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            byte[] byArray = ASN1OctetString.getInstance(ASN1Sequence.getInstance(subjectPublicKeyInfo.parsePublicKey()).getObjectAt(0)).getOctets();
            SABERParameters sABERParameters = Utils.saberParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return new SABERPublicKeyParameters(sABERParameters, byArray);
        }
    }

    private static class SLHDSAConverter
    extends SubjectPublicKeyInfoConverter {
        private SLHDSAConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            try {
                byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
                SLHDSAParameters sLHDSAParameters = Utils.slhdsaParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
                return new SLHDSAPublicKeyParameters(sLHDSAParameters, Arrays.copyOfRange(byArray, 4, byArray.length));
            }
            catch (Exception exception) {
                byte[] byArray = subjectPublicKeyInfo.getPublicKeyData().getOctets();
                SLHDSAParameters sLHDSAParameters = Utils.slhdsaParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
                return new SLHDSAPublicKeyParameters(sLHDSAParameters, byArray);
            }
        }
    }

    private static class SNTRUPrimeConverter
    extends SubjectPublicKeyInfoConverter {
        private SNTRUPrimeConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
            SNTRUPrimeParameters sNTRUPrimeParameters = Utils.sntruprimeParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return new SNTRUPrimePublicKeyParameters(sNTRUPrimeParameters, byArray);
        }
    }

    private static class SPHINCSConverter
    extends SubjectPublicKeyInfoConverter {
        private SPHINCSConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            return new SPHINCSPublicKeyParameters(subjectPublicKeyInfo.getPublicKeyData().getBytes(), Utils.sphincs256LookupTreeAlgName(SPHINCS256KeyParams.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters())));
        }
    }

    private static class SPHINCSPlusConverter
    extends SubjectPublicKeyInfoConverter {
        private SPHINCSPlusConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            try {
                byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
                SPHINCSPlusParameters sPHINCSPlusParameters = Utils.sphincsPlusParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
                return new SPHINCSPlusPublicKeyParameters(sPHINCSPlusParameters, Arrays.copyOfRange(byArray, 4, byArray.length));
            }
            catch (Exception exception) {
                byte[] byArray = subjectPublicKeyInfo.getPublicKeyData().getOctets();
                SPHINCSPlusParameters sPHINCSPlusParameters = Utils.sphincsPlusParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
                return new SPHINCSPlusPublicKeyParameters(sPHINCSPlusParameters, byArray);
            }
        }
    }

    private static class SnovaConverter
    extends SubjectPublicKeyInfoConverter {
        private SnovaConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
            SnovaParameters snovaParameters = Utils.snovaParamsLookup(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
            return new SnovaPublicKeyParameters(snovaParameters, byArray);
        }
    }

    private static abstract class SubjectPublicKeyInfoConverter {
        private SubjectPublicKeyInfoConverter() {
        }

        abstract AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException;
    }

    private static class XMSSConverter
    extends SubjectPublicKeyInfoConverter {
        private XMSSConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            XMSSKeyParams xMSSKeyParams = XMSSKeyParams.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            if (xMSSKeyParams != null) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = xMSSKeyParams.getTreeDigest().getAlgorithm();
                XMSSPublicKey xMSSPublicKey = XMSSPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
                return new XMSSPublicKeyParameters.Builder(new XMSSParameters(xMSSKeyParams.getHeight(), Utils.getDigest(aSN1ObjectIdentifier))).withPublicSeed(xMSSPublicKey.getPublicSeed()).withRoot(xMSSPublicKey.getRoot()).build();
            }
            byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
            return new XMSSPublicKeyParameters.Builder(XMSSParameters.lookupByOID(Pack.bigEndianToInt(byArray, 0))).withPublicKey(byArray).build();
        }
    }

    private static class XMSSMTConverter
    extends SubjectPublicKeyInfoConverter {
        private XMSSMTConverter() {
        }

        @Override
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object object) throws IOException {
            XMSSMTKeyParams xMSSMTKeyParams = XMSSMTKeyParams.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            if (xMSSMTKeyParams != null) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = xMSSMTKeyParams.getTreeDigest().getAlgorithm();
                XMSSPublicKey xMSSPublicKey = XMSSPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
                return new XMSSMTPublicKeyParameters.Builder(new XMSSMTParameters(xMSSMTKeyParams.getHeight(), xMSSMTKeyParams.getLayers(), Utils.getDigest(aSN1ObjectIdentifier))).withPublicSeed(xMSSPublicKey.getPublicSeed()).withRoot(xMSSPublicKey.getRoot()).build();
            }
            byte[] byArray = ASN1OctetString.getInstance(subjectPublicKeyInfo.parsePublicKey()).getOctets();
            return new XMSSMTPublicKeyParameters.Builder(XMSSMTParameters.lookupByOID(Pack.bigEndianToInt(byArray, 0))).withPublicKey(byArray).build();
        }
    }
}

