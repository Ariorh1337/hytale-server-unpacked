/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo;

import com.hypixel.hytale.server.npc.sensorinfo.ExtraInfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.IPositionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.ParameterProvider;
import javax.annotation.Nullable;

public interface InfoProvider {
    @Nullable
    public IPositionProvider getPositionProvider();

    @Nullable
    public ParameterProvider getParameterProvider(int var1);

    @Nullable
    public <E extends ExtraInfoProvider> E getExtraInfo(Class<E> var1);

    public <E extends ExtraInfoProvider> void passExtraInfo(E var1);

    @Nullable
    public <E extends ExtraInfoProvider> E getPassedExtraInfo(Class<E> var1);

    public boolean hasPosition();
}

