/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.cmce.BENES;
import org.bouncycastle.pqc.crypto.cmce.BENES12;
import org.bouncycastle.pqc.crypto.cmce.BENES13;
import org.bouncycastle.pqc.crypto.cmce.GF;
import org.bouncycastle.pqc.crypto.cmce.GF12;
import org.bouncycastle.pqc.crypto.cmce.GF13;
import org.bouncycastle.pqc.crypto.cmce.Utils;
import org.bouncycastle.util.Arrays;

class CMCEEngine {
    private int SYS_N;
    private int SYS_T;
    private int GFBITS;
    private int IRR_BYTES;
    private int COND_BYTES;
    private int PK_NROWS;
    private int PK_NCOLS;
    private int PK_ROW_BYTES;
    private int SYND_BYTES;
    private int GFMASK;
    private int[] poly;
    private final int defaultKeySize;
    private GF gf;
    private BENES benes;
    private boolean usePadding;
    private boolean countErrorIndices;
    private boolean usePivots;

    public int getIrrBytes() {
        return this.IRR_BYTES;
    }

    public int getCondBytes() {
        return this.COND_BYTES;
    }

    public int getPrivateKeySize() {
        return this.COND_BYTES + this.IRR_BYTES + this.SYS_N / 8 + 40;
    }

    public int getPublicKeySize() {
        if (this.usePadding) {
            return this.PK_NROWS * (this.SYS_N / 8 - (this.PK_NROWS - 1) / 8);
        }
        return this.PK_NROWS * this.PK_NCOLS / 8;
    }

    public int getCipherTextSize() {
        return this.SYND_BYTES;
    }

    public CMCEEngine(int n, int n2, int n3, int[] nArray, boolean bl, int n4) {
        this.usePivots = bl;
        this.SYS_N = n2;
        this.SYS_T = n3;
        this.GFBITS = n;
        this.poly = nArray;
        this.defaultKeySize = n4;
        this.IRR_BYTES = this.SYS_T * 2;
        this.COND_BYTES = (1 << this.GFBITS - 4) * (2 * this.GFBITS - 1);
        this.PK_NROWS = this.SYS_T * this.GFBITS;
        this.PK_NCOLS = this.SYS_N - this.PK_NROWS;
        this.PK_ROW_BYTES = (this.PK_NCOLS + 7) / 8;
        this.SYND_BYTES = (this.PK_NROWS + 7) / 8;
        this.GFMASK = (1 << this.GFBITS) - 1;
        if (this.GFBITS == 12) {
            this.gf = new GF12();
            this.benes = new BENES12(this.SYS_N, this.SYS_T, this.GFBITS);
        } else {
            this.gf = new GF13();
            this.benes = new BENES13(this.SYS_N, this.SYS_T, this.GFBITS);
        }
        this.usePadding = this.SYS_T % 8 != 0;
        this.countErrorIndices = 1 << this.GFBITS > this.SYS_N;
    }

    public byte[] generate_public_key_from_private_key(byte[] byArray) {
        byte[] byArray2 = new byte[this.getPublicKeySize()];
        short[] sArray = new short[1 << this.GFBITS];
        long[] lArray = new long[]{0L};
        int[] nArray = new int[1 << this.GFBITS];
        byte[] byArray3 = new byte[this.SYS_N / 8 + (1 << this.GFBITS) * 4];
        int n = byArray3.length - 32 - this.IRR_BYTES - (1 << this.GFBITS) * 4;
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update((byte)64);
        sHAKEDigest.update(byArray, 0, 32);
        sHAKEDigest.doFinal(byArray3, 0, byArray3.length);
        for (int i = 0; i < 1 << this.GFBITS; ++i) {
            nArray[i] = Utils.load4(byArray3, n + i * 4);
        }
        this.pk_gen(byArray2, byArray, nArray, sArray, lArray);
        return byArray2;
    }

    public byte[] decompress_private_key(byte[] byArray) {
        int n;
        Object[] objectArray;
        Object[] objectArray2;
        byte[] byArray2 = new byte[this.getPrivateKeySize()];
        System.arraycopy(byArray, 0, byArray2, 0, byArray.length);
        byte[] byArray3 = new byte[this.SYS_N / 8 + (1 << this.GFBITS) * 4 + this.IRR_BYTES + 32];
        int n2 = 0;
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update((byte)64);
        sHAKEDigest.update(byArray, 0, 32);
        sHAKEDigest.doFinal(byArray3, 0, byArray3.length);
        if (byArray.length <= 40) {
            objectArray2 = new short[this.SYS_T];
            objectArray = new byte[this.IRR_BYTES];
            n2 = byArray3.length - 32 - this.IRR_BYTES;
            for (n = 0; n < this.SYS_T; ++n) {
                objectArray2[n] = Utils.load_gf(byArray3, n2 + n * 2, this.GFMASK);
            }
            this.generate_irr_poly((short[])objectArray2);
            for (n = 0; n < this.SYS_T; ++n) {
                Utils.store_gf(objectArray, n * 2, objectArray2[n]);
            }
            System.arraycopy(objectArray, 0, byArray2, 40, this.IRR_BYTES);
        }
        if (byArray.length <= 40 + this.IRR_BYTES) {
            Object[] objectArray3;
            objectArray2 = new int[1 << this.GFBITS];
            objectArray = new short[1 << this.GFBITS];
            n2 = byArray3.length - 32 - this.IRR_BYTES - (1 << this.GFBITS) * 4;
            for (n = 0; n < 1 << this.GFBITS; ++n) {
                objectArray2[n] = Utils.load4(byArray3, n2 + n * 4);
            }
            if (this.usePivots) {
                objectArray3 = new long[]{0L};
                this.pk_gen(null, byArray2, (int[])objectArray2, (short[])objectArray, (long[])objectArray3);
            } else {
                objectArray3 = new long[1 << this.GFBITS];
                int n3 = 0;
                while (n3 < 1 << this.GFBITS) {
                    objectArray3[n3] = objectArray2[n3];
                    int n4 = n3;
                    objectArray3[n4] = objectArray3[n4] << 31;
                    int n5 = n3;
                    objectArray3[n5] = objectArray3[n5] | (long)n3;
                    int n6 = n3++;
                    objectArray3[n6] = objectArray3[n6] & Long.MAX_VALUE;
                }
                CMCEEngine.sort64(objectArray3, 0, objectArray3.length);
                for (n3 = 0; n3 < 1 << this.GFBITS; ++n3) {
                    objectArray[n3] = (short)(objectArray3[n3] & (long)this.GFMASK);
                }
            }
            objectArray3 = new byte[this.COND_BYTES];
            CMCEEngine.controlbitsfrompermutation((byte[])objectArray3, objectArray, this.GFBITS, 1 << this.GFBITS);
            System.arraycopy(objectArray3, 0, byArray2, this.IRR_BYTES + 40, objectArray3.length);
        }
        System.arraycopy(byArray3, 0, byArray2, this.getPrivateKeySize() - this.SYS_N / 8, this.SYS_N / 8);
        return byArray2;
    }

    public void kem_keypair(byte[] byArray, byte[] byArray2, SecureRandom secureRandom) {
        short[] sArray;
        byte[] byArray3 = new byte[1];
        byte[] byArray4 = new byte[32];
        byArray3[0] = 64;
        secureRandom.nextBytes(byArray4);
        byte[] byArray5 = new byte[this.SYS_N / 8 + (1 << this.GFBITS) * 4 + this.SYS_T * 2 + 32];
        int n = 0;
        byte[] byArray6 = byArray4;
        long[] lArray = new long[]{0L};
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        while (true) {
            int n2;
            int n3;
            sHAKEDigest.update(byArray3, 0, byArray3.length);
            sHAKEDigest.update(byArray4, 0, byArray4.length);
            sHAKEDigest.doFinal(byArray5, 0, byArray5.length);
            int n4 = byArray5.length - 32;
            byArray4 = Arrays.copyOfRange(byArray5, n4, n4 + 32);
            System.arraycopy(byArray6, 0, byArray2, 0, 32);
            byArray6 = Arrays.copyOfRange(byArray4, 0, 32);
            short[] sArray2 = new short[this.SYS_T];
            n4 = n3 = byArray5.length - 32 - 2 * this.SYS_T;
            for (n2 = 0; n2 < this.SYS_T; ++n2) {
                sArray2[n2] = Utils.load_gf(byArray5, n3 + n2 * 2, this.GFMASK);
            }
            if (this.generate_irr_poly(sArray2) == -1) continue;
            n = 40;
            for (n2 = 0; n2 < this.SYS_T; ++n2) {
                Utils.store_gf(byArray2, n + n2 * 2, sArray2[n2]);
            }
            int[] nArray = new int[1 << this.GFBITS];
            n4 -= (1 << this.GFBITS) * 4;
            for (int i = 0; i < 1 << this.GFBITS; ++i) {
                nArray[i] = Utils.load4(byArray5, n4 + i * 4);
            }
            sArray = new short[1 << this.GFBITS];
            if (this.pk_gen(byArray, byArray2, nArray, sArray, lArray) != -1) break;
        }
        byte[] byArray7 = new byte[this.COND_BYTES];
        CMCEEngine.controlbitsfrompermutation(byArray7, sArray, this.GFBITS, 1 << this.GFBITS);
        System.arraycopy(byArray7, 0, byArray2, this.IRR_BYTES + 40, byArray7.length);
        System.arraycopy(byArray5, n4 -= this.SYS_N / 8, byArray2, byArray2.length - this.SYS_N / 8, this.SYS_N / 8);
        if (!this.usePivots) {
            Utils.store8(byArray2, 32, 0xFFFFFFFFL);
        } else {
            Utils.store8(byArray2, 32, lArray[0]);
        }
    }

    private void syndrome(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int n;
        short[] sArray = new short[this.SYS_N / 8];
        int n2 = 0;
        int n3 = this.PK_NROWS % 8;
        for (n = 0; n < this.SYND_BYTES; ++n) {
            byArray[n] = 0;
        }
        for (n = 0; n < this.PK_NROWS; ++n) {
            int n4;
            for (n4 = 0; n4 < this.SYS_N / 8; ++n4) {
                sArray[n4] = 0;
            }
            for (n4 = 0; n4 < this.PK_ROW_BYTES; ++n4) {
                sArray[this.SYS_N / 8 - this.PK_ROW_BYTES + n4] = byArray2[n2 + n4];
            }
            if (this.usePadding) {
                for (n4 = this.SYS_N / 8 - 1; n4 >= this.SYS_N / 8 - this.PK_ROW_BYTES; --n4) {
                    sArray[n4] = (short)(((sArray[n4] & 0xFF) << n3 | (sArray[n4 - 1] & 0xFF) >>> 8 - n3) & 0xFF);
                }
            }
            int n5 = n / 8;
            sArray[n5] = (short)(sArray[n5] | 1 << n % 8);
            int n6 = 0;
            for (n4 = 0; n4 < this.SYS_N / 8; ++n4) {
                n6 = (byte)(n6 ^ sArray[n4] & byArray3[n4]);
            }
            n6 = (byte)(n6 ^ n6 >>> 4);
            n6 = (byte)(n6 ^ n6 >>> 2);
            n6 = (byte)(n6 ^ n6 >>> 1);
            n6 = (byte)(n6 & 1);
            int n7 = n / 8;
            byArray[n7] = (byte)(byArray[n7] | n6 << n % 8);
            n2 += this.PK_ROW_BYTES;
        }
    }

    private void generate_error_vector(byte[] byArray, SecureRandom secureRandom) {
        int n;
        int n2;
        short s;
        short[] sArray = new short[this.SYS_T * 2];
        short[] sArray2 = new short[this.SYS_T];
        byte[] byArray2 = new byte[this.SYS_T];
        while (true) {
            byte[] byArray3;
            if (this.countErrorIndices) {
                byArray3 = new byte[this.SYS_T * 4];
                secureRandom.nextBytes(byArray3);
                for (s = 0; s < this.SYS_T * 2; ++s) {
                    sArray[s] = Utils.load_gf(byArray3, s * 2, this.GFMASK);
                }
                s = 0;
                for (n2 = 0; n2 < this.SYS_T * 2 && s < this.SYS_T; ++n2) {
                    if (sArray[n2] >= this.SYS_N) continue;
                    sArray2[s++] = sArray[n2];
                }
                if (s < this.SYS_T) {
                    continue;
                }
            } else {
                byArray3 = new byte[this.SYS_T * 2];
                secureRandom.nextBytes(byArray3);
                for (s = 0; s < this.SYS_T; ++s) {
                    sArray2[s] = Utils.load_gf(byArray3, s * 2, this.GFMASK);
                }
            }
            s = 0;
            block4: for (n2 = 1; n2 < this.SYS_T && s != 1; ++n2) {
                for (n = 0; n < n2; ++n) {
                    if (sArray2[n2] != sArray2[n]) continue;
                    s = 1;
                    continue block4;
                }
            }
            if (s == 0) break;
        }
        for (s = 0; s < this.SYS_T; ++s) {
            byArray2[s] = (byte)(1 << (sArray2[s] & 7));
        }
        for (s = 0; s < this.SYS_N / 8; s = (short)((short)(s + 1))) {
            byArray[s] = 0;
            for (n2 = 0; n2 < this.SYS_T; ++n2) {
                n = CMCEEngine.same_mask32(s, (short)(sArray2[n2] >> 3));
                n = (short)(n & 0xFF);
                short s2 = s;
                byArray[s2] = (byte)(byArray[s2] | byArray2[n2] & n);
            }
        }
    }

    private void encrypt(byte[] byArray, byte[] byArray2, byte[] byArray3, SecureRandom secureRandom) {
        this.generate_error_vector(byArray3, secureRandom);
        this.syndrome(byArray, byArray2, byArray3);
    }

    public int kem_enc(byte[] byArray, byte[] byArray2, byte[] byArray3, SecureRandom secureRandom) {
        byte[] byArray4 = new byte[this.SYS_N / 8];
        int n = 0;
        if (this.usePadding) {
            n = this.check_pk_padding(byArray3);
        }
        this.encrypt(byArray, byArray3, byArray4, secureRandom);
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update((byte)1);
        sHAKEDigest.update(byArray4, 0, byArray4.length);
        sHAKEDigest.update(byArray, 0, byArray.length);
        sHAKEDigest.doFinal(byArray2, 0, byArray2.length);
        if (this.usePadding) {
            byte by = (byte)n;
            by = (byte)(by ^ 0xFF);
            int n2 = 0;
            while (n2 < this.SYND_BYTES) {
                int n3 = n2++;
                byArray[n3] = (byte)(byArray[n3] & by);
            }
            n2 = 0;
            while (n2 < 32) {
                int n4 = n2++;
                byArray2[n4] = (byte)(byArray2[n4] & by);
            }
            return n;
        }
        return 0;
    }

    public int kem_dec(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int n;
        byte[] byArray4 = new byte[this.SYS_N / 8];
        byte[] byArray5 = new byte[1 + this.SYS_N / 8 + this.SYND_BYTES];
        int n2 = 0;
        if (this.usePadding) {
            n2 = this.check_c_padding(byArray2);
        }
        byte by = (byte)this.decrypt(byArray4, byArray3, byArray2);
        short s = by;
        s = (short)(s - 1);
        s = (short)(s >> 8);
        s = (short)(s & 0xFF);
        byArray5[0] = (byte)(s & 1);
        for (n = 0; n < this.SYS_N / 8; ++n) {
            byArray5[1 + n] = (byte)(~s & byArray3[n + 40 + this.IRR_BYTES + this.COND_BYTES] | s & byArray4[n]);
        }
        for (n = 0; n < this.SYND_BYTES; ++n) {
            byArray5[1 + this.SYS_N / 8 + n] = byArray2[n];
        }
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(byArray5, 0, byArray5.length);
        sHAKEDigest.doFinal(byArray, 0, byArray.length);
        if (this.usePadding) {
            byte by2 = (byte)n2;
            n = 0;
            while (n < byArray.length) {
                int n3 = n++;
                byArray[n3] = (byte)(byArray[n3] | by2);
            }
            return n2;
        }
        return 0;
    }

    private int decrypt(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int n;
        int n2;
        short[] sArray = new short[this.SYS_T + 1];
        short[] sArray2 = new short[this.SYS_N];
        short[] sArray3 = new short[this.SYS_T * 2];
        short[] sArray4 = new short[this.SYS_T * 2];
        short[] sArray5 = new short[this.SYS_T + 1];
        short[] sArray6 = new short[this.SYS_N];
        byte[] byArray4 = new byte[this.SYS_N / 8];
        for (n2 = 0; n2 < this.SYND_BYTES; ++n2) {
            byArray4[n2] = byArray3[n2];
        }
        for (n2 = this.SYND_BYTES; n2 < this.SYS_N / 8; ++n2) {
            byArray4[n2] = 0;
        }
        for (n2 = 0; n2 < this.SYS_T; ++n2) {
            sArray[n2] = Utils.load_gf(byArray2, 40 + n2 * 2, this.GFMASK);
        }
        sArray[this.SYS_T] = 1;
        this.benes.support_gen(sArray2, byArray2);
        this.synd(sArray3, sArray, sArray2, byArray4);
        this.bm(sArray5, sArray3);
        this.root(sArray6, sArray5, sArray2);
        for (n2 = 0; n2 < this.SYS_N / 8; ++n2) {
            byArray[n2] = 0;
        }
        n2 = 0;
        for (n = 0; n < this.SYS_N; ++n) {
            short s = (short)(this.gf.gf_iszero(sArray6[n]) & 1);
            int n3 = n / 8;
            byArray[n3] = (byte)(byArray[n3] | s << n % 8);
            n2 += s;
        }
        this.synd(sArray4, sArray, sArray2, byArray);
        n = n2;
        n ^= this.SYS_T;
        for (int i = 0; i < this.SYS_T * 2; ++i) {
            n |= sArray3[i] ^ sArray4[i];
        }
        --n;
        n >>= 15;
        if (((n &= 1) ^ 1) != 0) {
            // empty if block
        }
        return n ^ 1;
    }

    private static int min(short s, int n) {
        if (s < n) {
            return s;
        }
        return n;
    }

    private void bm(short[] sArray, short[] sArray2) {
        int n;
        short s = 0;
        int n2 = 0;
        short[] sArray3 = new short[this.SYS_T + 1];
        short[] sArray4 = new short[this.SYS_T + 1];
        short[] sArray5 = new short[this.SYS_T + 1];
        short s2 = 1;
        for (n = 0; n < this.SYS_T + 1; ++n) {
            sArray5[n] = 0;
            sArray4[n] = 0;
        }
        sArray4[0] = 1;
        sArray5[1] = 1;
        for (s = 0; s < 2 * this.SYS_T; s = (short)(s + 1)) {
            short s3;
            int n3;
            n = 0;
            for (n3 = 0; n3 <= CMCEEngine.min(s, this.SYS_T); ++n3) {
                n ^= this.gf.gf_mul_ext(sArray4[n3], sArray2[s - n3]);
            }
            short s4 = s3 = this.gf.gf_reduce(n);
            s4 = (short)(s4 - 1);
            s4 = (short)(s4 >> 15);
            s4 = (short)(s4 & 1);
            s4 = (short)(s4 - 1);
            short s5 = s;
            s5 = (short)(s5 - 2 * n2);
            s5 = (short)(s5 >> 15);
            s5 = (short)(s5 & 1);
            s5 = (short)(s5 - 1);
            s5 = (short)(s5 & s4);
            for (n3 = 0; n3 <= this.SYS_T; ++n3) {
                sArray3[n3] = sArray4[n3];
            }
            short s6 = this.gf.gf_frac(s2, s3);
            for (n3 = 0; n3 <= this.SYS_T; ++n3) {
                int n4 = n3;
                sArray4[n4] = (short)(sArray4[n4] ^ this.gf.gf_mul(s6, sArray5[n3]) & s4);
            }
            n2 = (short)(n2 & ~s5 | s + 1 - n2 & s5);
            for (n3 = this.SYS_T - 1; n3 >= 0; --n3) {
                sArray5[n3 + 1] = (short)(sArray5[n3] & ~s5 | sArray3[n3] & s5);
            }
            sArray5[0] = 0;
            s2 = (short)(s2 & ~s5 | s3 & s5);
        }
        for (n = 0; n <= this.SYS_T; ++n) {
            sArray[n] = sArray4[this.SYS_T - n];
        }
    }

    private void synd(short[] sArray, short[] sArray2, short[] sArray3, byte[] byArray) {
        short s;
        short s2;
        int n = byArray[0] & 1;
        short s3 = sArray3[0];
        short s4 = this.eval(sArray2, s3);
        short s5 = this.gf.gf_inv(this.gf.gf_sq(s4));
        sArray[0] = s2 = (short)(s5 & -n);
        for (s = 1; s < 2 * this.SYS_T; ++s) {
            sArray[s] = s2 = this.gf.gf_mul(s2, s3);
        }
        for (n = 1; n < this.SYS_N; ++n) {
            s3 = (short)(byArray[n / 8] >> n % 8 & 1);
            s4 = sArray3[n];
            s5 = this.eval(sArray2, s4);
            s2 = this.gf.gf_inv(this.gf.gf_sq(s5));
            s = this.gf.gf_mul(s2, s3);
            sArray[0] = (short)(sArray[0] ^ s);
            int n2 = 1;
            while (n2 < 2 * this.SYS_T) {
                s = this.gf.gf_mul(s, s4);
                int n3 = n2++;
                sArray[n3] = (short)(sArray[n3] ^ s);
            }
        }
    }

    private int mov_columns(byte[][] byArray, short[] sArray, long[] lArray) {
        long l;
        int n;
        long l2;
        int n2;
        int n3;
        long[] lArray2 = new long[64];
        long[] lArray3 = new long[32];
        long l3 = 1L;
        byte[] byArray2 = new byte[9];
        int n4 = this.PK_NROWS - 32;
        int n5 = n4 / 8;
        int n6 = n4 % 8;
        if (this.usePadding) {
            for (n3 = 0; n3 < 32; ++n3) {
                for (n2 = 0; n2 < 9; ++n2) {
                    byArray2[n2] = byArray[n4 + n3][n5 + n2];
                }
                for (n2 = 0; n2 < 8; ++n2) {
                    byArray2[n2] = (byte)((byArray2[n2] & 0xFF) >> n6 | byArray2[n2 + 1] << 8 - n6);
                }
                lArray2[n3] = Utils.load8(byArray2, 0);
            }
        } else {
            for (n3 = 0; n3 < 32; ++n3) {
                lArray2[n3] = Utils.load8(byArray[n4 + n3], n5);
            }
        }
        lArray[0] = 0L;
        for (n3 = 0; n3 < 32; ++n3) {
            long l4;
            l2 = lArray2[n3];
            for (n2 = n3 + 1; n2 < 32; ++n2) {
                l2 |= lArray2[n2];
            }
            if (l2 == 0L) {
                return -1;
            }
            int n7 = CMCEEngine.ctz(l2);
            lArray3[n3] = n7;
            lArray[0] = lArray[0] | l3 << (int)lArray3[n3];
            for (n2 = n3 + 1; n2 < 32; ++n2) {
                l4 = lArray2[n3] >> n7 & 1L;
                int n8 = n3;
                lArray2[n8] = lArray2[n8] ^ lArray2[n2] & --l4;
            }
            n2 = n3 + 1;
            while (n2 < 32) {
                l4 = lArray2[n2] >> n7 & 1L;
                l4 = -l4;
                int n9 = n2++;
                lArray2[n9] = lArray2[n9] ^ lArray2[n3] & l4;
            }
        }
        for (n2 = 0; n2 < 32; ++n2) {
            for (n = n2 + 1; n < 64; ++n) {
                l = sArray[n4 + n2] ^ sArray[n4 + n];
                int n10 = n4 + n2;
                sArray[n10] = (short)((long)sArray[n10] ^ (l &= CMCEEngine.same_mask64((short)n, (short)lArray3[n2])));
                int n11 = n4 + n;
                sArray[n11] = (short)((long)sArray[n11] ^ l);
            }
        }
        for (n3 = 0; n3 < this.PK_NROWS; ++n3) {
            if (this.usePadding) {
                for (n = 0; n < 9; ++n) {
                    byArray2[n] = byArray[n3][n5 + n];
                }
                for (n = 0; n < 8; ++n) {
                    byArray2[n] = (byte)((byArray2[n] & 0xFF) >> n6 | byArray2[n + 1] << 8 - n6);
                }
                l2 = Utils.load8(byArray2, 0);
            } else {
                l2 = Utils.load8(byArray[n3], n5);
            }
            for (n2 = 0; n2 < 32; ++n2) {
                l = l2 >> n2;
                l ^= l2 >> (int)lArray3[n2];
                l2 ^= (l &= 1L) << (int)lArray3[n2];
                l2 ^= l << n2;
            }
            if (this.usePadding) {
                Utils.store8(byArray2, 0, l2);
                byArray[n3][n5 + 8] = (byte)((byArray[n3][n5 + 8] & 0xFF) >>> n6 << n6 | (byArray2[7] & 0xFF) >>> 8 - n6);
                byArray[n3][n5 + 0] = (byte)((byArray2[0] & 0xFF) << n6 | (byArray[n3][n5] & 0xFF) << 8 - n6 >>> 8 - n6);
                for (n = 7; n >= 1; --n) {
                    byArray[n3][n5 + n] = (byte)((byArray2[n] & 0xFF) << n6 | (byArray2[n - 1] & 0xFF) >>> 8 - n6);
                }
                continue;
            }
            Utils.store8(byArray[n3], n5, l2);
        }
        return 0;
    }

    private static int ctz(long l) {
        long l2 = 0x101010101010101L;
        long l3 = 0L;
        long l4 = l ^ 0xFFFFFFFFFFFFFFFFL;
        for (int i = 0; i < 8; ++i) {
            l3 += (l2 &= l4 >>> i);
        }
        long l5 = l3 & 0x808080808080808L;
        l5 |= l5 >>> 1;
        l5 |= l5 >>> 2;
        long l6 = l3;
        l6 += (l3 >>>= 8) & l5;
        for (int i = 2; i < 8; ++i) {
            l5 &= l5 >>> 8;
            l6 += (l3 >>>= 8) & l5;
        }
        return (int)l6 & 0xFF;
    }

    private static long same_mask64(short s, short s2) {
        long l = s ^ s2;
        --l;
        l >>>= 63;
        l = -l;
        return l;
    }

    private static byte same_mask32(short s, short s2) {
        int n = s ^ s2;
        --n;
        n >>>= 31;
        n = -n;
        return (byte)(n & 0xFF);
    }

    private static void layer(short[] sArray, byte[] byArray, int n, int n2, int n3) {
        int n4 = 1 << n2;
        int n5 = 0;
        for (int i = 0; i < n3; i += n4 * 2) {
            for (int j = 0; j < n4; ++j) {
                int n6 = sArray[i + j] ^ sArray[i + j + n4];
                int n7 = byArray[n + (n5 >> 3)] >> (n5 & 7) & 1;
                n7 = -n7;
                int n8 = i + j;
                sArray[n8] = (short)(sArray[n8] ^ (n6 &= n7));
                int n9 = i + j + n4;
                sArray[n9] = (short)(sArray[n9] ^ n6);
                ++n5;
            }
        }
    }

    private static void controlbitsfrompermutation(byte[] byArray, short[] sArray, long l, long l2) {
        int n;
        int[] nArray = new int[(int)(2L * l2)];
        short[] sArray2 = new short[(int)l2];
        do {
            int n2 = 0;
            while ((long)n2 < ((2L * l - 1L) * l2 / 2L + 7L) / 8L) {
                byArray[n2] = 0;
                ++n2;
            }
            CMCEEngine.cbrecursion(byArray, 0L, 1L, sArray, 0, l, l2, nArray);
            n2 = 0;
            while ((long)n2 < l2) {
                sArray2[n2] = (short)n2;
                ++n2;
            }
            int n3 = 0;
            n2 = 0;
            while ((long)n2 < l) {
                CMCEEngine.layer(sArray2, byArray, n3, n2, (int)l2);
                n3 = (int)((long)n3 + (l2 >> 4));
                ++n2;
            }
            for (n2 = (int)(l - 2L); n2 >= 0; --n2) {
                CMCEEngine.layer(sArray2, byArray, n3, n2, (int)l2);
                n3 = (int)((long)n3 + (l2 >> 4));
            }
            n = 0;
            n2 = 0;
            while ((long)n2 < l2) {
                n = (short)(n | sArray[n2] ^ sArray2[n2]);
                ++n2;
            }
        } while (n != 0);
    }

    static short get_q_short(int[] nArray, int n) {
        int n2 = n / 2;
        if (n % 2 == 0) {
            return (short)nArray[n2];
        }
        return (short)((nArray[n2] & 0xFFFF0000) >> 16);
    }

    static void cbrecursion(byte[] byArray, long l, long l2, short[] sArray, int n, long l3, long l4, int[] nArray) {
        int n2;
        int n3;
        long l5;
        long l6;
        int n4;
        int n5;
        int n6;
        long l7;
        if (l3 == 1L) {
            int n7 = (int)(l >> 3);
            byArray[n7] = (byte)(byArray[n7] ^ CMCEEngine.get_q_short(nArray, n) << (int)(l & 7L));
            return;
        }
        if (sArray != null) {
            for (l7 = 0L; l7 < l4; ++l7) {
                nArray[(int)l7] = (sArray[(int)l7] ^ 1) << 16 | sArray[(int)(l7 ^ 1L)];
            }
        } else {
            for (l7 = 0L; l7 < l4; ++l7) {
                nArray[(int)l7] = (CMCEEngine.get_q_short(nArray, (int)((long)n + l7)) ^ 1) << 16 | CMCEEngine.get_q_short(nArray, (int)((long)n + (l7 ^ 1L)));
            }
        }
        CMCEEngine.sort32(nArray, 0, (int)l4);
        for (l7 = 0L; l7 < l4; ++l7) {
            n6 = nArray[(int)l7];
            n5 = n6 & 0xFFFF;
            n4 = n5;
            if (l7 < (long)n4) {
                n4 = (int)l7;
            }
            nArray[(int)(l4 + l7)] = n5 << 16 | n4;
        }
        for (l7 = 0L; l7 < l4; ++l7) {
            nArray[(int)l7] = (int)((long)(nArray[(int)l7] << 16) | l7);
        }
        CMCEEngine.sort32(nArray, 0, (int)l4);
        for (l7 = 0L; l7 < l4; ++l7) {
            nArray[(int)l7] = (nArray[(int)l7] << 16) + (nArray[(int)(l4 + l7)] >> 16);
        }
        CMCEEngine.sort32(nArray, 0, (int)l4);
        if (l3 <= 10L) {
            for (l7 = 0L; l7 < l4; ++l7) {
                nArray[(int)(l4 + l7)] = (nArray[(int)l7] & 0xFFFF) << 10 | nArray[(int)(l4 + l7)] & 0x3FF;
            }
            for (l6 = 1L; l6 < l3 - 1L; ++l6) {
                for (l7 = 0L; l7 < l4; ++l7) {
                    nArray[(int)l7] = (int)((long)((nArray[(int)(l4 + l7)] & 0xFFFFFC00) << 6) | l7);
                }
                CMCEEngine.sort32(nArray, 0, (int)l4);
                for (l7 = 0L; l7 < l4; ++l7) {
                    nArray[(int)l7] = nArray[(int)l7] << 20 | nArray[(int)(l4 + l7)];
                }
                CMCEEngine.sort32(nArray, 0, (int)l4);
                for (l7 = 0L; l7 < l4; ++l7) {
                    n6 = nArray[(int)l7] & 0xFFFFF;
                    n5 = nArray[(int)l7] & 0xFFC00 | nArray[(int)(l4 + l7)] & 0x3FF;
                    if (n6 < n5) {
                        n5 = n6;
                    }
                    nArray[(int)(l4 + l7)] = n5;
                }
            }
            for (l7 = 0L; l7 < l4; ++l7) {
                int n8 = (int)(l4 + l7);
                nArray[n8] = nArray[n8] & 0x3FF;
            }
        } else {
            for (l7 = 0L; l7 < l4; ++l7) {
                nArray[(int)(l4 + l7)] = nArray[(int)l7] << 16 | nArray[(int)(l4 + l7)] & 0xFFFF;
            }
            for (l6 = 1L; l6 < l3 - 1L; ++l6) {
                for (l7 = 0L; l7 < l4; ++l7) {
                    nArray[(int)l7] = (int)((long)(nArray[(int)(l4 + l7)] & 0xFFFF0000) | l7);
                }
                CMCEEngine.sort32(nArray, 0, (int)l4);
                for (l7 = 0L; l7 < l4; ++l7) {
                    nArray[(int)l7] = nArray[(int)l7] << 16 | nArray[(int)(l4 + l7)] & 0xFFFF;
                }
                if (l6 < l3 - 2L) {
                    for (l7 = 0L; l7 < l4; ++l7) {
                        nArray[(int)(l4 + l7)] = nArray[(int)l7] & 0xFFFF0000 | nArray[(int)(l4 + l7)] >> 16;
                    }
                    CMCEEngine.sort32(nArray, (int)l4, (int)(l4 * 2L));
                    for (l7 = 0L; l7 < l4; ++l7) {
                        nArray[(int)(l4 + l7)] = nArray[(int)(l4 + l7)] << 16 | nArray[(int)l7] & 0xFFFF;
                    }
                }
                CMCEEngine.sort32(nArray, 0, (int)l4);
                for (l7 = 0L; l7 < l4; ++l7) {
                    n6 = nArray[(int)(l4 + l7)] & 0xFFFF0000 | nArray[(int)l7] & 0xFFFF;
                    if (n6 >= nArray[(int)(l4 + l7)]) continue;
                    nArray[(int)(l4 + l7)] = n6;
                }
            }
            for (l7 = 0L; l7 < l4; ++l7) {
                int n9 = (int)(l4 + l7);
                nArray[n9] = nArray[n9] & 0xFFFF;
            }
        }
        if (sArray != null) {
            for (l7 = 0L; l7 < l4; ++l7) {
                nArray[(int)l7] = (int)((long)(sArray[(int)l7] << 16) + l7);
            }
        } else {
            for (l7 = 0L; l7 < l4; ++l7) {
                nArray[(int)l7] = (int)((long)(CMCEEngine.get_q_short(nArray, (int)((long)n + l7)) << 16) + l7);
            }
        }
        CMCEEngine.sort32(nArray, 0, (int)l4);
        for (l5 = 0L; l5 < l4 / 2L; ++l5) {
            long l8 = 2L * l5;
            n4 = nArray[(int)(l4 + l8)] & 1;
            n3 = (int)(l8 + (long)n4);
            n2 = n3 ^ 1;
            int n10 = (int)(l >> 3);
            byArray[n10] = (byte)(byArray[n10] ^ n4 << (int)(l & 7L));
            l += l2;
            nArray[(int)(l4 + l8)] = nArray[(int)l8] << 16 | n3;
            nArray[(int)(l4 + l8 + 1L)] = nArray[(int)(l8 + 1L)] << 16 | n2;
        }
        CMCEEngine.sort32(nArray, (int)l4, (int)(l4 * 2L));
        l += (2L * l3 - 3L) * l2 * (l4 / 2L);
        for (long i = 0L; i < l4 / 2L; ++i) {
            long l9 = 2L * i;
            n4 = nArray[(int)(l4 + l9)] & 1;
            n3 = (int)(l9 + (long)n4);
            n2 = n3 ^ 1;
            int n11 = (int)(l >> 3);
            byArray[n11] = (byte)(byArray[n11] ^ n4 << (int)(l & 7L));
            l += l2;
            nArray[(int)l9] = n3 << 16 | nArray[(int)(l4 + l9)] & 0xFFFF;
            nArray[(int)(l9 + 1L)] = n2 << 16 | nArray[(int)(l4 + l9 + 1L)] & 0xFFFF;
        }
        CMCEEngine.sort32(nArray, 0, (int)l4);
        l -= (2L * l3 - 2L) * l2 * (l4 / 2L);
        short[] sArray2 = new short[(int)l4 * 4];
        for (l6 = 0L; l6 < l4 * 2L; ++l6) {
            sArray2[(int)(l6 * 2L + 0L)] = (short)nArray[(int)l6];
            sArray2[(int)(l6 * 2L + 1L)] = (short)((nArray[(int)l6] & 0xFFFF0000) >> 16);
        }
        for (l5 = 0L; l5 < l4 / 2L; ++l5) {
            sArray2[(int)l5] = (short)((nArray[(int)(2L * l5)] & 0xFFFF) >>> 1);
            sArray2[(int)(l5 + l4 / 2L)] = (short)((nArray[(int)(2L * l5 + 1L)] & 0xFFFF) >>> 1);
        }
        for (l6 = 0L; l6 < l4 / 2L; ++l6) {
            nArray[(int)(l4 + l4 / 4L + l6)] = sArray2[(int)(l6 * 2L + 1L)] << 16 | sArray2[(int)(l6 * 2L)];
        }
        CMCEEngine.cbrecursion(byArray, l, l2 * 2L, null, (int)(l4 + l4 / 4L) * 2, l3 - 1L, l4 / 2L, nArray);
        CMCEEngine.cbrecursion(byArray, l + l2, l2 * 2L, null, (int)((l4 + l4 / 4L) * 2L + l4 / 2L), l3 - 1L, l4 / 2L, nArray);
    }

    private int pk_gen(byte[] byArray, byte[] byArray2, int[] nArray, short[] sArray, long[] lArray) {
        block26: {
            int n;
            int n2;
            int n3;
            short[] sArray2 = new short[this.SYS_T + 1];
            sArray2[this.SYS_T] = 1;
            for (n3 = 0; n3 < this.SYS_T; ++n3) {
                sArray2[n3] = Utils.load_gf(byArray2, 40 + n3 * 2, this.GFMASK);
            }
            long[] lArray2 = new long[1 << this.GFBITS];
            n3 = 0;
            while (n3 < 1 << this.GFBITS) {
                lArray2[n3] = nArray[n3];
                int n4 = n3;
                lArray2[n4] = lArray2[n4] << 31;
                int n5 = n3;
                lArray2[n5] = lArray2[n5] | (long)n3;
                int n6 = n3++;
                lArray2[n6] = lArray2[n6] & Long.MAX_VALUE;
            }
            CMCEEngine.sort64(lArray2, 0, lArray2.length);
            for (n3 = 1; n3 < 1 << this.GFBITS; ++n3) {
                if (lArray2[n3 - 1] >> 31 != lArray2[n3] >> 31) continue;
                return -1;
            }
            short[] sArray3 = new short[this.SYS_N];
            for (n3 = 0; n3 < 1 << this.GFBITS; ++n3) {
                sArray[n3] = (short)(lArray2[n3] & (long)this.GFMASK);
            }
            for (n3 = 0; n3 < this.SYS_N; ++n3) {
                sArray3[n3] = Utils.bitrev(sArray[n3], this.GFBITS);
            }
            short[] sArray4 = new short[this.SYS_N];
            this.root(sArray4, sArray2, sArray3);
            for (n3 = 0; n3 < this.SYS_N; ++n3) {
                sArray4[n3] = this.gf.gf_inv(sArray4[n3]);
            }
            byte[][] byArray3 = new byte[this.PK_NROWS][this.SYS_N / 8];
            for (n3 = 0; n3 < this.PK_NROWS; ++n3) {
                for (n2 = 0; n2 < this.SYS_N / 8; ++n2) {
                    byArray3[n3][n2] = 0;
                }
            }
            for (n3 = 0; n3 < this.SYS_T; ++n3) {
                for (n2 = 0; n2 < this.SYS_N; n2 += 8) {
                    for (n = 0; n < this.GFBITS; ++n) {
                        byte by = (byte)(sArray4[n2 + 7] >>> n & 1);
                        by = (byte)(by << 1);
                        by = (byte)(by | sArray4[n2 + 6] >>> n & 1);
                        by = (byte)(by << 1);
                        by = (byte)(by | sArray4[n2 + 5] >>> n & 1);
                        by = (byte)(by << 1);
                        by = (byte)(by | sArray4[n2 + 4] >>> n & 1);
                        by = (byte)(by << 1);
                        by = (byte)(by | sArray4[n2 + 3] >>> n & 1);
                        by = (byte)(by << 1);
                        by = (byte)(by | sArray4[n2 + 2] >>> n & 1);
                        by = (byte)(by << 1);
                        by = (byte)(by | sArray4[n2 + 1] >>> n & 1);
                        by = (byte)(by << 1);
                        byArray3[n3 * this.GFBITS + n][n2 / 8] = by = (byte)(by | sArray4[n2 + 0] >>> n & 1);
                    }
                }
                for (n2 = 0; n2 < this.SYS_N; ++n2) {
                    sArray4[n2] = this.gf.gf_mul(sArray4[n2], sArray3[n2]);
                }
            }
            for (int i = 0; i < this.PK_NROWS; ++i) {
                int n7;
                byte by;
                n3 = i >>> 3;
                n2 = i & 7;
                if (this.usePivots && i == this.PK_NROWS - 32 && this.mov_columns(byArray3, sArray, lArray) != 0) {
                    return -1;
                }
                for (n = i + 1; n < this.PK_NROWS; ++n) {
                    by = (byte)(byArray3[i][n3] ^ byArray3[n][n3]);
                    by = (byte)(by >> n2);
                    by = (byte)(by & 1);
                    by = -by;
                    for (n7 = 0; n7 < this.SYS_N / 8; ++n7) {
                        byte[] byArray4 = byArray3[i];
                        int n8 = n7;
                        byArray4[n8] = (byte)(byArray4[n8] ^ byArray3[n][n7] & by);
                    }
                }
                if ((byArray3[i][n3] >> n2 & 1) == 0) {
                    return -1;
                }
                for (n = 0; n < this.PK_NROWS; ++n) {
                    if (n == i) continue;
                    by = (byte)(byArray3[n][n3] >> n2);
                    by = (byte)(by & 1);
                    by = -by;
                    for (n7 = 0; n7 < this.SYS_N / 8; ++n7) {
                        byte[] byArray5 = byArray3[n];
                        int n9 = n7;
                        byArray5[n9] = (byte)(byArray5[n9] ^ byArray3[i][n7] & by);
                    }
                }
            }
            if (byArray == null) break block26;
            if (this.usePadding) {
                int n10 = 0;
                int n11 = this.PK_NROWS % 8;
                if (n11 == 0) {
                    System.arraycopy(byArray3[n3], (this.PK_NROWS - 1) / 8, byArray, n10, this.SYS_N / 8);
                    n10 += this.SYS_N / 8;
                } else {
                    for (n3 = 0; n3 < this.PK_NROWS; ++n3) {
                        for (n2 = (this.PK_NROWS - 1) / 8; n2 < this.SYS_N / 8 - 1; ++n2) {
                            byArray[n10++] = (byte)((byArray3[n3][n2] & 0xFF) >>> n11 | byArray3[n3][n2 + 1] << 8 - n11);
                        }
                        byArray[n10++] = (byte)((byArray3[n3][n2] & 0xFF) >>> n11);
                    }
                }
            } else {
                int n12 = (this.SYS_N - this.PK_NROWS + 7) / 8;
                for (n3 = 0; n3 < this.PK_NROWS; ++n3) {
                    System.arraycopy(byArray3[n3], this.PK_NROWS / 8, byArray, n12 * n3, n12);
                }
            }
        }
        return 0;
    }

    private short eval(short[] sArray, short s) {
        short s2 = sArray[this.SYS_T];
        for (int i = this.SYS_T - 1; i >= 0; --i) {
            s2 = (short)(this.gf.gf_mul(s2, s) ^ sArray[i]);
        }
        return s2;
    }

    private void root(short[] sArray, short[] sArray2, short[] sArray3) {
        for (int i = 0; i < this.SYS_N; ++i) {
            sArray[i] = this.eval(sArray2, sArray3[i]);
        }
    }

    private int generate_irr_poly(short[] sArray) {
        short s;
        short[][] sArray2 = new short[this.SYS_T + 1][this.SYS_T];
        sArray2[0][0] = 1;
        System.arraycopy(sArray, 0, sArray2[1], 0, this.SYS_T);
        int[] nArray = new int[this.SYS_T * 2 - 1];
        for (s = 2; s < this.SYS_T; s += 2) {
            this.gf.gf_sqr_poly(this.SYS_T, this.poly, sArray2[s], sArray2[s >>> 1], nArray);
            this.gf.gf_mul_poly(this.SYS_T, this.poly, sArray2[s + 1], sArray2[s], sArray, nArray);
        }
        if (s == this.SYS_T) {
            this.gf.gf_sqr_poly(this.SYS_T, this.poly, sArray2[s], sArray2[s >>> 1], nArray);
        }
        for (int i = 0; i < this.SYS_T; ++i) {
            int n;
            int n2;
            for (s = i + 1; s < this.SYS_T; ++s) {
                n2 = this.gf.gf_iszero(sArray2[i][i]);
                for (n = i; n < this.SYS_T + 1; ++n) {
                    short[] sArray3 = sArray2[n];
                    int n3 = i;
                    sArray3[n3] = (short)(sArray3[n3] ^ (short)(sArray2[n][s] & n2));
                }
            }
            if (sArray2[i][i] == 0) {
                return -1;
            }
            s = this.gf.gf_inv(sArray2[i][i]);
            for (n2 = i; n2 < this.SYS_T + 1; ++n2) {
                sArray2[n2][i] = this.gf.gf_mul(sArray2[n2][i], s);
            }
            for (n2 = 0; n2 < this.SYS_T; ++n2) {
                if (n2 == i) continue;
                n = sArray2[i][n2];
                for (int j = i; j <= this.SYS_T; ++j) {
                    short[] sArray4 = sArray2[j];
                    int n4 = n2;
                    sArray4[n4] = (short)(sArray4[n4] ^ this.gf.gf_mul(sArray2[j][i], (short)n));
                }
            }
        }
        System.arraycopy(sArray2[this.SYS_T], 0, sArray, 0, this.SYS_T);
        return 0;
    }

    int check_pk_padding(byte[] byArray) {
        int n = 0;
        for (int i = 0; i < this.PK_NROWS; ++i) {
            n = (byte)(n | byArray[i * this.PK_ROW_BYTES + this.PK_ROW_BYTES - 1]);
        }
        n = (byte)((n & 0xFF) >>> this.PK_NCOLS % 8);
        n = (byte)(n - 1);
        int n2 = n = (int)((byte)((n & 0xFF) >>> 7));
        return n2 - 1;
    }

    int check_c_padding(byte[] byArray) {
        byte by = (byte)((byArray[this.SYND_BYTES - 1] & 0xFF) >>> this.PK_NROWS % 8);
        by = (byte)(by - 1);
        byte by2 = by = (byte)((by & 0xFF) >>> 7);
        return by2 - 1;
    }

    public int getDefaultSessionKeySize() {
        return this.defaultKeySize;
    }

    private static void sort32(int[] nArray, int n, int n2) {
        int n3 = n2 - n;
        if (n3 < 2) {
            return;
        }
        for (int i = 1; i < n3 - i; i += i) {
        }
        for (int i = i; i > 0; i >>>= 1) {
            int n4;
            int n5;
            int n6;
            for (n6 = 0; n6 < n3 - i; ++n6) {
                if ((n6 & i) != 0) continue;
                n5 = nArray[n + n6 + i] ^ nArray[n + n6];
                n4 = nArray[n + n6 + i] - nArray[n + n6];
                n4 ^= n5 & (n4 ^ nArray[n + n6 + i]);
                n4 >>= 31;
                int n7 = n + n6;
                nArray[n7] = nArray[n7] ^ (n4 &= n5);
                int n8 = n + n6 + i;
                nArray[n8] = nArray[n8] ^ n4;
            }
            n6 = 0;
            for (int j = i; j > i; j >>>= 1) {
                while (n6 < n3 - j) {
                    if ((n6 & i) == 0) {
                        n5 = nArray[n + n6 + i];
                        for (int k = j; k > i; k >>>= 1) {
                            n4 = nArray[n + n6 + k] ^ n5;
                            int n9 = nArray[n + n6 + k] - n5;
                            n9 ^= n4 & (n9 ^ nArray[n + n6 + k]);
                            n9 >>= 31;
                            n5 ^= (n9 &= n4);
                            int n10 = n + n6 + k;
                            nArray[n10] = nArray[n10] ^ n9;
                        }
                        nArray[n + n6 + i] = n5;
                    }
                    ++n6;
                }
            }
        }
    }

    private static void sort64(long[] lArray, int n, int n2) {
        int n3 = n2 - n;
        if (n3 < 2) {
            return;
        }
        for (int i = 1; i < n3 - i; i += i) {
        }
        for (int i = i; i > 0; i >>>= 1) {
            long l;
            int n4;
            for (n4 = 0; n4 < n3 - i; ++n4) {
                if ((n4 & i) != 0) continue;
                l = lArray[n + n4 + i] - lArray[n + n4];
                l >>>= 63;
                l = -l;
                int n5 = n + n4;
                lArray[n5] = lArray[n5] ^ (l &= lArray[n + n4] ^ lArray[n + n4 + i]);
                int n6 = n + n4 + i;
                lArray[n6] = lArray[n6] ^ l;
            }
            n4 = 0;
            for (int j = i; j > i; j >>>= 1) {
                while (n4 < n3 - j) {
                    if ((n4 & i) == 0) {
                        l = lArray[n + n4 + i];
                        for (int k = j; k > i; k >>>= 1) {
                            long l2 = lArray[n + n4 + k] - l;
                            l2 >>>= 63;
                            l2 = -l2;
                            l ^= (l2 &= l ^ lArray[n + n4 + k]);
                            int n7 = n + n4 + k;
                            lArray[n7] = lArray[n7] ^ l2;
                        }
                        lArray[n + n4 + i] = l;
                    }
                    ++n4;
                }
            }
        }
    }
}

