/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.pqc.crypto.frodo.FrodoMatrixGenerator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class FrodoEngine {
    static final int nbar = 8;
    private static final int mbar = 8;
    private static final int len_seedA = 128;
    private static final int len_z = 128;
    private static final int len_chi = 16;
    private static final int len_seedA_bytes = 16;
    private static final int len_z_bytes = 16;
    private static final int len_chi_bytes = 2;
    private final int D;
    private final int q;
    private final int n;
    private final int B;
    private final int len_sk_bytes;
    private final int len_pk_bytes;
    private final int len_ct_bytes;
    private final short[] T_chi;
    private final int len_mu;
    private final int len_seedSE;
    private final int len_s;
    private final int len_k;
    private final int len_pkh;
    private final int len_ss;
    private final int len_mu_bytes;
    private final int len_seedSE_bytes;
    private final int len_s_bytes;
    private final int len_k_bytes;
    private final int len_pkh_bytes;
    private final int len_ss_bytes;
    private final Xof digest;
    private final FrodoMatrixGenerator gen;

    public int getCipherTextSize() {
        return this.len_ct_bytes;
    }

    public int getSessionKeySize() {
        return this.len_ss_bytes;
    }

    public int getPrivateKeySize() {
        return this.len_sk_bytes;
    }

    public int getPublicKeySize() {
        return this.len_pk_bytes;
    }

    public FrodoEngine(int n, int n2, int n3, short[] sArray, Xof xof, FrodoMatrixGenerator frodoMatrixGenerator) {
        this.n = n;
        this.D = n2;
        this.q = 1 << n2;
        this.B = n3;
        this.len_seedSE = this.len_mu = n3 * 8 * 8;
        this.len_s = this.len_mu;
        this.len_k = this.len_mu;
        this.len_pkh = this.len_mu;
        this.len_ss = this.len_mu;
        this.len_mu_bytes = this.len_mu / 8;
        this.len_seedSE_bytes = this.len_seedSE / 8;
        this.len_s_bytes = this.len_s / 8;
        this.len_k_bytes = this.len_k / 8;
        this.len_pkh_bytes = this.len_pkh / 8;
        this.len_ss_bytes = this.len_ss / 8;
        this.len_ct_bytes = n2 * n * 8 / 8 + n2 * 8 * 8 / 8;
        this.len_pk_bytes = 16 + n2 * n * 8 / 8;
        this.len_sk_bytes = this.len_s_bytes + this.len_pk_bytes + (2 * n * 8 + this.len_pkh_bytes);
        this.T_chi = sArray;
        this.digest = xof;
        this.gen = frodoMatrixGenerator;
    }

    private short sample(short s) {
        short s2 = (short)((s & 0xFFFF) >>> 1);
        short s3 = 0;
        for (int i = 0; i < this.T_chi.length; ++i) {
            if (s2 <= this.T_chi[i]) continue;
            s3 = (short)(s3 + 1);
        }
        if ((s & 0xFFFF) % 2 == 1) {
            s3 = (short)(s3 * -1 & 0xFFFF);
        }
        return s3;
    }

    private short[] sample_matrix(short[] sArray, int n, int n2, int n3) {
        short[] sArray2 = new short[n2 * n3];
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < n3; ++j) {
                sArray2[i * n3 + j] = this.sample(sArray[i * n3 + j + n]);
            }
        }
        return sArray2;
    }

    private short[] matrix_transpose(short[] sArray, int n, int n2) {
        short[] sArray2 = new short[n * n2];
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < n; ++j) {
                sArray2[i * n + j] = sArray[j * n2 + i];
            }
        }
        return sArray2;
    }

    private short[] matrix_mul(short[] sArray, int n, int n2, short[] sArray2, int n3, int n4) {
        int n5 = this.q - 1;
        short[] sArray3 = new short[n * n4];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n4; ++j) {
                int n6 = 0;
                for (int k = 0; k < n2; ++k) {
                    n6 += sArray[i * n2 + k] * sArray2[k * n4 + j];
                }
                sArray3[i * n4 + j] = (short)(n6 & n5);
            }
        }
        return sArray3;
    }

    private short[] matrix_add(short[] sArray, short[] sArray2, int n, int n2) {
        int n3 = this.q - 1;
        short[] sArray3 = new short[n * n2];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n2; ++j) {
                sArray3[i * n2 + j] = (short)(sArray[i * n2 + j] + sArray2[i * n2 + j] & n3);
            }
        }
        return sArray3;
    }

    private byte[] pack(short[] sArray) {
        int n = sArray.length;
        byte[] byArray = new byte[this.D * n / 8];
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        while (n2 < byArray.length && (n3 < n || n3 == n && n5 > 0)) {
            int n6 = 0;
            while (n6 < 8) {
                int n7 = Math.min(8 - n6, n5);
                short s = (short)((1 << n7) - 1);
                byte by = (byte)(n4 >> n5 - n7 & s);
                byArray[n2] = (byte)(byArray[n2] + (by << 8 - n6 - n7));
                n6 = (byte)(n6 + n7);
                if ((n5 = (int)((byte)(n5 - n7))) != 0) continue;
                if (n3 >= n) break;
                n4 = sArray[n3];
                n5 = (byte)this.D;
                n3 = (short)(n3 + 1);
            }
            if (n6 != 8) continue;
            n2 = (short)(n2 + 1);
        }
        return byArray;
    }

    public void kem_keypair(byte[] byArray, byte[] byArray2, SecureRandom secureRandom) {
        byte[] byArray3 = new byte[this.len_s_bytes + this.len_seedSE_bytes + 16];
        secureRandom.nextBytes(byArray3);
        byte[] byArray4 = Arrays.copyOfRange(byArray3, 0, this.len_s_bytes);
        byte[] byArray5 = Arrays.copyOfRange(byArray3, this.len_s_bytes, this.len_s_bytes + this.len_seedSE_bytes);
        byte[] byArray6 = Arrays.copyOfRange(byArray3, this.len_s_bytes + this.len_seedSE_bytes, this.len_s_bytes + this.len_seedSE_bytes + 16);
        byte[] byArray7 = new byte[16];
        this.digest.update(byArray6, 0, byArray6.length);
        this.digest.doFinal(byArray7, 0, byArray7.length);
        short[] sArray = this.gen.genMatrix(byArray7);
        byte[] byArray8 = new byte[2 * this.n * 8 * 2];
        this.digest.update((byte)95);
        this.digest.update(byArray5, 0, byArray5.length);
        this.digest.doFinal(byArray8, 0, byArray8.length);
        short[] sArray2 = new short[2 * this.n * 8];
        for (int i = 0; i < sArray2.length; ++i) {
            sArray2[i] = Pack.littleEndianToShort(byArray8, i * 2);
        }
        short[] sArray3 = this.sample_matrix(sArray2, 0, 8, this.n);
        short[] sArray4 = this.matrix_transpose(sArray3, 8, this.n);
        short[] sArray5 = this.sample_matrix(sArray2, this.n * 8, this.n, 8);
        short[] sArray6 = this.matrix_add(this.matrix_mul(sArray, this.n, this.n, sArray4, this.n, 8), sArray5, this.n, 8);
        byte[] byArray9 = this.pack(sArray6);
        System.arraycopy(Arrays.concatenate(byArray7, byArray9), 0, byArray, 0, this.len_pk_bytes);
        byte[] byArray10 = new byte[this.len_pkh_bytes];
        this.digest.update(byArray, 0, byArray.length);
        this.digest.doFinal(byArray10, 0, byArray10.length);
        System.arraycopy(Arrays.concatenate(byArray4, byArray), 0, byArray2, 0, this.len_s_bytes + this.len_pk_bytes);
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < this.n; ++j) {
                System.arraycopy(Pack.shortToLittleEndian(sArray3[i * this.n + j]), 0, byArray2, this.len_s_bytes + this.len_pk_bytes + i * this.n * 2 + j * 2, 2);
            }
        }
        System.arraycopy(byArray10, 0, byArray2, this.len_sk_bytes - this.len_pkh_bytes, this.len_pkh_bytes);
    }

    private short[] unpack(byte[] byArray, int n, int n2) {
        short[] sArray = new short[n * n2];
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        while (n3 < sArray.length && (n4 < byArray.length || n4 == byArray.length && n6 > 0)) {
            int n7 = 0;
            while (n7 < this.D) {
                int n8 = Math.min(this.D - n7, n6);
                short s = (short)((1 << n8) - 1 & 0xFFFF);
                byte by = (byte)((n5 & 0xFF) >>> (n6 & 0xFF) - n8 & (s & 0xFFFF) & 0xFF);
                sArray[n3] = (short)((sArray[n3] & 0xFFFF) + ((by & 0xFF) << this.D - (n7 & 0xFF) - n8) & 0xFFFF);
                n7 = (byte)(n7 + n8);
                n6 = (byte)(n6 - n8);
                n5 = (byte)(n5 & ~(s << n6));
                if (n6 != 0) continue;
                if (n4 >= byArray.length) break;
                n5 = byArray[n4];
                n6 = 8;
                n4 = (short)(n4 + 1);
            }
            if (n7 != this.D) continue;
            n3 = (short)(n3 + 1);
        }
        return sArray;
    }

    private short[] encode(byte[] byArray) {
        int n = 0;
        int n2 = 0;
        short[] sArray = new short[64];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                int n3 = 0;
                for (int k = 0; k < this.B; ++k) {
                    n3 += (byArray[n] >>> n2 & 1) << k;
                    n += ++n2 >>> 3;
                    n2 &= 7;
                }
                sArray[i * 8 + j] = (short)(n3 * (this.q / (1 << this.B)));
            }
        }
        return sArray;
    }

    public void kem_enc(byte[] byArray, byte[] byArray2, byte[] byArray3, SecureRandom secureRandom) {
        byte[] byArray4 = Arrays.copyOfRange(byArray3, 0, 16);
        byte[] byArray5 = Arrays.copyOfRange(byArray3, 16, this.len_pk_bytes);
        byte[] byArray6 = new byte[this.len_mu_bytes];
        secureRandom.nextBytes(byArray6);
        byte[] byArray7 = new byte[this.len_pkh_bytes];
        this.digest.update(byArray3, 0, this.len_pk_bytes);
        this.digest.doFinal(byArray7, 0, this.len_pkh_bytes);
        byte[] byArray8 = new byte[this.len_seedSE + this.len_k];
        this.digest.update(byArray7, 0, this.len_pkh_bytes);
        this.digest.update(byArray6, 0, this.len_mu_bytes);
        this.digest.doFinal(byArray8, 0, this.len_seedSE_bytes + this.len_k_bytes);
        byte[] byArray9 = Arrays.copyOfRange(byArray8, 0, this.len_seedSE_bytes);
        byte[] byArray10 = Arrays.copyOfRange(byArray8, this.len_seedSE_bytes, this.len_seedSE_bytes + this.len_k_bytes);
        byte[] byArray11 = new byte[(16 * this.n + 64) * 2];
        this.digest.update((byte)-106);
        this.digest.update(byArray9, 0, byArray9.length);
        this.digest.doFinal(byArray11, 0, byArray11.length);
        short[] sArray = new short[byArray11.length / 2];
        for (int i = 0; i < sArray.length; ++i) {
            sArray[i] = Pack.littleEndianToShort(byArray11, i * 2);
        }
        short[] sArray2 = this.sample_matrix(sArray, 0, 8, this.n);
        short[] sArray3 = this.sample_matrix(sArray, 8 * this.n, 8, this.n);
        short[] sArray4 = this.gen.genMatrix(byArray4);
        short[] sArray5 = this.matrix_add(this.matrix_mul(sArray2, 8, this.n, sArray4, this.n, this.n), sArray3, 8, this.n);
        byte[] byArray12 = this.pack(sArray5);
        short[] sArray6 = this.sample_matrix(sArray, 16 * this.n, 8, 8);
        short[] sArray7 = this.unpack(byArray5, this.n, 8);
        short[] sArray8 = this.matrix_add(this.matrix_mul(sArray2, 8, this.n, sArray7, this.n, 8), sArray6, 8, 8);
        short[] sArray9 = this.encode(byArray6);
        short[] sArray10 = this.matrix_add(sArray8, sArray9, 8, 8);
        byte[] byArray13 = this.pack(sArray10);
        System.arraycopy(Arrays.concatenate(byArray12, byArray13), 0, byArray, 0, this.len_ct_bytes);
        this.digest.update(byArray12, 0, byArray12.length);
        this.digest.update(byArray13, 0, byArray13.length);
        this.digest.update(byArray10, 0, this.len_k_bytes);
        this.digest.doFinal(byArray2, 0, this.len_s_bytes);
    }

    private short[] matrix_sub(short[] sArray, short[] sArray2, int n, int n2) {
        int n3 = this.q - 1;
        short[] sArray3 = new short[n * n2];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n2; ++j) {
                sArray3[i * n2 + j] = (short)(sArray[i * n2 + j] - sArray2[i * n2 + j] & n3);
            }
        }
        return sArray3;
    }

    private byte[] decode(short[] sArray) {
        int n = 0;
        int n2 = 8;
        int n3 = 8;
        short s = (short)((1 << this.B) - 1);
        short s2 = (short)((1 << this.D) - 1);
        byte[] byArray = new byte[n2 * this.B];
        for (int i = 0; i < n3; ++i) {
            int n4;
            long l = 0L;
            for (n4 = 0; n4 < n2; ++n4) {
                short s3 = (short)((sArray[n] & s2) + (1 << this.D - this.B - 1) >> this.D - this.B);
                l |= (long)(s3 & s) << this.B * n4;
                ++n;
            }
            for (n4 = 0; n4 < this.B; ++n4) {
                byArray[i * this.B + n4] = (byte)(l >> 8 * n4 & 0xFFL);
            }
        }
        return byArray;
    }

    private short ctverify(short[] sArray, short[] sArray2, short[] sArray3, short[] sArray4) {
        int n;
        int n2 = 0;
        for (n = 0; n < sArray.length; n = (int)((short)(n + 1))) {
            n2 = (short)(n2 | sArray[n] ^ sArray3[n]);
        }
        for (n = 0; n < sArray2.length; n = (int)((short)(n + 1))) {
            n2 = (short)(n2 | sArray2[n] ^ sArray4[n]);
        }
        if (n2 == 0) {
            return 0;
        }
        return -1;
    }

    private byte[] ctselect(byte[] byArray, byte[] byArray2, short s) {
        byte[] byArray3 = new byte[byArray.length];
        for (int i = 0; i < byArray.length; ++i) {
            byArray3[i] = (byte)(~s & byArray[i] & 0xFF | s & byArray2[i] & 0xFF);
        }
        return byArray3;
    }

    public void kem_dec(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int n = 0;
        int n2 = 8 * this.n * this.D / 8;
        byte[] byArray4 = Arrays.copyOfRange(byArray2, n, n + n2);
        n += n2;
        n2 = 64 * this.D / 8;
        byte[] byArray5 = Arrays.copyOfRange(byArray2, n, n + n2);
        n = 0;
        n2 = this.len_s_bytes;
        byte[] byArray6 = Arrays.copyOfRange(byArray3, n, n + n2);
        n += n2;
        n2 = 16;
        byte[] byArray7 = Arrays.copyOfRange(byArray3, n, n + n2);
        n += n2;
        n2 = this.D * this.n * 8 / 8;
        byte[] byArray8 = Arrays.copyOfRange(byArray3, n, n + n2);
        n += n2;
        n2 = this.n * 8 * 16 / 8;
        byte[] byArray9 = Arrays.copyOfRange(byArray3, n, n + n2);
        short[] sArray = new short[8 * this.n];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < this.n; ++j) {
                sArray[i * this.n + j] = Pack.littleEndianToShort(byArray9, i * this.n * 2 + j * 2);
            }
        }
        short[] sArray2 = this.matrix_transpose(sArray, 8, this.n);
        n += n2;
        n2 = this.len_pkh_bytes;
        byte[] byArray10 = Arrays.copyOfRange(byArray3, n, n + n2);
        short[] sArray3 = this.unpack(byArray4, 8, this.n);
        short[] sArray4 = this.unpack(byArray5, 8, 8);
        short[] sArray5 = this.matrix_mul(sArray3, 8, this.n, sArray2, this.n, 8);
        short[] sArray6 = this.matrix_sub(sArray4, sArray5, 8, 8);
        byte[] byArray11 = this.decode(sArray6);
        byte[] byArray12 = new byte[this.len_seedSE_bytes + this.len_k_bytes];
        this.digest.update(byArray10, 0, this.len_pkh_bytes);
        this.digest.update(byArray11, 0, this.len_mu_bytes);
        this.digest.doFinal(byArray12, 0, this.len_seedSE_bytes + this.len_k_bytes);
        byte[] byArray13 = Arrays.copyOfRange(byArray12, this.len_seedSE_bytes, this.len_seedSE_bytes + this.len_k_bytes);
        byte[] byArray14 = new byte[(16 * this.n + 64) * 2];
        this.digest.update((byte)-106);
        this.digest.update(byArray12, 0, this.len_seedSE_bytes);
        this.digest.doFinal(byArray14, 0, byArray14.length);
        short[] sArray7 = new short[16 * this.n + 64];
        for (int i = 0; i < sArray7.length; ++i) {
            sArray7[i] = Pack.littleEndianToShort(byArray14, i * 2);
        }
        short[] sArray8 = this.sample_matrix(sArray7, 0, 8, this.n);
        short[] sArray9 = this.sample_matrix(sArray7, 8 * this.n, 8, this.n);
        short[] sArray10 = this.gen.genMatrix(byArray7);
        short[] sArray11 = this.matrix_add(this.matrix_mul(sArray8, 8, this.n, sArray10, this.n, this.n), sArray9, 8, this.n);
        short[] sArray12 = this.sample_matrix(sArray7, 16 * this.n, 8, 8);
        short[] sArray13 = this.unpack(byArray8, this.n, 8);
        short[] sArray14 = this.matrix_add(this.matrix_mul(sArray8, 8, this.n, sArray13, this.n, 8), sArray12, 8, 8);
        short[] sArray15 = this.matrix_add(sArray14, this.encode(byArray11), 8, 8);
        short s = this.ctverify(sArray3, sArray4, sArray11, sArray15);
        byte[] byArray15 = this.ctselect(byArray13, byArray6, s);
        this.digest.update(byArray4, 0, byArray4.length);
        this.digest.update(byArray5, 0, byArray5.length);
        this.digest.update(byArray15, 0, byArray15.length);
        this.digest.doFinal(byArray, 0, this.len_ss_bytes);
    }
}

