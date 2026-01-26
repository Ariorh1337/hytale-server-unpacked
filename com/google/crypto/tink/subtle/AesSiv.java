/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.daead.AesSivKey;
import com.google.crypto.tink.daead.subtle.DeterministicAeads;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.mac.internal.AesUtil;
import com.google.crypto.tink.prf.AesCmacPrfKey;
import com.google.crypto.tink.prf.AesCmacPrfParameters;
import com.google.crypto.tink.prf.Prf;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.PrfAesCmac;
import com.google.crypto.tink.subtle.SubtleUtil;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Arrays;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesSiv
implements DeterministicAead,
DeterministicAeads {
    public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
    private static final byte[] blockZero = new byte[16];
    private static final byte[] blockOne = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
    private static final int MAX_NUM_ASSOCIATED_DATA = 126;
    private final Prf cmacForS2V;
    private final byte[] aesCtrKey;
    private final byte[] outputPrefix;
    private static final ThreadLocal<Cipher> localAesCtrCipher = new ThreadLocal<Cipher>(){

        @Override
        protected Cipher initialValue() {
            try {
                return EngineFactory.CIPHER.getInstance("AES/CTR/NoPadding");
            }
            catch (GeneralSecurityException ex) {
                throw new IllegalStateException(ex);
            }
        }
    };

    @AccessesPartialKey
    public static DeterministicAeads create(AesSivKey key) throws GeneralSecurityException {
        return new AesSiv(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getOutputPrefix());
    }

    @AccessesPartialKey
    private static Prf createCmac(byte[] key) throws GeneralSecurityException {
        return PrfAesCmac.create(AesCmacPrfKey.create(AesCmacPrfParameters.create(key.length), SecretBytes.copyFrom(key, InsecureSecretKeyAccess.get())));
    }

    private AesSiv(byte[] key, com.google.crypto.tink.util.Bytes outputPrefix) throws GeneralSecurityException {
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Can not use AES-SIV in FIPS-mode.");
        }
        if (key.length != 32 && key.length != 64) {
            throw new InvalidKeyException("invalid key size: " + key.length + " bytes; key must have 32 or 64 bytes");
        }
        byte[] k1 = Arrays.copyOfRange(key, 0, key.length / 2);
        this.aesCtrKey = Arrays.copyOfRange(key, key.length / 2, key.length);
        this.cmacForS2V = AesSiv.createCmac(k1);
        this.outputPrefix = outputPrefix.toByteArray();
    }

    public AesSiv(byte[] key) throws GeneralSecurityException {
        this(key, com.google.crypto.tink.util.Bytes.copyFrom(new byte[0]));
    }

    private byte[] s2v(byte[] ... s) throws GeneralSecurityException {
        if (s.length == 0) {
            return this.cmacForS2V.compute(blockOne, 16);
        }
        byte[] result = this.cmacForS2V.compute(blockZero, 16);
        for (int i = 0; i < s.length - 1; ++i) {
            byte[] currBlock = s[i] == null ? new byte[]{} : s[i];
            result = Bytes.xor(AesUtil.dbl(result), this.cmacForS2V.compute(currBlock, 16));
        }
        byte[] lastBlock = s[s.length - 1];
        result = lastBlock.length >= 16 ? Bytes.xorEnd(lastBlock, result) : Bytes.xor(AesUtil.cmacPad(lastBlock), AesUtil.dbl(result));
        return this.cmacForS2V.compute(result, 16);
    }

    private void validateAssociatedDataLength(int associatedDataLength) throws GeneralSecurityException {
        if (associatedDataLength > 126) {
            throw new GeneralSecurityException("Too many associated datas: " + associatedDataLength + " > " + 126);
        }
    }

    private byte[] encryptInternal(byte[] plaintext, byte[] ... associatedDatas) throws GeneralSecurityException {
        this.validateAssociatedDataLength(associatedDatas.length);
        if (plaintext.length > Integer.MAX_VALUE - this.outputPrefix.length - 16) {
            throw new GeneralSecurityException("plaintext too long");
        }
        Cipher aesCtr = localAesCtrCipher.get();
        byte[][] s = (byte[][])Arrays.copyOf(associatedDatas, associatedDatas.length + 1);
        s[associatedDatas.length] = plaintext;
        byte[] computedIv = this.s2v(s);
        byte[] ivForJavaCrypto = (byte[])computedIv.clone();
        ivForJavaCrypto[8] = (byte)(ivForJavaCrypto[8] & 0x7F);
        ivForJavaCrypto[12] = (byte)(ivForJavaCrypto[12] & 0x7F);
        aesCtr.init(1, (Key)new SecretKeySpec(this.aesCtrKey, "AES"), new IvParameterSpec(ivForJavaCrypto));
        int outputSize = this.outputPrefix.length + computedIv.length + plaintext.length;
        byte[] output = Arrays.copyOf(this.outputPrefix, outputSize);
        System.arraycopy(computedIv, 0, output, this.outputPrefix.length, computedIv.length);
        int written = aesCtr.doFinal(plaintext, 0, plaintext.length, output, this.outputPrefix.length + computedIv.length);
        if (written != plaintext.length) {
            throw new GeneralSecurityException("not enough data written");
        }
        return output;
    }

    @Override
    public byte[] encryptDeterministicallyWithAssociatedDatas(byte[] plaintext, byte[] ... associatedDatas) throws GeneralSecurityException {
        return this.encryptInternal(plaintext, associatedDatas);
    }

    @Override
    public byte[] encryptDeterministically(byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
        return this.encryptInternal(plaintext, new byte[][]{associatedData});
    }

    private byte[] decryptInternal(byte[] ciphertext, byte[] ... associatedDatas) throws GeneralSecurityException {
        this.validateAssociatedDataLength(associatedDatas.length);
        if (ciphertext.length < 16 + this.outputPrefix.length) {
            throw new GeneralSecurityException("Ciphertext too short.");
        }
        if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
            throw new GeneralSecurityException("Decryption failed (OutputPrefix mismatch).");
        }
        Cipher aesCtr = localAesCtrCipher.get();
        byte[] expectedIv = Arrays.copyOfRange(ciphertext, this.outputPrefix.length, 16 + this.outputPrefix.length);
        byte[] ivForJavaCrypto = (byte[])expectedIv.clone();
        ivForJavaCrypto[8] = (byte)(ivForJavaCrypto[8] & 0x7F);
        ivForJavaCrypto[12] = (byte)(ivForJavaCrypto[12] & 0x7F);
        aesCtr.init(2, (Key)new SecretKeySpec(this.aesCtrKey, "AES"), new IvParameterSpec(ivForJavaCrypto));
        int offset = 16 + this.outputPrefix.length;
        int ctrCiphertextLen = ciphertext.length - offset;
        byte[] decryptedPt = aesCtr.doFinal(ciphertext, offset, ctrCiphertextLen);
        if (ctrCiphertextLen == 0 && decryptedPt == null && SubtleUtil.isAndroid()) {
            decryptedPt = new byte[]{};
        }
        byte[][] s = (byte[][])Arrays.copyOf(associatedDatas, associatedDatas.length + 1);
        s[associatedDatas.length] = decryptedPt;
        byte[] computedIv = this.s2v(s);
        if (Bytes.equal(expectedIv, computedIv)) {
            return decryptedPt;
        }
        throw new AEADBadTagException("Integrity check failed.");
    }

    @Override
    public byte[] decryptDeterministicallyWithAssociatedDatas(byte[] ciphertext, byte[] ... associatedDatas) throws GeneralSecurityException {
        return this.decryptInternal(ciphertext, associatedDatas);
    }

    @Override
    public byte[] decryptDeterministically(byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
        return this.decryptInternal(ciphertext, new byte[][]{associatedData});
    }
}

