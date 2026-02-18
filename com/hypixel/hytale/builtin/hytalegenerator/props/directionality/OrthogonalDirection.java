/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props.directionality;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import javax.annotation.Nonnull;

public enum OrthogonalDirection {
    N,
    S,
    E,
    W,
    U,
    D;

    @Nonnull
    public static final Codec<OrthogonalDirection> CODEC;

    static {
        CODEC = new EnumCodec<OrthogonalDirection>(OrthogonalDirection.class, EnumCodec.EnumStyle.LEGACY);
    }
}

