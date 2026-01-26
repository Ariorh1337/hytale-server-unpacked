/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum Page {
    None(0),
    Bench(1),
    Inventory(2),
    ToolsSettings(3),
    Map(4),
    MachinimaEditor(5),
    ContentCreation(6),
    Custom(7);

    public static final Page[] VALUES;
    private final int value;

    private Page(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static Page fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("Page", value);
    }

    static {
        VALUES = Page.values();
    }
}

