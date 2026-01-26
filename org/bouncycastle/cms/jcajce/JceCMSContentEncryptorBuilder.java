/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.JceAADStream;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCaptureStream;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.util.Strings;

public class JceCMSContentEncryptorBuilder {
    private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
    private static final byte[] hkdfSalt = Strings.toByteArray("The Cryptographic Message Syntax");
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    private SecureRandom random;
    private AlgorithmIdentifier algorithmIdentifier;
    private AlgorithmParameters algorithmParameters;
    private ASN1ObjectIdentifier kdfAlgorithm;

    public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this(aSN1ObjectIdentifier, KEY_SIZE_PROVIDER.getKeySize(aSN1ObjectIdentifier));
    }

    public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier aSN1ObjectIdentifier, int n) {
        this.encryptionOID = aSN1ObjectIdentifier;
        int n2 = KEY_SIZE_PROVIDER.getKeySize(aSN1ObjectIdentifier);
        if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.des_EDE3_CBC)) {
            if (n != 168 && n != n2) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = 168;
        } else if (aSN1ObjectIdentifier.equals(OIWObjectIdentifiers.desCBC)) {
            if (n != 56 && n != n2) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = 56;
        } else {
            if (n2 > 0 && n2 != n) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = n;
        }
    }

    public JceCMSContentEncryptorBuilder(AlgorithmIdentifier algorithmIdentifier) {
        this(algorithmIdentifier.getAlgorithm(), KEY_SIZE_PROVIDER.getKeySize(algorithmIdentifier.getAlgorithm()));
        this.algorithmIdentifier = algorithmIdentifier;
    }

    public JceCMSContentEncryptorBuilder setEnableSha256HKdf(boolean bl) {
        if (bl) {
            this.kdfAlgorithm = CMSObjectIdentifiers.id_alg_cek_hkdf_sha256;
        } else if (this.kdfAlgorithm != null) {
            if (this.kdfAlgorithm.equals(CMSObjectIdentifiers.id_alg_cek_hkdf_sha256)) {
                this.kdfAlgorithm = null;
            } else {
                throw new IllegalStateException("SHA256 HKDF not enabled");
            }
        }
        return this;
    }

    public JceCMSContentEncryptorBuilder setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceCMSContentEncryptorBuilder setProvider(String string) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(string));
        return this;
    }

    public JceCMSContentEncryptorBuilder setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public JceCMSContentEncryptorBuilder setAlgorithmParameters(AlgorithmParameters algorithmParameters) {
        this.algorithmParameters = algorithmParameters;
        return this;
    }

    public OutputEncryptor build() throws CMSException {
        KeyGenerator keyGenerator = this.helper.createKeyGenerator(this.encryptionOID);
        this.random = CryptoServicesRegistrar.getSecureRandom(this.random);
        if (this.keySize < 0) {
            keyGenerator.init(this.random);
        } else {
            keyGenerator.init(this.keySize, this.random);
        }
        return this.build(keyGenerator.generateKey());
    }

    public OutputEncryptor build(byte[] byArray) throws CMSException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(byArray, this.helper.getBaseCipherName(this.encryptionOID));
        return this.build(secretKeySpec);
    }

    public OutputEncryptor build(SecretKey secretKey) throws CMSException {
        ASN1Encodable aSN1Encodable;
        if (this.algorithmParameters != null) {
            if (this.helper.isAuthEnveloped(this.encryptionOID)) {
                return new CMSAuthOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, secretKey, this.algorithmParameters, this.random);
            }
            return new CMSOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, secretKey, this.algorithmParameters, this.random);
        }
        if (this.algorithmIdentifier != null && (aSN1Encodable = this.algorithmIdentifier.getParameters()) != null && !aSN1Encodable.equals(DERNull.INSTANCE)) {
            try {
                this.algorithmParameters = this.helper.createAlgorithmParameters(this.algorithmIdentifier.getAlgorithm());
                this.algorithmParameters.init(aSN1Encodable.toASN1Primitive().getEncoded());
            }
            catch (Exception exception) {
                throw new CMSException("unable to process provided algorithmIdentifier: " + exception.toString(), exception);
            }
        }
        if (this.helper.isAuthEnveloped(this.encryptionOID)) {
            return new CMSAuthOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, secretKey, this.algorithmParameters, this.random);
        }
        return new CMSOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, secretKey, this.algorithmParameters, this.random);
    }

    private static boolean checkForAEAD() {
        return (Boolean)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    return Cipher.class.getMethod("updateAAD", byte[].class) != null;
                }
                catch (Exception exception) {
                    return Boolean.FALSE;
                }
            }
        });
    }

    private class CMSAuthOutputEncryptor
    extends CMSOutEncryptor
    implements OutputAEADEncryptor {
        private MacCaptureStream macOut;

        CMSAuthOutputEncryptor(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2, SecretKey secretKey, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws CMSException {
            this.init(aSN1ObjectIdentifier, aSN1ObjectIdentifier2, secretKey, algorithmParameters, secureRandom);
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override
        public OutputStream getOutputStream(OutputStream outputStream) {
            AlgorithmIdentifier algorithmIdentifier = JceCMSContentEncryptorBuilder.this.kdfAlgorithm != null ? AlgorithmIdentifier.getInstance(this.algorithmIdentifier.getParameters()) : this.algorithmIdentifier;
            if (CMSAlgorithm.ChaCha20Poly1305.equals(this.algorithmIdentifier.getAlgorithm())) {
                this.macOut = new MacCaptureStream(outputStream, 16);
            } else {
                GCMParameters gCMParameters = GCMParameters.getInstance(algorithmIdentifier.getParameters());
                this.macOut = new MacCaptureStream(outputStream, gCMParameters.getIcvLen());
            }
            return new CipherOutputStream(this.macOut, this.cipher);
        }

        @Override
        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }

        @Override
        public OutputStream getAADStream() {
            if (JceCMSContentEncryptorBuilder.checkForAEAD()) {
                return new JceAADStream(this.cipher);
            }
            return null;
        }

        @Override
        public byte[] getMAC() {
            return this.macOut.getMac();
        }
    }

    private class CMSOutEncryptor {
        protected SecretKey encKey;
        protected AlgorithmIdentifier algorithmIdentifier;
        protected Cipher cipher;

        private CMSOutEncryptor() {
        }

        private void applyKdf(ASN1ObjectIdentifier aSN1ObjectIdentifier, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws CMSException {
            HKDFBytesGenerator hKDFBytesGenerator = new HKDFBytesGenerator(new SHA256Digest());
            byte[] byArray = this.encKey.getEncoded();
            try {
                hKDFBytesGenerator.init(new HKDFParameters(byArray, hkdfSalt, this.algorithmIdentifier.getEncoded("DER")));
            }
            catch (IOException iOException) {
                throw new CMSException("unable to encode enc algorithm parameters", iOException);
            }
            hKDFBytesGenerator.generateBytes(byArray, 0, byArray.length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(byArray, this.encKey.getAlgorithm());
            try {
                this.cipher.init(1, (Key)secretKeySpec, algorithmParameters, secureRandom);
            }
            catch (GeneralSecurityException generalSecurityException) {
                throw new CMSException("unable to initialize cipher: " + generalSecurityException.getMessage(), generalSecurityException);
            }
            this.algorithmIdentifier = new AlgorithmIdentifier(aSN1ObjectIdentifier, this.algorithmIdentifier);
        }

        protected void init(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2, SecretKey secretKey, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws CMSException {
            this.encKey = secretKey;
            secureRandom = CryptoServicesRegistrar.getSecureRandom(secureRandom);
            this.cipher = JceCMSContentEncryptorBuilder.this.helper.createCipher(aSN1ObjectIdentifier2);
            if (algorithmParameters == null) {
                algorithmParameters = JceCMSContentEncryptorBuilder.this.helper.generateParameters(aSN1ObjectIdentifier2, secretKey, secureRandom);
            }
            if (algorithmParameters != null) {
                this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(aSN1ObjectIdentifier2, algorithmParameters);
                if (aSN1ObjectIdentifier != null) {
                    this.applyKdf(aSN1ObjectIdentifier, algorithmParameters, secureRandom);
                } else {
                    try {
                        this.cipher.init(1, (Key)secretKey, algorithmParameters, secureRandom);
                    }
                    catch (GeneralSecurityException generalSecurityException) {
                        throw new CMSException("unable to initialize cipher: " + generalSecurityException.getMessage(), generalSecurityException);
                    }
                }
            } else {
                try {
                    this.cipher.init(1, (Key)secretKey, algorithmParameters, secureRandom);
                }
                catch (GeneralSecurityException generalSecurityException) {
                    throw new CMSException("unable to initialize cipher: " + generalSecurityException.getMessage(), generalSecurityException);
                }
                algorithmParameters = this.cipher.getParameters();
                this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(aSN1ObjectIdentifier2, algorithmParameters);
                if (aSN1ObjectIdentifier != null) {
                    this.applyKdf(aSN1ObjectIdentifier, algorithmParameters, secureRandom);
                }
            }
        }
    }

    private class CMSOutputEncryptor
    extends CMSOutEncryptor
    implements OutputEncryptor {
        CMSOutputEncryptor(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2, SecretKey secretKey, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws CMSException {
            this.init(aSN1ObjectIdentifier, aSN1ObjectIdentifier2, secretKey, algorithmParameters, secureRandom);
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }

        @Override
        public OutputStream getOutputStream(OutputStream outputStream) {
            return new CipherOutputStream(outputStream, this.cipher);
        }

        @Override
        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }
    }
}

