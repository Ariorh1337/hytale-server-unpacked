/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb.util;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractComparator;
import org.rocksdb.ComparatorOptions;
import org.rocksdb.util.BytewiseComparator;

public final class ReverseBytewiseComparator
extends AbstractComparator {
    public ReverseBytewiseComparator(ComparatorOptions comparatorOptions) {
        super(comparatorOptions);
    }

    @Override
    public String name() {
        return "rocksdb.java.ReverseBytewiseComparator";
    }

    @Override
    public int compare(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        return -BytewiseComparator._compare(byteBuffer, byteBuffer2);
    }

    @Override
    public void findShortestSeparator(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        int n;
        int n2;
        int n3;
        int n4 = Math.min(byteBuffer.remaining(), byteBuffer2.remaining());
        for (n3 = 0; n3 < n4 && byteBuffer.get(n3) == byteBuffer2.get(n3); ++n3) {
        }
        assert (n3 <= n4);
        if (n3 != n4 && (n2 = byteBuffer.get(n3) & 0xFF) > (n = byteBuffer2.get(n3) & 0xFF) && n3 < byteBuffer.remaining() - 1) {
            byteBuffer.limit(n3 + 1);
            assert (BytewiseComparator._compare(byteBuffer.duplicate(), byteBuffer2.duplicate()) > 0);
        }
    }
}

