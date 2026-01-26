/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.EciesAeadHkdfPublicKey;
import com.google.crypto.tink.proto.EciesAeadHkdfPublicKeyOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface EciesAeadHkdfPrivateKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasPublicKey();

    public EciesAeadHkdfPublicKey getPublicKey();

    public EciesAeadHkdfPublicKeyOrBuilder getPublicKeyOrBuilder();

    public ByteString getKeyValue();
}

