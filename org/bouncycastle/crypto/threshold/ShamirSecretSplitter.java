/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.threshold;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.crypto.threshold.Polynomial;
import org.bouncycastle.crypto.threshold.SecretShare;
import org.bouncycastle.crypto.threshold.SecretSplitter;
import org.bouncycastle.crypto.threshold.ShamirSplitSecret;
import org.bouncycastle.crypto.threshold.ShamirSplitSecretShare;
import org.bouncycastle.util.Arrays;

public class ShamirSecretSplitter
implements SecretSplitter {
    private final Polynomial poly;
    protected int l;
    protected SecureRandom random;

    public ShamirSecretSplitter(Algorithm algorithm, Mode mode, int n, SecureRandom secureRandom) {
        if (n < 0 || n > 65534) {
            throw new IllegalArgumentException("Invalid input: l ranges from 0 to 65534 (2^16-2) bytes.");
        }
        this.poly = Polynomial.newInstance(algorithm, mode);
        this.l = n;
        this.random = secureRandom;
    }

    @Override
    public ShamirSplitSecret split(int n, int n2) {
        int n3;
        byte[][] byArray = this.initP(n, n2);
        byte[][] byArray2 = new byte[n][this.l];
        ShamirSplitSecretShare[] shamirSplitSecretShareArray = new ShamirSplitSecretShare[this.l];
        for (n3 = 0; n3 < n; ++n3) {
            this.random.nextBytes(byArray2[n3]);
        }
        for (n3 = 0; n3 < byArray.length; ++n3) {
            shamirSplitSecretShareArray[n3] = new ShamirSplitSecretShare(this.poly.gfVecMul(byArray[n3], byArray2), n3 + 1);
        }
        return new ShamirSplitSecret(this.poly, shamirSplitSecretShareArray);
    }

    @Override
    public ShamirSplitSecret splitAround(SecretShare secretShare, int n, int n2) throws IOException {
        int n3;
        byte[][] byArray = this.initP(n, n2);
        byte[][] byArray2 = new byte[n][this.l];
        ShamirSplitSecretShare[] shamirSplitSecretShareArray = new ShamirSplitSecretShare[this.l];
        byte[] byArray3 = secretShare.getEncoded();
        shamirSplitSecretShareArray[0] = new ShamirSplitSecretShare(byArray3, 1);
        for (n3 = 0; n3 < n; ++n3) {
            this.random.nextBytes(byArray2[n3]);
        }
        for (n3 = 0; n3 < this.l; ++n3) {
            byte by = byArray2[1][n3];
            for (int i = 2; i < n; ++i) {
                by = (byte)(by ^ byArray2[i][n3]);
            }
            byArray2[0][n3] = (byte)(by ^ byArray3[n3]);
        }
        for (n3 = 1; n3 < byArray.length; ++n3) {
            shamirSplitSecretShareArray[n3] = new ShamirSplitSecretShare(this.poly.gfVecMul(byArray[n3], byArray2), n3 + 1);
        }
        return new ShamirSplitSecret(this.poly, shamirSplitSecretShareArray);
    }

    @Override
    public ShamirSplitSecret resplit(byte[] byArray, int n, int n2) {
        int n3;
        byte[][] byArray2 = this.initP(n, n2);
        byte[][] byArray3 = new byte[n][this.l];
        ShamirSplitSecretShare[] shamirSplitSecretShareArray = new ShamirSplitSecretShare[this.l];
        byArray3[0] = Arrays.clone(byArray);
        for (n3 = 1; n3 < n; ++n3) {
            this.random.nextBytes(byArray3[n3]);
        }
        for (n3 = 0; n3 < byArray2.length; ++n3) {
            shamirSplitSecretShareArray[n3] = new ShamirSplitSecretShare(this.poly.gfVecMul(byArray2[n3], byArray3), n3 + 1);
        }
        return new ShamirSplitSecret(this.poly, shamirSplitSecretShareArray);
    }

    private byte[][] initP(int n, int n2) {
        if (n < 1 || n > 255) {
            throw new IllegalArgumentException("Invalid input: m must be less than 256 and positive.");
        }
        if (n2 < n || n2 > 255) {
            throw new IllegalArgumentException("Invalid input: n must be less than 256 and greater than or equal to n.");
        }
        byte[][] byArray = new byte[n2][n];
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < n; ++j) {
                byArray[i][j] = this.poly.gfPow((byte)(i + 1), (byte)j);
            }
        }
        return byArray;
    }

    public static enum Algorithm {
        AES,
        RSA;

    }

    public static enum Mode {
        Native,
        Table;

    }
}

