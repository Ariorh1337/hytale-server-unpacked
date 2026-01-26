/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KmsAeadKeyFormat;
import com.google.crypto.tink.proto.KmsAeadKeyFormatOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface KmsAeadKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasParams();

    public KmsAeadKeyFormat getParams();

    public KmsAeadKeyFormatOrBuilder getParamsOrBuilder();
}

