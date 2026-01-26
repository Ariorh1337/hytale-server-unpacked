/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

class IndexedDigest {
    final long idx_tree;
    final int idx_leaf;
    final byte[] digest;

    IndexedDigest(long l, int n, byte[] byArray) {
        this.idx_tree = l;
        this.idx_leaf = n;
        this.digest = byArray;
    }
}

