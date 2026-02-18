/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb.util;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractComparator;
import org.rocksdb.ComparatorOptions;
import org.rocksdb.util.ByteUtil;

public final class BytewiseComparator
extends AbstractComparator {
    public BytewiseComparator(ComparatorOptions comparatorOptions) {
        super(comparatorOptions);
    }

    @Override
    public String name() {
        return "rocksdb.java.BytewiseComparator";
    }

    @Override
    public int compare(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        return BytewiseComparator._compare(byteBuffer, byteBuffer2);
    }

    static int _compare(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        assert (byteBuffer != null && byteBuffer2 != null);
        int n = byteBuffer.remaining() < byteBuffer2.remaining() ? byteBuffer.remaining() : byteBuffer2.remaining();
        int n2 = ByteUtil.memcmp(byteBuffer, byteBuffer2, n);
        if (n2 == 0) {
            if (byteBuffer.remaining() < byteBuffer2.remaining()) {
                n2 = -1;
            } else if (byteBuffer.remaining() > byteBuffer2.remaining()) {
                n2 = 1;
            }
        }
        return n2;
    }

    @Override
    public void findShortestSeparator(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        int n;
        int n2 = Math.min(byteBuffer.remaining(), byteBuffer2.remaining());
        for (n = 0; n < n2 && byteBuffer.get(n) == byteBuffer2.get(n); ++n) {
        }
        if (n < n2) {
            int n3;
            int n4 = byteBuffer.get(n) & 0xFF;
            if (n4 >= (n3 = byteBuffer2.get(n) & 0xFF)) {
                return;
            }
            assert (n4 < n3);
            if (n < byteBuffer2.remaining() - 1 || n4 + 1 < n3) {
                byteBuffer.put(n, (byte)((byteBuffer.get(n) & 0xFF) + 1));
                byteBuffer.limit(n + 1);
            } else {
                ++n;
                while (n < byteBuffer.remaining()) {
                    if ((byteBuffer.get(n) & 0xFF) < 255) {
                        byteBuffer.put(n, (byte)((byteBuffer.get(n) & 0xFF) + 1));
                        byteBuffer.limit(n + 1);
                        break;
                    }
                    ++n;
                }
            }
            assert (this.compare(byteBuffer.duplicate(), byteBuffer2.duplicate()) < 0);
        }
    }

    @Override
    public void findShortSuccessor(ByteBuffer byteBuffer) {
        int n = byteBuffer.remaining();
        for (int i = 0; i < n; ++i) {
            int n2 = byteBuffer.get(i) & 0xFF;
            if (n2 == 255) continue;
            byteBuffer.put(i, (byte)(n2 + 1));
            byteBuffer.limit(i + 1);
            return;
        }
    }
}

