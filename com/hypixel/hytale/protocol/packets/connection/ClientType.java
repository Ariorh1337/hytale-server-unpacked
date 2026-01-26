/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.connection;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum ClientType {
    Game(0),
    Editor(1);

    public static final ClientType[] VALUES;
    private final int value;

    private ClientType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ClientType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("ClientType", value);
    }

    static {
        VALUES = ClientType.values();
    }
}

