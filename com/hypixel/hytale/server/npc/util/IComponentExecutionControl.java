/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util;

public interface IComponentExecutionControl {
    public boolean processDelay(float var1);

    public void clearOnce();

    public void setOnce();

    public boolean isTriggered();
}

