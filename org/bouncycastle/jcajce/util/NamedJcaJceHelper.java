/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.util;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.crypto.Cipher;
import javax.crypto.ExemptionMechanism;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class NamedJcaJceHelper
implements JcaJceHelper {
    protected final String providerName;

    public NamedJcaJceHelper(String string) {
        this.providerName = string;
    }

    @Override
    public Cipher createCipher(String string) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        return Cipher.getInstance(string, this.providerName);
    }

    @Override
    public Mac createMac(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return Mac.getInstance(string, this.providerName);
    }

    @Override
    public KeyAgreement createKeyAgreement(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyAgreement.getInstance(string, this.providerName);
    }

    @Override
    public AlgorithmParameterGenerator createAlgorithmParameterGenerator(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return AlgorithmParameterGenerator.getInstance(string, this.providerName);
    }

    @Override
    public AlgorithmParameters createAlgorithmParameters(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return AlgorithmParameters.getInstance(string, this.providerName);
    }

    @Override
    public KeyGenerator createKeyGenerator(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyGenerator.getInstance(string, this.providerName);
    }

    @Override
    public KeyFactory createKeyFactory(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyFactory.getInstance(string, this.providerName);
    }

    @Override
    public SecretKeyFactory createSecretKeyFactory(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return SecretKeyFactory.getInstance(string, this.providerName);
    }

    @Override
    public KeyPairGenerator createKeyPairGenerator(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyPairGenerator.getInstance(string, this.providerName);
    }

    @Override
    public MessageDigest createDigest(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return MessageDigest.getInstance(string, this.providerName);
    }

    @Override
    public MessageDigest createMessageDigest(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return MessageDigest.getInstance(string, this.providerName);
    }

    @Override
    public Signature createSignature(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return Signature.getInstance(string, this.providerName);
    }

    @Override
    public CertificateFactory createCertificateFactory(String string) throws CertificateException, NoSuchProviderException {
        return CertificateFactory.getInstance(string, this.providerName);
    }

    @Override
    public SecureRandom createSecureRandom(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return SecureRandom.getInstance(string, this.providerName);
    }

    @Override
    public CertPathBuilder createCertPathBuilder(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return CertPathBuilder.getInstance(string, this.providerName);
    }

    @Override
    public CertPathValidator createCertPathValidator(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return CertPathValidator.getInstance(string, this.providerName);
    }

    @Override
    public CertStore createCertStore(String string, CertStoreParameters certStoreParameters) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        return CertStore.getInstance(string, certStoreParameters, this.providerName);
    }

    @Override
    public ExemptionMechanism createExemptionMechanism(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
        return ExemptionMechanism.getInstance(string, this.providerName);
    }

    @Override
    public KeyStore createKeyStore(String string) throws KeyStoreException, NoSuchProviderException {
        return KeyStore.getInstance(string, this.providerName);
    }
}

