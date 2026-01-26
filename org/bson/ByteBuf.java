/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface ByteBuf {
    public int capacity();

    public ByteBuf put(int var1, byte var2);

    public int remaining();

    public ByteBuf put(byte[] var1, int var2, int var3);

    public boolean hasRemaining();

    public ByteBuf put(byte var1);

    public ByteBuf flip();

    public byte[] array();

    public int limit();

    public ByteBuf position(int var1);

    public ByteBuf clear();

    public ByteBuf order(ByteOrder var1);

    public byte get();

    public byte get(int var1);

    public ByteBuf get(byte[] var1);

    public ByteBuf get(int var1, byte[] var2);

    public ByteBuf get(byte[] var1, int var2, int var3);

    public ByteBuf get(int var1, byte[] var2, int var3, int var4);

    public long getLong();

    public long getLong(int var1);

    public double getDouble();

    public double getDouble(int var1);

    public int getInt();

    public int getInt(int var1);

    public int position();

    public ByteBuf limit(int var1);

    public ByteBuf asReadOnly();

    public ByteBuf duplicate();

    public ByteBuffer asNIO();

    public int getReferenceCount();

    public ByteBuf retain();

    public void release();
}

