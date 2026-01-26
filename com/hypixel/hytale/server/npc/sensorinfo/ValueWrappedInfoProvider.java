/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo;

import com.hypixel.hytale.server.npc.sensorinfo.ExtraInfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.IPositionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.ParameterProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ValueWrappedInfoProvider
implements InfoProvider {
    @Nullable
    private final InfoProvider wrappedProvider;
    @Nonnull
    private final ParameterProvider parameterProvider;

    public ValueWrappedInfoProvider(@Nullable InfoProvider wrappedProvider, @Nonnull ParameterProvider parameterProvider) {
        this.wrappedProvider = wrappedProvider;
        this.parameterProvider = parameterProvider;
    }

    @Override
    @Nullable
    public IPositionProvider getPositionProvider() {
        return this.wrappedProvider != null ? this.wrappedProvider.getPositionProvider() : null;
    }

    @Override
    @Nullable
    public ParameterProvider getParameterProvider(int parameter) {
        ParameterProvider provider = this.parameterProvider.getParameterProvider(parameter);
        if (provider != null) {
            return provider;
        }
        return this.wrappedProvider != null ? this.wrappedProvider.getParameterProvider(parameter) : null;
    }

    @Override
    @Nullable
    public <E extends ExtraInfoProvider> E getExtraInfo(Class<E> clazz) {
        return this.wrappedProvider != null ? (E)this.wrappedProvider.getExtraInfo(clazz) : null;
    }

    @Override
    public <E extends ExtraInfoProvider> void passExtraInfo(E provider) {
        if (this.wrappedProvider != null) {
            this.wrappedProvider.passExtraInfo(provider);
        }
    }

    @Override
    @Nullable
    public <E extends ExtraInfoProvider> E getPassedExtraInfo(Class<E> clazz) {
        return this.wrappedProvider != null ? (E)this.wrappedProvider.getPassedExtraInfo(clazz) : null;
    }

    @Override
    public boolean hasPosition() {
        return this.wrappedProvider != null && this.wrappedProvider.hasPosition();
    }
}

