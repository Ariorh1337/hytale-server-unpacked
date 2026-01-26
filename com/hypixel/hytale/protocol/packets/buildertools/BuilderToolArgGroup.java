/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum BuilderToolArgGroup {
    Tool(0),
    Brush(1);

    public static final BuilderToolArgGroup[] VALUES;
    private final int value;

    private BuilderToolArgGroup(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static BuilderToolArgGroup fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("BuilderToolArgGroup", value);
    }

    static {
        VALUES = BuilderToolArgGroup.values();
    }
}

