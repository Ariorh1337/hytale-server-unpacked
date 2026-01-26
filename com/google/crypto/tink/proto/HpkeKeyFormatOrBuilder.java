/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HpkeParams;
import com.google.crypto.tink.proto.HpkeParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface HpkeKeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasParams();

    public HpkeParams getParams();

    public HpkeParamsOrBuilder getParamsOrBuilder();
}

