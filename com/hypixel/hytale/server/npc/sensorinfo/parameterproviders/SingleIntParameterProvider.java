/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo.parameterproviders;

import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.IntParameterProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.SingleParameterProvider;

public class SingleIntParameterProvider
extends SingleParameterProvider
implements IntParameterProvider {
    private int value;

    public SingleIntParameterProvider(int parameter) {
        super(parameter);
    }

    @Override
    public int getIntParameter() {
        return this.value;
    }

    @Override
    public void clear() {
        this.value = Integer.MIN_VALUE;
    }

    public void overrideInt(int value) {
        this.value = value;
    }
}

