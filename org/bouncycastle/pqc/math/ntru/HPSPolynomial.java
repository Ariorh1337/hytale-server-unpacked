/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;

public class HPSPolynomial
extends Polynomial {
    public HPSPolynomial(NTRUHPSParameterSet nTRUHPSParameterSet) {
        super(nTRUHPSParameterSet);
    }

    @Override
    public byte[] sqToBytes(int n) {
        int n2;
        int n3;
        byte[] byArray = new byte[n];
        short[] sArray = new short[8];
        for (n3 = 0; n3 < this.params.packDegree() / 8; ++n3) {
            for (n2 = 0; n2 < 8; ++n2) {
                sArray[n2] = (short)HPSPolynomial.modQ(this.coeffs[8 * n3 + n2] & 0xFFFF, this.params.q());
            }
            byArray[11 * n3 + 0] = (byte)(sArray[0] & 0xFF);
            byArray[11 * n3 + 1] = (byte)(sArray[0] >>> 8 | (sArray[1] & 0x1F) << 3);
            byArray[11 * n3 + 2] = (byte)(sArray[1] >>> 5 | (sArray[2] & 3) << 6);
            byArray[11 * n3 + 3] = (byte)(sArray[2] >>> 2 & 0xFF);
            byArray[11 * n3 + 4] = (byte)(sArray[2] >>> 10 | (sArray[3] & 0x7F) << 1);
            byArray[11 * n3 + 5] = (byte)(sArray[3] >>> 7 | (sArray[4] & 0xF) << 4);
            byArray[11 * n3 + 6] = (byte)(sArray[4] >>> 4 | (sArray[5] & 1) << 7);
            byArray[11 * n3 + 7] = (byte)(sArray[5] >>> 1 & 0xFF);
            byArray[11 * n3 + 8] = (byte)(sArray[5] >>> 9 | (sArray[6] & 0x3F) << 2);
            byArray[11 * n3 + 9] = (byte)(sArray[6] >>> 6 | (sArray[7] & 7) << 5);
            byArray[11 * n3 + 10] = (byte)(sArray[7] >>> 3);
        }
        for (n2 = 0; n2 < this.params.packDegree() - 8 * n3; ++n2) {
            sArray[n2] = (short)HPSPolynomial.modQ(this.coeffs[8 * n3 + n2] & 0xFFFF, this.params.q());
        }
        while (n2 < 8) {
            sArray[n2] = 0;
            ++n2;
        }
        switch (this.params.packDegree() & 7) {
            case 4: {
                byArray[11 * n3 + 0] = (byte)(sArray[0] & 0xFF);
                byArray[11 * n3 + 1] = (byte)(sArray[0] >>> 8 | (sArray[1] & 0x1F) << 3);
                byArray[11 * n3 + 2] = (byte)(sArray[1] >>> 5 | (sArray[2] & 3) << 6);
                byArray[11 * n3 + 3] = (byte)(sArray[2] >>> 2 & 0xFF);
                byArray[11 * n3 + 4] = (byte)(sArray[2] >>> 10 | (sArray[3] & 0x7F) << 1);
                byArray[11 * n3 + 5] = (byte)(sArray[3] >>> 7 | (sArray[4] & 0xF) << 4);
                break;
            }
            case 2: {
                byArray[11 * n3 + 0] = (byte)(sArray[0] & 0xFF);
                byArray[11 * n3 + 1] = (byte)(sArray[0] >>> 8 | (sArray[1] & 0x1F) << 3);
                byArray[11 * n3 + 2] = (byte)(sArray[1] >>> 5 | (sArray[2] & 3) << 6);
            }
        }
        return byArray;
    }

    @Override
    public void sqFromBytes(byte[] byArray) {
        int n;
        int n2 = this.coeffs.length;
        for (n = 0; n < this.params.packDegree() / 8; ++n) {
            this.coeffs[8 * n + 0] = (short)((byArray[11 * n + 0] & 0xFF) >>> 0 | ((short)(byArray[11 * n + 1] & 0xFF) & 7) << 8);
            this.coeffs[8 * n + 1] = (short)((byArray[11 * n + 1] & 0xFF) >>> 3 | ((short)(byArray[11 * n + 2] & 0xFF) & 0x3F) << 5);
            this.coeffs[8 * n + 2] = (short)((byArray[11 * n + 2] & 0xFF) >>> 6 | ((short)(byArray[11 * n + 3] & 0xFF) & 0xFF) << 2 | ((short)(byArray[11 * n + 4] & 0xFF) & 1) << 10);
            this.coeffs[8 * n + 3] = (short)((byArray[11 * n + 4] & 0xFF) >>> 1 | ((short)(byArray[11 * n + 5] & 0xFF) & 0xF) << 7);
            this.coeffs[8 * n + 4] = (short)((byArray[11 * n + 5] & 0xFF) >>> 4 | ((short)(byArray[11 * n + 6] & 0xFF) & 0x7F) << 4);
            this.coeffs[8 * n + 5] = (short)((byArray[11 * n + 6] & 0xFF) >>> 7 | ((short)(byArray[11 * n + 7] & 0xFF) & 0xFF) << 1 | ((short)(byArray[11 * n + 8] & 0xFF) & 3) << 9);
            this.coeffs[8 * n + 6] = (short)((byArray[11 * n + 8] & 0xFF) >>> 2 | ((short)(byArray[11 * n + 9] & 0xFF) & 0x1F) << 6);
            this.coeffs[8 * n + 7] = (short)((byArray[11 * n + 9] & 0xFF) >>> 5 | ((short)(byArray[11 * n + 10] & 0xFF) & 0xFF) << 3);
        }
        switch (this.params.packDegree() & 7) {
            case 4: {
                this.coeffs[8 * n + 0] = (short)((byArray[11 * n + 0] & 0xFF) >>> 0 | ((short)(byArray[11 * n + 1] & 0xFF) & 7) << 8);
                this.coeffs[8 * n + 1] = (short)((byArray[11 * n + 1] & 0xFF) >>> 3 | ((short)(byArray[11 * n + 2] & 0xFF) & 0x3F) << 5);
                this.coeffs[8 * n + 2] = (short)((byArray[11 * n + 2] & 0xFF) >>> 6 | ((short)(byArray[11 * n + 3] & 0xFF) & 0xFF) << 2 | ((short)(byArray[11 * n + 4] & 0xFF) & 1) << 10);
                this.coeffs[8 * n + 3] = (short)((byArray[11 * n + 4] & 0xFF) >>> 1 | ((short)(byArray[11 * n + 5] & 0xFF) & 0xF) << 7);
                break;
            }
            case 2: {
                this.coeffs[8 * n + 0] = (short)((byArray[11 * n + 0] & 0xFF) >>> 0 | ((short)(byArray[11 * n + 1] & 0xFF) & 7) << 8);
                this.coeffs[8 * n + 1] = (short)((byArray[11 * n + 1] & 0xFF) >>> 3 | ((short)(byArray[11 * n + 2] & 0xFF) & 0x3F) << 5);
            }
        }
        this.coeffs[n2 - 1] = 0;
    }

    @Override
    public void lift(Polynomial polynomial) {
        int n = this.coeffs.length;
        System.arraycopy(polynomial.coeffs, 0, this.coeffs, 0, n);
        this.z3ToZq();
    }
}

