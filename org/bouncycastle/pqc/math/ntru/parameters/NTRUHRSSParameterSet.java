/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.math.ntru.parameters;

import org.bouncycastle.pqc.math.ntru.HRSS1373Polynomial;
import org.bouncycastle.pqc.math.ntru.HRSSPolynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;

public abstract class NTRUHRSSParameterSet
extends NTRUParameterSet {
    NTRUHRSSParameterSet(int n, int n2, int n3, int n4, int n5) {
        super(n, n2, n3, n4, n5);
    }

    @Override
    public Polynomial createPolynomial() {
        return this.n() == 1373 ? new HRSS1373Polynomial(this) : new HRSSPolynomial(this);
    }

    @Override
    public int sampleFgBytes() {
        return 2 * this.sampleIidBytes();
    }

    @Override
    public int sampleRmBytes() {
        return 2 * this.sampleIidBytes();
    }
}

