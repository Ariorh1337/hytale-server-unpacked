/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum Rotation {
    None(0),
    Ninety(1),
    OneEighty(2),
    TwoSeventy(3);

    public static final Rotation[] VALUES;
    private final int value;

    private Rotation(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static Rotation fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("Rotation", value);
    }

    static {
        VALUES = Rotation.values();
    }
}

