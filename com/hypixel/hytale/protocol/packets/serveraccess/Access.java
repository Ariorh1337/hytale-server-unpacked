/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.serveraccess;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum Access {
    Private(0),
    LAN(1),
    Friend(2),
    Open(3);

    public static final Access[] VALUES;
    private final int value;

    private Access(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static Access fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("Access", value);
    }

    static {
        VALUES = Access.values();
    }
}

