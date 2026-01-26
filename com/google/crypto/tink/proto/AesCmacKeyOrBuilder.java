/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesCmacParams;
import com.google.crypto.tink.proto.AesCmacParamsOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface AesCmacKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public ByteString getKeyValue();

    public boolean hasParams();

    public AesCmacParams getParams();

    public AesCmacParamsOrBuilder getParamsOrBuilder();
}

