/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.event;

public enum EventPriority {
    FIRST(-21844),
    EARLY(-10922),
    NORMAL(0),
    LATE(10922),
    LAST(21844);

    private final short value;

    private EventPriority(short value) {
        this.value = value;
    }

    public short getValue() {
        return this.value;
    }
}

