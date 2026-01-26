/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.util.Arrays;

public class SNTRUPrimePrivateKeyParameters
extends SNTRUPrimeKeyParameters {
    private final byte[] f;
    private final byte[] ginv;
    private final byte[] pk;
    private final byte[] rho;
    private final byte[] hash;

    public SNTRUPrimePrivateKeyParameters(SNTRUPrimeParameters sNTRUPrimeParameters, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5) {
        super(true, sNTRUPrimeParameters);
        this.f = Arrays.clone(byArray);
        this.ginv = Arrays.clone(byArray2);
        this.pk = Arrays.clone(byArray3);
        this.rho = Arrays.clone(byArray4);
        this.hash = Arrays.clone(byArray5);
    }

    public byte[] getF() {
        return Arrays.clone(this.f);
    }

    public byte[] getGinv() {
        return Arrays.clone(this.ginv);
    }

    public byte[] getPk() {
        return Arrays.clone(this.pk);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getHash() {
        return Arrays.clone(this.hash);
    }

    public byte[] getEncoded() {
        byte[] byArray = new byte[this.getParameters().getPrivateKeyBytes()];
        System.arraycopy(this.f, 0, byArray, 0, this.f.length);
        System.arraycopy(this.ginv, 0, byArray, this.f.length, this.ginv.length);
        System.arraycopy(this.pk, 0, byArray, this.f.length + this.ginv.length, this.pk.length);
        System.arraycopy(this.rho, 0, byArray, this.f.length + this.ginv.length + this.pk.length, this.rho.length);
        System.arraycopy(this.hash, 0, byArray, this.f.length + this.ginv.length + this.pk.length + this.rho.length, this.hash.length);
        return byArray;
    }
}

