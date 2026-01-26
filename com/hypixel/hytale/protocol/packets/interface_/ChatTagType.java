/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum ChatTagType {
    Item(0);

    public static final ChatTagType[] VALUES;
    private final int value;

    private ChatTagType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ChatTagType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("ChatTagType", value);
    }

    static {
        VALUES = ChatTagType.values();
    }
}

