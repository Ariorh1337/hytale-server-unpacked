/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.io.IOException;
import java.io.InputStream;
import org.bson.BSONCallback;
import org.bson.BSONObject;

public interface BSONDecoder {
    public BSONObject readObject(byte[] var1);

    public BSONObject readObject(InputStream var1) throws IOException;

    public int decode(byte[] var1, BSONCallback var2);

    public int decode(InputStream var1, BSONCallback var2) throws IOException;
}

