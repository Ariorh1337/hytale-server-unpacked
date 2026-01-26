/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtHmacAlgorithm;
import com.google.protobuf.MessageOrBuilder;

public interface JwtHmacKeyFormatOrBuilder
extends MessageOrBuilder {
    public int getVersion();

    public int getAlgorithmValue();

    public JwtHmacAlgorithm getAlgorithm();

    public int getKeySize();
}

