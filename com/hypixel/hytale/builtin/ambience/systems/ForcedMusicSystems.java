/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.ambience.systems;

import com.hypixel.hytale.builtin.ambience.components.AmbienceTracker;
import com.hypixel.hytale.builtin.ambience.resources.AmbienceResource;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.world.UpdateEnvironmentMusic;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ForcedMusicSystems {
    private static final Query<EntityStore> TICK_QUERY = Archetype.of(Player.getComponentType(), PlayerRef.getComponentType(), AmbienceTracker.getComponentType());

    public static class Tick
    extends EntityTickingSystem<EntityStore> {
        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            int desired;
            AmbienceResource ambienceResource = store.getResource(AmbienceResource.getResourceType());
            AmbienceTracker tracker = archetypeChunk.getComponent(index, AmbienceTracker.getComponentType());
            PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
            int have = tracker.getForcedMusicIndex();
            if (have == (desired = ambienceResource.getForcedMusicIndex())) {
                return;
            }
            tracker.setForcedMusicIndex(desired);
            UpdateEnvironmentMusic pooledPacket = tracker.getMusicPacket();
            pooledPacket.environmentIndex = desired;
            playerRef.getPacketHandler().write((Packet)pooledPacket);
        }

        @Override
        @Nullable
        public Query<EntityStore> getQuery() {
            return TICK_QUERY;
        }
    }

    public static class PlayerAdded
    extends HolderSystem<EntityStore> {
        @Override
        public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
            holder.ensureComponent(AmbienceTracker.getComponentType());
        }

        @Override
        public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
            AmbienceTracker ambienceTrackerComponent = holder.getComponent(AmbienceTracker.getComponentType());
            assert (ambienceTrackerComponent != null);
            PlayerRef playerRefComponent = holder.getComponent(PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            UpdateEnvironmentMusic pooledPacket = ambienceTrackerComponent.getMusicPacket();
            pooledPacket.environmentIndex = 0;
            playerRefComponent.getPacketHandler().write((Packet)pooledPacket);
        }

        @Override
        @Nullable
        public Query<EntityStore> getQuery() {
            return Query.and(PlayerRef.getComponentType(), AmbienceTracker.getComponentType());
        }
    }
}

