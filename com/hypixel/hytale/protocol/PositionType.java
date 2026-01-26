/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum PositionType {
    AttachedToPlusOffset(0),
    Custom(1);

    public static final PositionType[] VALUES;
    private final int value;

    private PositionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static PositionType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("PositionType", value);
    }

    static {
        VALUES = PositionType.values();
    }
}

