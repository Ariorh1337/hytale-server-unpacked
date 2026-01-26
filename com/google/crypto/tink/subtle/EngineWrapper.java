/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Signature;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;

public interface EngineWrapper<T> {
    public T getInstance(String var1, Provider var2) throws GeneralSecurityException;

    public static class TKeyAgreement
    implements EngineWrapper<KeyAgreement> {
        @Override
        public KeyAgreement getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
            if (provider == null) {
                return KeyAgreement.getInstance(algorithm);
            }
            return KeyAgreement.getInstance(algorithm, provider);
        }
    }

    public static class TKeyFactory
    implements EngineWrapper<KeyFactory> {
        @Override
        public KeyFactory getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
            if (provider == null) {
                return KeyFactory.getInstance(algorithm);
            }
            return KeyFactory.getInstance(algorithm, provider);
        }
    }

    public static class TSignature
    implements EngineWrapper<Signature> {
        @Override
        public Signature getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
            if (provider == null) {
                return Signature.getInstance(algorithm);
            }
            return Signature.getInstance(algorithm, provider);
        }
    }

    public static class TMessageDigest
    implements EngineWrapper<MessageDigest> {
        @Override
        public MessageDigest getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
            if (provider == null) {
                return MessageDigest.getInstance(algorithm);
            }
            return MessageDigest.getInstance(algorithm, provider);
        }
    }

    public static class TKeyPairGenerator
    implements EngineWrapper<KeyPairGenerator> {
        @Override
        public KeyPairGenerator getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
            if (provider == null) {
                return KeyPairGenerator.getInstance(algorithm);
            }
            return KeyPairGenerator.getInstance(algorithm, provider);
        }
    }

    public static class TMac
    implements EngineWrapper<Mac> {
        @Override
        public Mac getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
            if (provider == null) {
                return Mac.getInstance(algorithm);
            }
            return Mac.getInstance(algorithm, provider);
        }
    }

    public static class TCipher
    implements EngineWrapper<Cipher> {
        @Override
        public Cipher getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
            if (provider == null) {
                return Cipher.getInstance(algorithm);
            }
            return Cipher.getInstance(algorithm, provider);
        }
    }
}

