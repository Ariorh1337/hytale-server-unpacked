/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.local;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import javax.annotation.Nonnull;

public class LocalSpawnController
implements Component<EntityStore> {
    private double timeToNextRunSeconds = SpawningPlugin.get().getLocalSpawnControllerJoinDelay();

    public static ComponentType<EntityStore, LocalSpawnController> getComponentType() {
        return SpawningPlugin.get().getLocalSpawnControllerComponentType();
    }

    public void setTimeToNextRunSeconds(double seconds) {
        this.timeToNextRunSeconds = seconds;
    }

    public boolean tickTimeToNextRunSeconds(float dt) {
        double d;
        this.timeToNextRunSeconds -= (double)dt;
        return d <= 0.0;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        LocalSpawnController controller = new LocalSpawnController();
        controller.timeToNextRunSeconds = this.timeToNextRunSeconds;
        return controller;
    }

    @Nonnull
    public String toString() {
        return "LocalSpawnController{timeToNextRunSeconds=" + this.timeToNextRunSeconds + "}";
    }
}

