/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

public enum NetworkChannel {
    Default(0),
    Chunks(1),
    WorldMap(2);

    public static final NetworkChannel[] VALUES;
    public static final int COUNT;
    private final int value;

    private NetworkChannel(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static NetworkChannel fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw new IllegalArgumentException("Invalid network channel: " + value);
    }

    static {
        VALUES = NetworkChannel.values();
        COUNT = VALUES.length;
    }
}

