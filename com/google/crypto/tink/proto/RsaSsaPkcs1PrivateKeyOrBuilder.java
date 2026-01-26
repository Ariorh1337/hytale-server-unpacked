/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.RsaSsaPkcs1PublicKey;
import com.google.crypto.tink.proto.RsaSsaPkcs1PublicKeyOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPkcs1PrivateKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasPublicKey();

    public RsaSsaPkcs1PublicKey getPublicKey();

    public RsaSsaPkcs1PublicKeyOrBuilder getPublicKeyOrBuilder();

    public ByteString getD();

    public ByteString getP();

    public ByteString getQ();

    public ByteString getDp();

    public ByteString getDq();

    public ByteString getCrt();
}

