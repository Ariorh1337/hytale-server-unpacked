/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.HashType;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface EciesHkdfKemParamsOrBuilder
extends MessageOrBuilder {
    public int getCurveTypeValue();

    public EllipticCurveType getCurveType();

    public int getHkdfHashTypeValue();

    public HashType getHkdfHashType();

    public ByteString getHkdfSalt();
}

