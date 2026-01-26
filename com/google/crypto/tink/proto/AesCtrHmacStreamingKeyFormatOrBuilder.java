/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesCtrHmacStreamingParams;
import com.google.crypto.tink.proto.AesCtrHmacStreamingParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface AesCtrHmacStreamingKeyFormatOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasParams();

    public AesCtrHmacStreamingParams getParams();

    public AesCtrHmacStreamingParamsOrBuilder getParamsOrBuilder();

    public int getKeySize();
}

