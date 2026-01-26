/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtHmacAlgorithm;
import com.google.crypto.tink.proto.JwtHmacKey;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtHmacKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public int getAlgorithmValue();

    public JwtHmacAlgorithm getAlgorithm();

    public ByteString getKeyValue();

    public boolean hasCustomKid();

    public JwtHmacKey.CustomKid getCustomKid();

    public JwtHmacKey.CustomKidOrBuilder getCustomKidOrBuilder();
}

