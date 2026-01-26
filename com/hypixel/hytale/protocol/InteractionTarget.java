/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum InteractionTarget {
    User(0),
    Owner(1),
    Target(2);

    public static final InteractionTarget[] VALUES;
    private final int value;

    private InteractionTarget(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static InteractionTarget fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("InteractionTarget", value);
    }

    static {
        VALUES = InteractionTarget.values();
    }
}

