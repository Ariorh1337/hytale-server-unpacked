/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesCtrKeyFormat;
import com.google.crypto.tink.proto.AesCtrKeyFormatOrBuilder;
import com.google.crypto.tink.proto.HmacKeyFormat;
import com.google.crypto.tink.proto.HmacKeyFormatOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface AesCtrHmacAeadKeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasAesCtrKeyFormat();

    public AesCtrKeyFormat getAesCtrKeyFormat();

    public AesCtrKeyFormatOrBuilder getAesCtrKeyFormatOrBuilder();

    public boolean hasHmacKeyFormat();

    public HmacKeyFormat getHmacKeyFormat();

    public HmacKeyFormatOrBuilder getHmacKeyFormatOrBuilder();
}

