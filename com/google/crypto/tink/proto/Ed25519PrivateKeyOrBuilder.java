/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.Ed25519PublicKey;
import com.google.crypto.tink.proto.Ed25519PublicKeyOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface Ed25519PrivateKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public ByteString getKeyValue();

    public boolean hasPublicKey();

    public Ed25519PublicKey getPublicKey();

    public Ed25519PublicKeyOrBuilder getPublicKeyOrBuilder();
}

