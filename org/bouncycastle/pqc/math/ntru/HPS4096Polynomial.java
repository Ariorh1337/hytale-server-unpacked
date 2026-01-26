/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.HPSPolynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;

public class HPS4096Polynomial
extends HPSPolynomial {
    public HPS4096Polynomial(NTRUHPSParameterSet nTRUHPSParameterSet) {
        super(nTRUHPSParameterSet);
    }

    @Override
    public byte[] sqToBytes(int n) {
        byte[] byArray = new byte[n];
        int n2 = this.params.q();
        for (int i = 0; i < this.params.packDegree() / 2; ++i) {
            byArray[3 * i + 0] = (byte)(HPS4096Polynomial.modQ(this.coeffs[2 * i + 0] & 0xFFFF, n2) & 0xFF);
            byArray[3 * i + 1] = (byte)(HPS4096Polynomial.modQ(this.coeffs[2 * i + 0] & 0xFFFF, n2) >>> 8 | (HPS4096Polynomial.modQ(this.coeffs[2 * i + 1] & 0xFFFF, n2) & 0xF) << 4);
            byArray[3 * i + 2] = (byte)(HPS4096Polynomial.modQ(this.coeffs[2 * i + 1] & 0xFFFF, n2) >>> 4);
        }
        return byArray;
    }

    @Override
    public void sqFromBytes(byte[] byArray) {
        for (int i = 0; i < this.params.packDegree() / 2; ++i) {
            this.coeffs[2 * i + 0] = (short)((byArray[3 * i + 0] & 0xFF) >>> 0 | ((short)(byArray[3 * i + 1] & 0xFF) & 0xF) << 8);
            this.coeffs[2 * i + 1] = (short)((byArray[3 * i + 1] & 0xFF) >>> 4 | ((short)(byArray[3 * i + 2] & 0xFF) & 0xFF) << 4);
        }
        this.coeffs[this.params.n() - 1] = 0;
    }
}

