/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HkdfPrfParams;
import com.google.crypto.tink.proto.HkdfPrfParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface HkdfPrfKeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasParams();

    public HkdfPrfParams getParams();

    public HkdfPrfParamsOrBuilder getParamsOrBuilder();

    public int getKeySize();

    public int getVersion();
}

