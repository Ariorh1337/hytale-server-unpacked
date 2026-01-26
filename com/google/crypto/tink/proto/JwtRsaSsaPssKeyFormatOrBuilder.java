/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtRsaSsaPssAlgorithm;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPssKeyFormatOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public int getAlgorithmValue();

    public JwtRsaSsaPssAlgorithm getAlgorithm();

    public int getModulusSizeInBits();

    public ByteString getPublicExponent();
}

