/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.controllers;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.controllers.SpawnController;
import com.hypixel.hytale.server.spawning.jobs.SpawnJob;
import javax.annotation.Nonnull;

public abstract class SpawnControllerSystem<J extends SpawnJob, T extends SpawnController<J>>
extends EntityTickingSystem<EntityStore> {
    protected void tickController(@Nonnull T spawnController, @Nonnull Store<EntityStore> store) {
        World world = store.getExternalData().getWorld();
        if (world.getPlayerCount() == 0 || !world.getWorldConfig().isSpawningNPC() || ((SpawnController)spawnController).isUnspawnable() || world.getChunkStore().getStore().getEntityCount() == 0) {
            return;
        }
        if ((double)((SpawnController)spawnController).getActualNPCs() > ((SpawnController)spawnController).getExpectedNPCs()) {
            return;
        }
        this.prepareSpawnJobGeneration(spawnController, store);
        this.createRandomSpawnJobs(spawnController, store);
    }

    protected abstract void prepareSpawnJobGeneration(T var1, ComponentAccessor<EntityStore> var2);

    protected void createRandomSpawnJobs(@Nonnull T spawnController, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        while (((SpawnController)spawnController).getActiveJobCount() < ((SpawnController)spawnController).getMaxActiveJobs() && ((SpawnController)spawnController).createRandomSpawnJob(componentAccessor) != null) {
        }
    }
}

