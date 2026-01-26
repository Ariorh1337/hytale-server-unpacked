/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.entityeffect.config;

import com.hypixel.hytale.codec.codecs.EnumCodec;
import javax.annotation.Nonnull;

public enum RemovalBehavior {
    COMPLETE,
    INFINITE,
    DURATION;

    @Nonnull
    public static final EnumCodec<RemovalBehavior> CODEC;

    static {
        CODEC = new EnumCodec<RemovalBehavior>(RemovalBehavior.class);
    }
}

