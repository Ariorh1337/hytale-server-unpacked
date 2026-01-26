/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.asseteditor;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum JsonUpdateType {
    SetProperty(0),
    InsertProperty(1),
    RemoveProperty(2);

    public static final JsonUpdateType[] VALUES;
    private final int value;

    private JsonUpdateType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static JsonUpdateType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("JsonUpdateType", value);
    }

    static {
        VALUES = JsonUpdateType.values();
    }
}

