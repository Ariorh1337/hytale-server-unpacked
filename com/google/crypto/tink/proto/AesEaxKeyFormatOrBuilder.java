/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesEaxParams;
import com.google.crypto.tink.proto.AesEaxParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface AesEaxKeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasParams();

    public AesEaxParams getParams();

    public AesEaxParamsOrBuilder getParamsOrBuilder();

    public int getKeySize();
}

