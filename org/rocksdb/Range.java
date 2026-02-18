/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Slice;

public class Range {
    final Slice start;
    final Slice limit;

    public Range(Slice slice, Slice slice2) {
        this.start = slice;
        this.limit = slice2;
    }
}

