/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeyData;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface KeyDataOrBuilder
extends MessageOrBuilder {
    public String getTypeUrl();

    public ByteString getTypeUrlBytes();

    public ByteString getValue();

    public int getKeyMaterialTypeValue();

    public KeyData.KeyMaterialType getKeyMaterialType();
}

