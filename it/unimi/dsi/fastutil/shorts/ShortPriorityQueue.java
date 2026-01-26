/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.shorts.ShortComparator;

public interface ShortPriorityQueue
extends PriorityQueue<Short> {
    @Override
    public void enqueue(short var1);

    public short dequeueShort();

    public short firstShort();

    default public short lastShort() {
        throw new UnsupportedOperationException();
    }

    public ShortComparator comparator();

    @Override
    @Deprecated
    default public void enqueue(Short x) {
        this.enqueue((short)x);
    }

    @Override
    @Deprecated
    default public Short dequeue() {
        return this.dequeueShort();
    }

    @Override
    @Deprecated
    default public Short first() {
        return this.firstShort();
    }

    @Override
    @Deprecated
    default public Short last() {
        return this.lastShort();
    }
}

