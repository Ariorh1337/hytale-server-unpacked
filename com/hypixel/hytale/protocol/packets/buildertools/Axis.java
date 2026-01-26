/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum Axis {
    X(0),
    Y(1),
    Z(2);

    public static final Axis[] VALUES;
    private final int value;

    private Axis(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static Axis fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("Axis", value);
    }

    static {
        VALUES = Axis.values();
    }
}

