/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HashType;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface HkdfPrfParamsOrBuilder
extends MessageOrBuilder {
    public int getHashValue();

    public HashType getHash();

    public ByteString getSalt();
}

