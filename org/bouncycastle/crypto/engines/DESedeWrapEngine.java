/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

public class DESedeWrapEngine
implements Wrapper {
    private CBCBlockCipher engine;
    private KeyParameter param;
    private ParametersWithIV paramPlusIV;
    private byte[] iv;
    private boolean forWrapping;
    private static final byte[] IV2 = new byte[]{74, -35, -94, 44, 121, -24, 33, 5};
    Digest sha1 = DigestFactory.createSHA1();
    byte[] digest = new byte[20];

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        SecureRandom secureRandom;
        this.forWrapping = bl;
        this.engine = new CBCBlockCipher(new DESedeEngine());
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            cipherParameters = parametersWithRandom.getParameters();
            secureRandom = parametersWithRandom.getRandom();
        } else {
            secureRandom = CryptoServicesRegistrar.getSecureRandom();
        }
        if (cipherParameters instanceof KeyParameter) {
            this.param = (KeyParameter)cipherParameters;
            if (this.forWrapping) {
                this.iv = new byte[8];
                secureRandom.nextBytes(this.iv);
                this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
            }
        } else if (cipherParameters instanceof ParametersWithIV) {
            this.paramPlusIV = (ParametersWithIV)cipherParameters;
            this.iv = this.paramPlusIV.getIV();
            this.param = (KeyParameter)this.paramPlusIV.getParameters();
            if (this.forWrapping) {
                if (this.iv == null || this.iv.length != 8) {
                    throw new IllegalArgumentException("IV is not 8 octets");
                }
            } else {
                throw new IllegalArgumentException("You should not supply an IV for unwrapping");
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "DESede";
    }

    @Override
    public byte[] wrap(byte[] byArray, int n, int n2) {
        if (!this.forWrapping) {
            throw new IllegalStateException("Not initialized for wrapping");
        }
        byte[] byArray2 = new byte[n2];
        System.arraycopy(byArray, n, byArray2, 0, n2);
        byte[] byArray3 = this.calculateCMSKeyChecksum(byArray2);
        byte[] byArray4 = new byte[byArray2.length + byArray3.length];
        System.arraycopy(byArray2, 0, byArray4, 0, byArray2.length);
        System.arraycopy(byArray3, 0, byArray4, byArray2.length, byArray3.length);
        int n3 = this.engine.getBlockSize();
        if (byArray4.length % n3 != 0) {
            throw new IllegalStateException("Not multiple of block length");
        }
        this.engine.init(true, this.paramPlusIV);
        byte[] byArray5 = new byte[byArray4.length];
        for (int i = 0; i != byArray4.length; i += n3) {
            this.engine.processBlock(byArray4, i, byArray5, i);
        }
        byte[] byArray6 = new byte[this.iv.length + byArray5.length];
        System.arraycopy(this.iv, 0, byArray6, 0, this.iv.length);
        System.arraycopy(byArray5, 0, byArray6, this.iv.length, byArray5.length);
        Arrays.reverseInPlace(byArray6);
        ParametersWithIV parametersWithIV = new ParametersWithIV(this.param, IV2);
        this.engine.init(true, parametersWithIV);
        for (int i = 0; i != byArray6.length; i += n3) {
            this.engine.processBlock(byArray6, i, byArray6, i);
        }
        return byArray6;
    }

    @Override
    public byte[] unwrap(byte[] byArray, int n, int n2) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("Not set for unwrapping");
        }
        if (byArray == null) {
            throw new InvalidCipherTextException("Null pointer as ciphertext");
        }
        int n3 = this.engine.getBlockSize();
        if (n2 % n3 != 0) {
            throw new InvalidCipherTextException("Ciphertext not multiple of " + n3);
        }
        ParametersWithIV parametersWithIV = new ParametersWithIV(this.param, IV2);
        this.engine.init(false, parametersWithIV);
        byte[] byArray2 = new byte[n2];
        for (int i = 0; i != n2; i += n3) {
            this.engine.processBlock(byArray, n + i, byArray2, i);
        }
        Arrays.reverseInPlace(byArray2);
        this.iv = new byte[8];
        byte[] byArray3 = new byte[byArray2.length - 8];
        System.arraycopy(byArray2, 0, this.iv, 0, 8);
        System.arraycopy(byArray2, 8, byArray3, 0, byArray2.length - 8);
        this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
        this.engine.init(false, this.paramPlusIV);
        byte[] byArray4 = new byte[byArray3.length];
        for (int i = 0; i != byArray4.length; i += n3) {
            this.engine.processBlock(byArray3, i, byArray4, i);
        }
        byte[] byArray5 = new byte[byArray4.length - 8];
        byte[] byArray6 = new byte[8];
        System.arraycopy(byArray4, 0, byArray5, 0, byArray4.length - 8);
        System.arraycopy(byArray4, byArray4.length - 8, byArray6, 0, 8);
        if (!this.checkCMSKeyChecksum(byArray5, byArray6)) {
            throw new InvalidCipherTextException("Checksum inside ciphertext is corrupted");
        }
        return byArray5;
    }

    private byte[] calculateCMSKeyChecksum(byte[] byArray) {
        byte[] byArray2 = new byte[8];
        this.sha1.update(byArray, 0, byArray.length);
        this.sha1.doFinal(this.digest, 0);
        System.arraycopy(this.digest, 0, byArray2, 0, 8);
        return byArray2;
    }

    private boolean checkCMSKeyChecksum(byte[] byArray, byte[] byArray2) {
        return Arrays.constantTimeAreEqual(this.calculateCMSKeyChecksum(byArray), byArray2);
    }
}

