/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc8032;

abstract class Wnaf {
    Wnaf() {
    }

    static void getSignedVar(int[] nArray, int n, byte[] byArray) {
        int n2;
        int[] nArray2 = new int[nArray.length * 2];
        int n3 = nArray[nArray.length - 1] >> 31;
        int n4 = nArray.length;
        int n5 = nArray2.length;
        while (--n4 >= 0) {
            n2 = nArray[n4];
            nArray2[--n5] = n2 >>> 16 | n3 << 16;
            nArray2[--n5] = n3 = n2;
        }
        n3 = 32 - n;
        n4 = 0;
        n5 = 0;
        n2 = 0;
        while (n2 < nArray2.length) {
            int n6 = nArray2[n2];
            while (n4 < 16) {
                int n7 = n6 >>> n4;
                int n8 = n7 & 1;
                if (n8 == n5) {
                    ++n4;
                    continue;
                }
                int n9 = (n7 | 1) << n3;
                n5 = n9 >>> 31;
                byArray[(n2 << 4) + n4] = (byte)(n9 >> n3);
                n4 += n;
            }
            ++n2;
            n4 -= 16;
        }
    }
}

