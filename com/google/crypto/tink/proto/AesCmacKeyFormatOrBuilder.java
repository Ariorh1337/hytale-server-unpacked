/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesCmacParams;
import com.google.crypto.tink.proto.AesCmacParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface AesCmacKeyFormatOrBuilder
extends MessageOrBuilder {
    public int getKeySize();

    public boolean hasParams();

    public AesCmacParams getParams();

    public AesCmacParamsOrBuilder getParamsOrBuilder();
}

