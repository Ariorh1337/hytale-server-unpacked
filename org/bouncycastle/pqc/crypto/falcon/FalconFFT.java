/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.pqc.crypto.falcon.FPREngine;

class FalconFFT {
    FalconFFT() {
    }

    static void FFT(double[] dArray, int n, int n2) {
        int n3;
        int n4 = 1 << n2;
        int n5 = n3 = n4 >> 1;
        int n6 = 1;
        int n7 = 2;
        while (n6 < n2) {
            int n8 = n5 >> 1;
            int n9 = n7 >> 1;
            int n10 = 0;
            int n11 = 0;
            while (n10 < n9) {
                int n12 = n11 + n8 + n;
                int n13 = n7 + n10 << 1;
                double d = FPREngine.fpr_gm_tab[n13];
                double d2 = FPREngine.fpr_gm_tab[n13 + 1];
                n13 = n + n11;
                int n14 = n13 + n3;
                int n15 = n13 + n8;
                int n16 = n15 + n3;
                while (n13 < n12) {
                    double d3 = dArray[n13];
                    double d4 = dArray[n14];
                    double d5 = dArray[n15];
                    double d6 = dArray[n16];
                    double d7 = d5 * d - d6 * d2;
                    double d8 = d5 * d2 + d6 * d;
                    dArray[n13] = d3 + d7;
                    dArray[n14] = d4 + d8;
                    dArray[n15] = d3 - d7;
                    dArray[n16] = d4 - d8;
                    ++n13;
                    ++n14;
                    ++n15;
                    ++n16;
                }
                ++n10;
                n11 += n5;
            }
            n5 = n8;
            ++n6;
            n7 <<= 1;
        }
    }

    static void iFFT(double[] dArray, int n, int n2) {
        int n3;
        int n4 = 1 << n2;
        int n5 = 1;
        int n6 = n4;
        int n7 = n4 >> 1;
        for (n3 = n2; n3 > 1; --n3) {
            int n8 = n6 >> 1;
            int n9 = n5 << 1;
            int n10 = 0;
            for (int i = 0; i < n7; i += n9) {
                int n11 = i + n5 + n;
                int n12 = n8 + n10 << 1;
                double d = FPREngine.fpr_gm_tab[n12];
                double d2 = -FPREngine.fpr_gm_tab[n12 + 1];
                n12 = n + i;
                int n13 = n12 + n7;
                int n14 = n12 + n5;
                int n15 = n14 + n7;
                while (n12 < n11) {
                    double d3 = dArray[n12];
                    double d4 = dArray[n13];
                    double d5 = dArray[n14];
                    double d6 = dArray[n15];
                    dArray[n12] = d3 + d5;
                    dArray[n13] = d4 + d6;
                    dArray[n14] = (d3 -= d5) * d - (d4 -= d6) * d2;
                    dArray[n15] = d3 * d2 + d4 * d;
                    ++n12;
                    ++n13;
                    ++n14;
                    ++n15;
                }
                ++n10;
            }
            n5 = n9;
            n6 = n8;
        }
        if (n2 > 0) {
            double d = FPREngine.fpr_p2_tab[n2];
            for (n3 = 0; n3 < n4; ++n3) {
                dArray[n + n3] = dArray[n + n3] * d;
            }
        }
    }

    static void poly_add(double[] dArray, int n, double[] dArray2, int n2, int n3) {
        int n4 = 1 << n3;
        for (int i = 0; i < n4; ++i) {
            int n5 = n + i;
            dArray[n5] = dArray[n5] + dArray2[n2 + i];
        }
    }

    static void poly_sub(double[] dArray, int n, double[] dArray2, int n2, int n3) {
        int n4 = 1 << n3;
        for (int i = 0; i < n4; ++i) {
            int n5 = n + i;
            dArray[n5] = dArray[n5] - dArray2[n2 + i];
        }
    }

    static void poly_neg(double[] dArray, int n, int n2) {
        int n3 = 1 << n2;
        for (int i = 0; i < n3; ++i) {
            dArray[n + i] = -dArray[n + i];
        }
    }

    static void poly_adj_fft(double[] dArray, int n, int n2) {
        int n3 = 1 << n2;
        for (int i = n3 >> 1; i < n3; ++i) {
            dArray[n + i] = -dArray[n + i];
        }
    }

    static void poly_mul_fft(double[] dArray, int n, double[] dArray2, int n2, int n3) {
        int n4 = 1 << n3;
        int n5 = n4 >> 1;
        int n6 = 0;
        int n7 = n;
        int n8 = n + n5;
        int n9 = n2;
        while (n6 < n5) {
            double d = dArray[n7];
            double d2 = dArray[n8];
            double d3 = dArray2[n9];
            double d4 = dArray2[n9 + n5];
            dArray[n7] = d * d3 - d2 * d4;
            dArray[n8] = d * d4 + d2 * d3;
            ++n6;
            ++n7;
            ++n9;
            ++n8;
        }
    }

    static void poly_muladj_fft(double[] dArray, int n, double[] dArray2, int n2, int n3) {
        int n4 = 1 << n3;
        int n5 = n4 >> 1;
        int n6 = 0;
        int n7 = n;
        while (n6 < n5) {
            double d = dArray[n7];
            double d2 = dArray[n7 + n5];
            double d3 = dArray2[n2 + n6];
            double d4 = dArray2[n2 + n6 + n5];
            dArray[n7] = d * d3 + d2 * d4;
            dArray[n7 + n5] = d2 * d3 - d * d4;
            ++n6;
            ++n7;
        }
    }

    static void poly_mulselfadj_fft(double[] dArray, int n, int n2) {
        int n3 = 1 << n2;
        int n4 = n3 >> 1;
        for (int i = 0; i < n4; ++i) {
            double d = dArray[n + i];
            double d2 = dArray[n + i + n4];
            dArray[n + i] = d * d + d2 * d2;
            dArray[n + i + n4] = 0.0;
        }
    }

    static void poly_mulconst(double[] dArray, int n, double d, int n2) {
        int n3 = 1 << n2;
        for (int i = 0; i < n3; ++i) {
            dArray[n + i] = dArray[n + i] * d;
        }
    }

    static void poly_invnorm2_fft(double[] dArray, int n, double[] dArray2, int n2, double[] dArray3, int n3, int n4) {
        int n5 = 1 << n4;
        int n6 = n5 >> 1;
        for (int i = 0; i < n6; ++i) {
            double d = dArray2[n2 + i];
            double d2 = dArray2[n2 + i + n6];
            double d3 = dArray3[n3 + i];
            double d4 = dArray3[n3 + i + n6];
            dArray[n + i] = 1.0 / (d * d + d2 * d2 + d3 * d3 + d4 * d4);
        }
    }

    static void poly_add_muladj_fft(double[] dArray, double[] dArray2, double[] dArray3, double[] dArray4, double[] dArray5, int n) {
        int n2 = 1 << n;
        int n3 = n2 >> 1;
        for (int i = 0; i < n3; ++i) {
            int n4 = i + n3;
            double d = dArray2[i];
            double d2 = dArray2[n4];
            double d3 = dArray3[i];
            double d4 = dArray3[n4];
            double d5 = dArray4[i];
            double d6 = dArray4[n4];
            double d7 = dArray5[i];
            double d8 = dArray5[n4];
            double d9 = d * d5 + d2 * d6;
            double d10 = d2 * d5 - d * d6;
            double d11 = d3 * d7 + d4 * d8;
            double d12 = d4 * d7 - d3 * d8;
            dArray[i] = d9 + d11;
            dArray[n4] = d10 + d12;
        }
    }

    static void poly_mul_autoadj_fft(double[] dArray, int n, double[] dArray2, int n2, int n3) {
        int n4 = 1 << n3;
        int n5 = n4 >> 1;
        for (int i = 0; i < n5; ++i) {
            int n6 = n + i;
            dArray[n6] = dArray[n6] * dArray2[n2 + i];
            int n7 = n + i + n5;
            dArray[n7] = dArray[n7] * dArray2[n2 + i];
        }
    }

    static void poly_div_autoadj_fft(double[] dArray, int n, double[] dArray2, int n2, int n3) {
        int n4 = 1 << n3;
        int n5 = n4 >> 1;
        for (int i = 0; i < n5; ++i) {
            double d = 1.0 / dArray2[n2 + i];
            int n6 = n + i;
            dArray[n6] = dArray[n6] * d;
            int n7 = n + i + n5;
            dArray[n7] = dArray[n7] * d;
        }
    }

    static void poly_LDL_fft(double[] dArray, int n, double[] dArray2, int n2, double[] dArray3, int n3, int n4) {
        int n5 = 1 << n4;
        int n6 = n5 >> 1;
        int n7 = 0;
        int n8 = n6;
        int n9 = n2;
        int n10 = n2 + n6;
        while (n7 < n6) {
            double d = dArray[n + n7];
            double d2 = dArray[n + n8];
            double d3 = dArray2[n9];
            double d4 = dArray2[n10];
            double d5 = 1.0 / (d * d + d2 * d2);
            double d6 = d * d5;
            d = d3 * d6 - d4 * (d5 *= -d2);
            d2 = d3 * d5 + d4 * d6;
            d6 = d3;
            d5 = d4;
            d3 = d * d6 + d2 * d5;
            d4 = d * -d5 + d2 * d6;
            int n11 = n3 + n7;
            dArray3[n11] = dArray3[n11] - d3;
            int n12 = n3 + n8;
            dArray3[n12] = dArray3[n12] - d4;
            dArray2[n9] = d;
            dArray2[n10] = -d2;
            ++n7;
            ++n8;
            ++n9;
            ++n10;
        }
    }

    static void poly_split_fft(double[] dArray, int n, double[] dArray2, int n2, double[] dArray3, int n3, int n4) {
        int n5 = 1 << n4;
        int n6 = n5 >> 1;
        int n7 = n6 >> 1;
        dArray[n] = dArray3[n3];
        dArray2[n2] = dArray3[n3 + n6];
        for (int i = 0; i < n7; ++i) {
            int n8 = n3 + (i << 1);
            double d = dArray3[n8];
            double d2 = dArray3[n8++ + n6];
            double d3 = dArray3[n8];
            double d4 = dArray3[n8 + n6];
            dArray[n + i] = (d + d3) * 0.5;
            dArray[n + i + n7] = (d2 + d4) * 0.5;
            double d5 = d - d3;
            double d6 = d2 - d4;
            n8 = i + n6 << 1;
            d3 = FPREngine.fpr_gm_tab[n8];
            d4 = -FPREngine.fpr_gm_tab[n8 + 1];
            n8 = n2 + i;
            dArray2[n8] = (d5 * d3 - d6 * d4) * 0.5;
            dArray2[n8 + n7] = (d5 * d4 + d6 * d3) * 0.5;
        }
    }

    static void poly_merge_fft(double[] dArray, int n, double[] dArray2, int n2, double[] dArray3, int n3, int n4) {
        int n5 = 1 << n4;
        int n6 = n5 >> 1;
        int n7 = n6 >> 1;
        dArray[n] = dArray2[n2];
        dArray[n + n6] = dArray3[n3];
        for (int i = 0; i < n7; ++i) {
            int n8 = n3 + i;
            double d = dArray3[n8];
            double d2 = dArray3[n8 + n7];
            n8 = i + n6 << 1;
            double d3 = FPREngine.fpr_gm_tab[n8];
            double d4 = FPREngine.fpr_gm_tab[n8 + 1];
            double d5 = d * d3 - d2 * d4;
            double d6 = d * d4 + d2 * d3;
            n8 = n2 + i;
            d = dArray2[n8];
            d2 = dArray2[n8 + n7];
            n8 = n + (i << 1);
            dArray[n8] = d + d5;
            dArray[n8++ + n6] = d2 + d6;
            dArray[n8] = d - d5;
            dArray[n8 + n6] = d2 - d6;
        }
    }
}

