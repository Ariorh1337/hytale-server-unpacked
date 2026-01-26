/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum ParticleScaleRatioConstraint {
    OneToOne(0),
    Preserved(1),
    None(2);

    public static final ParticleScaleRatioConstraint[] VALUES;
    private final int value;

    private ParticleScaleRatioConstraint(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ParticleScaleRatioConstraint fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("ParticleScaleRatioConstraint", value);
    }

    static {
        VALUES = ParticleScaleRatioConstraint.values();
    }
}

