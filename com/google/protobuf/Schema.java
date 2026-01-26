/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ArrayDecoders;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.Reader;
import com.google.protobuf.Writer;
import java.io.IOException;

@CheckReturnValue
interface Schema<T> {
    public void writeTo(T var1, Writer var2) throws IOException;

    public void mergeFrom(T var1, Reader var2, ExtensionRegistryLite var3) throws IOException;

    public void mergeFrom(T var1, byte[] var2, int var3, int var4, ArrayDecoders.Registers var5) throws IOException;

    public void makeImmutable(T var1);

    public boolean isInitialized(T var1);

    public T newInstance();

    public boolean equals(T var1, T var2);

    public int hashCode(T var1);

    public void mergeFrom(T var1, T var2);

    public int getSerializedSize(T var1);
}

