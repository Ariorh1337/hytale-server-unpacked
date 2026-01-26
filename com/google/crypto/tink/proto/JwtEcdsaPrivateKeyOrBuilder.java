/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtEcdsaPublicKey;
import com.google.crypto.tink.proto.JwtEcdsaPublicKeyOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtEcdsaPrivateKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasPublicKey();

    public JwtEcdsaPublicKey getPublicKey();

    public JwtEcdsaPublicKeyOrBuilder getPublicKeyOrBuilder();

    public ByteString getKeyValue();
}

