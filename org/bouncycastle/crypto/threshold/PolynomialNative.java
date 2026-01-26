/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.threshold;

import org.bouncycastle.crypto.threshold.Polynomial;
import org.bouncycastle.crypto.threshold.ShamirSecretSplitter;

class PolynomialNative
extends Polynomial {
    private final int IRREDUCIBLE;

    public PolynomialNative(ShamirSecretSplitter.Algorithm algorithm) {
        switch (algorithm) {
            case AES: {
                this.IRREDUCIBLE = 283;
                break;
            }
            case RSA: {
                this.IRREDUCIBLE = 285;
                break;
            }
            default: {
                throw new IllegalArgumentException("The algorithm is not correct");
            }
        }
    }

    @Override
    protected byte gfMul(int n, int n2) {
        int n3 = 0;
        while (n2 > 0) {
            if ((n2 & 1) != 0) {
                n3 ^= n;
            }
            if (((n <<= 1) & 0x100) != 0) {
                n ^= this.IRREDUCIBLE;
            }
            n2 >>= 1;
        }
        while (n3 >= 256) {
            if ((n3 & 0x100) != 0) {
                n3 ^= this.IRREDUCIBLE;
            }
            n3 <<= 1;
        }
        return (byte)(n3 & 0xFF);
    }

    @Override
    protected byte gfDiv(int n, int n2) {
        return this.gfMul(n, this.gfPow((byte)n2, (byte)-2) & 0xFF);
    }
}

