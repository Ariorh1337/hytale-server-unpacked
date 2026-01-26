/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.KeyTemplateOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface KmsEnvelopeAeadKeyFormatOrBuilder
extends MessageOrBuilder {
    public String getKekUri();

    public ByteString getKekUriBytes();

    public boolean hasDekTemplate();

    public KeyTemplate getDekTemplate();

    public KeyTemplateOrBuilder getDekTemplateOrBuilder();
}

