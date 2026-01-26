/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.MlDsaParams;
import com.google.crypto.tink.proto.MlDsaParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface MlDsaKeyFormatOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasParams();

    public MlDsaParams getParams();

    public MlDsaParamsOrBuilder getParamsOrBuilder();
}

