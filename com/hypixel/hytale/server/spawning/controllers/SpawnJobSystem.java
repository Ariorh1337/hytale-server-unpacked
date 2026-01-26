/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.controllers;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.ISpawnableWithModel;
import com.hypixel.hytale.server.spawning.SpawningContext;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import com.hypixel.hytale.server.spawning.controllers.SpawnController;
import com.hypixel.hytale.server.spawning.jobs.SpawnJob;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public abstract class SpawnJobSystem<J extends SpawnJob, T extends SpawnController<J>>
extends EntityTickingSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final int JOB_BUDGET = 64;

    protected void tickSpawnJobs(@Nonnull T spawnController, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        World world = store.getExternalData().getWorld();
        if (world.getPlayerCount() == 0 || !world.getWorldConfig().isSpawningNPC() || ((SpawnController)spawnController).isUnspawnable() || world.getChunkStore().getStore().getEntityCount() == 0) {
            return;
        }
        if ((double)((SpawnController)spawnController).getActualNPCs() > ((SpawnController)spawnController).getExpectedNPCs()) {
            return;
        }
        int blockBudget = SpawningPlugin.get().getTickColumnBudget() / world.getTps();
        try {
            while (blockBudget > 0 && ((SpawnController)spawnController).getActiveJobCount() != 0) {
                int jobIndex = 0;
                while (jobIndex >= 0 && jobIndex < ((SpawnController)spawnController).getActiveJobCount() && blockBudget > 0) {
                    Object job = ((SpawnController)spawnController).getSpawnJob(jobIndex);
                    if (job != null) {
                        ((SpawnJob)job).setColumnBudget(Math.min(64, blockBudget));
                        Result result = this.runJob(spawnController, job, commandBuffer);
                        blockBudget -= ((SpawnJob)job).getBudgetUsed();
                        if (result != Result.TRY_AGAIN) {
                            List activeJobs = ((SpawnController)spawnController).getActiveJobs();
                            jobIndex = activeJobs.indexOf(job);
                            if (jobIndex == -1) continue;
                            activeJobs.remove(jobIndex);
                            if (result == Result.PENDING_SPAWN) continue;
                            ((SpawnController)spawnController).addIdleJob(job);
                            continue;
                        }
                        ++jobIndex;
                        continue;
                    }
                    jobIndex = -1;
                }
            }
        }
        catch (Throwable t) {
            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(t)).log("Failed to tick Spawn Jobs: ");
        }
    }

    protected void onStartRun(@Nonnull J spawnJob) {
        ((SpawnJob)spawnJob).setBudgetUsed(0);
    }

    protected abstract void onEndProbing(T var1, J var2, Result var3, ComponentAccessor<EntityStore> var4);

    protected abstract boolean pickSpawnPosition(T var1, J var2, CommandBuffer<EntityStore> var3);

    protected abstract Result trySpawn(T var1, J var2, CommandBuffer<EntityStore> var3);

    protected abstract Result spawn(World var1, T var2, J var3, CommandBuffer<EntityStore> var4);

    protected Result endProbing(T spawnController, @Nonnull J spawnJob, Result result, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (!((SpawnJob)spawnJob).isTerminated()) {
            this.onEndProbing(spawnController, spawnJob, result, componentAccessor);
            ((SpawnJob)spawnJob).reset();
            ((SpawnJob)spawnJob).setTerminated(true);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Result runJob(T spawnController, @Nonnull J spawnJob, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        ISpawnableWithModel spawnable;
        this.onStartRun(spawnJob);
        if (((SpawnJob)spawnJob).shouldTerminate()) {
            return Result.FAILED;
        }
        try {
            spawnable = ((SpawnJob)spawnJob).getSpawnable();
        }
        catch (IllegalArgumentException e) {
            this.endProbing(spawnController, spawnJob, Result.PERMANENT_FAILURE, commandBuffer);
            throw e;
        }
        if (spawnable == null) {
            HytaleLogger.Api context = LOGGER.at(Level.FINEST);
            if (context.isEnabled()) {
                context.log("Spawn job %s: Terminated, spawnable %s gone", ((SpawnJob)spawnJob).getJobId(), (Object)((SpawnJob)spawnJob).getSpawnableName());
            }
            return this.endProbing(spawnController, spawnJob, Result.FAILED, commandBuffer);
        }
        SpawningContext spawningContext = ((SpawnJob)spawnJob).getSpawningContext();
        if (!spawningContext.setSpawnable(spawnable)) {
            HytaleLogger.Api context = LOGGER.at(Level.FINEST);
            if (context.isEnabled()) {
                context.log("Spawn job %s: Terminated, Unable to set spawnable %s", ((SpawnJob)spawnJob).getJobId(), (Object)((SpawnJob)spawnJob).getSpawnableName());
            }
            return this.endProbing(spawnController, spawnJob, Result.FAILED, commandBuffer);
        }
        try {
            while (((SpawnJob)spawnJob).budgetAvailable()) {
                Result context;
                if (((SpawnJob)spawnJob).shouldTerminate()) {
                    LOGGER.at(Level.FINEST).log("Spawn job %s: Terminated", ((SpawnJob)spawnJob).getJobId());
                    context = Result.FAILED;
                    return context;
                }
                if (!this.pickSpawnPosition(spawnController, spawnJob, commandBuffer)) {
                    context = this.endProbing(spawnController, spawnJob, Result.FAILED, commandBuffer);
                    return context;
                }
                Result result = this.trySpawn(spawnController, spawnJob, commandBuffer);
                if (result == Result.TRY_AGAIN) continue;
                Result result2 = result;
                return result2;
            }
        }
        finally {
            spawningContext.release();
        }
        return Result.TRY_AGAIN;
    }

    public static enum Result {
        SUCCESS,
        FAILED,
        TRY_AGAIN,
        PERMANENT_FAILURE,
        PENDING_SPAWN;

    }
}

