/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.hpke.AEAD;
import org.bouncycastle.crypto.hpke.HKDF;

public class HPKEContext {
    protected final AEAD aead;
    protected final HKDF hkdf;
    protected final byte[] exporterSecret;
    protected final byte[] suiteId;

    HPKEContext(AEAD aEAD, HKDF hKDF, byte[] byArray, byte[] byArray2) {
        this.aead = aEAD;
        this.hkdf = hKDF;
        this.exporterSecret = byArray;
        this.suiteId = byArray2;
    }

    public byte[] export(byte[] byArray, int n) {
        return this.hkdf.LabeledExpand(this.exporterSecret, this.suiteId, "sec", byArray, n);
    }

    public byte[] seal(byte[] byArray, byte[] byArray2) throws InvalidCipherTextException {
        return this.aead.seal(byArray, byArray2);
    }

    public byte[] seal(byte[] byArray, byte[] byArray2, int n, int n2) throws InvalidCipherTextException {
        return this.aead.seal(byArray, byArray2, n, n2);
    }

    public byte[] open(byte[] byArray, byte[] byArray2) throws InvalidCipherTextException {
        return this.aead.open(byArray, byArray2);
    }

    public byte[] open(byte[] byArray, byte[] byArray2, int n, int n2) throws InvalidCipherTextException {
        return this.aead.open(byArray, byArray2, n, n2);
    }

    public byte[] extract(byte[] byArray, byte[] byArray2) {
        return this.hkdf.Extract(byArray, byArray2);
    }

    public byte[] expand(byte[] byArray, byte[] byArray2, int n) {
        return this.hkdf.Expand(byArray, byArray2, n);
    }
}

