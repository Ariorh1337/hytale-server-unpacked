/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum NotificationStyle {
    Default(0),
    Danger(1),
    Warning(2),
    Success(3);

    public static final NotificationStyle[] VALUES;
    private final int value;

    private NotificationStyle(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static NotificationStyle fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("NotificationStyle", value);
    }

    static {
        VALUES = NotificationStyle.values();
    }
}

