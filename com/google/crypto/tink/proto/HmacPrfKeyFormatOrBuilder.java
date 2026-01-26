/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HmacPrfParams;
import com.google.crypto.tink.proto.HmacPrfParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface HmacPrfKeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasParams();

    public HmacPrfParams getParams();

    public HmacPrfParamsOrBuilder getParamsOrBuilder();

    public int getKeySize();

    public int getVersion();
}

