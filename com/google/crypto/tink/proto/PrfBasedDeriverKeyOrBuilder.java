/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyDataOrBuilder;
import com.google.crypto.tink.proto.PrfBasedDeriverParams;
import com.google.crypto.tink.proto.PrfBasedDeriverParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface PrfBasedDeriverKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasPrfKey();

    public KeyData getPrfKey();

    public KeyDataOrBuilder getPrfKeyOrBuilder();

    public boolean hasParams();

    public PrfBasedDeriverParams getParams();

    public PrfBasedDeriverParamsOrBuilder getParamsOrBuilder();
}

