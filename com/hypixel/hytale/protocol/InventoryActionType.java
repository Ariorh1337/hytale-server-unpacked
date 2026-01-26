/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum InventoryActionType {
    TakeAll(0),
    PutAll(1),
    QuickStack(2),
    Sort(3);

    public static final InventoryActionType[] VALUES;
    private final int value;

    private InventoryActionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static InventoryActionType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("InventoryActionType", value);
    }

    static {
        VALUES = InventoryActionType.values();
    }
}

