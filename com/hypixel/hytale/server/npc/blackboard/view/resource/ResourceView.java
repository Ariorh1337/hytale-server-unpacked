/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.blackboard.view.resource;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.blackboard.view.BlockRegionView;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class ResourceView
extends BlockRegionView<ResourceView> {
    private final long index;
    private final IntSet[] reservationsBySection = new IntSet[10];
    private final Map<Ref<EntityStore>, BlockReservation> reservationsByEntity = new HashMap<Ref<EntityStore>, BlockReservation>();

    public ResourceView(long index) {
        this.index = index;
    }

    @Override
    public boolean isOutdated(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        return false;
    }

    @Override
    @Nonnull
    public ResourceView getUpdatedView(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        return this;
    }

    @Override
    public void initialiseEntity(@Nonnull Ref<EntityStore> ref, @Nonnull NPCEntity npcComponent) {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void onWorldRemoved() {
    }

    public boolean isBlockReserved(int x, int y, int z) {
        IntSet section = this.reservationsBySection[ResourceView.indexSection(y)];
        if (section == null) {
            return false;
        }
        return section.contains(ResourceView.indexBlock(x, y, z));
    }

    public void reserveBlock(@Nonnull NPCEntity entity, int x, int y, int z) {
        int sectionIndex = ResourceView.indexSection(y);
        IntSet section = this.reservationsBySection[sectionIndex];
        if (section == null) {
            this.reservationsBySection[sectionIndex] = section = new IntOpenHashSet();
        }
        int blockIndex = ResourceView.indexBlock(x, y, z);
        section.add(blockIndex);
        this.reservationsByEntity.put(entity.getReference(), new BlockReservation(sectionIndex, blockIndex));
    }

    public void clearReservation(@Nonnull Ref<EntityStore> ref) {
        BlockReservation reservation = this.reservationsByEntity.remove(ref);
        if (reservation == null) {
            return;
        }
        IntSet section = this.reservationsBySection[reservation.getSectionIndex()];
        section.remove(reservation.getBlockIndex());
    }

    public long getIndex() {
        return this.index;
    }

    @Nonnull
    public Map<Ref<EntityStore>, BlockReservation> getReservationsByEntity() {
        return this.reservationsByEntity;
    }

    public static class BlockReservation {
        private final int sectionIndex;
        private final int blockIndex;

        public BlockReservation(int sectionIndex, int blockIndex) {
            this.sectionIndex = sectionIndex;
            this.blockIndex = blockIndex;
        }

        public int getSectionIndex() {
            return this.sectionIndex;
        }

        public int getBlockIndex() {
            return this.blockIndex;
        }
    }
}

