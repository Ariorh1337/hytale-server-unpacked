/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum EntityPart {
    Self(0),
    Entity(1),
    PrimaryItem(2),
    SecondaryItem(3);

    public static final EntityPart[] VALUES;
    private final int value;

    private EntityPart(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static EntityPart fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("EntityPart", value);
    }

    static {
        VALUES = EntityPart.values();
    }
}

