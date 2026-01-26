/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

class Reduce {
    Reduce() {
    }

    static int montgomeryReduce(long l) {
        int n = (int)(l * 58728449L);
        n = (int)(l - (long)n * 8380417L >>> 32);
        return n;
    }

    static int reduce32(int n) {
        int n2 = n + 0x400000 >> 23;
        n2 = n - n2 * 8380417;
        return n2;
    }

    static int conditionalAddQ(int n) {
        n += n >> 31 & 0x7FE001;
        return n;
    }
}

