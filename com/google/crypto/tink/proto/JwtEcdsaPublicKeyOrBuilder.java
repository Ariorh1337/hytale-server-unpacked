/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtEcdsaAlgorithm;
import com.google.crypto.tink.proto.JwtEcdsaPublicKey;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtEcdsaPublicKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public int getAlgorithmValue();

    public JwtEcdsaAlgorithm getAlgorithm();

    public ByteString getX();

    public ByteString getY();

    public boolean hasCustomKid();

    public JwtEcdsaPublicKey.CustomKid getCustomKid();

    public JwtEcdsaPublicKey.CustomKidOrBuilder getCustomKidOrBuilder();
}

