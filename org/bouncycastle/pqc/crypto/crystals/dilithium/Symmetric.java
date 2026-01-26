/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

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

    @Deprecated
    static class AesSymmetric
    extends Symmetric {
        private final StreamCipher cipher = SICBlockCipher.newInstance(AESEngine.newInstance());

        AesSymmetric() {
            super(64, 64);
        }

        private void aes128(byte[] byArray, int n, int n2) {
            byte[] byArray2 = new byte[n2];
            this.cipher.processBytes(byArray2, 0, n2, byArray, n);
        }

        private void streamInit(byte[] byArray, short s) {
            byte[] byArray2 = new byte[12];
            byArray2[0] = (byte)s;
            byArray2[1] = (byte)(s >> 8);
            ParametersWithIV parametersWithIV = new ParametersWithIV(new KeyParameter(byArray, 0, 32), byArray2);
            this.cipher.init(true, parametersWithIV);
        }

        @Override
        void stream128init(byte[] byArray, short s) {
            this.streamInit(byArray, s);
        }

        @Override
        void stream256init(byte[] byArray, short s) {
            this.streamInit(byArray, s);
        }

        @Override
        void stream128squeezeBlocks(byte[] byArray, int n, int n2) {
            this.aes128(byArray, n, n2);
        }

        @Override
        void stream256squeezeBlocks(byte[] byArray, int n, int n2) {
            this.aes128(byArray, n, n2);
        }
    }

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

