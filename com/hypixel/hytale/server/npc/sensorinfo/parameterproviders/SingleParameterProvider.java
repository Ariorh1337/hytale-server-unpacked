/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo.parameterproviders;

import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.ParameterProvider;
import javax.annotation.Nonnull;

public abstract class SingleParameterProvider
implements ParameterProvider {
    private final int parameter;

    public SingleParameterProvider(int parameter) {
        this.parameter = parameter;
    }

    @Override
    @Nonnull
    public ParameterProvider getParameterProvider(int parameter) {
        if (this.parameter != parameter) {
            throw new IllegalStateException("Parameter does not match!");
        }
        return this;
    }
}

