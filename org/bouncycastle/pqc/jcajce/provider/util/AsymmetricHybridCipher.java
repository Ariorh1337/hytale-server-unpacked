/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.pqc.jcajce.provider.util.CipherSpiExt;

public abstract class AsymmetricHybridCipher
extends CipherSpiExt {
    protected AlgorithmParameterSpec paramSpec;

    @Override
    protected final void setMode(String string) {
    }

    @Override
    protected final void setPadding(String string) {
    }

    @Override
    public final byte[] getIV() {
        return null;
    }

    @Override
    public final int getBlockSize() {
        return 0;
    }

    @Override
    public final AlgorithmParameterSpec getParameters() {
        return this.paramSpec;
    }

    @Override
    public final int getOutputSize(int n) {
        return this.opMode == 1 ? this.encryptOutputSize(n) : this.decryptOutputSize(n);
    }

    public final void initEncrypt(Key key) throws InvalidKeyException {
        try {
            this.initEncrypt(key, null, CryptoServicesRegistrar.getSecureRandom());
        }
        catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }

    public final void initEncrypt(Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.initEncrypt(key, null, secureRandom);
        }
        catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }

    public final void initEncrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.initEncrypt(key, algorithmParameterSpec, CryptoServicesRegistrar.getSecureRandom());
    }

    @Override
    public final void initEncrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.opMode = 1;
        this.initCipherEncrypt(key, algorithmParameterSpec, secureRandom);
    }

    public final void initDecrypt(Key key) throws InvalidKeyException {
        try {
            this.initDecrypt(key, null);
        }
        catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }

    @Override
    public final void initDecrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.opMode = 2;
        this.initCipherDecrypt(key, algorithmParameterSpec);
    }

    @Override
    public abstract byte[] update(byte[] var1, int var2, int var3);

    @Override
    public final int update(byte[] byArray, int n, int n2, byte[] byArray2, int n3) throws ShortBufferException {
        if (byArray2.length < this.getOutputSize(n2)) {
            throw new ShortBufferException("output");
        }
        byte[] byArray3 = this.update(byArray, n, n2);
        System.arraycopy(byArray3, 0, byArray2, n3, byArray3.length);
        return byArray3.length;
    }

    @Override
    public abstract byte[] doFinal(byte[] var1, int var2, int var3) throws BadPaddingException;

    @Override
    public final int doFinal(byte[] byArray, int n, int n2, byte[] byArray2, int n3) throws ShortBufferException, BadPaddingException {
        if (byArray2.length < this.getOutputSize(n2)) {
            throw new ShortBufferException("Output buffer too short.");
        }
        byte[] byArray3 = this.doFinal(byArray, n, n2);
        System.arraycopy(byArray3, 0, byArray2, n3, byArray3.length);
        return byArray3.length;
    }

    protected abstract int encryptOutputSize(int var1);

    protected abstract int decryptOutputSize(int var1);

    protected abstract void initCipherEncrypt(Key var1, AlgorithmParameterSpec var2, SecureRandom var3) throws InvalidKeyException, InvalidAlgorithmParameterException;

    protected abstract void initCipherDecrypt(Key var1, AlgorithmParameterSpec var2) throws InvalidKeyException, InvalidAlgorithmParameterException;
}

