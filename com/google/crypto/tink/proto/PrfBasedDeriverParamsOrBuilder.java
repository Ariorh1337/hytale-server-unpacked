/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.KeyTemplateOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface PrfBasedDeriverParamsOrBuilder
extends MessageOrBuilder {
    public boolean hasDerivedKeyTemplate();

    public KeyTemplate getDerivedKeyTemplate();

    public KeyTemplateOrBuilder getDerivedKeyTemplateOrBuilder();
}

