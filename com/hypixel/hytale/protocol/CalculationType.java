/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum CalculationType {
    Additive(0),
    Multiplicative(1);

    public static final CalculationType[] VALUES;
    private final int value;

    private CalculationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static CalculationType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("CalculationType", value);
    }

    static {
        VALUES = CalculationType.values();
    }
}

