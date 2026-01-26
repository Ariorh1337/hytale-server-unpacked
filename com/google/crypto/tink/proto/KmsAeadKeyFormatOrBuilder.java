/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface KmsAeadKeyFormatOrBuilder
extends MessageOrBuilder {
    public String getKeyUri();

    public ByteString getKeyUriBytes();
}

