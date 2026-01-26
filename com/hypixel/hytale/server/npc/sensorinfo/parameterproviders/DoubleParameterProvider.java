/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo.parameterproviders;

import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.ParameterProvider;

public interface DoubleParameterProvider
extends ParameterProvider {
    public static final double NOT_PROVIDED = -1.7976931348623157E308;

    public double getDoubleParameter();
}

