/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.threshold;

import org.bouncycastle.crypto.threshold.PolynomialNative;
import org.bouncycastle.crypto.threshold.PolynomialTable;
import org.bouncycastle.crypto.threshold.ShamirSecretSplitter;

abstract class Polynomial {
    Polynomial() {
    }

    public static Polynomial newInstance(ShamirSecretSplitter.Algorithm algorithm, ShamirSecretSplitter.Mode mode) {
        if (mode == ShamirSecretSplitter.Mode.Native) {
            return new PolynomialNative(algorithm);
        }
        return new PolynomialTable(algorithm);
    }

    protected abstract byte gfMul(int var1, int var2);

    protected abstract byte gfDiv(int var1, int var2);

    protected byte gfPow(int n, byte by) {
        int n2 = 1;
        for (int i = 0; i < 8; ++i) {
            if ((by & 1 << i) != 0) {
                n2 = this.gfMul(n2 & 0xFF, n & 0xFF);
            }
            n = this.gfMul(n & 0xFF, n & 0xFF);
        }
        return (byte)n2;
    }

    public byte[] gfVecMul(byte[] byArray, byte[][] byArray2) {
        byte[] byArray3 = new byte[byArray2[0].length];
        for (int i = 0; i < byArray2[0].length; ++i) {
            int n = 0;
            for (int j = 0; j < byArray.length; ++j) {
                n ^= this.gfMul(byArray[j] & 0xFF, byArray2[j][i] & 0xFF);
            }
            byArray3[i] = (byte)n;
        }
        return byArray3;
    }
}

