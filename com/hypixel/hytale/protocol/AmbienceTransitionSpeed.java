/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum AmbienceTransitionSpeed {
    Default(0),
    Fast(1),
    Instant(2);

    public static final AmbienceTransitionSpeed[] VALUES;
    private final int value;

    private AmbienceTransitionSpeed(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static AmbienceTransitionSpeed fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("AmbienceTransitionSpeed", value);
    }

    static {
        VALUES = AmbienceTransitionSpeed.values();
    }
}

