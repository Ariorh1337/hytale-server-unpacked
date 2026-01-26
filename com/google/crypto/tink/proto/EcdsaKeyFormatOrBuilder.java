/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.EcdsaParams;
import com.google.crypto.tink.proto.EcdsaParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface EcdsaKeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasParams();

    public EcdsaParams getParams();

    public EcdsaParamsOrBuilder getParamsOrBuilder();

    public int getVersion();
}

