/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.dependency;

public enum OrderPriority {
    CLOSEST(-1431655764),
    CLOSE(-715827882),
    NORMAL(0),
    FURTHER(0x2AAAAAAA),
    FURTHEST(0x55555554);

    private final int value;

    private OrderPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}

