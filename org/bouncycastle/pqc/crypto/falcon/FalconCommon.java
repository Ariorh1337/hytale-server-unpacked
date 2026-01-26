/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.crypto.digests.SHAKEDigest;

class FalconCommon {
    static final int[] l2bound = new int[]{0, 101498, 208714, 428865, 892039, 1852696, 3842630, 7959734, 16468416, 34034726, 70265242};

    FalconCommon() {
    }

    static void hash_to_point_vartime(SHAKEDigest sHAKEDigest, short[] sArray, int n) {
        int n2 = 0;
        int n3 = 1 << n;
        byte[] byArray = new byte[2];
        while (n3 > 0) {
            sHAKEDigest.doOutput(byArray, 0, 2);
            int n4 = (byArray[0] & 0xFF) << 8 | byArray[1] & 0xFF;
            if (n4 >= 61445) continue;
            sArray[n2++] = (short)(n4 %= 12289);
            --n3;
        }
    }

    static int is_short(short[] sArray, int n, short[] sArray2, int n2) {
        int n3 = 1 << n2;
        int n4 = 0;
        int n5 = 0;
        for (int i = 0; i < n3; ++i) {
            short s = sArray[n + i];
            n5 |= (n4 += s * s);
            s = sArray2[i];
            n5 |= (n4 += s * s);
        }
        return ((long)(n4 |= -(n5 >>> 31)) & 0xFFFFFFFFL) <= (long)l2bound[n2] ? 1 : 0;
    }

    static int is_short_half(int n, short[] sArray, int n2) {
        int n3 = 1 << n2;
        int n4 = -(n >>> 31);
        for (int i = 0; i < n3; ++i) {
            short s = sArray[i];
            n4 |= (n += s * s);
        }
        return ((long)(n |= -(n4 >>> 31)) & 0xFFFFFFFFL) <= (long)l2bound[n2] ? 1 : 0;
    }
}

