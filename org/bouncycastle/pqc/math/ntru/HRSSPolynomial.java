/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSSParameterSet;

public class HRSSPolynomial
extends Polynomial {
    public HRSSPolynomial(NTRUHRSSParameterSet nTRUHRSSParameterSet) {
        super(nTRUHRSSParameterSet);
    }

    @Override
    public byte[] sqToBytes(int n) {
        int n2;
        int n3;
        byte[] byArray = new byte[n];
        short[] sArray = new short[8];
        for (n3 = 0; n3 < this.params.packDegree() / 8; ++n3) {
            for (n2 = 0; n2 < 8; ++n2) {
                sArray[n2] = (short)HRSSPolynomial.modQ(this.coeffs[8 * n3 + n2] & 0xFFFF, this.params.q());
            }
            byArray[13 * n3 + 0] = (byte)(sArray[0] & 0xFF);
            byArray[13 * n3 + 1] = (byte)(sArray[0] >>> 8 | (sArray[1] & 7) << 5);
            byArray[13 * n3 + 2] = (byte)(sArray[1] >>> 3 & 0xFF);
            byArray[13 * n3 + 3] = (byte)(sArray[1] >>> 11 | (sArray[2] & 0x3F) << 2);
            byArray[13 * n3 + 4] = (byte)(sArray[2] >>> 6 | (sArray[3] & 1) << 7);
            byArray[13 * n3 + 5] = (byte)(sArray[3] >>> 1 & 0xFF);
            byArray[13 * n3 + 6] = (byte)(sArray[3] >>> 9 | (sArray[4] & 0xF) << 4);
            byArray[13 * n3 + 7] = (byte)(sArray[4] >>> 4 & 0xFF);
            byArray[13 * n3 + 8] = (byte)(sArray[4] >>> 12 | (sArray[5] & 0x7F) << 1);
            byArray[13 * n3 + 9] = (byte)(sArray[5] >>> 7 | (sArray[6] & 3) << 6);
            byArray[13 * n3 + 10] = (byte)(sArray[6] >>> 2 & 0xFF);
            byArray[13 * n3 + 11] = (byte)(sArray[6] >>> 10 | (sArray[7] & 0x1F) << 3);
            byArray[13 * n3 + 12] = (byte)(sArray[7] >>> 5);
        }
        for (n2 = 0; n2 < this.params.packDegree() - 8 * n3; ++n2) {
            sArray[n2] = (short)HRSSPolynomial.modQ(this.coeffs[8 * n3 + n2] & 0xFFFF, this.params.q());
        }
        while (n2 < 8) {
            sArray[n2] = 0;
            ++n2;
        }
        switch (this.params.packDegree() - 8 * (this.params.packDegree() / 8)) {
            case 4: {
                byArray[13 * n3 + 0] = (byte)(sArray[0] & 0xFF);
                byArray[13 * n3 + 1] = (byte)(sArray[0] >>> 8 | (sArray[1] & 7) << 5);
                byArray[13 * n3 + 2] = (byte)(sArray[1] >>> 3 & 0xFF);
                byArray[13 * n3 + 3] = (byte)(sArray[1] >>> 11 | (sArray[2] & 0x3F) << 2);
                byArray[13 * n3 + 4] = (byte)(sArray[2] >>> 6 | (sArray[3] & 1) << 7);
                byArray[13 * n3 + 5] = (byte)(sArray[3] >>> 1 & 0xFF);
                byArray[13 * n3 + 6] = (byte)(sArray[3] >>> 9 | (sArray[4] & 0xF) << 4);
            }
            case 2: {
                byArray[13 * n3 + 0] = (byte)(sArray[0] & 0xFF);
                byArray[13 * n3 + 1] = (byte)(sArray[0] >>> 8 | (sArray[1] & 7) << 5);
                byArray[13 * n3 + 2] = (byte)(sArray[1] >>> 3 & 0xFF);
                byArray[13 * n3 + 3] = (byte)(sArray[1] >>> 11 | (sArray[2] & 0x3F) << 2);
            }
        }
        return byArray;
    }

    @Override
    public void sqFromBytes(byte[] byArray) {
        int n;
        for (n = 0; n < this.params.packDegree() / 8; ++n) {
            this.coeffs[8 * n + 0] = (short)(byArray[13 * n + 0] & 0xFF | ((short)(byArray[13 * n + 1] & 0xFF) & 0x1F) << 8);
            this.coeffs[8 * n + 1] = (short)((byArray[13 * n + 1] & 0xFF) >>> 5 | (short)(byArray[13 * n + 2] & 0xFF) << 3 | ((short)(byArray[13 * n + 3] & 0xFF) & 3) << 11);
            this.coeffs[8 * n + 2] = (short)((byArray[13 * n + 3] & 0xFF) >>> 2 | ((short)(byArray[13 * n + 4] & 0xFF) & 0x7F) << 6);
            this.coeffs[8 * n + 3] = (short)((byArray[13 * n + 4] & 0xFF) >>> 7 | (short)(byArray[13 * n + 5] & 0xFF) << 1 | ((short)(byArray[13 * n + 6] & 0xFF) & 0xF) << 9);
            this.coeffs[8 * n + 4] = (short)((byArray[13 * n + 6] & 0xFF) >>> 4 | (short)(byArray[13 * n + 7] & 0xFF) << 4 | ((short)(byArray[13 * n + 8] & 0xFF) & 1) << 12);
            this.coeffs[8 * n + 5] = (short)((byArray[13 * n + 8] & 0xFF) >>> 1 | ((short)(byArray[13 * n + 9] & 0xFF) & 0x3F) << 7);
            this.coeffs[8 * n + 6] = (short)((byArray[13 * n + 9] & 0xFF) >>> 6 | (short)(byArray[13 * n + 10] & 0xFF) << 2 | ((short)(byArray[13 * n + 11] & 0xFF) & 7) << 10);
            this.coeffs[8 * n + 7] = (short)((byArray[13 * n + 11] & 0xFF) >>> 3 | (short)(byArray[13 * n + 12] & 0xFF) << 5);
        }
        switch (this.params.packDegree() & 7) {
            case 4: {
                this.coeffs[8 * n + 0] = (short)(byArray[13 * n + 0] & 0xFF | ((short)(byArray[13 * n + 1] & 0xFF) & 0x1F) << 8);
                this.coeffs[8 * n + 1] = (short)((byArray[13 * n + 1] & 0xFF) >>> 5 | (short)(byArray[13 * n + 2] & 0xFF) << 3 | ((short)(byArray[13 * n + 3] & 0xFF) & 3) << 11);
                this.coeffs[8 * n + 2] = (short)((byArray[13 * n + 3] & 0xFF) >>> 2 | ((short)(byArray[13 * n + 4] & 0xFF) & 0x7F) << 6);
                this.coeffs[8 * n + 3] = (short)((byArray[13 * n + 4] & 0xFF) >>> 7 | (short)(byArray[13 * n + 5] & 0xFF) << 1 | ((short)(byArray[13 * n + 6] & 0xFF) & 0xF) << 9);
                break;
            }
            case 2: {
                this.coeffs[8 * n + 0] = (short)(byArray[13 * n + 0] & 0xFF | ((short)(byArray[13 * n + 1] & 0xFF) & 0x1F) << 8);
                this.coeffs[8 * n + 1] = (short)((byArray[13 * n + 1] & 0xFF) >>> 5 | (short)(byArray[13 * n + 2] & 0xFF) << 3 | ((short)(byArray[13 * n + 3] & 0xFF) & 3) << 11);
            }
        }
        this.coeffs[this.params.n() - 1] = 0;
    }

    @Override
    public void lift(Polynomial polynomial) {
        int n;
        int n2 = this.coeffs.length;
        Polynomial polynomial2 = this.params.createPolynomial();
        short s = (short)(3 - n2 % 3);
        polynomial2.coeffs[0] = (short)(polynomial.coeffs[0] * (2 - s) + polynomial.coeffs[1] * 0 + polynomial.coeffs[2] * s);
        polynomial2.coeffs[1] = (short)(polynomial.coeffs[1] * (2 - s) + polynomial.coeffs[2] * 0);
        polynomial2.coeffs[2] = (short)(polynomial.coeffs[2] * (2 - s));
        short s2 = 0;
        for (n = 3; n < n2; ++n) {
            polynomial2.coeffs[0] = (short)(polynomial2.coeffs[0] + polynomial.coeffs[n] * (s2 + 2 * s));
            polynomial2.coeffs[1] = (short)(polynomial2.coeffs[1] + polynomial.coeffs[n] * (s2 + s));
            polynomial2.coeffs[2] = (short)(polynomial2.coeffs[2] + polynomial.coeffs[n] * s2);
            s2 = (short)((s2 + s) % 3);
        }
        polynomial2.coeffs[1] = (short)(polynomial2.coeffs[1] + polynomial.coeffs[0] * (s2 + s));
        polynomial2.coeffs[2] = (short)(polynomial2.coeffs[2] + polynomial.coeffs[0] * s2);
        polynomial2.coeffs[2] = (short)(polynomial2.coeffs[2] + polynomial.coeffs[1] * (s2 + s));
        for (n = 3; n < n2; ++n) {
            polynomial2.coeffs[n] = (short)(polynomial2.coeffs[n - 3] + 2 * (polynomial.coeffs[n] + polynomial.coeffs[n - 1] + polynomial.coeffs[n - 2]));
        }
        polynomial2.mod3PhiN();
        polynomial2.z3ToZq();
        this.coeffs[0] = -polynomial2.coeffs[0];
        for (n = 0; n < n2 - 1; ++n) {
            this.coeffs[n + 1] = (short)(polynomial2.coeffs[n] - polynomial2.coeffs[n + 1]);
        }
    }
}

