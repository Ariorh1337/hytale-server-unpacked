/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.connection;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum DisconnectType {
    Disconnect(0),
    Crash(1);

    public static final DisconnectType[] VALUES;
    private final int value;

    private DisconnectType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static DisconnectType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("DisconnectType", value);
    }

    static {
        VALUES = DisconnectType.values();
    }
}

