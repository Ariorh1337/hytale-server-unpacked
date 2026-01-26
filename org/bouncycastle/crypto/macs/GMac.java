/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.GCMModeCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class GMac
implements Mac {
    private final GCMModeCipher cipher;
    private final int macSizeBits;

    public GMac(GCMModeCipher gCMModeCipher) {
        this.cipher = gCMModeCipher;
        this.macSizeBits = 128;
    }

    public GMac(GCMModeCipher gCMModeCipher, int n) {
        this.cipher = gCMModeCipher;
        this.macSizeBits = n;
    }

    @Override
    public void init(CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("GMAC requires ParametersWithIV");
        }
        ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
        byte[] byArray = parametersWithIV.getIV();
        KeyParameter keyParameter = (KeyParameter)parametersWithIV.getParameters();
        this.cipher.init(true, new AEADParameters(keyParameter, this.macSizeBits, byArray));
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getUnderlyingCipher().getAlgorithmName() + "-GMAC";
    }

    @Override
    public int getMacSize() {
        return this.macSizeBits / 8;
    }

    @Override
    public void update(byte by) throws IllegalStateException {
        this.cipher.processAADByte(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) throws DataLengthException, IllegalStateException {
        this.cipher.processAADBytes(byArray, n, n2);
    }

    @Override
    public int doFinal(byte[] byArray, int n) throws DataLengthException, IllegalStateException {
        try {
            return this.cipher.doFinal(byArray, n);
        }
        catch (InvalidCipherTextException invalidCipherTextException) {
            throw new IllegalStateException(invalidCipherTextException.toString());
        }
    }

    @Override
    public void reset() {
        this.cipher.reset();
    }
}

