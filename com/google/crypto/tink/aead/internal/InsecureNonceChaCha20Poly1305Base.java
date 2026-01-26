/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.aead.internal.InsecureNonceChaCha20Base;
import com.google.crypto.tink.aead.internal.Poly1305;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import javax.crypto.AEADBadTagException;

abstract class InsecureNonceChaCha20Poly1305Base {
    public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
    private final InsecureNonceChaCha20Base chacha20;
    private final InsecureNonceChaCha20Base macKeyChaCha20;

    public InsecureNonceChaCha20Poly1305Base(byte[] key) throws GeneralSecurityException {
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Can not use ChaCha20Poly1305 in FIPS-mode.");
        }
        this.chacha20 = this.newChaCha20Instance(key, 1);
        this.macKeyChaCha20 = this.newChaCha20Instance(key, 0);
    }

    abstract InsecureNonceChaCha20Base newChaCha20Instance(byte[] var1, int var2) throws InvalidKeyException;

    public byte[] encrypt(byte[] nonce, byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
        if (plaintext.length > 0x7FFFFFEF) {
            throw new GeneralSecurityException("plaintext too long");
        }
        ByteBuffer ciphertext = ByteBuffer.allocate(plaintext.length + 16);
        this.encrypt(ciphertext, nonce, plaintext, associatedData);
        return ciphertext.array();
    }

    public void encrypt(ByteBuffer output, byte[] nonce, byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
        if (output.remaining() < plaintext.length + 16) {
            throw new IllegalArgumentException("Given ByteBuffer output is too small");
        }
        int firstPosition = output.position();
        this.chacha20.encrypt(output, nonce, plaintext);
        output.position(firstPosition);
        output.limit(output.limit() - 16);
        byte[] aad = associatedData;
        if (aad == null) {
            aad = new byte[]{};
        }
        byte[] tag = Poly1305.computeMac(this.getMacKey(nonce), InsecureNonceChaCha20Poly1305Base.macDataRfc8439(aad, output));
        output.limit(output.limit() + 16);
        output.put(tag);
    }

    public byte[] decrypt(byte[] nonce, byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
        return this.decrypt(ByteBuffer.wrap(ciphertext), nonce, associatedData);
    }

    public byte[] decrypt(ByteBuffer ciphertext, byte[] nonce, byte[] associatedData) throws GeneralSecurityException {
        if (ciphertext.remaining() < 16) {
            throw new GeneralSecurityException("ciphertext too short");
        }
        int firstPosition = ciphertext.position();
        byte[] tag = new byte[16];
        ciphertext.position(ciphertext.limit() - 16);
        ciphertext.get(tag);
        ciphertext.position(firstPosition);
        ciphertext.limit(ciphertext.limit() - 16);
        byte[] aad = associatedData;
        if (aad == null) {
            aad = new byte[]{};
        }
        try {
            Poly1305.verifyMac(this.getMacKey(nonce), InsecureNonceChaCha20Poly1305Base.macDataRfc8439(aad, ciphertext), tag);
        }
        catch (GeneralSecurityException ex) {
            throw new AEADBadTagException(ex.toString());
        }
        ciphertext.position(firstPosition);
        return this.chacha20.decrypt(nonce, ciphertext);
    }

    private byte[] getMacKey(byte[] nonce) throws GeneralSecurityException {
        ByteBuffer firstBlock = this.macKeyChaCha20.chacha20Block(nonce, 0);
        byte[] result = new byte[32];
        firstBlock.get(result);
        return result;
    }

    private static byte[] macDataRfc8439(byte[] aad, ByteBuffer ciphertext) {
        int aadPaddedLen = aad.length % 16 == 0 ? aad.length : aad.length + 16 - aad.length % 16;
        int ciphertextLen = ciphertext.remaining();
        int ciphertextPaddedLen = ciphertextLen % 16 == 0 ? ciphertextLen : ciphertextLen + 16 - ciphertextLen % 16;
        ByteBuffer macData = ByteBuffer.allocate(aadPaddedLen + ciphertextPaddedLen + 16).order(ByteOrder.LITTLE_ENDIAN);
        macData.put(aad);
        macData.position(aadPaddedLen);
        macData.put(ciphertext);
        macData.position(aadPaddedLen + ciphertextPaddedLen);
        macData.putLong(aad.length);
        macData.putLong(ciphertextLen);
        return macData.array();
    }
}

