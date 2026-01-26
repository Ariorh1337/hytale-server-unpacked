/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

class FalconSmallPrime {
    int p;
    int g;
    int s;

    FalconSmallPrime(int n, int n2, int n3) {
        this.p = n;
        this.g = n2;
        this.s = n3;
    }
}

