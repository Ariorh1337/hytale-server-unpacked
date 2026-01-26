/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo.parameterproviders;

import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.ParameterProvider;

public interface IntParameterProvider
extends ParameterProvider {
    public static final int NOT_PROVIDED = Integer.MIN_VALUE;

    public int getIntParameter();
}

