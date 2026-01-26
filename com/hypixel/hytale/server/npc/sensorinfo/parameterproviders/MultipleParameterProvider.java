/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo.parameterproviders;

import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.ParameterProvider;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class MultipleParameterProvider
implements ParameterProvider {
    private final Int2ObjectMap<ParameterProvider> providers = new Int2ObjectOpenHashMap<ParameterProvider>();

    @Override
    public ParameterProvider getParameterProvider(int parameter) {
        return (ParameterProvider)this.providers.get(parameter);
    }

    @Override
    public void clear() {
        this.providers.values().forEach(ParameterProvider::clear);
    }

    public void addParameterProvider(int parameter, ParameterProvider provider) {
        this.providers.put(parameter, provider);
    }
}

