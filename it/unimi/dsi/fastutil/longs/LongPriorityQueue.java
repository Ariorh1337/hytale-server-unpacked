/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.longs.LongComparator;

public interface LongPriorityQueue
extends PriorityQueue<Long> {
    @Override
    public void enqueue(long var1);

    public long dequeueLong();

    public long firstLong();

    default public long lastLong() {
        throw new UnsupportedOperationException();
    }

    public LongComparator comparator();

    @Override
    @Deprecated
    default public void enqueue(Long x) {
        this.enqueue((long)x);
    }

    @Override
    @Deprecated
    default public Long dequeue() {
        return this.dequeueLong();
    }

    @Override
    @Deprecated
    default public Long first() {
        return this.firstLong();
    }

    @Override
    @Deprecated
    default public Long last() {
        return this.lastLong();
    }
}

