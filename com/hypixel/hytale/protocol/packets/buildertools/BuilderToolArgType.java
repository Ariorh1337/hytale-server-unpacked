/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum BuilderToolArgType {
    Bool(0),
    Float(1),
    Int(2),
    String(3),
    Block(4),
    Mask(5),
    BrushShape(6),
    BrushOrigin(7),
    BrushAxis(8),
    Rotation(9),
    Option(10);

    public static final BuilderToolArgType[] VALUES;
    private final int value;

    private BuilderToolArgType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static BuilderToolArgType fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("BuilderToolArgType", value);
    }

    static {
        VALUES = BuilderToolArgType.values();
    }
}

