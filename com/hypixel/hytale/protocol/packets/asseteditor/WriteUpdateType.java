/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.asseteditor;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum WriteUpdateType {
    Add(0),
    Update(1),
    Remove(2);

    public static final WriteUpdateType[] VALUES;
    private final int value;

    private WriteUpdateType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static WriteUpdateType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("WriteUpdateType", value);
    }

    static {
        VALUES = WriteUpdateType.values();
    }
}

