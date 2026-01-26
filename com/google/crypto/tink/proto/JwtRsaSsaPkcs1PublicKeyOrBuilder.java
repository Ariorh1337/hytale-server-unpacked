/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtRsaSsaPkcs1Algorithm;
import com.google.crypto.tink.proto.JwtRsaSsaPkcs1PublicKey;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPkcs1PublicKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public int getAlgorithmValue();

    public JwtRsaSsaPkcs1Algorithm getAlgorithm();

    public ByteString getN();

    public ByteString getE();

    public boolean hasCustomKid();

    public JwtRsaSsaPkcs1PublicKey.CustomKid getCustomKid();

    public JwtRsaSsaPkcs1PublicKey.CustomKidOrBuilder getCustomKidOrBuilder();
}

