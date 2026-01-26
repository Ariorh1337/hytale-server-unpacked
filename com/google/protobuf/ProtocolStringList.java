/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import java.util.List;

public interface ProtocolStringList
extends List<String> {
    public List<ByteString> asByteStringList();
}

