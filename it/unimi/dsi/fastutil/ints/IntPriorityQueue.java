/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.ints.IntComparator;

public interface IntPriorityQueue
extends PriorityQueue<Integer> {
    @Override
    public void enqueue(int var1);

    public int dequeueInt();

    public int firstInt();

    default public int lastInt() {
        throw new UnsupportedOperationException();
    }

    public IntComparator comparator();

    @Override
    @Deprecated
    default public void enqueue(Integer x) {
        this.enqueue((int)x);
    }

    @Override
    @Deprecated
    default public Integer dequeue() {
        return this.dequeueInt();
    }

    @Override
    @Deprecated
    default public Integer first() {
        return this.firstInt();
    }

    @Override
    @Deprecated
    default public Integer last() {
        return this.lastInt();
    }
}

