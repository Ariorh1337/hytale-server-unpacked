/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.floats.FloatComparator;

public interface FloatPriorityQueue
extends PriorityQueue<Float> {
    @Override
    public void enqueue(float var1);

    public float dequeueFloat();

    public float firstFloat();

    default public float lastFloat() {
        throw new UnsupportedOperationException();
    }

    public FloatComparator comparator();

    @Override
    @Deprecated
    default public void enqueue(Float x) {
        this.enqueue(x.floatValue());
    }

    @Override
    @Deprecated
    default public Float dequeue() {
        return Float.valueOf(this.dequeueFloat());
    }

    @Override
    @Deprecated
    default public Float first() {
        return Float.valueOf(this.firstFloat());
    }

    @Override
    @Deprecated
    default public Float last() {
        return Float.valueOf(this.lastFloat());
    }
}

