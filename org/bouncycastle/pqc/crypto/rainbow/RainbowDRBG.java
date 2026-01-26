/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.pqc.crypto.rainbow.RainbowUtil;
import org.bouncycastle.util.Arrays;

class RainbowDRBG
extends SecureRandom {
    private byte[] seed;
    private byte[] key;
    private byte[] v;
    private Digest hashAlgo;

    public RainbowDRBG(byte[] byArray, Digest digest) {
        this.seed = byArray;
        this.hashAlgo = digest;
        this.init(256);
    }

    private void init(int n) {
        if (this.seed.length >= 48) {
            this.randombytes_init(this.seed, n);
        } else {
            byte[] byArray = RainbowUtil.hash(this.hashAlgo, this.seed, 48 - this.seed.length);
            this.randombytes_init(Arrays.concatenate(this.seed, byArray), n);
        }
    }

    @Override
    public void nextBytes(byte[] byArray) {
        byte[] byArray2 = new byte[16];
        int n = 0;
        int n2 = byArray.length;
        while (n2 > 0) {
            for (int i = 15; i >= 0; --i) {
                if ((this.v[i] & 0xFF) != 255) {
                    int n3 = i;
                    this.v[n3] = (byte)(this.v[n3] + 1);
                    break;
                }
                this.v[i] = 0;
            }
            this.AES256_ECB(this.key, this.v, byArray2, 0);
            if (n2 > 15) {
                System.arraycopy(byArray2, 0, byArray, n, byArray2.length);
                n += 16;
                n2 -= 16;
                continue;
            }
            System.arraycopy(byArray2, 0, byArray, n, n2);
            n2 = 0;
        }
        this.AES256_CTR_DRBG_Update(null, this.key, this.v);
    }

    private void AES256_ECB(byte[] byArray, byte[] byArray2, byte[] byArray3, int n) {
        try {
            AESEngine aESEngine = new AESEngine();
            aESEngine.init(true, new KeyParameter(byArray));
            for (int i = 0; i != byArray2.length; i += 16) {
                aESEngine.processBlock(byArray2, i, byArray3, n + i);
            }
        }
        catch (Throwable throwable) {
            throw new IllegalStateException("drbg failure: " + throwable.getMessage(), throwable);
        }
    }

    private void AES256_CTR_DRBG_Update(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int n;
        byte[] byArray4 = new byte[48];
        for (n = 0; n < 3; ++n) {
            for (int i = 15; i >= 0; --i) {
                if ((byArray3[i] & 0xFF) != 255) {
                    int n2 = i;
                    byArray3[n2] = (byte)(byArray3[n2] + 1);
                    break;
                }
                byArray3[i] = 0;
            }
            this.AES256_ECB(byArray2, byArray3, byArray4, 16 * n);
        }
        if (byArray != null) {
            for (n = 0; n < 48; ++n) {
                int n3 = n;
                byArray4[n3] = (byte)(byArray4[n3] ^ byArray[n]);
            }
        }
        System.arraycopy(byArray4, 0, byArray2, 0, byArray2.length);
        System.arraycopy(byArray4, 32, byArray3, 0, byArray3.length);
    }

    private void randombytes_init(byte[] byArray, int n) {
        byte[] byArray2 = new byte[48];
        System.arraycopy(byArray, 0, byArray2, 0, byArray2.length);
        this.key = new byte[32];
        this.v = new byte[16];
        this.AES256_CTR_DRBG_Update(byArray2, this.key, this.v);
    }
}

