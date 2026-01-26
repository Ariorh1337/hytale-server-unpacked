/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

final class Objects {
    Objects() {
    }

    static void checkFromIndexSize(int n, int n2, int n3) {
        if ((n3 | n | n2) < 0 || n2 > n3 - n) {
            throw new IndexOutOfBoundsException(String.format("Range [%s, %<s + %s) out of bounds for length %s", n, n2, n3));
        }
    }
}

