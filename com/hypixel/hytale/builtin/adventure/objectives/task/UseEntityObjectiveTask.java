/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.task;

import com.hypixel.hytale.builtin.adventure.objectives.DialogPage;
import com.hypixel.hytale.builtin.adventure.objectives.Objective;
import com.hypixel.hytale.builtin.adventure.objectives.ObjectiveDataStore;
import com.hypixel.hytale.builtin.adventure.objectives.ObjectivePlugin;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.UseEntityObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.task.CountObjectiveTask;
import com.hypixel.hytale.builtin.adventure.objectives.transaction.TransactionRecord;
import com.hypixel.hytale.builtin.adventure.objectives.transaction.UseEntityTransactionRecord;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;

public class UseEntityObjectiveTask
extends CountObjectiveTask {
    public static final BuilderCodec<UseEntityObjectiveTask> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(UseEntityObjectiveTask.class, UseEntityObjectiveTask::new, CountObjectiveTask.CODEC).append(new KeyedCodec<T[]>("NpcUUIDs", new ArrayCodec<UUID>(Codec.UUID_BINARY, UUID[]::new)), (useEntityObjectiveTask, uuids) -> {
        useEntityObjectiveTask.npcUUIDs.clear();
        Collections.addAll(useEntityObjectiveTask.npcUUIDs, uuids);
    }, useEntityObjectiveTask -> (UUID[])useEntityObjectiveTask.npcUUIDs.toArray(UUID[]::new)).add()).build();
    @Nonnull
    protected Set<UUID> npcUUIDs = new HashSet<UUID>();

    public UseEntityObjectiveTask(@Nonnull UseEntityObjectiveTaskAsset asset, int taskSetIndex, int taskIndex) {
        super(asset, taskSetIndex, taskIndex);
    }

    protected UseEntityObjectiveTask() {
    }

    @Override
    @Nonnull
    public UseEntityObjectiveTaskAsset getAsset() {
        return (UseEntityObjectiveTaskAsset)super.getAsset();
    }

    @Override
    @Nonnull
    protected TransactionRecord[] setup0(@Nonnull Objective objective, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        UUID objectiveUUID = objective.getObjectiveUUID();
        ObjectiveDataStore objectiveDataStore = ObjectivePlugin.get().getObjectiveDataStore();
        String taskId = this.getAsset().getTaskId();
        for (UUID playerUUID : objective.getActivePlayerUUIDs()) {
            objectiveDataStore.addEntityTaskForPlayer(playerUUID, taskId, objectiveUUID);
        }
        return TransactionRecord.appendTransaction(null, new UseEntityTransactionRecord(objectiveUUID, taskId));
    }

    public boolean increaseTaskCompletion(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, int qty, @Nonnull Objective objective, @Nonnull PlayerRef playerRef, UUID npcUUID) {
        UseEntityObjectiveTaskAsset.DialogOptions dialogOptions;
        if (!this.npcUUIDs.add(npcUUID)) {
            playerRef.sendMessage(Message.translation("server.modules.objective.task.alreadyInteractedWithNPC"));
            return false;
        }
        super.increaseTaskCompletion(store, ref, qty, objective);
        if (this.isComplete() && (dialogOptions = this.getAsset().getDialogOptions()) != null) {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            playerRef.sendMessage(Message.join(Message.translation(dialogOptions.getEntityNameKey()), Message.raw(": "), Message.translation(dialogOptions.getDialogKey())));
            playerComponent.getPageManager().openCustomPage(ref, store, new DialogPage(playerRef, dialogOptions));
        }
        return true;
    }

    @Override
    @Nonnull
    public String toString() {
        return "UseEntityObjectiveTask{npcUUIDs=" + String.valueOf(this.npcUUIDs) + "} " + super.toString();
    }
}

