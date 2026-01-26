/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.SlhDsaParams;
import com.google.crypto.tink.proto.SlhDsaParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface SlhDsaKeyFormatOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasParams();

    public SlhDsaParams getParams();

    public SlhDsaParamsOrBuilder getParamsOrBuilder();
}

