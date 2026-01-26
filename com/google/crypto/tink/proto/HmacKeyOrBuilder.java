/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HmacParams;
import com.google.crypto.tink.proto.HmacParamsOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface HmacKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasParams();

    public HmacParams getParams();

    public HmacParamsOrBuilder getParamsOrBuilder();

    public ByteString getKeyValue();
}

