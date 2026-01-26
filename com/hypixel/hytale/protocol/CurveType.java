/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum CurveType {
    Linear(0),
    QuartIn(1),
    QuartOut(2),
    QuartInOut(3);

    public static final CurveType[] VALUES;
    private final int value;

    private CurveType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static CurveType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("CurveType", value);
    }

    static {
        VALUES = CurveType.values();
    }
}

