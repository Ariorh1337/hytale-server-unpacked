/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HkdfPrfParams;
import com.google.crypto.tink.proto.HkdfPrfParamsOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface HkdfPrfKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasParams();

    public HkdfPrfParams getParams();

    public HkdfPrfParamsOrBuilder getParamsOrBuilder();

    public ByteString getKeyValue();
}

