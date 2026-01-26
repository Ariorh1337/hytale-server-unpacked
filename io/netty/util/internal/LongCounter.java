/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

public interface LongCounter {
    public void add(long var1);

    public void increment();

    public void decrement();

    public long value();
}

