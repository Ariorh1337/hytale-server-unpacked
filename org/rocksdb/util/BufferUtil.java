/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb.util;

public class BufferUtil {
    public static void CheckBounds(int n, int n2, int n3) {
        if ((n | n2 | n + n2 | n3 - (n + n2)) < 0) {
            throw new IndexOutOfBoundsException(String.format("offset(%d), len(%d), size(%d)", n, n2, n3));
        }
    }
}

