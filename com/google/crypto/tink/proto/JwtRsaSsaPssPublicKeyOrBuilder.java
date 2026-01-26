/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtRsaSsaPssAlgorithm;
import com.google.crypto.tink.proto.JwtRsaSsaPssPublicKey;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPssPublicKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public int getAlgorithmValue();

    public JwtRsaSsaPssAlgorithm getAlgorithm();

    public ByteString getN();

    public ByteString getE();

    public boolean hasCustomKid();

    public JwtRsaSsaPssPublicKey.CustomKid getCustomKid();

    public JwtRsaSsaPssPublicKey.CustomKidOrBuilder getCustomKidOrBuilder();
}

