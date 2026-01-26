/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.XAesGcmParams;
import com.google.crypto.tink.proto.XAesGcmParamsOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface XAesGcmKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasParams();

    public XAesGcmParams getParams();

    public XAesGcmParamsOrBuilder getParamsOrBuilder();

    public ByteString getKeyValue();
}

