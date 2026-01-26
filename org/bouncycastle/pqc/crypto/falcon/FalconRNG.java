/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Pack;

class FalconRNG {
    byte[] bd = new byte[512];
    int ptr = 0;
    byte[] sd = new byte[256];

    FalconRNG() {
    }

    void prng_init(SHAKEDigest sHAKEDigest) {
        sHAKEDigest.doOutput(this.sd, 0, 56);
        this.prng_refill();
    }

    void prng_refill() {
        int[] nArray = new int[]{1634760805, 857760878, 2036477234, 1797285236};
        long l = Pack.littleEndianToLong(this.sd, 48);
        int[] nArray2 = new int[16];
        for (int i = 0; i < 8; ++i) {
            int n;
            System.arraycopy(nArray, 0, nArray2, 0, nArray.length);
            Pack.littleEndianToInt(this.sd, 0, nArray2, 4, 12);
            nArray2[14] = nArray2[14] ^ (int)l;
            nArray2[15] = nArray2[15] ^ (int)(l >>> 32);
            for (int j = 0; j < 10; ++j) {
                this.QROUND(0, 4, 8, 12, nArray2);
                this.QROUND(1, 5, 9, 13, nArray2);
                this.QROUND(2, 6, 10, 14, nArray2);
                this.QROUND(3, 7, 11, 15, nArray2);
                this.QROUND(0, 5, 10, 15, nArray2);
                this.QROUND(1, 6, 11, 12, nArray2);
                this.QROUND(2, 7, 8, 13, nArray2);
                this.QROUND(3, 4, 9, 14, nArray2);
            }
            for (n = 0; n < 4; ++n) {
                int n2 = n;
                nArray2[n2] = nArray2[n2] + nArray[n];
            }
            for (n = 4; n < 14; ++n) {
                int n3 = n;
                nArray2[n3] = nArray2[n3] + Pack.littleEndianToInt(this.sd, 4 * n - 16);
            }
            nArray2[14] = nArray2[14] + (Pack.littleEndianToInt(this.sd, 40) ^ (int)l);
            nArray2[15] = nArray2[15] + (Pack.littleEndianToInt(this.sd, 44) ^ (int)(l >>> 32));
            ++l;
            for (n = 0; n < 16; ++n) {
                Pack.intToLittleEndian(nArray2[n], this.bd, (i << 2) + (n << 5));
            }
        }
        Pack.longToLittleEndian(l, this.sd, 48);
        this.ptr = 0;
    }

    private void QROUND(int n, int n2, int n3, int n4, int[] nArray) {
        int n5 = n;
        nArray[n5] = nArray[n5] + nArray[n2];
        int n6 = n4;
        nArray[n6] = nArray[n6] ^ nArray[n];
        nArray[n4] = nArray[n4] << 16 | nArray[n4] >>> 16;
        int n7 = n3;
        nArray[n7] = nArray[n7] + nArray[n4];
        int n8 = n2;
        nArray[n8] = nArray[n8] ^ nArray[n3];
        nArray[n2] = nArray[n2] << 12 | nArray[n2] >>> 20;
        int n9 = n;
        nArray[n9] = nArray[n9] + nArray[n2];
        int n10 = n4;
        nArray[n10] = nArray[n10] ^ nArray[n];
        nArray[n4] = nArray[n4] << 8 | nArray[n4] >>> 24;
        int n11 = n3;
        nArray[n11] = nArray[n11] + nArray[n4];
        int n12 = n2;
        nArray[n12] = nArray[n12] ^ nArray[n3];
        nArray[n2] = nArray[n2] << 7 | nArray[n2] >>> 25;
    }

    long prng_get_u64() {
        int n = this.ptr;
        if (n >= this.bd.length - 9) {
            this.prng_refill();
            n = 0;
        }
        this.ptr = n + 8;
        return Pack.littleEndianToLong(this.bd, n);
    }

    byte prng_get_u8() {
        byte by = this.bd[this.ptr++];
        if (this.ptr == this.bd.length) {
            this.prng_refill();
        }
        return by;
    }
}

