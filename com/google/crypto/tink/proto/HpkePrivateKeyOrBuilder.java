/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HpkePublicKey;
import com.google.crypto.tink.proto.HpkePublicKeyOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface HpkePrivateKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasPublicKey();

    public HpkePublicKey getPublicKey();

    public HpkePublicKeyOrBuilder getPublicKeyOrBuilder();

    public ByteString getPrivateKey();
}

