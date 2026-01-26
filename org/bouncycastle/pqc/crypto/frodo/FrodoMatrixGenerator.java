/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

abstract class FrodoMatrixGenerator {
    int n;
    int q;

    public FrodoMatrixGenerator(int n, int n2) {
        this.n = n;
        this.q = n2;
    }

    abstract short[] genMatrix(byte[] var1);

    static class Aes128MatrixGenerator
    extends FrodoMatrixGenerator {
        public Aes128MatrixGenerator(int n, int n2) {
            super(n, n2);
        }

        @Override
        short[] genMatrix(byte[] byArray) {
            short[] sArray = new short[this.n * this.n];
            byte[] byArray2 = new byte[16];
            byte[] byArray3 = new byte[16];
            AESEngine aESEngine = new AESEngine();
            aESEngine.init(true, new KeyParameter(byArray));
            for (int i = 0; i < this.n; ++i) {
                Pack.shortToLittleEndian((short)i, byArray2, 0);
                for (int j = 0; j < this.n; j += 8) {
                    Pack.shortToLittleEndian((short)j, byArray2, 2);
                    aESEngine.processBlock(byArray2, 0, byArray3, 0);
                    for (int k = 0; k < 8; ++k) {
                        sArray[i * this.n + j + k] = (short)(Pack.littleEndianToShort(byArray3, 2 * k) & this.q - 1);
                    }
                }
            }
            return sArray;
        }
    }

    static class Shake128MatrixGenerator
    extends FrodoMatrixGenerator {
        public Shake128MatrixGenerator(int n, int n2) {
            super(n, n2);
        }

        @Override
        short[] genMatrix(byte[] byArray) {
            short[] sArray = new short[this.n * this.n];
            byte[] byArray2 = new byte[16 * this.n / 8];
            byte[] byArray3 = new byte[2 + byArray.length];
            System.arraycopy(byArray, 0, byArray3, 2, byArray.length);
            SHAKEDigest sHAKEDigest = new SHAKEDigest(128);
            for (short s = 0; s < this.n; s = (short)(s + 1)) {
                Pack.shortToLittleEndian(s, byArray3, 0);
                sHAKEDigest.update(byArray3, 0, byArray3.length);
                sHAKEDigest.doFinal(byArray2, 0, byArray2.length);
                for (int n = 0; n < this.n; n = (int)((short)(n + 1))) {
                    sArray[s * this.n + n] = (short)(Pack.littleEndianToShort(byArray2, 2 * n) & this.q - 1);
                }
            }
            return sArray;
        }
    }
}

