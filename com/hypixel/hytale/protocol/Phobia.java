/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum Phobia {
    None(0),
    Arachnophobia(1),
    Ophidiophobia(2);

    public static final Phobia[] VALUES;
    private final int value;

    private Phobia(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static Phobia fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("Phobia", value);
    }

    static {
        VALUES = Phobia.values();
    }
}

