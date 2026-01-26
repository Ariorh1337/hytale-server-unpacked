/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.pqc.crypto.falcon.FPREngine;
import org.bouncycastle.pqc.crypto.falcon.FalconRNG;
import org.bouncycastle.pqc.crypto.falcon.SamplerCtx;

class SamplerZ {
    SamplerZ() {
    }

    static int sample(SamplerCtx samplerCtx, double d, double d2) {
        return SamplerZ.sampler(samplerCtx, d, d2);
    }

    static int gaussian0_sampler(FalconRNG falconRNG) {
        int[] nArray = new int[]{10745844, 3068844, 3741698, 5559083, 1580863, 8248194, 2260429, 13669192, 2736639, 708981, 4421575, 10046180, 169348, 7122675, 4136815, 30538, 13063405, 7650655, 4132, 14505003, 7826148, 417, 16768101, 11363290, 31, 8444042, 8086568, 1, 12844466, 265321, 0, 1232676, 13644283, 0, 38047, 9111839, 0, 870, 6138264, 0, 14, 12545723, 0, 0, 3104126, 0, 0, 28824, 0, 0, 198, 0, 0, 1};
        long l = falconRNG.prng_get_u64();
        int n = falconRNG.prng_get_u8() & 0xFF;
        int n2 = (int)l & 0xFFFFFF;
        int n3 = (int)(l >>> 24) & 0xFFFFFF;
        int n4 = (int)(l >>> 48) | n << 16;
        int n5 = 0;
        for (int i = 0; i < nArray.length; i += 3) {
            int n6 = nArray[i + 2];
            int n7 = nArray[i + 1];
            int n8 = nArray[i];
            int n9 = n2 - n6 >>> 31;
            n9 = n3 - n7 - n9 >>> 31;
            n9 = n4 - n8 - n9 >>> 31;
            n5 += n9;
        }
        return n5;
    }

    private static int BerExp(FalconRNG falconRNG, double d, double d2) {
        int n;
        int n2 = (int)(d * 1.4426950408889634);
        double d3 = d - (double)n2 * 0.6931471805599453;
        int n3 = n2;
        n3 ^= (n3 ^ 0x3F) & -(63 - n3 >>> 31);
        n2 = n3;
        long l = (FPREngine.fpr_expm_p63(d3, d2) << 1) - 1L >>> n2;
        int n4 = 64;
        while ((n = (falconRNG.prng_get_u8() & 0xFF) - ((int)(l >>> (n4 -= 8)) & 0xFF)) == 0 && n4 > 0) {
        }
        return n >>> 31;
    }

    private static int sampler(SamplerCtx samplerCtx, double d, double d2) {
        int n;
        int n2;
        SamplerCtx samplerCtx2 = samplerCtx;
        int n3 = (int)FPREngine.fpr_floor(d);
        double d3 = d - (double)n3;
        double d4 = d2 * d2 * 0.5;
        double d5 = d2 * samplerCtx2.sigma_min;
        do {
            n2 = SamplerZ.gaussian0_sampler(samplerCtx2.p);
            int n4 = samplerCtx2.p.prng_get_u8() & 0xFF & 1;
            n = n4 + ((n4 << 1) - 1) * n2;
            double d6 = (double)n - d3;
            d6 = d6 * d6 * d4;
        } while (SamplerZ.BerExp(samplerCtx2.p, d6 -= (double)(n2 * n2) * 0.15086504887537272, d5) == 0);
        return n3 + n;
    }
}

