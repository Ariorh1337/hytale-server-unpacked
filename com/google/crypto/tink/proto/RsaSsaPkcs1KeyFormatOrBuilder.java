/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.RsaSsaPkcs1Params;
import com.google.crypto.tink.proto.RsaSsaPkcs1ParamsOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPkcs1KeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasParams();

    public RsaSsaPkcs1Params getParams();

    public RsaSsaPkcs1ParamsOrBuilder getParamsOrBuilder();

    public int getModulusSizeInBits();

    public ByteString getPublicExponent();
}

