/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.saber.Poly;
import org.bouncycastle.pqc.crypto.saber.Symmetric;
import org.bouncycastle.pqc.crypto.saber.Utils;
import org.bouncycastle.util.Arrays;

class SABEREngine {
    public static final int SABER_EP = 10;
    public static final int SABER_N = 256;
    private static final int SABER_SEEDBYTES = 32;
    private static final int SABER_NOISE_SEEDBYTES = 32;
    private static final int SABER_KEYBYTES = 32;
    private static final int SABER_HASHBYTES = 32;
    private final int SABER_L;
    private final int SABER_MU;
    private final int SABER_ET;
    private final int SABER_POLYCOINBYTES;
    private final int SABER_EQ;
    private final int SABER_POLYBYTES;
    private final int SABER_POLYVECBYTES;
    private final int SABER_POLYCOMPRESSEDBYTES;
    private final int SABER_POLYVECCOMPRESSEDBYTES;
    private final int SABER_SCALEBYTES_KEM;
    private final int SABER_INDCPA_PUBLICKEYBYTES;
    private final int SABER_INDCPA_SECRETKEYBYTES;
    private final int SABER_PUBLICKEYBYTES;
    private final int SABER_SECRETKEYBYTES;
    private final int SABER_BYTES_CCA_DEC;
    private final int defaultKeySize;
    private final int h1;
    private final int h2;
    private final Utils utils;
    private final Poly poly;
    private final boolean usingAES;
    protected final boolean usingEffectiveMasking;
    protected final Symmetric symmetric;

    public int getSABER_N() {
        return 256;
    }

    public int getSABER_EP() {
        return 10;
    }

    public int getSABER_KEYBYTES() {
        return 32;
    }

    public int getSABER_L() {
        return this.SABER_L;
    }

    public int getSABER_ET() {
        return this.SABER_ET;
    }

    public int getSABER_POLYBYTES() {
        return this.SABER_POLYBYTES;
    }

    public int getSABER_POLYVECBYTES() {
        return this.SABER_POLYVECBYTES;
    }

    public int getSABER_SEEDBYTES() {
        return 32;
    }

    public int getSABER_POLYCOINBYTES() {
        return this.SABER_POLYCOINBYTES;
    }

    public int getSABER_NOISE_SEEDBYTES() {
        return 32;
    }

    public int getSABER_MU() {
        return this.SABER_MU;
    }

    public Utils getUtils() {
        return this.utils;
    }

    public int getSessionKeySize() {
        return this.defaultKeySize / 8;
    }

    public int getCipherTextSize() {
        return this.SABER_BYTES_CCA_DEC;
    }

    public int getPublicKeySize() {
        return this.SABER_PUBLICKEYBYTES;
    }

    public int getPrivateKeySize() {
        return this.SABER_SECRETKEYBYTES;
    }

    public SABEREngine(int n, int n2, boolean bl, boolean bl2) {
        this.defaultKeySize = n2;
        this.usingAES = bl;
        this.usingEffectiveMasking = bl2;
        this.SABER_L = n;
        if (n == 2) {
            this.SABER_MU = 10;
            this.SABER_ET = 3;
        } else if (n == 3) {
            this.SABER_MU = 8;
            this.SABER_ET = 4;
        } else {
            this.SABER_MU = 6;
            this.SABER_ET = 6;
        }
        this.symmetric = bl ? new Symmetric.AesSymmetric() : new Symmetric.ShakeSymmetric();
        if (bl2) {
            this.SABER_EQ = 12;
            this.SABER_POLYCOINBYTES = 64;
        } else {
            this.SABER_EQ = 13;
            this.SABER_POLYCOINBYTES = this.SABER_MU * 256 / 8;
        }
        this.SABER_POLYBYTES = this.SABER_EQ * 256 / 8;
        this.SABER_POLYVECBYTES = this.SABER_L * this.SABER_POLYBYTES;
        this.SABER_POLYCOMPRESSEDBYTES = 320;
        this.SABER_POLYVECCOMPRESSEDBYTES = this.SABER_L * this.SABER_POLYCOMPRESSEDBYTES;
        this.SABER_SCALEBYTES_KEM = this.SABER_ET * 256 / 8;
        this.SABER_INDCPA_PUBLICKEYBYTES = this.SABER_POLYVECCOMPRESSEDBYTES + 32;
        this.SABER_INDCPA_SECRETKEYBYTES = this.SABER_POLYVECBYTES;
        this.SABER_PUBLICKEYBYTES = this.SABER_INDCPA_PUBLICKEYBYTES;
        this.SABER_SECRETKEYBYTES = this.SABER_INDCPA_SECRETKEYBYTES + this.SABER_INDCPA_PUBLICKEYBYTES + 32 + 32;
        this.SABER_BYTES_CCA_DEC = this.SABER_POLYVECCOMPRESSEDBYTES + this.SABER_SCALEBYTES_KEM;
        this.h1 = 1 << this.SABER_EQ - 10 - 1;
        this.h2 = 256 - (1 << 10 - this.SABER_ET - 1) + (1 << this.SABER_EQ - 10 - 1);
        this.utils = new Utils(this);
        this.poly = new Poly(this);
    }

    private void indcpa_kem_keypair(byte[] byArray, byte[] byArray2, SecureRandom secureRandom) {
        short[][][] sArray = new short[this.SABER_L][this.SABER_L][256];
        short[][] sArray2 = new short[this.SABER_L][256];
        short[][] sArray3 = new short[this.SABER_L][256];
        byte[] byArray3 = new byte[32];
        byte[] byArray4 = new byte[32];
        secureRandom.nextBytes(byArray3);
        this.symmetric.prf(byArray3, byArray3, 32, 32);
        secureRandom.nextBytes(byArray4);
        this.poly.GenMatrix(sArray, byArray3);
        this.poly.GenSecret(sArray2, byArray4);
        this.poly.MatrixVectorMul(sArray, sArray2, sArray3, 1);
        for (int i = 0; i < this.SABER_L; ++i) {
            for (int j = 0; j < 256; ++j) {
                sArray3[i][j] = (short)((sArray3[i][j] + this.h1 & 0xFFFF) >>> this.SABER_EQ - 10);
            }
        }
        this.utils.POLVECq2BS(byArray2, sArray2);
        this.utils.POLVECp2BS(byArray, sArray3);
        System.arraycopy(byArray3, 0, byArray, this.SABER_POLYVECCOMPRESSEDBYTES, byArray3.length);
    }

    public int crypto_kem_keypair(byte[] byArray, byte[] byArray2, SecureRandom secureRandom) {
        this.indcpa_kem_keypair(byArray, byArray2, secureRandom);
        for (int i = 0; i < this.SABER_INDCPA_PUBLICKEYBYTES; ++i) {
            byArray2[i + this.SABER_INDCPA_SECRETKEYBYTES] = byArray[i];
        }
        this.symmetric.hash_h(byArray2, byArray, this.SABER_SECRETKEYBYTES - 64);
        byte[] byArray3 = new byte[32];
        secureRandom.nextBytes(byArray3);
        System.arraycopy(byArray3, 0, byArray2, this.SABER_SECRETKEYBYTES - 32, byArray3.length);
        return 0;
    }

    private void indcpa_kem_enc(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        int n;
        short[][][] sArray = new short[this.SABER_L][this.SABER_L][256];
        short[][] sArray2 = new short[this.SABER_L][256];
        short[][] sArray3 = new short[this.SABER_L][256];
        short[][] sArray4 = new short[this.SABER_L][256];
        short[] sArray5 = new short[256];
        short[] sArray6 = new short[256];
        byte[] byArray5 = Arrays.copyOfRange(byArray3, this.SABER_POLYVECCOMPRESSEDBYTES, byArray3.length);
        this.poly.GenMatrix(sArray, byArray5);
        this.poly.GenSecret(sArray2, byArray2);
        this.poly.MatrixVectorMul(sArray, sArray2, sArray3, 0);
        for (int i = 0; i < this.SABER_L; ++i) {
            for (n = 0; n < 256; ++n) {
                sArray3[i][n] = (short)((sArray3[i][n] + this.h1 & 0xFFFF) >>> this.SABER_EQ - 10);
            }
        }
        this.utils.POLVECp2BS(byArray4, sArray3);
        this.utils.BS2POLVECp(byArray3, sArray4);
        this.poly.InnerProd(sArray4, sArray2, sArray6);
        this.utils.BS2POLmsg(byArray, sArray5);
        for (n = 0; n < 256; ++n) {
            sArray6[n] = (short)((sArray6[n] - (sArray5[n] << 9) + this.h1 & 0xFFFF) >>> 10 - this.SABER_ET);
        }
        this.utils.POLT2BS(byArray4, this.SABER_POLYVECCOMPRESSEDBYTES, sArray6);
    }

    public int crypto_kem_enc(byte[] byArray, byte[] byArray2, byte[] byArray3, SecureRandom secureRandom) {
        byte[] byArray4 = new byte[64];
        byte[] byArray5 = new byte[64];
        byte[] byArray6 = new byte[32];
        secureRandom.nextBytes(byArray6);
        this.symmetric.hash_h(byArray6, byArray6, 0);
        System.arraycopy(byArray6, 0, byArray5, 0, 32);
        this.symmetric.hash_h(byArray5, byArray3, 32);
        this.symmetric.hash_g(byArray4, byArray5);
        this.indcpa_kem_enc(byArray5, Arrays.copyOfRange(byArray4, 32, byArray4.length), byArray3, byArray);
        this.symmetric.hash_h(byArray4, byArray, 32);
        byte[] byArray7 = new byte[32];
        this.symmetric.hash_h(byArray7, byArray4, 0);
        System.arraycopy(byArray7, 0, byArray2, 0, this.defaultKeySize / 8);
        return 0;
    }

    private void indcpa_kem_dec(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        short[][] sArray = new short[this.SABER_L][256];
        short[][] sArray2 = new short[this.SABER_L][256];
        short[] sArray3 = new short[256];
        short[] sArray4 = new short[256];
        this.utils.BS2POLVECq(byArray, 0, sArray);
        this.utils.BS2POLVECp(byArray2, sArray2);
        this.poly.InnerProd(sArray2, sArray, sArray3);
        this.utils.BS2POLT(byArray2, this.SABER_POLYVECCOMPRESSEDBYTES, sArray4);
        for (int i = 0; i < 256; ++i) {
            sArray3[i] = (short)((sArray3[i] + this.h2 - (sArray4[i] << 10 - this.SABER_ET) & 0xFFFF) >> 9);
        }
        this.utils.POLmsg2BS(byArray3, sArray3);
    }

    public int crypto_kem_dec(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        byte[] byArray4 = new byte[this.SABER_BYTES_CCA_DEC];
        byte[] byArray5 = new byte[64];
        byte[] byArray6 = new byte[64];
        byte[] byArray7 = Arrays.copyOfRange(byArray3, this.SABER_INDCPA_SECRETKEYBYTES, byArray3.length);
        this.indcpa_kem_dec(byArray3, byArray2, byArray5);
        for (int i = 0; i < 32; ++i) {
            byArray5[32 + i] = byArray3[this.SABER_SECRETKEYBYTES - 64 + i];
        }
        this.symmetric.hash_g(byArray6, byArray5);
        this.indcpa_kem_enc(byArray5, Arrays.copyOfRange(byArray6, 32, byArray6.length), byArray7, byArray4);
        int n = SABEREngine.verify(byArray2, byArray4, this.SABER_BYTES_CCA_DEC);
        this.symmetric.hash_h(byArray6, byArray2, 32);
        SABEREngine.cmov(byArray6, byArray3, this.SABER_SECRETKEYBYTES - 32, 32, (byte)n);
        byte[] byArray8 = new byte[32];
        this.symmetric.hash_h(byArray8, byArray6, 0);
        System.arraycopy(byArray8, 0, byArray, 0, this.defaultKeySize / 8);
        return 0;
    }

    static int verify(byte[] byArray, byte[] byArray2, int n) {
        long l = 0L;
        for (int i = 0; i < n; ++i) {
            l |= (long)(byArray[i] ^ byArray2[i]);
        }
        l = -l >>> 63;
        return (int)l;
    }

    static void cmov(byte[] byArray, byte[] byArray2, int n, int n2, byte by) {
        by = -by;
        for (int i = 0; i < n2; ++i) {
            int n3 = i;
            byArray[n3] = (byte)(byArray[n3] ^ by & (byArray2[i + n] ^ byArray[i]));
        }
    }
}

