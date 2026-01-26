/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.math.ntru.parameters;

import org.bouncycastle.pqc.math.ntru.Polynomial;

public abstract class NTRUParameterSet {
    private final int n;
    private final int logQ;
    private final int seedBytes;
    private final int prfKeyBytes;
    private final int sharedKeyBytes;

    public NTRUParameterSet(int n, int n2, int n3, int n4, int n5) {
        this.n = n;
        this.logQ = n2;
        this.seedBytes = n3;
        this.prfKeyBytes = n4;
        this.sharedKeyBytes = n5;
    }

    public abstract Polynomial createPolynomial();

    public int n() {
        return this.n;
    }

    public int logQ() {
        return this.logQ;
    }

    public int q() {
        return 1 << this.logQ;
    }

    public int seedBytes() {
        return this.seedBytes;
    }

    public int prfKeyBytes() {
        return this.prfKeyBytes;
    }

    public int sharedKeyBytes() {
        return this.sharedKeyBytes;
    }

    public int sampleIidBytes() {
        return this.n - 1;
    }

    public int sampleFixedTypeBytes() {
        return (30 * (this.n - 1) + 7) / 8;
    }

    public abstract int sampleFgBytes();

    public abstract int sampleRmBytes();

    public int packDegree() {
        return this.n - 1;
    }

    public int packTrinaryBytes() {
        return (this.packDegree() + 4) / 5;
    }

    public int owcpaMsgBytes() {
        return 2 * this.packTrinaryBytes();
    }

    public int owcpaPublicKeyBytes() {
        return (this.logQ * this.packDegree() + 7) / 8;
    }

    public int owcpaSecretKeyBytes() {
        return 2 * this.packTrinaryBytes() + this.owcpaPublicKeyBytes();
    }

    public int owcpaBytes() {
        return (this.logQ * this.packDegree() + 7) / 8;
    }

    public int ntruPublicKeyBytes() {
        return this.owcpaPublicKeyBytes();
    }

    public int ntruSecretKeyBytes() {
        return this.owcpaSecretKeyBytes() + this.prfKeyBytes;
    }

    public int ntruCiphertextBytes() {
        return this.owcpaBytes();
    }
}

