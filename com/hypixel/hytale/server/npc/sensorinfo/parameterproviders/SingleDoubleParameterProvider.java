/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo.parameterproviders;

import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.DoubleParameterProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.SingleParameterProvider;

public class SingleDoubleParameterProvider
extends SingleParameterProvider
implements DoubleParameterProvider {
    private double value;

    public SingleDoubleParameterProvider(int parameter) {
        super(parameter);
    }

    @Override
    public double getDoubleParameter() {
        return this.value;
    }

    @Override
    public void clear() {
        this.value = -1.7976931348623157E308;
    }

    public void overrideDouble(double value) {
        this.value = value;
    }
}

