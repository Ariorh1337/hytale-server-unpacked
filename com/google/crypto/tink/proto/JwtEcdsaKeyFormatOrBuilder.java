/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtEcdsaAlgorithm;
import com.google.protobuf.MessageOrBuilder;

public interface JwtEcdsaKeyFormatOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public int getAlgorithmValue();

    public JwtEcdsaAlgorithm getAlgorithm();
}

