/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.beacons;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.random.RandomExtra;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import javax.annotation.Nonnull;

public class InitialBeaconDelay
implements Component<EntityStore> {
    private double loadTimeSpawnDelay;

    public static ComponentType<EntityStore, InitialBeaconDelay> getComponentType() {
        return SpawningPlugin.get().getInitialBeaconDelayComponentType();
    }

    public void setLoadTimeSpawnDelay(double loadTimeSpawnDelay) {
        this.loadTimeSpawnDelay = loadTimeSpawnDelay;
    }

    public boolean tickLoadTimeSpawnDelay(float dt) {
        double d;
        if (this.loadTimeSpawnDelay <= 0.0) {
            return true;
        }
        this.loadTimeSpawnDelay -= (double)dt;
        return d <= 0.0;
    }

    public void setupInitialSpawnDelay(@Nonnull double[] initialSpawnDelay) {
        this.loadTimeSpawnDelay = RandomExtra.randomRange(initialSpawnDelay[0], initialSpawnDelay[1]);
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        InitialBeaconDelay delay = new InitialBeaconDelay();
        delay.setLoadTimeSpawnDelay(this.loadTimeSpawnDelay);
        return delay;
    }
}

