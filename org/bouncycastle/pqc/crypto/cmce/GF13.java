/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.pqc.crypto.cmce.GF;

final class GF13
extends GF {
    GF13() {
    }

    @Override
    protected void gf_mul_poly(int n, int[] nArray, short[] sArray, short[] sArray2, short[] sArray3, int[] nArray2) {
        short s;
        short s2;
        int n2;
        nArray2[0] = this.gf_mul_ext(sArray2[0], sArray3[0]);
        for (n2 = 1; n2 < n; ++n2) {
            nArray2[n2 + n2 - 1] = 0;
            s2 = sArray2[n2];
            s = sArray3[n2];
            for (int i = 0; i < n2; ++i) {
                int n3 = n2 + i;
                nArray2[n3] = nArray2[n3] ^ this.gf_mul_ext_par(s2, sArray3[i], sArray2[i], s);
            }
            nArray2[n2 + n2] = this.gf_mul_ext(s2, s);
        }
        for (n2 = (n - 1) * 2; n2 >= n; --n2) {
            s2 = nArray2[n2];
            for (s = 0; s < nArray.length; ++s) {
                int n4 = n2 - n + nArray[s];
                nArray2[n4] = nArray2[n4] ^ s2;
            }
        }
        for (n2 = 0; n2 < n; ++n2) {
            sArray[n2] = this.gf_reduce(nArray2[n2]);
        }
    }

    @Override
    protected void gf_sqr_poly(int n, int[] nArray, short[] sArray, short[] sArray2, int[] nArray2) {
        int n2;
        nArray2[0] = this.gf_sq_ext(sArray2[0]);
        for (n2 = 1; n2 < n; ++n2) {
            nArray2[n2 + n2 - 1] = 0;
            nArray2[n2 + n2] = this.gf_sq_ext(sArray2[n2]);
        }
        for (n2 = (n - 1) * 2; n2 >= n; --n2) {
            int n3 = nArray2[n2];
            for (int i = 0; i < nArray.length; ++i) {
                int n4 = n2 - n + nArray[i];
                nArray2[n4] = nArray2[n4] ^ n3;
            }
        }
        for (n2 = 0; n2 < n; ++n2) {
            sArray[n2] = this.gf_reduce(nArray2[n2]);
        }
    }

    @Override
    protected short gf_frac(short s, short s2) {
        short s3 = this.gf_sqmul(s, s);
        short s4 = this.gf_sq2mul(s3, s3);
        short s5 = this.gf_sq2(s4);
        s5 = this.gf_sq2mul(s5, s4);
        s5 = this.gf_sq2(s5);
        s5 = this.gf_sq2mul(s5, s4);
        return this.gf_sqmul(s5, s2);
    }

    @Override
    protected short gf_inv(short s) {
        return this.gf_frac(s, (short)1);
    }

    @Override
    protected short gf_mul(short s, short s2) {
        short s3 = s;
        short s4 = s2;
        int n = s3 * (s4 & 1);
        for (int i = 1; i < 13; ++i) {
            n ^= s3 * (s4 & 1 << i);
        }
        return this.gf_reduce(n);
    }

    @Override
    protected int gf_mul_ext(short s, short s2) {
        short s3 = s;
        short s4 = s2;
        int n = s3 * (s4 & 1);
        for (int i = 1; i < 13; ++i) {
            n ^= s3 * (s4 & 1 << i);
        }
        return n;
    }

    private int gf_mul_ext_par(short s, short s2, short s3, short s4) {
        short s5 = s;
        short s6 = s2;
        short s7 = s3;
        short s8 = s4;
        int n = s5 * (s6 & 1);
        int n2 = s7 * (s8 & 1);
        for (int i = 1; i < 13; ++i) {
            n ^= s5 * (s6 & 1 << i);
            n2 ^= s7 * (s8 & 1 << i);
        }
        return n ^ n2;
    }

    @Override
    protected short gf_reduce(int n) {
        int n2 = n & 0x1FFF;
        int n3 = n >>> 13;
        int n4 = n3 << 4 ^ n3 << 3 ^ n3 << 1;
        int n5 = n4 >>> 13;
        int n6 = n4 & 0x1FFF;
        int n7 = n5 << 4 ^ n5 << 3 ^ n5 << 1;
        return (short)(n2 ^ n3 ^ n5 ^ n6 ^ n7);
    }

    @Override
    protected short gf_sq(short s) {
        int n = Interleave.expand16to32(s);
        return this.gf_reduce(n);
    }

    @Override
    protected int gf_sq_ext(short s) {
        return Interleave.expand16to32(s);
    }

    private short gf_sq2(short s) {
        int n = Interleave.expand16to32(s);
        s = this.gf_reduce(n);
        int n2 = Interleave.expand16to32(s);
        return this.gf_reduce(n2);
    }

    private short gf_sqmul(short s, short s2) {
        long l = s;
        long l2 = s2;
        long l3 = (l2 << 6) * (l & 0x40L);
        l ^= l << 7;
        l3 ^= (l2 << 0) * (l & 0x4001L);
        l3 ^= (l2 << 1) * (l & 0x8002L);
        l3 ^= (l2 << 2) * (l & 0x10004L);
        l3 ^= (l2 << 3) * (l & 0x20008L);
        l3 ^= (l2 << 4) * (l & 0x40010L);
        long l4 = (l3 ^= (l2 << 5) * (l & 0x80020L)) & 0x1FFC000000L;
        return this.gf_reduce((int)(l3 ^= l4 >>> 18 ^ l4 >>> 20 ^ l4 >>> 24 ^ l4 >>> 26) & 0x3FFFFFF);
    }

    private short gf_sq2mul(short s, short s2) {
        long l = s;
        long l2 = s2;
        long l3 = (l2 << 18) * (l & 0x40L);
        l ^= l << 21;
        l3 ^= (l2 << 0) * (l & 0x10000001L);
        l3 ^= (l2 << 3) * (l & 0x20000002L);
        l3 ^= (l2 << 6) * (l & 0x40000004L);
        l3 ^= (l2 << 9) * (l & 0x80000008L);
        l3 ^= (l2 << 12) * (l & 0x100000010L);
        long l4 = (l3 ^= (l2 << 15) * (l & 0x200000020L)) & 0x1FFFF80000000000L;
        l3 ^= l4 >>> 18 ^ l4 >>> 20 ^ l4 >>> 24 ^ l4 >>> 26;
        l4 = l3 & 0x7FFFC000000L;
        return this.gf_reduce((int)(l3 ^= l4 >>> 18 ^ l4 >>> 20 ^ l4 >>> 24 ^ l4 >>> 26) & 0x3FFFFFF);
    }
}

