/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum RaycastMode {
    FollowMotion(0),
    FollowLook(1);

    public static final RaycastMode[] VALUES;
    private final int value;

    private RaycastMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static RaycastMode fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("RaycastMode", value);
    }

    static {
        VALUES = RaycastMode.values();
    }
}

