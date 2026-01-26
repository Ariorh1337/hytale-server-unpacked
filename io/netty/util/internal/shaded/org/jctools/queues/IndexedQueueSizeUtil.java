/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

public final class IndexedQueueSizeUtil {
    public static final int PLAIN_DIVISOR = 1;
    public static final int IGNORE_PARITY_DIVISOR = 2;

    public static int size(IndexedQueue iq, int divisor) {
        long currentProducerIndex;
        long before;
        long after = iq.lvConsumerIndex();
        do {
            before = after;
            currentProducerIndex = iq.lvProducerIndex();
        } while (before != (after = iq.lvConsumerIndex()));
        long size = (currentProducerIndex - after) / (long)divisor;
        return IndexedQueueSizeUtil.sanitizedSize(iq.capacity(), size);
    }

    public static int sanitizedSize(int capacity, long size) {
        if (size < 0L) {
            return 0;
        }
        if (capacity != -1 && size > (long)capacity) {
            return capacity;
        }
        if (size > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)size;
    }

    public static boolean isEmpty(IndexedQueue iq) {
        return iq.lvConsumerIndex() >= iq.lvProducerIndex();
    }

    public static interface IndexedQueue {
        public long lvConsumerIndex();

        public long lvProducerIndex();

        public int capacity();
    }
}

