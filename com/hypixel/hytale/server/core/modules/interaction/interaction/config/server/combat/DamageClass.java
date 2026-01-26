/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat;

import com.hypixel.hytale.codec.codecs.EnumCodec;

public enum DamageClass {
    UNKNOWN,
    LIGHT,
    CHARGED,
    SIGNATURE;

    public static final EnumCodec<DamageClass> CODEC;

    static {
        CODEC = new EnumCodec<DamageClass>(DamageClass.class);
    }
}

