/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.KeyTemplateOrBuilder;
import com.google.crypto.tink.proto.PrfBasedDeriverParams;
import com.google.crypto.tink.proto.PrfBasedDeriverParamsOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface PrfBasedDeriverKeyFormatOrBuilder
extends MessageOrBuilder {
    public boolean hasPrfKeyTemplate();

    public KeyTemplate getPrfKeyTemplate();

    public KeyTemplateOrBuilder getPrfKeyTemplateOrBuilder();

    public boolean hasParams();

    public PrfBasedDeriverParams getParams();

    public PrfBasedDeriverParamsOrBuilder getParamsOrBuilder();
}

