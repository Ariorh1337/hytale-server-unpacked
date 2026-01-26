/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.codec.codecs.EnumCodec;

public enum SupportDropType {
    BREAK,
    DESTROY;

    public static final EnumCodec<SupportDropType> CODEC;

    static {
        CODEC = new EnumCodec<SupportDropType>(SupportDropType.class);
    }
}

