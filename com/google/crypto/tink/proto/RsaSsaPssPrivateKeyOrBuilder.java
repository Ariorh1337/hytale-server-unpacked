/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.RsaSsaPssPublicKey;
import com.google.crypto.tink.proto.RsaSsaPssPublicKeyOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPssPrivateKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasPublicKey();

    public RsaSsaPssPublicKey getPublicKey();

    public RsaSsaPssPublicKeyOrBuilder getPublicKeyOrBuilder();

    public ByteString getD();

    public ByteString getP();

    public ByteString getQ();

    public ByteString getDp();

    public ByteString getDq();

    public ByteString getCrt();
}

