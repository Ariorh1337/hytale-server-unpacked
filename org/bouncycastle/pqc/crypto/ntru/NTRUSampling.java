/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.pqc.crypto.ntru.PolynomialPair;
import org.bouncycastle.pqc.math.ntru.HPSPolynomial;
import org.bouncycastle.pqc.math.ntru.HRSSPolynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

class NTRUSampling {
    private final NTRUParameterSet params;

    public NTRUSampling(NTRUParameterSet nTRUParameterSet) {
        this.params = nTRUParameterSet;
    }

    public PolynomialPair sampleFg(byte[] byArray) {
        if (this.params instanceof NTRUHRSSParameterSet) {
            HRSSPolynomial hRSSPolynomial = this.sampleIidPlus(Arrays.copyOfRange(byArray, 0, this.params.sampleIidBytes()));
            HRSSPolynomial hRSSPolynomial2 = this.sampleIidPlus(Arrays.copyOfRange(byArray, this.params.sampleIidBytes(), byArray.length));
            return new PolynomialPair(hRSSPolynomial, hRSSPolynomial2);
        }
        if (this.params instanceof NTRUHPSParameterSet) {
            HPSPolynomial hPSPolynomial = (HPSPolynomial)this.sampleIid(Arrays.copyOfRange(byArray, 0, this.params.sampleIidBytes()));
            HPSPolynomial hPSPolynomial2 = this.sampleFixedType(Arrays.copyOfRange(byArray, this.params.sampleIidBytes(), byArray.length));
            return new PolynomialPair(hPSPolynomial, hPSPolynomial2);
        }
        throw new IllegalArgumentException("Invalid polynomial type");
    }

    public PolynomialPair sampleRm(byte[] byArray) {
        if (this.params instanceof NTRUHRSSParameterSet) {
            HRSSPolynomial hRSSPolynomial = (HRSSPolynomial)this.sampleIid(Arrays.copyOfRange(byArray, 0, this.params.sampleIidBytes()));
            HRSSPolynomial hRSSPolynomial2 = (HRSSPolynomial)this.sampleIid(Arrays.copyOfRange(byArray, this.params.sampleIidBytes(), byArray.length));
            return new PolynomialPair(hRSSPolynomial, hRSSPolynomial2);
        }
        if (this.params instanceof NTRUHPSParameterSet) {
            HPSPolynomial hPSPolynomial = (HPSPolynomial)this.sampleIid(Arrays.copyOfRange(byArray, 0, this.params.sampleIidBytes()));
            HPSPolynomial hPSPolynomial2 = this.sampleFixedType(Arrays.copyOfRange(byArray, this.params.sampleIidBytes(), byArray.length));
            return new PolynomialPair(hPSPolynomial, hPSPolynomial2);
        }
        throw new IllegalArgumentException("Invalid polynomial type");
    }

    public Polynomial sampleIid(byte[] byArray) {
        Polynomial polynomial = this.params.createPolynomial();
        for (int i = 0; i < this.params.n() - 1; ++i) {
            polynomial.coeffs[i] = (short)NTRUSampling.mod3(byArray[i] & 0xFF);
        }
        polynomial.coeffs[this.params.n() - 1] = 0;
        return polynomial;
    }

    public HPSPolynomial sampleFixedType(byte[] byArray) {
        int n;
        int n2 = this.params.n();
        int n3 = ((NTRUHPSParameterSet)this.params).weight();
        HPSPolynomial hPSPolynomial = new HPSPolynomial((NTRUHPSParameterSet)this.params);
        int[] nArray = new int[n2 - 1];
        for (n = 0; n < (n2 - 1) / 4; ++n) {
            nArray[4 * n + 0] = ((byArray[15 * n + 0] & 0xFF) << 2) + ((byArray[15 * n + 1] & 0xFF) << 10) + ((byArray[15 * n + 2] & 0xFF) << 18) + ((byArray[15 * n + 3] & 0xFF) << 26);
            nArray[4 * n + 1] = ((byArray[15 + n * 3] & 0xFF & 0xC0) >> 4) + ((byArray[15 * n + 4] & 0xFF) << 4) + ((byArray[15 * n + 5] & 0xFF) << 12) + ((byArray[15 * n + 6] & 0xFF) << 20) + ((byArray[15 * n + 7] & 0xFF) << 28);
            nArray[4 * n + 2] = ((byArray[15 + n * 7] & 0xFF & 0xF0) >> 2) + ((byArray[15 * n + 8] & 0xFF) << 6) + ((byArray[15 * n + 9] & 0xFF) << 14) + ((byArray[15 * n + 10] & 0xFF) << 22) + ((byArray[15 * n + 11] & 0xFF) << 30);
            nArray[4 * n + 3] = (byArray[15 * n + 11] & 0xFF & 0xFC) + ((byArray[15 * n + 12] & 0xFF) << 8) + ((byArray[15 * n + 13] & 0xFF) << 16) + ((byArray[15 * n + 14] & 0xFF) << 24);
        }
        if (n2 - 1 > (n2 - 1) / 4 * 4) {
            n = (n2 - 1) / 4;
            nArray[4 * n + 0] = ((byArray[15 * n + 0] & 0xFF) << 2) + ((byArray[15 * n + 1] & 0xFF) << 10) + ((byArray[15 * n + 2] & 0xFF) << 18) + ((byArray[15 * n + 3] & 0xFF) << 26);
            nArray[4 * n + 1] = ((byArray[15 + n * 3] & 0xFF & 0xC0) >> 4) + ((byArray[15 * n + 4] & 0xFF) << 4) + ((byArray[15 * n + 5] & 0xFF) << 12) + ((byArray[15 * n + 6] & 0xFF) << 20) + ((byArray[15 * n + 7] & 0xFF) << 28);
        }
        n = 0;
        while (n < n3 / 2) {
            int n4 = n++;
            nArray[n4] = nArray[n4] | 1;
        }
        n = n3 / 2;
        while (n < n3) {
            int n5 = n++;
            nArray[n5] = nArray[n5] | 2;
        }
        java.util.Arrays.sort(nArray);
        for (n = 0; n < n2 - 1; ++n) {
            hPSPolynomial.coeffs[n] = (short)(nArray[n] & 3);
        }
        hPSPolynomial.coeffs[n2 - 1] = 0;
        return hPSPolynomial;
    }

    public HRSSPolynomial sampleIidPlus(byte[] byArray) {
        int n;
        int n2 = this.params.n();
        int n3 = 0;
        HRSSPolynomial hRSSPolynomial = (HRSSPolynomial)this.sampleIid(byArray);
        for (n = 0; n < n2 - 1; ++n) {
            hRSSPolynomial.coeffs[n] = (short)(hRSSPolynomial.coeffs[n] | -(hRSSPolynomial.coeffs[n] >>> 1));
        }
        for (n = 0; n < n2 - 1; ++n) {
            n3 = (short)(n3 + (short)(hRSSPolynomial.coeffs[n + 1] * hRSSPolynomial.coeffs[n]));
        }
        n3 = (short)(1 | -((n3 & 0xFFFF) >>> 15));
        for (n = 0; n < n2 - 1; n += 2) {
            hRSSPolynomial.coeffs[n] = (short)(n3 * hRSSPolynomial.coeffs[n]);
        }
        for (n = 0; n < n2 - 1; ++n) {
            hRSSPolynomial.coeffs[n] = (short)(3 & (hRSSPolynomial.coeffs[n] & 0xFFFF ^ (hRSSPolynomial.coeffs[n] & 0xFFFF) >>> 15));
        }
        return hRSSPolynomial;
    }

    private static int mod3(int n) {
        return n % 3;
    }
}

