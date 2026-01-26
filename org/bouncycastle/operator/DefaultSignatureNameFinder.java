/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.operator.AlgorithmNameFinder;

public class DefaultSignatureNameFinder
implements AlgorithmNameFinder {
    private static final Map oids = new HashMap();
    private static final Map digests = new HashMap();

    private static void addSignatureName(ASN1ObjectIdentifier aSN1ObjectIdentifier, String string) {
        if (oids.containsKey(aSN1ObjectIdentifier)) {
            throw new IllegalStateException("object identifier already present in addSignatureName");
        }
        oids.put(aSN1ObjectIdentifier, string);
    }

    @Override
    public boolean hasAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return oids.containsKey(aSN1ObjectIdentifier);
    }

    @Override
    public String getAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = (String)oids.get(aSN1ObjectIdentifier);
        if (string != null) {
            return string;
        }
        return aSN1ObjectIdentifier.getId();
    }

    @Override
    public String getAlgorithmName(AlgorithmIdentifier algorithmIdentifier) {
        ASN1Encodable aSN1Encodable = algorithmIdentifier.getParameters();
        if (aSN1Encodable != null && !DERNull.INSTANCE.equals(aSN1Encodable) && algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            RSASSAPSSparams rSASSAPSSparams = RSASSAPSSparams.getInstance(aSN1Encodable);
            AlgorithmIdentifier algorithmIdentifier2 = rSASSAPSSparams.getMaskGenAlgorithm();
            if (algorithmIdentifier2.getAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1)) {
                AlgorithmIdentifier algorithmIdentifier3 = rSASSAPSSparams.getHashAlgorithm();
                ASN1ObjectIdentifier aSN1ObjectIdentifier = AlgorithmIdentifier.getInstance(algorithmIdentifier2.getParameters()).getAlgorithm();
                if (aSN1ObjectIdentifier.equals(algorithmIdentifier3.getAlgorithm())) {
                    return DefaultSignatureNameFinder.getDigestName(algorithmIdentifier3.getAlgorithm()) + "WITHRSAANDMGF1";
                }
                return DefaultSignatureNameFinder.getDigestName(algorithmIdentifier3.getAlgorithm()) + "WITHRSAANDMGF1USING" + DefaultSignatureNameFinder.getDigestName(aSN1ObjectIdentifier);
            }
            return DefaultSignatureNameFinder.getDigestName(rSASSAPSSparams.getHashAlgorithm().getAlgorithm()) + "WITHRSAAND" + algorithmIdentifier2.getAlgorithm().getId();
        }
        if (oids.containsKey(algorithmIdentifier.getAlgorithm())) {
            return (String)oids.get(algorithmIdentifier.getAlgorithm());
        }
        return algorithmIdentifier.getAlgorithm().getId();
    }

    private static String getDigestName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = (String)digests.get(aSN1ObjectIdentifier);
        if (string != null) {
            return string;
        }
        return aSN1ObjectIdentifier.getId();
    }

    static {
        DefaultSignatureNameFinder.addSignatureName(PKCSObjectIdentifiers.id_RSASSA_PSS, "RSASSA-PSS");
        DefaultSignatureNameFinder.addSignatureName(EdECObjectIdentifiers.id_Ed25519, "ED25519");
        DefaultSignatureNameFinder.addSignatureName(EdECObjectIdentifiers.id_Ed448, "ED448");
        DefaultSignatureNameFinder.addSignatureName(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"), "SHA1WITHRSA");
        DefaultSignatureNameFinder.addSignatureName(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224WITHRSA");
        DefaultSignatureNameFinder.addSignatureName(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256WITHRSA");
        DefaultSignatureNameFinder.addSignatureName(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384WITHRSA");
        DefaultSignatureNameFinder.addSignatureName(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512WITHRSA");
        DefaultSignatureNameFinder.addSignatureName(X509ObjectIdentifiers.id_rsassa_pss_shake128, "SHAKE128WITHRSAPSS");
        DefaultSignatureNameFinder.addSignatureName(X509ObjectIdentifiers.id_rsassa_pss_shake256, "SHAKE256WITHRSAPSS");
        DefaultSignatureNameFinder.addSignatureName(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410");
        DefaultSignatureNameFinder.addSignatureName(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410");
        DefaultSignatureNameFinder.addSignatureName(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "GOST3411-2012-256WITHECGOST3410-2012-256");
        DefaultSignatureNameFinder.addSignatureName(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "GOST3411-2012-512WITHECGOST3410-2012-512");
        DefaultSignatureNameFinder.addSignatureName(BSIObjectIdentifiers.ecdsa_plain_SHA1, "SHA1WITHPLAIN-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(BSIObjectIdentifiers.ecdsa_plain_SHA224, "SHA224WITHPLAIN-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(BSIObjectIdentifiers.ecdsa_plain_SHA256, "SHA256WITHPLAIN-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(BSIObjectIdentifiers.ecdsa_plain_SHA384, "SHA384WITHPLAIN-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(BSIObjectIdentifiers.ecdsa_plain_SHA512, "SHA512WITHPLAIN-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(BSIObjectIdentifiers.ecdsa_plain_SHA3_224, "SHA3-224WITHPLAIN-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(BSIObjectIdentifiers.ecdsa_plain_SHA3_256, "SHA3-256WITHPLAIN-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(BSIObjectIdentifiers.ecdsa_plain_SHA3_384, "SHA3-384WITHPLAIN-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(BSIObjectIdentifiers.ecdsa_plain_SHA3_512, "SHA3-512WITHPLAIN-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160WITHPLAIN-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1WITHCVC-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224WITHCVC-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256WITHCVC-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384WITHCVC-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512WITHCVC-ECDSA");
        DefaultSignatureNameFinder.addSignatureName(IsaraObjectIdentifiers.id_alg_xmss, "XMSS");
        DefaultSignatureNameFinder.addSignatureName(IsaraObjectIdentifiers.id_alg_xmssmt, "XMSSMT");
        DefaultSignatureNameFinder.addSignatureName(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, "RIPEMD128WITHRSA");
        DefaultSignatureNameFinder.addSignatureName(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, "RIPEMD160WITHRSA");
        DefaultSignatureNameFinder.addSignatureName(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, "RIPEMD256WITHRSA");
        DefaultSignatureNameFinder.addSignatureName(new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"), "MD5WITHRSA");
        DefaultSignatureNameFinder.addSignatureName(new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"), "MD2WITHRSA");
        DefaultSignatureNameFinder.addSignatureName(new ASN1ObjectIdentifier("1.2.840.10040.4.3"), "SHA1WITHDSA");
        DefaultSignatureNameFinder.addSignatureName(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1WITHECDSA");
        DefaultSignatureNameFinder.addSignatureName(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224WITHECDSA");
        DefaultSignatureNameFinder.addSignatureName(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256WITHECDSA");
        DefaultSignatureNameFinder.addSignatureName(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384WITHECDSA");
        DefaultSignatureNameFinder.addSignatureName(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512WITHECDSA");
        DefaultSignatureNameFinder.addSignatureName(X509ObjectIdentifiers.id_ecdsa_with_shake128, "SHAKE128WITHECDSA");
        DefaultSignatureNameFinder.addSignatureName(X509ObjectIdentifiers.id_ecdsa_with_shake256, "SHAKE256WITHECDSA");
        DefaultSignatureNameFinder.addSignatureName(OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
        DefaultSignatureNameFinder.addSignatureName(OIWObjectIdentifiers.dsaWithSHA1, "SHA1WITHDSA");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.dsa_with_sha224, "SHA224WITHDSA");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.dsa_with_sha256, "SHA256WITHDSA");
        DefaultSignatureNameFinder.addSignatureName(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, "LMS");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_ml_dsa_44, "ML-DSA-44");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_ml_dsa_65, "ML-DSA-65");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_ml_dsa_87, "ML-DSA-87");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512, "ML-DSA-44-WITH-SHA512");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512, "ML-DSA-65-WITH-SHA512");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512, "ML-DSA-87-WITH-SHA512");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_sha2_128s, "SLH-DSA-SHA2-128S");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_sha2_128f, "SLH-DSA-SHA2-128F");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_sha2_192s, "SLH-DSA-SHA2-192S");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_sha2_192f, "SLH-DSA-SHA2-192F");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_sha2_256s, "SLH-DSA-SHA2-256S");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_sha2_256f, "SLH-DSA-SHA2-256F");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_shake_128s, "SLH-DSA-SHAKE-128S");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_shake_128f, "SLH-DSA-SHAKE-128F");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_shake_192s, "SLH-DSA-SHAKE-192S");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_shake_192f, "SLH-DSA-SHAKE-192F");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_shake_256s, "SLH-DSA-SHAKE-256S");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_slh_dsa_shake_256f, "SLH-DSA-SHAKE-256F");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256, "SLH-DSA-SHA2-128S-WITH-SHA256");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256, "SLH-DSA-SHA2-128F-WITH-SHA256");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512, "SLH-DSA-SHA2-192S-WITH-SHA512");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512, "SLH-DSA-SHA2-192F-WITH-SHA512");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512, "SLH-DSA-SHA2-256S-WITH-SHA512");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512, "SLH-DSA-SHA2-256F-WITH-SHA512");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128, "SLH-DSA-SHAKE-128S-WITH-SHAKE128");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128, "SLH-DSA-SHAKE-128F-WITH-SHAKE128");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256, "SLH-DSA-SHAKE-192S-WITH-SHAKE256");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256, "SLH-DSA-SHAKE-192F-WITH-SHAKE256");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256, "SLH-DSA-SHAKE-256S-WITH-SHAKE256");
        DefaultSignatureNameFinder.addSignatureName(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256, "SLH-DSA-SHAKE-256F-WITH-SHAKE256");
        digests.put(OIWObjectIdentifiers.idSHA1, "SHA1");
        digests.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        digests.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        digests.put(NISTObjectIdentifiers.id_sha384, "SHA384");
        digests.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        digests.put(NISTObjectIdentifiers.id_shake128, "SHAKE128");
        digests.put(NISTObjectIdentifiers.id_shake256, "SHAKE256");
        digests.put(NISTObjectIdentifiers.id_sha3_224, "SHA3-224");
        digests.put(NISTObjectIdentifiers.id_sha3_256, "SHA3-256");
        digests.put(NISTObjectIdentifiers.id_sha3_384, "SHA3-384");
        digests.put(NISTObjectIdentifiers.id_sha3_512, "SHA3-512");
        digests.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
        digests.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
        digests.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
    }
}

