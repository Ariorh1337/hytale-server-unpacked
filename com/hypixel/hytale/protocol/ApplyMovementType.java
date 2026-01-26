/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum ApplyMovementType {
    CharacterController(0),
    Position(1);

    public static final ApplyMovementType[] VALUES;
    private final int value;

    private ApplyMovementType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ApplyMovementType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("ApplyMovementType", value);
    }

    static {
        VALUES = ApplyMovementType.values();
    }
}

