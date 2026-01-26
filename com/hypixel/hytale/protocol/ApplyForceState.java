/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum ApplyForceState {
    Waiting(0),
    Ground(1),
    Collision(2),
    Timer(3);

    public static final ApplyForceState[] VALUES;
    private final int value;

    private ApplyForceState(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ApplyForceState fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("ApplyForceState", value);
    }

    static {
        VALUES = ApplyForceState.values();
    }
}

