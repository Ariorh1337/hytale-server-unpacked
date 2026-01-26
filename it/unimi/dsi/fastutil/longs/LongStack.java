/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Stack;

public interface LongStack
extends Stack<Long> {
    @Override
    public void push(long var1);

    public long popLong();

    public long topLong();

    public long peekLong(int var1);

    @Override
    @Deprecated
    default public void push(Long o) {
        this.push((long)o);
    }

    @Override
    @Deprecated
    default public Long pop() {
        return this.popLong();
    }

    @Override
    @Deprecated
    default public Long top() {
        return this.topLong();
    }

    @Override
    @Deprecated
    default public Long peek(int i) {
        return this.peekLong(i);
    }
}

