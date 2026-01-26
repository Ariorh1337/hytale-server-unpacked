/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum BlockSoundEvent {
    Walk(0),
    Land(1),
    MoveIn(2),
    MoveOut(3),
    Hit(4),
    Break(5),
    Build(6),
    Clone(7),
    Harvest(8);

    public static final BlockSoundEvent[] VALUES;
    private final int value;

    private BlockSoundEvent(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static BlockSoundEvent fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("BlockSoundEvent", value);
    }

    static {
        VALUES = BlockSoundEvent.values();
    }
}

