/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.MlDsaPublicKey;
import com.google.crypto.tink.proto.MlDsaPublicKeyOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface MlDsaPrivateKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public ByteString getKeyValue();

    public boolean hasPublicKey();

    public MlDsaPublicKey getPublicKey();

    public MlDsaPublicKeyOrBuilder getPublicKeyOrBuilder();
}

