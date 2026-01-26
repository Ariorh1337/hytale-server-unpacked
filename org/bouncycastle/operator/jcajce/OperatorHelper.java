/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.operator.DefaultSignatureNameFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;

class OperatorHelper {
    private static final Map oids = new HashMap();
    private static final Map asymmetricWrapperAlgNames = new HashMap();
    private static final Map symmetricWrapperAlgNames = new HashMap();
    private static final Map symmetricKeyAlgNames = new HashMap();
    private static final Map symmetricWrapperKeySizes = new HashMap();
    private static final Map oaepParamsMap = new HashMap();
    private static DefaultSignatureNameFinder sigFinder = new DefaultSignatureNameFinder();
    private JcaJceHelper helper;

    OperatorHelper(JcaJceHelper jcaJceHelper) {
        this.helper = jcaJceHelper;
    }

    String getWrappingAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (String)symmetricWrapperAlgNames.get(aSN1ObjectIdentifier);
    }

    int getKeySizeInBits(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (Integer)symmetricWrapperKeySizes.get(aSN1ObjectIdentifier);
    }

    KeyPairGenerator createKeyPairGenerator(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        try {
            String string = null;
            if (string != null) {
                try {
                    return this.helper.createKeyPairGenerator(string);
                }
                catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    // empty catch block
                }
            }
            return this.helper.createKeyPairGenerator(aSN1ObjectIdentifier.getId());
        }
        catch (GeneralSecurityException generalSecurityException) {
            throw new CMSException("cannot create key agreement: " + generalSecurityException.getMessage(), generalSecurityException);
        }
    }

    Cipher createCipher(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws OperatorCreationException {
        try {
            return this.helper.createCipher(aSN1ObjectIdentifier.getId());
        }
        catch (GeneralSecurityException generalSecurityException) {
            throw new OperatorCreationException("cannot create cipher: " + generalSecurityException.getMessage(), generalSecurityException);
        }
    }

    KeyAgreement createKeyAgreement(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws OperatorCreationException {
        try {
            String string = null;
            if (string != null) {
                try {
                    return this.helper.createKeyAgreement(string);
                }
                catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    // empty catch block
                }
            }
            return this.helper.createKeyAgreement(aSN1ObjectIdentifier.getId());
        }
        catch (GeneralSecurityException generalSecurityException) {
            throw new OperatorCreationException("cannot create key agreement: " + generalSecurityException.getMessage(), generalSecurityException);
        }
    }

    Cipher createAsymmetricWrapper(AlgorithmIdentifier algorithmIdentifier, Map map) throws OperatorCreationException {
        if (algorithmIdentifier == null) {
            throw new NullPointerException("'algorithmID' cannot be null");
        }
        ASN1ObjectIdentifier aSN1ObjectIdentifier = algorithmIdentifier.getAlgorithm();
        try {
            block17: {
                String string = null;
                if (map != null && !map.isEmpty()) {
                    string = (String)map.get(aSN1ObjectIdentifier);
                }
                if (string == null) {
                    string = (String)asymmetricWrapperAlgNames.get(aSN1ObjectIdentifier);
                }
                if (string != null) {
                    if (string.indexOf("OAEPPadding") > 0) {
                        try {
                            ASN1ObjectIdentifier aSN1ObjectIdentifier2;
                            OAEPParamsValue oAEPParamsValue;
                            RSAESOAEPparams rSAESOAEPparams = RSAESOAEPparams.getInstance(algorithmIdentifier.getParameters());
                            if (rSAESOAEPparams != null && (oAEPParamsValue = (OAEPParamsValue)oaepParamsMap.get(aSN1ObjectIdentifier2 = rSAESOAEPparams.getHashAlgorithm().getAlgorithm())) != null && oAEPParamsValue.matches(rSAESOAEPparams.withDefaultPSource())) {
                                string = oAEPParamsValue.getCipherName();
                            }
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                    try {
                        return this.helper.createCipher(string);
                    }
                    catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                        if (string.equals("RSA/ECB/PKCS1Padding")) {
                            try {
                                return this.helper.createCipher("RSA/NONE/PKCS1Padding");
                            }
                            catch (NoSuchAlgorithmException noSuchAlgorithmException2) {
                                break block17;
                            }
                        }
                        if (string.indexOf("ECB/OAEPWith") <= 0) break block17;
                        int n = string.indexOf("ECB");
                        try {
                            return this.helper.createCipher(string.substring(0, n) + "NONE" + string.substring(n + 3));
                        }
                        catch (NoSuchAlgorithmException noSuchAlgorithmException3) {
                            // empty catch block
                        }
                    }
                }
            }
            return this.helper.createCipher(aSN1ObjectIdentifier.getId());
        }
        catch (GeneralSecurityException generalSecurityException) {
            throw new OperatorCreationException("cannot create cipher: " + generalSecurityException.getMessage(), generalSecurityException);
        }
    }

    Cipher createSymmetricWrapper(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws OperatorCreationException {
        try {
            String string = (String)symmetricWrapperAlgNames.get(aSN1ObjectIdentifier);
            if (string != null) {
                try {
                    return this.helper.createCipher(string);
                }
                catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    // empty catch block
                }
            }
            return this.helper.createCipher(aSN1ObjectIdentifier.getId());
        }
        catch (GeneralSecurityException generalSecurityException) {
            throw new OperatorCreationException("cannot create cipher: " + generalSecurityException.getMessage(), generalSecurityException);
        }
    }

    AlgorithmParameters createAlgorithmParameters(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        AlgorithmParameters algorithmParameters = null;
        if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.rsaEncryption)) {
            return null;
        }
        if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSAES_OAEP)) {
            try {
                algorithmParameters = this.helper.createAlgorithmParameters("OAEP");
            }
            catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            }
            catch (NoSuchProviderException noSuchProviderException) {
                throw new OperatorCreationException("cannot create algorithm parameters: " + noSuchProviderException.getMessage(), noSuchProviderException);
            }
        }
        if (algorithmParameters == null) {
            try {
                algorithmParameters = this.helper.createAlgorithmParameters(algorithmIdentifier.getAlgorithm().getId());
            }
            catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                return null;
            }
            catch (NoSuchProviderException noSuchProviderException) {
                throw new OperatorCreationException("cannot create algorithm parameters: " + noSuchProviderException.getMessage(), noSuchProviderException);
            }
        }
        try {
            algorithmParameters.init(algorithmIdentifier.getParameters().toASN1Primitive().getEncoded());
        }
        catch (IOException iOException) {
            throw new OperatorCreationException("cannot initialise algorithm parameters: " + iOException.getMessage(), iOException);
        }
        return algorithmParameters;
    }

    MessageDigest createDigest(AlgorithmIdentifier algorithmIdentifier) throws GeneralSecurityException {
        MessageDigest messageDigest;
        try {
            messageDigest = algorithmIdentifier.getAlgorithm().equals(NISTObjectIdentifiers.id_shake256_len) ? this.helper.createMessageDigest("SHAKE256-" + ASN1Integer.getInstance(algorithmIdentifier.getParameters()).getValue()) : (algorithmIdentifier.getAlgorithm().equals(NISTObjectIdentifiers.id_shake128_len) ? this.helper.createMessageDigest("SHAKE128-" + ASN1Integer.getInstance(algorithmIdentifier.getParameters()).getValue()) : this.helper.createMessageDigest(MessageDigestUtils.getDigestName(algorithmIdentifier.getAlgorithm())));
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            if (oids.get(algorithmIdentifier.getAlgorithm()) != null) {
                String string = (String)oids.get(algorithmIdentifier.getAlgorithm());
                messageDigest = this.helper.createMessageDigest(string);
            }
            throw noSuchAlgorithmException;
        }
        return messageDigest;
    }

    Signature createSignature(AlgorithmIdentifier algorithmIdentifier) throws GeneralSecurityException {
        ASN1Sequence aSN1Sequence;
        Object object;
        Signature signature;
        String string = OperatorHelper.getSignatureName(algorithmIdentifier);
        try {
            signature = this.helper.createSignature(string);
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            if (string.endsWith("WITHRSAANDMGF1")) {
                object = string.substring(0, string.indexOf(87)) + "WITHRSASSA-PSS";
                signature = this.helper.createSignature((String)object);
            }
            throw noSuchAlgorithmException;
        }
        if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS) && this.notDefaultPSSParams(aSN1Sequence = ASN1Sequence.getInstance(algorithmIdentifier.getParameters()))) {
            try {
                object = this.helper.createAlgorithmParameters("PSS");
                ((AlgorithmParameters)object).init(aSN1Sequence.getEncoded());
                signature.setParameter(((AlgorithmParameters)object).getParameterSpec(PSSParameterSpec.class));
            }
            catch (IOException iOException) {
                throw new GeneralSecurityException("unable to process PSS parameters: " + iOException.getMessage());
            }
        }
        return signature;
    }

    Signature createRawSignature(AlgorithmIdentifier algorithmIdentifier) {
        Signature signature;
        try {
            String string = OperatorHelper.getSignatureName(algorithmIdentifier);
            string = "NONE" + string.substring(string.indexOf("WITH"));
            signature = this.helper.createSignature(string);
            if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
                AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters(string);
                AlgorithmParametersUtils.loadParameters(algorithmParameters, algorithmIdentifier.getParameters());
                PSSParameterSpec pSSParameterSpec = algorithmParameters.getParameterSpec(PSSParameterSpec.class);
                signature.setParameter(pSSParameterSpec);
            }
        }
        catch (Exception exception) {
            return null;
        }
        return signature;
    }

    private static String getSignatureName(AlgorithmIdentifier algorithmIdentifier) {
        return sigFinder.getAlgorithmName(algorithmIdentifier);
    }

    static String getDigestName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = MessageDigestUtils.getDigestName(aSN1ObjectIdentifier);
        int n = string.indexOf(45);
        if (n > 0 && !string.startsWith("SHA3")) {
            return string.substring(0, n) + string.substring(n + 1);
        }
        return string;
    }

    public X509Certificate convertCertificate(X509CertificateHolder x509CertificateHolder) throws CertificateException {
        try {
            CertificateFactory certificateFactory = this.helper.createCertificateFactory("X.509");
            return (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(x509CertificateHolder.getEncoded()));
        }
        catch (IOException iOException) {
            throw new OpCertificateException("cannot get encoded form of certificate: " + iOException.getMessage(), iOException);
        }
        catch (NoSuchProviderException noSuchProviderException) {
            throw new OpCertificateException("cannot find factory provider: " + noSuchProviderException.getMessage(), noSuchProviderException);
        }
    }

    public PublicKey convertPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws OperatorCreationException {
        try {
            KeyFactory keyFactory = this.helper.createKeyFactory(subjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId());
            return keyFactory.generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        }
        catch (IOException iOException) {
            throw new OperatorCreationException("cannot get encoded form of key: " + iOException.getMessage(), iOException);
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new OperatorCreationException("cannot create key factory: " + noSuchAlgorithmException.getMessage(), noSuchAlgorithmException);
        }
        catch (NoSuchProviderException noSuchProviderException) {
            throw new OperatorCreationException("cannot find factory provider: " + noSuchProviderException.getMessage(), noSuchProviderException);
        }
        catch (InvalidKeySpecException invalidKeySpecException) {
            throw new OperatorCreationException("cannot create key factory: " + invalidKeySpecException.getMessage(), invalidKeySpecException);
        }
    }

    String getKeyAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = (String)symmetricKeyAlgNames.get(aSN1ObjectIdentifier);
        if (string != null) {
            return string;
        }
        return aSN1ObjectIdentifier.getId();
    }

    private boolean notDefaultPSSParams(ASN1Sequence aSN1Sequence) throws GeneralSecurityException {
        if (aSN1Sequence == null || aSN1Sequence.size() == 0) {
            return false;
        }
        RSASSAPSSparams rSASSAPSSparams = RSASSAPSSparams.getInstance(aSN1Sequence);
        if (!rSASSAPSSparams.getMaskGenAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1)) {
            return true;
        }
        if (!rSASSAPSSparams.getHashAlgorithm().equals(AlgorithmIdentifier.getInstance(rSASSAPSSparams.getMaskGenAlgorithm().getParameters()))) {
            return true;
        }
        MessageDigest messageDigest = this.createDigest(rSASSAPSSparams.getHashAlgorithm());
        return rSASSAPSSparams.getSaltLength().intValue() != messageDigest.getDigestLength();
    }

    static {
        oids.put(OIWObjectIdentifiers.idSHA1, "SHA1");
        oids.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        oids.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        oids.put(NISTObjectIdentifiers.id_sha384, "SHA384");
        oids.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        oids.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
        oids.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
        oids.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(OIWObjectIdentifiers.elGamalAlgorithm, "Elgamal/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA/ECB/OAEPPadding");
        asymmetricWrapperAlgNames.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, "DESEDEWrap");
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap, "RC2Wrap");
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes128_wrap, "AESWrap");
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes192_wrap, "AESWrap");
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes256_wrap, "AESWrap");
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia128_wrap, "CamelliaWrap");
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia192_wrap, "CamelliaWrap");
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia256_wrap, "CamelliaWrap");
        symmetricWrapperAlgNames.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, "SEEDWrap");
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
        symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Integers.valueOf(192));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes128_wrap, Integers.valueOf(128));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes192_wrap, Integers.valueOf(192));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes256_wrap, Integers.valueOf(256));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia128_wrap, Integers.valueOf(128));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia192_wrap, Integers.valueOf(192));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia256_wrap, Integers.valueOf(256));
        symmetricWrapperKeySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, Integers.valueOf(128));
        symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.aes, "AES");
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes128_CBC, "AES");
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes192_CBC, "AES");
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes256_CBC, "AES");
        symmetricKeyAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
        symmetricKeyAlgNames.put(PKCSObjectIdentifiers.RC2_CBC, "RC2");
        OAEPParamsValue.add(oaepParamsMap, "RSA/ECB/OAEPWithSHA-1AndMGF1Padding", OIWObjectIdentifiers.idSHA1);
        OAEPParamsValue.add(oaepParamsMap, "RSA/ECB/OAEPWithSHA-224AndMGF1Padding", NISTObjectIdentifiers.id_sha224);
        OAEPParamsValue.add(oaepParamsMap, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", NISTObjectIdentifiers.id_sha256);
        OAEPParamsValue.add(oaepParamsMap, "RSA/ECB/OAEPWithSHA-384AndMGF1Padding", NISTObjectIdentifiers.id_sha384);
        OAEPParamsValue.add(oaepParamsMap, "RSA/ECB/OAEPWithSHA-512AndMGF1Padding", NISTObjectIdentifiers.id_sha512);
    }

    private static class OAEPParamsValue {
        private String cipherName;
        private byte[] derEncoding;

        static void add(Map map, String string, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
            try {
                RSAESOAEPparams rSAESOAEPparams = OAEPParamsValue.createOAEPParams(aSN1ObjectIdentifier);
                byte[] byArray = OAEPParamsValue.getDEREncoding(rSAESOAEPparams);
                map.put(aSN1ObjectIdentifier, new OAEPParamsValue(string, byArray));
            }
            catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        private OAEPParamsValue(String string, byte[] byArray) {
            this.cipherName = string;
            this.derEncoding = byArray;
        }

        String getCipherName() {
            return this.cipherName;
        }

        boolean matches(RSAESOAEPparams rSAESOAEPparams) throws IOException {
            return Arrays.areEqual(this.derEncoding, OAEPParamsValue.getDEREncoding(rSAESOAEPparams));
        }

        private static RSAESOAEPparams createOAEPParams(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(aSN1ObjectIdentifier, DERNull.INSTANCE);
            AlgorithmIdentifier algorithmIdentifier2 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, algorithmIdentifier);
            return new RSAESOAEPparams(algorithmIdentifier, algorithmIdentifier2, RSAESOAEPparams.DEFAULT_P_SOURCE_ALGORITHM);
        }

        private static byte[] getDEREncoding(RSAESOAEPparams rSAESOAEPparams) throws IOException {
            return rSAESOAEPparams.getEncoded("DER");
        }
    }

    private static class OpCertificateException
    extends CertificateException {
        private Throwable cause;

        public OpCertificateException(String string, Throwable throwable) {
            super(string);
            this.cause = throwable;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}

