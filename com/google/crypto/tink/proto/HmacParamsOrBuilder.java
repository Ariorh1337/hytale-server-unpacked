/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HashType;
import com.google.protobuf.MessageOrBuilder;

public interface HmacParamsOrBuilder
extends MessageOrBuilder {
    public int getHashValue();

    public HashType getHash();

    public int getTagSize();
}

