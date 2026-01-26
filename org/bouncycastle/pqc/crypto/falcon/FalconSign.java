/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.falcon.FPREngine;
import org.bouncycastle.pqc.crypto.falcon.FalconCommon;
import org.bouncycastle.pqc.crypto.falcon.FalconFFT;
import org.bouncycastle.pqc.crypto.falcon.SamplerCtx;
import org.bouncycastle.pqc.crypto.falcon.SamplerZ;

class FalconSign {
    FalconSign() {
    }

    void smallints_to_fpr(double[] dArray, int n, byte[] byArray, int n2) {
        int n3 = 1 << n2;
        for (int i = 0; i < n3; ++i) {
            dArray[n + i] = byArray[i];
        }
    }

    void ffSampling_fft_dyntree(SamplerCtx samplerCtx, double[] dArray, int n, double[] dArray2, int n2, double[] dArray3, int n3, double[] dArray4, int n4, double[] dArray5, int n5, int n6, int n7, double[] dArray6, int n8) {
        if (n7 == 0) {
            double d = dArray3[n3];
            d = Math.sqrt(d) * FPREngine.fpr_inv_sigma[n6];
            dArray[n] = SamplerZ.sample(samplerCtx, dArray[n], d);
            dArray2[n2] = SamplerZ.sample(samplerCtx, dArray2[n2], d);
            return;
        }
        int n9 = 1 << n7;
        int n10 = n9 >> 1;
        FalconFFT.poly_LDL_fft(dArray3, n3, dArray4, n4, dArray5, n5, n7);
        FalconFFT.poly_split_fft(dArray6, n8, dArray6, n8 + n10, dArray3, n3, n7);
        System.arraycopy(dArray6, n8, dArray3, n3, n9);
        FalconFFT.poly_split_fft(dArray6, n8, dArray6, n8 + n10, dArray5, n5, n7);
        System.arraycopy(dArray6, n8, dArray5, n5, n9);
        System.arraycopy(dArray4, n4, dArray6, n8, n9);
        System.arraycopy(dArray3, n3, dArray4, n4, n10);
        System.arraycopy(dArray5, n5, dArray4, n4 + n10, n10);
        int n11 = n8 + n9;
        FalconFFT.poly_split_fft(dArray6, n11, dArray6, n11 + n10, dArray2, n2, n7);
        this.ffSampling_fft_dyntree(samplerCtx, dArray6, n11, dArray6, n11 + n10, dArray5, n5, dArray5, n5 + n10, dArray4, n4 + n10, n6, n7 - 1, dArray6, n11 + n9);
        FalconFFT.poly_merge_fft(dArray6, n8 + (n9 << 1), dArray6, n11, dArray6, n11 + n10, n7);
        System.arraycopy(dArray2, n2, dArray6, n11, n9);
        FalconFFT.poly_sub(dArray6, n11, dArray6, n8 + (n9 << 1), n7);
        System.arraycopy(dArray6, n8 + (n9 << 1), dArray2, n2, n9);
        FalconFFT.poly_mul_fft(dArray6, n8, dArray6, n11, n7);
        FalconFFT.poly_add(dArray, n, dArray6, n8, n7);
        int n12 = n8;
        FalconFFT.poly_split_fft(dArray6, n12, dArray6, n12 + n10, dArray, n, n7);
        this.ffSampling_fft_dyntree(samplerCtx, dArray6, n12, dArray6, n12 + n10, dArray3, n3, dArray3, n3 + n10, dArray4, n4, n6, n7 - 1, dArray6, n12 + n9);
        FalconFFT.poly_merge_fft(dArray, n, dArray6, n12, dArray6, n12 + n10, n7);
    }

    int do_sign_dyn(SamplerCtx samplerCtx, short[] sArray, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, short[] sArray2, int n, double[] dArray, int n2) {
        int n3;
        int n4 = 1 << n;
        int n5 = n2;
        int n6 = n5 + n4;
        int n7 = n6 + n4;
        int n8 = n7 + n4;
        this.smallints_to_fpr(dArray, n6, byArray, n);
        this.smallints_to_fpr(dArray, n5, byArray2, n);
        this.smallints_to_fpr(dArray, n8, byArray3, n);
        this.smallints_to_fpr(dArray, n7, byArray4, n);
        FalconFFT.FFT(dArray, n6, n);
        FalconFFT.FFT(dArray, n5, n);
        FalconFFT.FFT(dArray, n8, n);
        FalconFFT.FFT(dArray, n7, n);
        FalconFFT.poly_neg(dArray, n6, n);
        FalconFFT.poly_neg(dArray, n8, n);
        int n9 = n8 + n4;
        int n10 = n9 + n4;
        System.arraycopy(dArray, n6, dArray, n9, n4);
        FalconFFT.poly_mulselfadj_fft(dArray, n9, n);
        System.arraycopy(dArray, n5, dArray, n10, n4);
        FalconFFT.poly_muladj_fft(dArray, n10, dArray, n7, n);
        FalconFFT.poly_mulselfadj_fft(dArray, n5, n);
        FalconFFT.poly_add(dArray, n5, dArray, n9, n);
        System.arraycopy(dArray, n6, dArray, n9, n4);
        FalconFFT.poly_muladj_fft(dArray, n6, dArray, n8, n);
        FalconFFT.poly_add(dArray, n6, dArray, n10, n);
        FalconFFT.poly_mulselfadj_fft(dArray, n7, n);
        System.arraycopy(dArray, n8, dArray, n10, n4);
        FalconFFT.poly_mulselfadj_fft(dArray, n10, n);
        FalconFFT.poly_add(dArray, n7, dArray, n10, n);
        int n11 = n5;
        int n12 = n6;
        int n13 = n7;
        n6 = n9;
        n9 = n6 + n4;
        n10 = n9 + n4;
        for (n3 = 0; n3 < n4; ++n3) {
            dArray[n9 + n3] = sArray2[n3];
        }
        FalconFFT.FFT(dArray, n9, n);
        double d = 8.137358613394092E-5;
        System.arraycopy(dArray, n9, dArray, n10, n4);
        FalconFFT.poly_mul_fft(dArray, n10, dArray, n6, n);
        FalconFFT.poly_mulconst(dArray, n10, -d, n);
        FalconFFT.poly_mul_fft(dArray, n9, dArray, n8, n);
        FalconFFT.poly_mulconst(dArray, n9, d, n);
        System.arraycopy(dArray, n9, dArray, n8, 2 * n4);
        n9 = n13 + n4;
        n10 = n9 + n4;
        this.ffSampling_fft_dyntree(samplerCtx, dArray, n9, dArray, n10, dArray, n11, dArray, n12, dArray, n13, n, n, dArray, n10 + n4);
        n6 = n5 + n4;
        n7 = n6 + n4;
        n8 = n7 + n4;
        System.arraycopy(dArray, n9, dArray, n8 + n4, n4 * 2);
        n9 = n8 + n4;
        n10 = n9 + n4;
        this.smallints_to_fpr(dArray, n6, byArray, n);
        this.smallints_to_fpr(dArray, n5, byArray2, n);
        this.smallints_to_fpr(dArray, n8, byArray3, n);
        this.smallints_to_fpr(dArray, n7, byArray4, n);
        FalconFFT.FFT(dArray, n6, n);
        FalconFFT.FFT(dArray, n5, n);
        FalconFFT.FFT(dArray, n8, n);
        FalconFFT.FFT(dArray, n7, n);
        FalconFFT.poly_neg(dArray, n6, n);
        FalconFFT.poly_neg(dArray, n8, n);
        int n14 = n10 + n4;
        int n15 = n14 + n4;
        System.arraycopy(dArray, n9, dArray, n14, n4);
        System.arraycopy(dArray, n10, dArray, n15, n4);
        FalconFFT.poly_mul_fft(dArray, n14, dArray, n5, n);
        FalconFFT.poly_mul_fft(dArray, n15, dArray, n7, n);
        FalconFFT.poly_add(dArray, n14, dArray, n15, n);
        System.arraycopy(dArray, n9, dArray, n15, n4);
        FalconFFT.poly_mul_fft(dArray, n15, dArray, n6, n);
        System.arraycopy(dArray, n14, dArray, n9, n4);
        FalconFFT.poly_mul_fft(dArray, n10, dArray, n8, n);
        FalconFFT.poly_add(dArray, n10, dArray, n15, n);
        FalconFFT.iFFT(dArray, n9, n);
        FalconFFT.iFFT(dArray, n10, n);
        int n16 = 0;
        int n17 = 0;
        for (n3 = 0; n3 < n4; ++n3) {
            int n18 = (sArray2[n3] & 0xFFFF) - (int)FPREngine.fpr_rint(dArray[n9 + n3]);
            n17 |= (n16 += n18 * n18);
        }
        n16 |= -(n17 >>> 31);
        short[] sArray3 = new short[n4];
        for (n3 = 0; n3 < n4; ++n3) {
            sArray3[n3] = (short)(-FPREngine.fpr_rint(dArray[n10 + n3]));
        }
        if (FalconCommon.is_short_half(n16, sArray3, n) != 0) {
            System.arraycopy(sArray3, 0, sArray, 0, n4);
            return 1;
        }
        return 0;
    }

    void sign_dyn(short[] sArray, SHAKEDigest sHAKEDigest, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, short[] sArray2, int n, double[] dArray) {
        SamplerCtx samplerCtx;
        int n2 = 0;
        do {
            samplerCtx = new SamplerCtx();
            samplerCtx.sigma_min = FPREngine.fpr_sigma_min[n];
            samplerCtx.p.prng_init(sHAKEDigest);
        } while (this.do_sign_dyn(samplerCtx, sArray, byArray, byArray2, byArray3, byArray4, sArray2, n, dArray, n2) == 0);
    }
}

