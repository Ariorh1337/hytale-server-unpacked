/*
 * Decompiled with CFR 0.152.
 */
package org.bson.io;

import java.io.Closeable;
import org.bson.io.BsonInputMark;
import org.bson.types.ObjectId;

public interface BsonInput
extends Closeable {
    public int getPosition();

    public byte readByte();

    public void readBytes(byte[] var1);

    public void readBytes(byte[] var1, int var2, int var3);

    public long readInt64();

    public double readDouble();

    public int readInt32();

    public String readString();

    public ObjectId readObjectId();

    public String readCString();

    public void skipCString();

    public void skip(int var1);

    public BsonInputMark getMark(int var1);

    public boolean hasRemaining();

    @Override
    public void close();
}

