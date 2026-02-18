/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb.util;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractComparator;
import org.rocksdb.ComparatorOptions;

public final class IntComparator
extends AbstractComparator {
    public IntComparator(ComparatorOptions comparatorOptions) {
        super(comparatorOptions);
    }

    @Override
    public String name() {
        return "rocksdb.java.IntComparator";
    }

    @Override
    public int compare(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        return this.compareIntKeys(byteBuffer, byteBuffer2);
    }

    private int compareIntKeys(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        int n;
        int n2 = byteBuffer.getInt();
        long l = (long)n2 - (long)(n = byteBuffer2.getInt());
        int n3 = l < Integer.MIN_VALUE ? Integer.MIN_VALUE : (l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l);
        return n3;
    }
}

