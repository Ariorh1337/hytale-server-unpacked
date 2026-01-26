/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesCtrParams;
import com.google.crypto.tink.proto.AesCtrParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface AesCtrKeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasParams();

    public AesCtrParams getParams();

    public AesCtrParamsOrBuilder getParamsOrBuilder();

    public int getKeySize();
}

