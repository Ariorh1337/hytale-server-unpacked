/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.suppression.component;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import com.hypixel.hytale.server.spawning.suppression.component.ChunkSuppressionEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class ChunkSuppressionQueue
implements Resource<ChunkStore> {
    private final List<Map.Entry<Ref<ChunkStore>, ChunkSuppressionEntry>> toAdd = new ObjectArrayList<Map.Entry<Ref<ChunkStore>, ChunkSuppressionEntry>>();
    private final List<Ref<ChunkStore>> toRemove = new ObjectArrayList<Ref<ChunkStore>>();

    public static ResourceType<ChunkStore, ChunkSuppressionQueue> getResourceType() {
        return SpawningPlugin.get().getChunkSuppressionQueueResourceType();
    }

    @Nonnull
    public List<Map.Entry<Ref<ChunkStore>, ChunkSuppressionEntry>> getToAdd() {
        return this.toAdd;
    }

    @Nonnull
    public List<Ref<ChunkStore>> getToRemove() {
        return this.toRemove;
    }

    public void queueForAdd(@Nonnull Ref<ChunkStore> reference, @Nonnull ChunkSuppressionEntry entry) {
        this.toAdd.add(Map.entry(reference, entry));
    }

    public void queueForRemove(Ref<ChunkStore> reference) {
        this.toRemove.add(reference);
    }

    @Override
    @Nonnull
    public Resource<ChunkStore> clone() {
        ChunkSuppressionQueue queue = new ChunkSuppressionQueue();
        queue.toAdd.addAll(this.toAdd);
        queue.toRemove.addAll(this.toRemove);
        return queue;
    }
}

