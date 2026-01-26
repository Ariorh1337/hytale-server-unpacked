/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Stack;

public interface ByteStack
extends Stack<Byte> {
    @Override
    public void push(byte var1);

    public byte popByte();

    public byte topByte();

    public byte peekByte(int var1);

    @Override
    @Deprecated
    default public void push(Byte o) {
        this.push((byte)o);
    }

    @Override
    @Deprecated
    default public Byte pop() {
        return this.popByte();
    }

    @Override
    @Deprecated
    default public Byte top() {
        return this.topByte();
    }

    @Override
    @Deprecated
    default public Byte peek(int i) {
        return this.peekByte(i);
    }
}

