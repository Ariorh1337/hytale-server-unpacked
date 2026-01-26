/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.math.ntru.parameters;

import org.bouncycastle.pqc.math.ntru.HPSPolynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;

public abstract class NTRUHPSParameterSet
extends NTRUParameterSet {
    NTRUHPSParameterSet(int n, int n2, int n3, int n4, int n5) {
        super(n, n2, n3, n4, n5);
    }

    @Override
    public Polynomial createPolynomial() {
        return new HPSPolynomial(this);
    }

    @Override
    public int sampleFgBytes() {
        return this.sampleIidBytes() + this.sampleFixedTypeBytes();
    }

    @Override
    public int sampleRmBytes() {
        return this.sampleIidBytes() + this.sampleFixedTypeBytes();
    }

    public int weight() {
        return this.q() / 8 - 2;
    }
}

