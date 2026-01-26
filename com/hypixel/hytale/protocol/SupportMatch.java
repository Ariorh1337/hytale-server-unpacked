/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum SupportMatch {
    Ignored(0),
    Required(1),
    Disallowed(2);

    public static final SupportMatch[] VALUES;
    private final int value;

    private SupportMatch(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static SupportMatch fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("SupportMatch", value);
    }

    static {
        VALUES = SupportMatch.values();
    }
}

