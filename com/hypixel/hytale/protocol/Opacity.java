/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum Opacity {
    Solid(0),
    Semitransparent(1),
    Cutout(2),
    Transparent(3);

    public static final Opacity[] VALUES;
    private final int value;

    private Opacity(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static Opacity fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("Opacity", value);
    }

    static {
        VALUES = Opacity.values();
    }
}

