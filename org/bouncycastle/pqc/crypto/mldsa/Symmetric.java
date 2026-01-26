/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.crypto.digests.SHAKEDigest;

abstract class Symmetric {
    final int stream128BlockBytes;
    final int stream256BlockBytes;

    Symmetric(int n, int n2) {
        this.stream128BlockBytes = n;
        this.stream256BlockBytes = n2;
    }

    abstract void stream128init(byte[] var1, short var2);

    abstract void stream256init(byte[] var1, short var2);

    abstract void stream128squeezeBlocks(byte[] var1, int var2, int var3);

    abstract void stream256squeezeBlocks(byte[] var1, int var2, int var3);

    static class ShakeSymmetric
    extends Symmetric {
        private final SHAKEDigest digest128 = new SHAKEDigest(128);
        private final SHAKEDigest digest256 = new SHAKEDigest(256);

        ShakeSymmetric() {
            super(168, 136);
        }

        private void streamInit(SHAKEDigest sHAKEDigest, byte[] byArray, short s) {
            sHAKEDigest.reset();
            byte[] byArray2 = new byte[]{(byte)s, (byte)(s >> 8)};
            sHAKEDigest.update(byArray, 0, byArray.length);
            sHAKEDigest.update(byArray2, 0, byArray2.length);
        }

        @Override
        void stream128init(byte[] byArray, short s) {
            this.streamInit(this.digest128, byArray, s);
        }

        @Override
        void stream256init(byte[] byArray, short s) {
            this.streamInit(this.digest256, byArray, s);
        }

        @Override
        void stream128squeezeBlocks(byte[] byArray, int n, int n2) {
            this.digest128.doOutput(byArray, n, n2);
        }

        @Override
        void stream256squeezeBlocks(byte[] byArray, int n, int n2) {
            this.digest256.doOutput(byArray, n, n2);
        }
    }
}

