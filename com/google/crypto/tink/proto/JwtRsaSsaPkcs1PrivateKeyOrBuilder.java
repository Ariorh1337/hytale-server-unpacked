/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtRsaSsaPkcs1PublicKey;
import com.google.crypto.tink.proto.JwtRsaSsaPkcs1PublicKeyOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPkcs1PrivateKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasPublicKey();

    public JwtRsaSsaPkcs1PublicKey getPublicKey();

    public JwtRsaSsaPkcs1PublicKeyOrBuilder getPublicKeyOrBuilder();

    public ByteString getD();

    public ByteString getP();

    public ByteString getQ();

    public ByteString getDp();

    public ByteString getDq();

    public ByteString getCrt();
}

