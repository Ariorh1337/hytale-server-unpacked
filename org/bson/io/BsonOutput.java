/*
 * Decompiled with CFR 0.152.
 */
package org.bson.io;

import java.io.Closeable;
import org.bson.types.ObjectId;

public interface BsonOutput
extends Closeable {
    public int getPosition();

    public int getSize();

    public void truncateToPosition(int var1);

    public void writeBytes(byte[] var1);

    public void writeBytes(byte[] var1, int var2, int var3);

    public void writeByte(int var1);

    public void writeCString(String var1);

    public void writeString(String var1);

    public void writeDouble(double var1);

    public void writeInt32(int var1);

    public void writeInt32(int var1, int var2);

    public void writeInt64(long var1);

    public void writeObjectId(ObjectId var1);

    @Override
    public void close();
}

