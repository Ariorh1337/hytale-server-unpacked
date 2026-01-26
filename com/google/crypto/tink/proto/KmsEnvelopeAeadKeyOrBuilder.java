/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KmsEnvelopeAeadKeyFormat;
import com.google.crypto.tink.proto.KmsEnvelopeAeadKeyFormatOrBuilder;
import com.google.protobuf.MessageOrBuilder;

public interface KmsEnvelopeAeadKeyOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public boolean hasParams();

    public KmsEnvelopeAeadKeyFormat getParams();

    public KmsEnvelopeAeadKeyFormatOrBuilder getParamsOrBuilder();
}

