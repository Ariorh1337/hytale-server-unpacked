/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.threshold;

import java.io.IOException;
import org.bouncycastle.crypto.threshold.Polynomial;
import org.bouncycastle.crypto.threshold.ShamirSecretSplitter;
import org.bouncycastle.crypto.threshold.ShamirSplitSecretShare;
import org.bouncycastle.crypto.threshold.SplitSecret;

public class ShamirSplitSecret
implements SplitSecret {
    private final ShamirSplitSecretShare[] secretShares;
    private final Polynomial poly;

    public ShamirSplitSecret(ShamirSecretSplitter.Algorithm algorithm, ShamirSecretSplitter.Mode mode, ShamirSplitSecretShare[] shamirSplitSecretShareArray) {
        this.secretShares = shamirSplitSecretShareArray;
        this.poly = Polynomial.newInstance(algorithm, mode);
    }

    ShamirSplitSecret(Polynomial polynomial, ShamirSplitSecretShare[] shamirSplitSecretShareArray) {
        this.secretShares = shamirSplitSecretShareArray;
        this.poly = polynomial;
    }

    public ShamirSplitSecretShare[] getSecretShares() {
        return this.secretShares;
    }

    public ShamirSplitSecret multiple(int n) throws IOException {
        for (int i = 0; i < this.secretShares.length; ++i) {
            byte[] byArray = this.secretShares[i].getEncoded();
            for (int j = 0; j < byArray.length; ++j) {
                byArray[j] = this.poly.gfMul(byArray[j] & 0xFF, n);
            }
            this.secretShares[i] = new ShamirSplitSecretShare(byArray, i + 1);
        }
        return this;
    }

    public ShamirSplitSecret divide(int n) throws IOException {
        for (int i = 0; i < this.secretShares.length; ++i) {
            byte[] byArray = this.secretShares[i].getEncoded();
            for (int j = 0; j < byArray.length; ++j) {
                byArray[j] = this.poly.gfDiv(byArray[j] & 0xFF, n);
            }
            this.secretShares[i] = new ShamirSplitSecretShare(byArray, i + 1);
        }
        return this;
    }

    @Override
    public byte[] getSecret() throws IOException {
        int n = this.secretShares.length;
        byte[] byArray = new byte[n];
        byte[] byArray2 = new byte[n - 1];
        byte[][] byArray3 = new byte[n][this.secretShares[0].getEncoded().length];
        for (int i = 0; i < n; ++i) {
            int n2;
            byArray3[i] = this.secretShares[i].getEncoded();
            int n3 = 0;
            for (n2 = 0; n2 < n; ++n2) {
                if (n2 == i) continue;
                int n4 = n3;
                n3 = (byte)(n3 + 1);
                byArray2[n4] = this.poly.gfDiv(this.secretShares[n2].r, this.secretShares[i].r ^ this.secretShares[n2].r);
            }
            n3 = 1;
            for (n2 = 0; n2 != byArray2.length; ++n2) {
                n3 = this.poly.gfMul(n3 & 0xFF, byArray2[n2] & 0xFF);
            }
            byArray[i] = n3;
        }
        return this.poly.gfVecMul(byArray, byArray3);
    }
}

