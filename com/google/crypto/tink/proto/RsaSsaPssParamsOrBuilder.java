/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HashType;
import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPssParamsOrBuilder
extends MessageOrBuilder {
    public int getSigHashValue();

    public HashType getSigHash();

    public int getMgf1HashValue();

    public HashType getMgf1Hash();

    public int getSaltLength();
}

