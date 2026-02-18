/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractComparator;

class AbstractComparatorJniBridge {
    AbstractComparatorJniBridge() {
    }

    private static int compareInternal(AbstractComparator abstractComparator, ByteBuffer byteBuffer, int n, ByteBuffer byteBuffer2, int n2) {
        if (n != -1) {
            byteBuffer.mark();
            byteBuffer.limit(n);
        }
        if (n2 != -1) {
            byteBuffer2.mark();
            byteBuffer2.limit(n2);
        }
        int n3 = abstractComparator.compare(byteBuffer, byteBuffer2);
        if (n != -1) {
            byteBuffer.reset();
        }
        if (n2 != -1) {
            byteBuffer2.reset();
        }
        return n3;
    }

    private static int findShortestSeparatorInternal(AbstractComparator abstractComparator, ByteBuffer byteBuffer, int n, ByteBuffer byteBuffer2, int n2) {
        if (n != -1) {
            byteBuffer.limit(n);
        }
        if (n2 != -1) {
            byteBuffer2.limit(n2);
        }
        abstractComparator.findShortestSeparator(byteBuffer, byteBuffer2);
        return byteBuffer.remaining();
    }

    private static int findShortSuccessorInternal(AbstractComparator abstractComparator, ByteBuffer byteBuffer, int n) {
        if (n != -1) {
            byteBuffer.limit(n);
        }
        abstractComparator.findShortSuccessor(byteBuffer);
        return byteBuffer.remaining();
    }
}

