/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum ItemGridInfoDisplayMode {
    Tooltip(0),
    Adjacent(1),
    None(2);

    public static final ItemGridInfoDisplayMode[] VALUES;
    private final int value;

    private ItemGridInfoDisplayMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ItemGridInfoDisplayMode fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("ItemGridInfoDisplayMode", value);
    }

    static {
        VALUES = ItemGridInfoDisplayMode.values();
    }
}

