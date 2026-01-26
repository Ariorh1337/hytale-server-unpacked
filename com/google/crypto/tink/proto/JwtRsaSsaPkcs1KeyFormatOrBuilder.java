/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtRsaSsaPkcs1Algorithm;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPkcs1KeyFormatOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public int getAlgorithmValue();

    public JwtRsaSsaPkcs1Algorithm getAlgorithm();

    public int getModulusSizeInBits();

    public ByteString getPublicExponent();
}

