/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo;

import com.hypixel.hytale.server.npc.sensorinfo.ExtraInfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.ParameterProvider;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public abstract class InfoProviderBase
implements InfoProvider {
    protected final ParameterProvider parameterProvider;
    @Nullable
    protected final Map<Class<? extends ExtraInfoProvider>, ExtraInfoProvider> extraProviders;
    protected ExtraInfoProvider passedExtraInfo;

    public InfoProviderBase() {
        this(null);
    }

    public InfoProviderBase(ParameterProvider parameterProvider) {
        this.parameterProvider = parameterProvider;
        this.extraProviders = null;
    }

    public InfoProviderBase(ParameterProvider parameterProvider, ExtraInfoProvider ... providers) {
        this.parameterProvider = parameterProvider;
        this.extraProviders = new HashMap<Class<? extends ExtraInfoProvider>, ExtraInfoProvider>();
        for (ExtraInfoProvider provider : providers) {
            if (this.extraProviders.put(provider.getType(), provider) == null) continue;
            throw new IllegalArgumentException("More than one type of " + provider.getType().getSimpleName() + " provider registered!");
        }
    }

    @Override
    @Nullable
    public ParameterProvider getParameterProvider(int parameter) {
        if (this.parameterProvider == null) {
            return null;
        }
        return this.parameterProvider.getParameterProvider(parameter);
    }

    @Override
    @Nullable
    public <E extends ExtraInfoProvider> E getExtraInfo(Class<E> clazz) {
        if (this.extraProviders == null) {
            return null;
        }
        return (E)this.extraProviders.get(clazz);
    }

    @Override
    public <E extends ExtraInfoProvider> void passExtraInfo(E provider) {
        this.passedExtraInfo = provider;
    }

    @Override
    public <E extends ExtraInfoProvider> E getPassedExtraInfo(Class<E> clazz) {
        return (E)this.passedExtraInfo;
    }
}

