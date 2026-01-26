/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.util.Arrays;

public class NTRULPRimePrivateKeyParameters
extends NTRULPRimeKeyParameters {
    private final byte[] enca;
    private final byte[] pk;
    private final byte[] rho;
    private final byte[] hash;

    public NTRULPRimePrivateKeyParameters(NTRULPRimeParameters nTRULPRimeParameters, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        super(true, nTRULPRimeParameters);
        this.enca = Arrays.clone(byArray);
        this.pk = Arrays.clone(byArray2);
        this.rho = Arrays.clone(byArray3);
        this.hash = Arrays.clone(byArray4);
    }

    public byte[] getEnca() {
        return Arrays.clone(this.enca);
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
        System.arraycopy(this.enca, 0, byArray, 0, this.enca.length);
        System.arraycopy(this.pk, 0, byArray, this.enca.length, this.pk.length);
        System.arraycopy(this.rho, 0, byArray, this.enca.length + this.pk.length, this.rho.length);
        System.arraycopy(this.hash, 0, byArray, this.enca.length + this.pk.length + this.rho.length, this.hash.length);
        return byArray;
    }
}

