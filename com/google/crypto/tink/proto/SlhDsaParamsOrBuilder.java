/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.SlhDsaHashType;
import com.google.crypto.tink.proto.SlhDsaSignatureType;
import com.google.protobuf.MessageOrBuilder;

public interface SlhDsaParamsOrBuilder
extends MessageOrBuilder {
    public int getKeySize();

    public int getHashTypeValue();

    public SlhDsaHashType getHashType();

    public int getSigTypeValue();

    public SlhDsaSignatureType getSigType();
}

