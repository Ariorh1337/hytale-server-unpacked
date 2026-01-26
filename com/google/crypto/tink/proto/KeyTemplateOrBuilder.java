/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface KeyTemplateOrBuilder
extends MessageOrBuilder {
    public String getTypeUrl();

    public ByteString getTypeUrlBytes();

    public ByteString getValue();

    public int getOutputPrefixTypeValue();

    public OutputPrefixType getOutputPrefixType();
}

