/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorCreationException;

class CMSUtils {
    private static final Set mqvAlgs = new HashSet();
    private static final Set ecAlgs = new HashSet();
    private static final Set gostAlgs = new HashSet();
    private static final Map asymmetricWrapperAlgNames = new HashMap();
    private static Map<ASN1ObjectIdentifier, String> wrapAlgNames = new HashMap<ASN1ObjectIdentifier, String>();

    CMSUtils() {
    }

    static boolean isMQV(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return mqvAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isEC(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return ecAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isGOST(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return gostAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isRFC2631(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_ESDH) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_SSDH);
    }

    static String getWrapAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return wrapAlgNames.get(aSN1ObjectIdentifier);
    }

    static PrivateKey cleanPrivateKey(PrivateKey privateKey) {
        if (privateKey instanceof AnnotatedPrivateKey) {
            return CMSUtils.cleanPrivateKey(((AnnotatedPrivateKey)privateKey).getKey());
        }
        return privateKey;
    }

    static IssuerAndSerialNumber getIssuerAndSerialNumber(X509Certificate x509Certificate) throws CertificateEncodingException {
        Certificate certificate = Certificate.getInstance(x509Certificate.getEncoded());
        return new IssuerAndSerialNumber(certificate.getIssuer(), x509Certificate.getSerialNumber());
    }

    static byte[] getSubjectKeyId(X509Certificate x509Certificate) {
        byte[] byArray = x509Certificate.getExtensionValue(Extension.subjectKeyIdentifier.getId());
        if (byArray != null) {
            return ASN1OctetString.getInstance(ASN1OctetString.getInstance(byArray).getOctets()).getOctets();
        }
        return null;
    }

    static EnvelopedDataHelper createContentHelper(Provider provider) {
        if (provider != null) {
            return new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        }
        return new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    }

    static EnvelopedDataHelper createContentHelper(String string) {
        if (string != null) {
            return new EnvelopedDataHelper(new NamedJcaJceExtHelper(string));
        }
        return new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    }

    static ASN1Encodable extractParameters(AlgorithmParameters algorithmParameters) throws CMSException {
        try {
            return AlgorithmParametersUtils.extractParameters(algorithmParameters);
        }
        catch (IOException iOException) {
            throw new CMSException("cannot extract parameters: " + iOException.getMessage(), iOException);
        }
    }

    static void loadParameters(AlgorithmParameters algorithmParameters, ASN1Encodable aSN1Encodable) throws CMSException {
        try {
            AlgorithmParametersUtils.loadParameters(algorithmParameters, aSN1Encodable);
        }
        catch (IOException iOException) {
            throw new CMSException("error encoding algorithm parameters.", iOException);
        }
    }

    static Key getJceKey(GenericKey genericKey) {
        if (genericKey.getRepresentation() instanceof Key) {
            return (Key)genericKey.getRepresentation();
        }
        if (genericKey.getRepresentation() instanceof byte[]) {
            return new SecretKeySpec((byte[])genericKey.getRepresentation(), "ENC");
        }
        throw new IllegalArgumentException("unknown generic key type");
    }

    static Cipher createAsymmetricWrapper(JcaJceHelper jcaJceHelper, ASN1ObjectIdentifier aSN1ObjectIdentifier, Map map) throws OperatorCreationException {
        try {
            block9: {
                String string = null;
                if (!map.isEmpty()) {
                    string = (String)map.get(aSN1ObjectIdentifier);
                }
                if (string == null) {
                    string = (String)asymmetricWrapperAlgNames.get(aSN1ObjectIdentifier);
                }
                if (string != null) {
                    try {
                        return jcaJceHelper.createCipher(string);
                    }
                    catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                        if (!string.equals("RSA/ECB/PKCS1Padding")) break block9;
                        try {
                            return jcaJceHelper.createCipher("RSA/NONE/PKCS1Padding");
                        }
                        catch (NoSuchAlgorithmException noSuchAlgorithmException2) {
                            // empty catch block
                        }
                    }
                }
            }
            return jcaJceHelper.createCipher(aSN1ObjectIdentifier.getId());
        }
        catch (GeneralSecurityException generalSecurityException) {
            throw new OperatorCreationException("cannot create cipher: " + generalSecurityException.getMessage(), generalSecurityException);
        }
    }

    public static int getKekSize(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier.equals(CMSAlgorithm.AES256_WRAP) || aSN1ObjectIdentifier.equals(CMSAlgorithm.AES256_WRAP_PAD)) {
            return 32;
        }
        if (aSN1ObjectIdentifier.equals(CMSAlgorithm.AES128_WRAP) || aSN1ObjectIdentifier.equals(CMSAlgorithm.AES128_WRAP_PAD)) {
            return 16;
        }
        if (aSN1ObjectIdentifier.equals(CMSAlgorithm.AES192_WRAP) || aSN1ObjectIdentifier.equals(CMSAlgorithm.AES192_WRAP_PAD)) {
            return 24;
        }
        throw new IllegalArgumentException("unknown wrap algorithm");
    }

    static {
        wrapAlgNames.put(CMSAlgorithm.AES128_WRAP, "AESWRAP");
        wrapAlgNames.put(CMSAlgorithm.AES192_WRAP, "AESWRAP");
        wrapAlgNames.put(CMSAlgorithm.AES256_WRAP, "AESWRAP");
        wrapAlgNames.put(CMSAlgorithm.AES128_WRAP_PAD, "AES-KWP");
        wrapAlgNames.put(CMSAlgorithm.AES192_WRAP_PAD, "AES-KWP");
        wrapAlgNames.put(CMSAlgorithm.AES256_WRAP_PAD, "AES-KWP");
        mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512);
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(OIWObjectIdentifiers.elGamalAlgorithm, "Elgamal/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA/ECB/OAEPPadding");
        asymmetricWrapperAlgNames.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        asymmetricWrapperAlgNames.put(ISOIECObjectIdentifiers.id_kem_rsa, "RSA-KTS-KEM-KWS");
    }
}

