/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.SlhDsaPublicKey;
import com.google.crypto.tink.proto.SlhDsaPublicKeyOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface SlhDsaPrivateKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public ByteString getKeyValue();

    public boolean hasPublicKey();

    public SlhDsaPublicKey getPublicKey();

    public SlhDsaPublicKeyOrBuilder getPublicKeyOrBuilder();
}

