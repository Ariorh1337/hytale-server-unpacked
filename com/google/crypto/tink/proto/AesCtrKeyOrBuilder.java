/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesCtrParams;
import com.google.crypto.tink.proto.AesCtrParamsOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface AesCtrKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasParams();

    public AesCtrParams getParams();

    public AesCtrParamsOrBuilder getParamsOrBuilder();

    public ByteString getKeyValue();
}

