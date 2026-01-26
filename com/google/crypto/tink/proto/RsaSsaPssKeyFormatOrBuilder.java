/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.RsaSsaPssParams;
import com.google.crypto.tink.proto.RsaSsaPssParamsOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPssKeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasParams();

    public RsaSsaPssParams getParams();

    public RsaSsaPssParamsOrBuilder getParamsOrBuilder();

    public int getModulusSizeInBits();

    public ByteString getPublicExponent();
}

