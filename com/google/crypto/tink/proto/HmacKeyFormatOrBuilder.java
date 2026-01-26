/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HmacParams;
import com.google.crypto.tink.proto.HmacParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface HmacKeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasParams();

    public HmacParams getParams();

    public HmacParamsOrBuilder getParamsOrBuilder();

    public int getKeySize();

    public int getVersion();
}

