/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesGcmHkdfStreamingParams;
import com.google.crypto.tink.proto.AesGcmHkdfStreamingParamsOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface AesGcmHkdfStreamingKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasParams();

    public AesGcmHkdfStreamingParams getParams();

    public AesGcmHkdfStreamingParamsOrBuilder getParamsOrBuilder();

    public ByteString getKeyValue();
}

