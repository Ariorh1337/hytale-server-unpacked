/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.flock;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.Flock;
import com.hypixel.hytale.server.flock.FlockMembership;
import com.hypixel.hytale.server.flock.FlockPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import javax.annotation.Nonnull;

public class FlockDeathSystems {

    public static class EntityDeath
    extends DeathSystems.OnDeathSystem {
        @Nonnull
        private final Query<EntityStore> query = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, Query.not(Player.getComponentType()));

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Damage.Source source;
            Role role;
            NPCEntity npcComponent = commandBuffer.getComponent(ref, NPCEntity.getComponentType());
            if (npcComponent != null && (role = npcComponent.getRole()) != null && !role.isCorpseStaysInFlock()) {
                commandBuffer.tryRemoveComponent(ref, FlockMembership.getComponentType());
            }
            Damage damageInfo = component.getDeathInfo();
            Ref<EntityStore> sourceRef = null;
            if (damageInfo != null && (source = damageInfo.getSource()) instanceof Damage.EntitySource) {
                Damage.EntitySource entitySource = (Damage.EntitySource)source;
                sourceRef = entitySource.getRef();
            }
            if (sourceRef == null) {
                return;
            }
            Flock attackerFlock = FlockPlugin.getFlock(commandBuffer, sourceRef);
            if (attackerFlock != null) {
                attackerFlock.onTargetKilled(commandBuffer, ref);
            }
        }
    }

    public static class PlayerDeath
    extends DeathSystems.OnDeathSystem {
        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return Player.getComponentType();
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            commandBuffer.tryRemoveComponent(ref, FlockMembership.getComponentType());
        }
    }
}

