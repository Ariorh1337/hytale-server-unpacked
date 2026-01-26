/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HashType;
import com.google.protobuf.MessageOrBuilder;

public interface AesGcmHkdfStreamingParamsOrBuilder
extends MessageOrBuilder {
    public int getCiphertextSegmentSize();

    public int getDerivedKeySize();

    public int getHkdfHashTypeValue();

    public HashType getHkdfHashType();
}

