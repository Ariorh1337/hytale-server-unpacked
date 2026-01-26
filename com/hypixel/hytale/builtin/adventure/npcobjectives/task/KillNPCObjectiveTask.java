/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.npcobjectives.task;

import com.hypixel.hytale.builtin.adventure.npcobjectives.assets.KillObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.npcobjectives.resources.KillTrackerResource;
import com.hypixel.hytale.builtin.adventure.npcobjectives.task.KillObjectiveTask;
import com.hypixel.hytale.builtin.adventure.npcobjectives.transaction.KillTaskTransaction;
import com.hypixel.hytale.builtin.adventure.objectives.Objective;
import com.hypixel.hytale.builtin.adventure.objectives.transaction.TransactionRecord;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class KillNPCObjectiveTask
extends KillObjectiveTask {
    public static final BuilderCodec<KillNPCObjectiveTask> CODEC = BuilderCodec.builder(KillNPCObjectiveTask.class, KillNPCObjectiveTask::new, KillObjectiveTask.CODEC).build();

    public KillNPCObjectiveTask(@Nonnull KillObjectiveTaskAsset asset, int taskSetIndex, int taskIndex) {
        super(asset, taskSetIndex, taskIndex);
    }

    protected KillNPCObjectiveTask() {
    }

    @Override
    @Nonnull
    protected TransactionRecord[] setup0(@Nonnull Objective objective, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        KillTaskTransaction transaction = new KillTaskTransaction(this, objective, store);
        store.getResource(KillTrackerResource.getResourceType()).watch(transaction);
        return new TransactionRecord[]{transaction};
    }

    @Override
    @Nonnull
    public String toString() {
        return "KillNPCObjectiveTask{} " + super.toString();
    }
}

