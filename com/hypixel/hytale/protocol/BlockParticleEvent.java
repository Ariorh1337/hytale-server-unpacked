/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum BlockParticleEvent {
    Walk(0),
    Run(1),
    Sprint(2),
    SoftLand(3),
    HardLand(4),
    MoveOut(5),
    Hit(6),
    Break(7),
    Build(8),
    Physics(9);

    public static final BlockParticleEvent[] VALUES;
    private final int value;

    private BlockParticleEvent(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static BlockParticleEvent fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("BlockParticleEvent", value);
    }

    static {
        VALUES = BlockParticleEvent.values();
    }
}

