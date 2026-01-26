/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.Ed25519;
import com.google.crypto.tink.signature.Ed25519Parameters;
import com.google.crypto.tink.signature.Ed25519PrivateKey;
import com.google.crypto.tink.signature.internal.Ed25519SignJce;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.Random;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public final class Ed25519Sign
implements PublicKeySign {
    public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
    public static final int SECRET_KEY_LEN = 32;
    private final byte[] hashedPrivateKey;
    private final byte[] publicKey;
    private final byte[] outputPrefix;
    private final byte[] messageSuffix;

    @AccessesPartialKey
    public static PublicKeySign create(Ed25519PrivateKey key) throws GeneralSecurityException {
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Can not use Ed25519 in FIPS-mode.");
        }
        try {
            return Ed25519SignJce.create(key);
        }
        catch (GeneralSecurityException generalSecurityException) {
            byte[] byArray;
            byte[] byArray2 = key.getPrivateKeyBytes().toByteArray(InsecureSecretKeyAccess.get());
            byte[] byArray3 = key.getOutputPrefix().toByteArray();
            if (key.getParameters().getVariant().equals(Ed25519Parameters.Variant.LEGACY)) {
                byte[] byArray4 = new byte[1];
                byArray = byArray4;
                byArray4[0] = 0;
            } else {
                byArray = new byte[]{};
            }
            return new Ed25519Sign(byArray2, byArray3, byArray);
        }
    }

    private Ed25519Sign(byte[] privateKey, byte[] outputPrefix, byte[] messageSuffix) throws GeneralSecurityException {
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Can not use Ed25519 in FIPS-mode.");
        }
        if (privateKey.length != 32) {
            throw new IllegalArgumentException(String.format("Given private key's length is not %s", 32));
        }
        this.hashedPrivateKey = Ed25519.getHashedScalar(privateKey);
        this.publicKey = Ed25519.scalarMultWithBaseToBytes(this.hashedPrivateKey);
        this.outputPrefix = outputPrefix;
        this.messageSuffix = messageSuffix;
    }

    public Ed25519Sign(byte[] privateKey) throws GeneralSecurityException {
        this(privateKey, new byte[0], new byte[0]);
    }

    private byte[] noPrefixSign(byte[] data) throws GeneralSecurityException {
        return Ed25519.sign(data, this.publicKey, this.hashedPrivateKey);
    }

    @Override
    public byte[] sign(byte[] data) throws GeneralSecurityException {
        byte[] signature = this.messageSuffix.length == 0 ? this.noPrefixSign(data) : this.noPrefixSign(Bytes.concat(data, this.messageSuffix));
        if (this.outputPrefix.length == 0) {
            return signature;
        }
        return Bytes.concat(this.outputPrefix, signature);
    }

    public static final class KeyPair {
        private final byte[] publicKey;
        private final byte[] privateKey;

        private KeyPair(byte[] publicKey, byte[] privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public byte[] getPublicKey() {
            return Arrays.copyOf(this.publicKey, this.publicKey.length);
        }

        public byte[] getPrivateKey() {
            return Arrays.copyOf(this.privateKey, this.privateKey.length);
        }

        public static KeyPair newKeyPair() throws GeneralSecurityException {
            return KeyPair.newKeyPairFromSeed(Random.randBytes(32));
        }

        public static KeyPair newKeyPairFromSeed(byte[] secretSeed) throws GeneralSecurityException {
            if (secretSeed.length != 32) {
                throw new IllegalArgumentException(String.format("Given secret seed length is not %s", 32));
            }
            byte[] privateKey = secretSeed;
            byte[] publicKey = Ed25519.scalarMultWithBaseToBytes(Ed25519.getHashedScalar(privateKey));
            return new KeyPair(publicKey, privateKey);
        }
    }
}

