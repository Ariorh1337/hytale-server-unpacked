/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.EcdsaPublicKey;
import com.google.crypto.tink.proto.EcdsaPublicKeyOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface EcdsaPrivateKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasPublicKey();

    public EcdsaPublicKey getPublicKey();

    public EcdsaPublicKeyOrBuilder getPublicKeyOrBuilder();

    public ByteString getKeyValue();
}

