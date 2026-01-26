/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesEaxParams;
import com.google.crypto.tink.proto.AesEaxParamsOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface AesEaxKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasParams();

    public AesEaxParams getParams();

    public AesEaxParamsOrBuilder getParamsOrBuilder();

    public ByteString getKeyValue();
}

