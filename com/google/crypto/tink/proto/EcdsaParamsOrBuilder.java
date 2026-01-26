/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.EcdsaSignatureEncoding;
import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.HashType;
import com.google.protobuf.MessageOrBuilder;

public interface EcdsaParamsOrBuilder
extends MessageOrBuilder {
    public int getHashTypeValue();

    public HashType getHashType();

    public int getCurveValue();

    public EllipticCurveType getCurve();

    public int getEncodingValue();

    public EcdsaSignatureEncoding getEncoding();
}

