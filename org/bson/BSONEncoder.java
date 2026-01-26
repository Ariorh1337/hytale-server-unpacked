/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BSONObject;
import org.bson.io.OutputBuffer;

public interface BSONEncoder {
    public byte[] encode(BSONObject var1);

    public int putObject(BSONObject var1);

    public void done();

    public void set(OutputBuffer var1);
}

