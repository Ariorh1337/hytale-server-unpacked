/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo;

import com.hypixel.hytale.server.npc.instructions.Sensor;
import com.hypixel.hytale.server.npc.sensorinfo.ExtraInfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.IPositionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.ParameterProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WrappedInfoProvider
implements InfoProvider {
    @Nonnull
    private final List<Sensor> sensors;
    @Nullable
    private IPositionProvider positionMatch;
    protected ExtraInfoProvider passedExtraInfo;

    public WrappedInfoProvider() {
        this.sensors = new ObjectArrayList<Sensor>();
    }

    public WrappedInfoProvider(Sensor[] sensors) {
        this.sensors = List.of(sensors);
    }

    @Override
    @Nullable
    public <E extends ExtraInfoProvider> E getExtraInfo(Class<E> clazz) {
        for (int i = 0; i < this.sensors.size(); ++i) {
            E specificInfo;
            InfoProvider info = this.sensors.get(i).getSensorInfo();
            if (info == null || (specificInfo = info.getExtraInfo(clazz)) == null) continue;
            return specificInfo;
        }
        return null;
    }

    @Override
    public <E extends ExtraInfoProvider> void passExtraInfo(E provider) {
        this.passedExtraInfo = provider;
    }

    @Override
    public <E extends ExtraInfoProvider> E getPassedExtraInfo(Class<E> clazz) {
        return (E)this.passedExtraInfo;
    }

    @Override
    public boolean hasPosition() {
        return this.positionMatch != null && this.positionMatch.hasPosition();
    }

    @Override
    @Nullable
    public IPositionProvider getPositionProvider() {
        return this.positionMatch;
    }

    @Override
    @Nullable
    public ParameterProvider getParameterProvider(int parameter) {
        return null;
    }

    public void clearMatches() {
        this.sensors.clear();
    }

    public void addMatch(Sensor sensor) {
        this.sensors.add(sensor);
    }

    public void clearPositionMatch() {
        this.positionMatch = null;
    }

    public void setPositionMatch(IPositionProvider provider) {
        this.positionMatch = provider;
    }
}

