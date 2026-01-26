/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

class IndexedHash {
    final int order;
    final byte[] digest;

    IndexedHash(int n, byte[] byArray) {
        this.order = n;
        this.digest = byArray;
    }
}

