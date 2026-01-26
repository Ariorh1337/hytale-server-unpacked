/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtRsaSsaPssPublicKey;
import com.google.crypto.tink.proto.JwtRsaSsaPssPublicKeyOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPssPrivateKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasPublicKey();

    public JwtRsaSsaPssPublicKey getPublicKey();

    public JwtRsaSsaPssPublicKeyOrBuilder getPublicKeyOrBuilder();

    public ByteString getD();

    public ByteString getP();

    public ByteString getQ();

    public ByteString getDp();

    public ByteString getDq();

    public ByteString getCrt();
}

