/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

abstract class Symmetric {
    Symmetric() {
    }

    abstract void hash_h(byte[] var1, byte[] var2, int var3);

    abstract void hash_g(byte[] var1, byte[] var2);

    abstract void prf(byte[] var1, byte[] var2, int var3, int var4);

    static class AesSymmetric
    extends Symmetric {
        private final SHA256Digest sha256Digest = new SHA256Digest();
        private final SHA512Digest sha512Digest = new SHA512Digest();
        private final StreamCipher cipher = SICBlockCipher.newInstance(AESEngine.newInstance());

        AesSymmetric() {
        }

        @Override
        void hash_h(byte[] byArray, byte[] byArray2, int n) {
            this.sha256Digest.update(byArray2, 0, byArray2.length);
            this.sha256Digest.doFinal(byArray, n);
        }

        @Override
        void hash_g(byte[] byArray, byte[] byArray2) {
            this.sha512Digest.update(byArray2, 0, byArray2.length);
            this.sha512Digest.doFinal(byArray, 0);
        }

        @Override
        void prf(byte[] byArray, byte[] byArray2, int n, int n2) {
            ParametersWithIV parametersWithIV = new ParametersWithIV(new KeyParameter(byArray2, 0, n), new byte[16]);
            this.cipher.init(true, parametersWithIV);
            byte[] byArray3 = new byte[n2];
            this.cipher.processBytes(byArray3, 0, n2, byArray, 0);
        }
    }

    static class ShakeSymmetric
    extends Symmetric {
        private final SHA3Digest sha3Digest256;
        private final SHA3Digest sha3Digest512;
        private final Xof shakeDigest = new SHAKEDigest(128);

        ShakeSymmetric() {
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
        void prf(byte[] byArray, byte[] byArray2, int n, int n2) {
            this.shakeDigest.reset();
            this.shakeDigest.update(byArray2, 0, n);
            this.shakeDigest.doFinal(byArray, 0, n2);
        }
    }
}

