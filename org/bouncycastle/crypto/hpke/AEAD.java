/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.modes.ChaCha20Poly1305;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Pack;

public class AEAD {
    private final short aeadId;
    private final byte[] key;
    private final byte[] baseNonce;
    private long seq = 0L;
    private AEADCipher cipher;

    public AEAD(short s, byte[] byArray, byte[] byArray2) {
        this.key = byArray;
        this.baseNonce = byArray2;
        this.aeadId = s;
        this.seq = 0L;
        switch (s) {
            case 1: 
            case 2: {
                this.cipher = GCMBlockCipher.newInstance(AESEngine.newInstance());
                break;
            }
            case 3: {
                this.cipher = new ChaCha20Poly1305();
                break;
            }
        }
    }

    public byte[] seal(byte[] byArray, byte[] byArray2) throws InvalidCipherTextException {
        return this.process(true, byArray, byArray2, 0, byArray2.length);
    }

    public byte[] seal(byte[] byArray, byte[] byArray2, int n, int n2) throws InvalidCipherTextException {
        Arrays.validateSegment(byArray2, n, n2);
        return this.process(true, byArray, byArray2, n, n2);
    }

    public byte[] open(byte[] byArray, byte[] byArray2) throws InvalidCipherTextException {
        return this.process(false, byArray, byArray2, 0, byArray2.length);
    }

    public byte[] open(byte[] byArray, byte[] byArray2, int n, int n2) throws InvalidCipherTextException {
        Arrays.validateSegment(byArray2, n, n2);
        return this.process(false, byArray, byArray2, n, n2);
    }

    private byte[] computeNonce() {
        byte[] byArray = Pack.longToBigEndian(this.seq++);
        byte[] byArray2 = Arrays.clone(this.baseNonce);
        Bytes.xorTo(8, byArray, 0, byArray2, byArray2.length - 8);
        return byArray2;
    }

    private byte[] process(boolean bl, byte[] byArray, byte[] byArray2, int n, int n2) throws InvalidCipherTextException {
        ParametersWithIV parametersWithIV;
        switch (this.aeadId) {
            case 1: 
            case 2: 
            case 3: {
                parametersWithIV = new ParametersWithIV(new KeyParameter(this.key), this.computeNonce());
                break;
            }
            default: {
                throw new IllegalStateException("Export only mode, cannot be used to seal/open");
            }
        }
        this.cipher.init(bl, parametersWithIV);
        this.cipher.processAADBytes(byArray, 0, byArray.length);
        byte[] byArray3 = new byte[this.cipher.getOutputSize(n2)];
        int n3 = this.cipher.processBytes(byArray2, n, n2, byArray3, 0);
        n3 += this.cipher.doFinal(byArray3, n3);
        if (n3 != byArray3.length) {
            throw new IllegalStateException();
        }
        return byArray3;
    }
}

