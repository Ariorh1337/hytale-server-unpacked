/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.setup;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum ClientFeature {
    SplitVelocity(0),
    Mantling(1),
    SprintForce(2),
    CrouchSlide(3),
    SafetyRoll(4),
    DisplayHealthBars(5),
    DisplayCombatText(6),
    CanHideHelmet(7),
    CanHideCuirass(8),
    CanHideGauntlets(9),
    CanHidePants(10);

    public static final ClientFeature[] VALUES;
    private final int value;

    private ClientFeature(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ClientFeature fromValue(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        throw ProtocolException.invalidEnumValue("ClientFeature", value);
    }

    static {
        VALUES = ClientFeature.values();
    }
}

