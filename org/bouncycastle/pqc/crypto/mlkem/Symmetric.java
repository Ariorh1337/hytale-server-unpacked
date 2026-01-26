/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;

abstract class Symmetric {
    final int xofBlockBytes;

    abstract void hash_h(byte[] var1, byte[] var2, int var3);

    abstract void hash_g(byte[] var1, byte[] var2);

    abstract void xofAbsorb(byte[] var1, byte var2, byte var3);

    abstract void xofSqueezeBlocks(byte[] var1, int var2, int var3);

    abstract void prf(byte[] var1, byte[] var2, byte var3);

    abstract void kdf(byte[] var1, byte[] var2);

    Symmetric(int n) {
        this.xofBlockBytes = n;
    }

    static class ShakeSymmetric
    extends Symmetric {
        private final SHAKEDigest xof = new SHAKEDigest(128);
        private final SHA3Digest sha3Digest512;
        private final SHA3Digest sha3Digest256;
        private final SHAKEDigest shakeDigest = new SHAKEDigest(256);

        ShakeSymmetric() {
            super(168);
            this.sha3Digest256 = new SHA3Digest(256);
            this.sha3Digest512 = new SHA3Digest(512);
        }

        @Override
        void hash_h(byte[] byArray, byte[] byArray2, int n) {
            this.sha3Digest256.update(byArray2, 0, byArray2.length);
            this.sha3Digest256.doFinal(byArray, n);
        }

        @Override
        void hash_g(byte[] byArray, byte[] byArray2) {
            this.sha3Digest512.update(byArray2, 0, byArray2.length);
            this.sha3Digest512.doFinal(byArray, 0);
        }

        @Override
        void xofAbsorb(byte[] byArray, byte by, byte by2) {
            this.xof.reset();
            byte[] byArray2 = new byte[byArray.length + 2];
            System.arraycopy(byArray, 0, byArray2, 0, byArray.length);
            byArray2[byArray.length] = by;
            byArray2[byArray.length + 1] = by2;
            this.xof.update(byArray2, 0, byArray.length + 2);
        }

        @Override
        void xofSqueezeBlocks(byte[] byArray, int n, int n2) {
            this.xof.doOutput(byArray, n, n2);
        }

        @Override
        void prf(byte[] byArray, byte[] byArray2, byte by) {
            byte[] byArray3 = new byte[byArray2.length + 1];
            System.arraycopy(byArray2, 0, byArray3, 0, byArray2.length);
            byArray3[byArray2.length] = by;
            this.shakeDigest.update(byArray3, 0, byArray3.length);
            this.shakeDigest.doFinal(byArray, 0, byArray.length);
        }

        @Override
        void kdf(byte[] byArray, byte[] byArray2) {
            this.shakeDigest.update(byArray2, 0, byArray2.length);
            this.shakeDigest.doFinal(byArray, 0, byArray.length);
        }
    }
}

