/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.plugin;

public enum PluginState {
    NONE,
    SETUP,
    START,
    ENABLED,
    SHUTDOWN,
    DISABLED,
    FAILED;


    public boolean isInactive() {
        return switch (this.ordinal()) {
            case 0, 5, 6 -> true;
            default -> false;
        };
    }
}

