/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.falcon.FalconCodec;
import org.bouncycastle.pqc.crypto.falcon.FalconCommon;
import org.bouncycastle.pqc.crypto.falcon.FalconKeyGen;
import org.bouncycastle.pqc.crypto.falcon.FalconSign;
import org.bouncycastle.pqc.crypto.falcon.FalconVrfy;
import org.bouncycastle.util.Arrays;

class FalconNIST {
    final int NONCELEN;
    final int LOGN;
    private final int N;
    private final SecureRandom rand;
    private final int CRYPTO_SECRETKEYBYTES;
    private final int CRYPTO_PUBLICKEYBYTES;
    final int CRYPTO_BYTES;

    FalconNIST(int n, int n2, SecureRandom secureRandom) {
        this.rand = secureRandom;
        this.LOGN = n;
        this.NONCELEN = n2;
        this.N = 1 << n;
        this.CRYPTO_PUBLICKEYBYTES = 1 + 14 * this.N / 8;
        if (n == 10) {
            this.CRYPTO_SECRETKEYBYTES = 2305;
            this.CRYPTO_BYTES = 1330;
        } else if (n == 9 || n == 8) {
            this.CRYPTO_SECRETKEYBYTES = 1 + 6 * this.N * 2 / 8 + this.N;
            this.CRYPTO_BYTES = 690;
        } else if (n == 7 || n == 6) {
            this.CRYPTO_SECRETKEYBYTES = 1 + 7 * this.N * 2 / 8 + this.N;
            this.CRYPTO_BYTES = 690;
        } else {
            this.CRYPTO_SECRETKEYBYTES = 1 + this.N * 2 + this.N;
            this.CRYPTO_BYTES = 690;
        }
    }

    byte[][] crypto_sign_keypair(byte[] byArray, byte[] byArray2) {
        byte[] byArray3 = new byte[this.N];
        byte[] byArray4 = new byte[this.N];
        byte[] byArray5 = new byte[this.N];
        short[] sArray = new short[this.N];
        byte[] byArray6 = new byte[48];
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        this.rand.nextBytes(byArray6);
        sHAKEDigest.update(byArray6, 0, byArray6.length);
        FalconKeyGen.keygen(sHAKEDigest, byArray3, byArray4, byArray5, sArray, this.LOGN);
        byArray2[0] = (byte)(80 + this.LOGN);
        int n = 1;
        int n2 = FalconCodec.trim_i8_encode(byArray2, n, this.CRYPTO_SECRETKEYBYTES - n, byArray3, this.LOGN, FalconCodec.max_fg_bits[this.LOGN]);
        if (n2 == 0) {
            throw new IllegalStateException("f encode failed");
        }
        byte[] byArray7 = Arrays.copyOfRange(byArray2, n, n + n2);
        if ((n2 = FalconCodec.trim_i8_encode(byArray2, n += n2, this.CRYPTO_SECRETKEYBYTES - n, byArray4, this.LOGN, FalconCodec.max_fg_bits[this.LOGN])) == 0) {
            throw new IllegalStateException("g encode failed");
        }
        byte[] byArray8 = Arrays.copyOfRange(byArray2, n, n + n2);
        if ((n2 = FalconCodec.trim_i8_encode(byArray2, n += n2, this.CRYPTO_SECRETKEYBYTES - n, byArray5, this.LOGN, FalconCodec.max_FG_bits[this.LOGN])) == 0) {
            throw new IllegalStateException("F encode failed");
        }
        byte[] byArray9 = Arrays.copyOfRange(byArray2, n, n + n2);
        if ((n += n2) != this.CRYPTO_SECRETKEYBYTES) {
            throw new IllegalStateException("secret key encoding failed");
        }
        byArray[0] = (byte)this.LOGN;
        n2 = FalconCodec.modq_encode(byArray, this.CRYPTO_PUBLICKEYBYTES - 1, sArray, this.LOGN);
        if (n2 != this.CRYPTO_PUBLICKEYBYTES - 1) {
            throw new IllegalStateException("public key encoding failed");
        }
        return new byte[][]{Arrays.copyOfRange(byArray, 1, byArray.length), byArray7, byArray8, byArray9};
    }

    byte[] crypto_sign(byte[] byArray, byte[] byArray2, int n, byte[] byArray3) {
        byte[] byArray4 = new byte[this.N];
        byte[] byArray5 = new byte[this.N];
        byte[] byArray6 = new byte[this.N];
        byte[] byArray7 = new byte[this.N];
        short[] sArray = new short[this.N];
        short[] sArray2 = new short[this.N];
        byte[] byArray8 = new byte[48];
        byte[] byArray9 = new byte[this.NONCELEN];
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        FalconSign falconSign = new FalconSign();
        int n2 = 0;
        int n3 = FalconCodec.trim_i8_decode(byArray4, this.LOGN, FalconCodec.max_fg_bits[this.LOGN], byArray3, 0, this.CRYPTO_SECRETKEYBYTES - n2);
        if (n3 == 0) {
            throw new IllegalStateException("f decode failed");
        }
        if ((n3 = FalconCodec.trim_i8_decode(byArray5, this.LOGN, FalconCodec.max_fg_bits[this.LOGN], byArray3, n2 += n3, this.CRYPTO_SECRETKEYBYTES - n2)) == 0) {
            throw new IllegalStateException("g decode failed");
        }
        if ((n3 = FalconCodec.trim_i8_decode(byArray6, this.LOGN, FalconCodec.max_FG_bits[this.LOGN], byArray3, n2 += n3, this.CRYPTO_SECRETKEYBYTES - n2)) == 0) {
            throw new IllegalArgumentException("F decode failed");
        }
        if ((n2 += n3) != this.CRYPTO_SECRETKEYBYTES - 1) {
            throw new IllegalStateException("full key not used");
        }
        if (!FalconVrfy.complete_private(byArray7, byArray4, byArray5, byArray6, this.LOGN, new short[2 * this.N])) {
            throw new IllegalStateException("complete_private failed");
        }
        this.rand.nextBytes(byArray9);
        sHAKEDigest.update(byArray9, 0, this.NONCELEN);
        sHAKEDigest.update(byArray2, 0, n);
        FalconCommon.hash_to_point_vartime(sHAKEDigest, sArray2, this.LOGN);
        this.rand.nextBytes(byArray8);
        sHAKEDigest.reset();
        sHAKEDigest.update(byArray8, 0, byArray8.length);
        falconSign.sign_dyn(sArray, sHAKEDigest, byArray4, byArray5, byArray6, byArray7, sArray2, this.LOGN, new double[10 * this.N]);
        byte[] byArray10 = new byte[this.CRYPTO_BYTES - 2 - this.NONCELEN];
        int n4 = FalconCodec.comp_encode(byArray10, byArray10.length, sArray, this.LOGN);
        if (n4 == 0) {
            throw new IllegalStateException("signature failed to generate");
        }
        byArray[0] = (byte)(48 + this.LOGN);
        System.arraycopy(byArray9, 0, byArray, 1, this.NONCELEN);
        System.arraycopy(byArray10, 0, byArray, 1 + this.NONCELEN, n4);
        return Arrays.copyOfRange(byArray, 0, 1 + this.NONCELEN + n4);
    }

    int crypto_sign_open(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        short[] sArray = new short[this.N];
        short[] sArray2 = new short[this.N];
        short[] sArray3 = new short[this.N];
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        if (FalconCodec.modq_decode(sArray, this.LOGN, byArray4, this.CRYPTO_PUBLICKEYBYTES - 1) != this.CRYPTO_PUBLICKEYBYTES - 1) {
            return -1;
        }
        FalconVrfy.to_ntt_monty(sArray, this.LOGN);
        int n = byArray.length;
        int n2 = byArray3.length;
        if (n < 1 || FalconCodec.comp_decode(sArray3, this.LOGN, byArray, n) != n) {
            return -1;
        }
        sHAKEDigest.update(byArray2, 0, this.NONCELEN);
        sHAKEDigest.update(byArray3, 0, n2);
        FalconCommon.hash_to_point_vartime(sHAKEDigest, sArray2, this.LOGN);
        if (FalconVrfy.verify_raw(sArray2, sArray3, sArray, this.LOGN, new short[this.N]) == 0) {
            return -1;
        }
        return 0;
    }
}

