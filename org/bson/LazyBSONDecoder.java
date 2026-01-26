/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.bson.BSONCallback;
import org.bson.BSONDecoder;
import org.bson.BSONException;
import org.bson.BSONObject;
import org.bson.Bits;
import org.bson.LazyBSONCallback;

public class LazyBSONDecoder
implements BSONDecoder {
    private static final int BYTES_IN_INTEGER = 4;

    @Override
    public BSONObject readObject(byte[] bytes) {
        LazyBSONCallback bsonCallback = new LazyBSONCallback();
        this.decode(bytes, (BSONCallback)bsonCallback);
        return (BSONObject)bsonCallback.get();
    }

    @Override
    public BSONObject readObject(InputStream in) throws IOException {
        LazyBSONCallback bsonCallback = new LazyBSONCallback();
        this.decode(in, (BSONCallback)bsonCallback);
        return (BSONObject)bsonCallback.get();
    }

    @Override
    public int decode(byte[] bytes, BSONCallback callback) {
        try {
            return this.decode(new ByteArrayInputStream(bytes), callback);
        }
        catch (IOException e) {
            throw new BSONException("Invalid bytes received", e);
        }
    }

    @Override
    public int decode(InputStream in, BSONCallback callback) throws IOException {
        byte[] documentSizeBuffer = new byte[4];
        int documentSize = Bits.readInt(in, documentSizeBuffer);
        byte[] documentBytes = Arrays.copyOf(documentSizeBuffer, documentSize);
        Bits.readFully(in, documentBytes, 4, documentSize - 4);
        callback.gotBinary(null, (byte)0, documentBytes);
        return documentSize;
    }
}

