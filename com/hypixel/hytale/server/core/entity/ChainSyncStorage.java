/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionEntry;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ChainSyncStorage {
    public InteractionState getClientState();

    public void setClientState(InteractionState var1);

    @Nullable
    public InteractionEntry getInteraction(int var1);

    public void putInteractionSyncData(int var1, InteractionSyncData var2);

    public void updateSyncPosition(int var1);

    public boolean isSyncDataOutOfOrder(int var1);

    public void syncFork(@Nonnull Ref<EntityStore> var1, @Nonnull InteractionManager var2, @Nonnull SyncInteractionChain var3);

    public void clearInteractionSyncData(int var1);
}

