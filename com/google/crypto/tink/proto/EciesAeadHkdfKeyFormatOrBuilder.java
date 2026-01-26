/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.EciesAeadHkdfParams;
import com.google.crypto.tink.proto.EciesAeadHkdfParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface EciesAeadHkdfKeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasParams();

    public EciesAeadHkdfParams getParams();

    public EciesAeadHkdfParamsOrBuilder getParamsOrBuilder();
}

