/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo.parameterproviders;

import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.SingleParameterProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.StringParameterProvider;
import javax.annotation.Nullable;

public class SingleStringParameterProvider
extends SingleParameterProvider
implements StringParameterProvider {
    @Nullable
    private String value;

    public SingleStringParameterProvider(int parameter) {
        super(parameter);
    }

    @Override
    @Nullable
    public String getStringParameter() {
        return this.value;
    }

    @Override
    public void clear() {
        this.value = null;
    }

    public void overrideString(String value) {
        this.value = value;
    }
}

