/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.SlhDsaParams;
import com.google.crypto.tink.proto.SlhDsaParamsOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface SlhDsaPublicKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public ByteString getKeyValue();

    public boolean hasParams();

    public SlhDsaParams getParams();

    public SlhDsaParamsOrBuilder getParamsOrBuilder();
}

