/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Stack;

public interface ShortStack
extends Stack<Short> {
    @Override
    public void push(short var1);

    public short popShort();

    public short topShort();

    public short peekShort(int var1);

    @Override
    @Deprecated
    default public void push(Short o) {
        this.push((short)o);
    }

    @Override
    @Deprecated
    default public Short pop() {
        return this.popShort();
    }

    @Override
    @Deprecated
    default public Short top() {
        return this.topShort();
    }

    @Override
    @Deprecated
    default public Short peek(int i) {
        return this.peekShort(i);
    }
}

