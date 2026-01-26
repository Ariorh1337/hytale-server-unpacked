/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.machinima;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum SceneUpdateType {
    Update(0),
    Play(1),
    Stop(2),
    Frame(3),
    Save(4);

    public static final SceneUpdateType[] VALUES;
    private final int value;

    private SceneUpdateType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static SceneUpdateType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("SceneUpdateType", value);
    }

    static {
        VALUES = SceneUpdateType.values();
    }
}

