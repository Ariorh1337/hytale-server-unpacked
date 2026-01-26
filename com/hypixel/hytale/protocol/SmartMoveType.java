/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum SmartMoveType {
    EquipOrMergeStack(0),
    PutInHotbarOrWindow(1),
    PutInHotbarOrBackpack(2);

    public static final SmartMoveType[] VALUES;
    private final int value;

    private SmartMoveType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static SmartMoveType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("SmartMoveType", value);
    }

    static {
        VALUES = SmartMoveType.values();
    }
}

