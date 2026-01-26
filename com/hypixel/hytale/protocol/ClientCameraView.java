/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum ClientCameraView {
    FirstPerson(0),
    ThirdPerson(1),
    Custom(2);

    public static final ClientCameraView[] VALUES;
    private final int value;

    private ClientCameraView(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ClientCameraView fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("ClientCameraView", value);
    }

    static {
        VALUES = ClientCameraView.values();
    }
}

