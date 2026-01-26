/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.entityeffect.config;

import com.hypixel.hytale.codec.codecs.EnumCodec;
import javax.annotation.Nonnull;

public enum OverlapBehavior {
    EXTEND,
    OVERWRITE,
    IGNORE;

    @Nonnull
    public static final EnumCodec<OverlapBehavior> CODEC;

    static {
        CODEC = new EnumCodec<OverlapBehavior>(OverlapBehavior.class);
    }
}

