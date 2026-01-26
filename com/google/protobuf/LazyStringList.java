/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import java.util.Collection;
import java.util.List;

public interface LazyStringList
extends ProtocolStringList {
    public ByteString getByteString(int var1);

    public Object getRaw(int var1);

    public byte[] getByteArray(int var1);

    public void add(ByteString var1);

    public void add(byte[] var1);

    @Override
    public void set(int var1, ByteString var2);

    @Override
    public void set(int var1, byte[] var2);

    public boolean addAllByteString(Collection<? extends ByteString> var1);

    public boolean addAllByteArray(Collection<byte[]> var1);

    public List<?> getUnderlyingElements();

    public void mergeFrom(LazyStringList var1);

    public List<byte[]> asByteArrayList();

    public LazyStringList getUnmodifiableView();
}

