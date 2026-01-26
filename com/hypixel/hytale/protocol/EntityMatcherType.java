/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum EntityMatcherType {
    Server(0),
    VulnerableMatcher(1),
    Player(2);

    public static final EntityMatcherType[] VALUES;
    private final int value;

    private EntityMatcherType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static EntityMatcherType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("EntityMatcherType", value);
    }

    static {
        VALUES = EntityMatcherType.values();
    }
}

