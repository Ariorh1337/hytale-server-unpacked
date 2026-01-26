/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum ParticleRotationInfluence {
    None(0),
    Billboard(1),
    BillboardY(2),
    BillboardVelocity(3),
    Velocity(4);

    public static final ParticleRotationInfluence[] VALUES;
    private final int value;

    private ParticleRotationInfluence(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ParticleRotationInfluence fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("ParticleRotationInfluence", value);
    }

    static {
        VALUES = ParticleRotationInfluence.values();
    }
}

