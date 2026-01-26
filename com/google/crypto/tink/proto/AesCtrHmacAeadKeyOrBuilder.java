/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesCtrKey;
import com.google.crypto.tink.proto.AesCtrKeyOrBuilder;
import com.google.crypto.tink.proto.HmacKey;
import com.google.crypto.tink.proto.HmacKeyOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface AesCtrHmacAeadKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasAesCtrKey();

    public AesCtrKey getAesCtrKey();

    public AesCtrKeyOrBuilder getAesCtrKeyOrBuilder();

    public boolean hasHmacKey();

    public HmacKey getHmacKey();

    public HmacKeyOrBuilder getHmacKeyOrBuilder();
}

