/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.systems;

import com.hypixel.hytale.common.thread.ticking.Tickable;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.logger.sentry.SkipSentryException;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entity.component.ActiveAnimationComponent;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.FromPrefab;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsSystems;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.modules.physics.systems.PhysicsValuesAddSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderInfo;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.EventSlotMapper;
import com.hypixel.hytale.server.npc.blackboard.view.event.block.BlockEventType;
import com.hypixel.hytale.server.npc.blackboard.view.event.entity.EntityEventType;
import com.hypixel.hytale.server.npc.components.FailedSpawnComponent;
import com.hypixel.hytale.server.npc.components.Timers;
import com.hypixel.hytale.server.npc.components.messaging.BeaconSupport;
import com.hypixel.hytale.server.npc.components.messaging.NPCBlockEventSupport;
import com.hypixel.hytale.server.npc.components.messaging.NPCEntityEventSupport;
import com.hypixel.hytale.server.npc.components.messaging.PlayerBlockEventSupport;
import com.hypixel.hytale.server.npc.components.messaging.PlayerEntityEventSupport;
import com.hypixel.hytale.server.npc.decisionmaker.stateevaluator.StateEvaluator;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.SpawnEffect;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.valuestore.ValueStore;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class RoleBuilderSystem
extends HolderSystem<EntityStore> {
    @Nonnull
    private final ComponentType<EntityStore, NPCEntity> npcComponentType;
    @Nonnull
    private final ComponentType<EntityStore, TransformComponent> transformComponentType = TransformComponent.getComponentType();
    @Nonnull
    private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();
    @Nonnull
    private final ComponentType<EntityStore, PersistentModel> persistentModelComponentType = PersistentModel.getComponentType();
    @Nonnull
    private final Set<Dependency<EntityStore>> dependencies;
    @Nonnull
    private final Query<EntityStore> query;

    public RoleBuilderSystem() {
        this.npcComponentType = NPCEntity.getComponentType();
        this.dependencies = Set.of(new SystemDependency(Order.AFTER, EntityStatsSystems.Setup.class), new SystemDependency(Order.AFTER, PhysicsValuesAddSystem.class));
        this.query = Archetype.of(this.npcComponentType, this.transformComponentType);
    }

    @Override
    @Nonnull
    public Set<Dependency<EntityStore>> getDependencies() {
        return this.dependencies;
    }

    @Override
    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }

    @Override
    public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
        boolean spawnedOrPrefab;
        ValueStore.Builder valueStoreBuilder;
        StateEvaluator stateEvaluator;
        Tickable[] timers;
        EventSlotMapper<Enum> npcEventSlotMapper;
        EventSlotMapper<Enum> playerEventSlotMapper;
        Object2IntMap<String> beaconSlotMappings;
        Role role;
        BuilderInfo builderInfo;
        Builder<Role> roleBuilder;
        NPCEntity npcComponent = holder.getComponent(this.npcComponentType);
        assert (npcComponent != null);
        if (npcComponent.getRole() != null) {
            return;
        }
        NPCPlugin npcPlugin = NPCPlugin.get();
        int roleIndex = npcComponent.getRoleIndex();
        if (roleIndex == Integer.MIN_VALUE) {
            String roleName = npcComponent.getRoleName();
            roleIndex = npcPlugin.getIndex(roleName);
            if (roleIndex < 0) {
                this.fail(holder);
                npcPlugin.getLogger().at(Level.SEVERE).log("Reloading nonexistent role %s!", roleName);
                return;
            }
            if (npcPlugin.tryGetCachedValidRole(roleIndex) == null) {
                this.fail(holder);
                npcPlugin.getLogger().at(Level.SEVERE).log("Reloading invalid role %s!", roleName);
                return;
            }
            npcComponent.setRoleIndex(roleIndex);
        }
        if (!(roleBuilder = (builderInfo = npcPlugin.prepareRoleBuilderInfo(roleIndex)).getBuilder()).isSpawnable()) {
            this.fail(holder);
            npcPlugin.getLogger().at(Level.SEVERE).log("Attempting to spawn un-spawnable (abstract) role: %s", npcComponent.getRoleName());
            return;
        }
        BuilderSupport builderSupport = new BuilderSupport(npcPlugin.getBuilderManager(), npcComponent, holder, new ExecutionContext(), roleBuilder, null);
        try {
            role = NPCPlugin.buildRole(roleBuilder, builderInfo, builderSupport, roleIndex);
        }
        catch (SkipSentryException e) {
            this.fail(holder);
            npcPlugin.getLogger().at(Level.SEVERE).log("Error: %s for NPC %s", (Object)e.getMessage(), (Object)npcComponent.getRole());
            return;
        }
        npcComponent.setRole(role);
        if (role.isInvulnerable()) {
            holder.ensureComponent(Invulnerable.getComponentType());
        }
        Message roleNameMessage = Message.translation(role.getNameTranslationKey());
        holder.putComponent(DisplayNameComponent.getComponentType(), new DisplayNameComponent(roleNameMessage));
        Interactions interactionsComponent = holder.ensureAndGetComponent(Interactions.getComponentType());
        interactionsComponent.setInteractionId(InteractionType.Use, "*UseNPC");
        if (role.getDeathInteraction() != null) {
            interactionsComponent.setInteractionId(InteractionType.Death, role.getDeathInteraction());
        }
        if ((beaconSlotMappings = builderSupport.getBeaconSlotMappings()) != null) {
            BeaconSupport beaconSupport = new BeaconSupport();
            beaconSupport.initialise(beaconSlotMappings);
            holder.putComponent(BeaconSupport.getComponentType(), beaconSupport);
        }
        if (builderSupport.hasBlockEventSupport()) {
            playerEventSlotMapper = builderSupport.getPlayerBlockEventSlotMapper();
            if (playerEventSlotMapper != null) {
                PlayerBlockEventSupport playerBlockEventSupport = new PlayerBlockEventSupport();
                playerBlockEventSupport.initialise(playerEventSlotMapper.getEventSlotMappings(), playerEventSlotMapper.getEventSlotRanges(), playerEventSlotMapper.getEventSlotCount());
                holder.putComponent(PlayerBlockEventSupport.getComponentType(), playerBlockEventSupport);
            }
            if ((npcEventSlotMapper = builderSupport.getNPCBlockEventSlotMapper()) != null) {
                NPCBlockEventSupport npcBlockEventSupport = new NPCBlockEventSupport();
                npcBlockEventSupport.initialise(npcEventSlotMapper.getEventSlotMappings(), npcEventSlotMapper.getEventSlotRanges(), npcEventSlotMapper.getEventSlotCount());
                holder.putComponent(NPCBlockEventSupport.getComponentType(), npcBlockEventSupport);
            }
            for (int i = 0; i < BlockEventType.VALUES.length; ++i) {
                BlockEventType type = BlockEventType.VALUES[i];
                IntSet sets = builderSupport.getBlockChangeSets(type);
                if (sets == null) continue;
                npcComponent.addBlackboardBlockChangeSets(type, sets);
            }
        }
        if (builderSupport.hasEntityEventSupport()) {
            playerEventSlotMapper = builderSupport.getPlayerEntityEventSlotMapper();
            if (playerEventSlotMapper != null) {
                PlayerEntityEventSupport playerEntityEventSupport = new PlayerEntityEventSupport();
                playerEntityEventSupport.initialise(playerEventSlotMapper.getEventSlotMappings(), playerEventSlotMapper.getEventSlotRanges(), playerEventSlotMapper.getEventSlotCount());
                holder.putComponent(PlayerEntityEventSupport.getComponentType(), playerEntityEventSupport);
            }
            if ((npcEventSlotMapper = builderSupport.getNPCEntityEventSlotMapper()) != null) {
                NPCEntityEventSupport npcEntityEventSupport = new NPCEntityEventSupport();
                npcEntityEventSupport.initialise(npcEventSlotMapper.getEventSlotMappings(), npcEventSlotMapper.getEventSlotRanges(), npcEventSlotMapper.getEventSlotCount());
                holder.putComponent(NPCEntityEventSupport.getComponentType(), npcEntityEventSupport);
            }
            for (EntityEventType type : EntityEventType.VALUES) {
                IntSet sets = builderSupport.getEventNPCGroups(type);
                if (sets == null) continue;
                npcComponent.addBlackboardEntityEventSets(type, sets);
            }
        }
        if ((timers = builderSupport.allocateTimers()) != null) {
            holder.putComponent(Timers.getComponentType(), new Timers(timers));
        }
        if ((stateEvaluator = builderSupport.getStateEvaluator()) != null) {
            holder.putComponent(StateEvaluator.getComponentType(), stateEvaluator);
        }
        if ((valueStoreBuilder = builderSupport.getValueStoreBuilder()) != null) {
            holder.putComponent(ValueStore.getComponentType(), valueStoreBuilder.build());
        }
        holder.ensureComponent(EffectControllerComponent.getComponentType());
        holder.ensureComponent(ActiveAnimationComponent.getComponentType());
        boolean fromPrefab = holder.getArchetype().contains(FromPrefab.getComponentType());
        boolean bl = spawnedOrPrefab = reason.equals((Object)AddReason.SPAWN) || fromPrefab;
        if (spawnedOrPrefab) {
            ModelComponent modelComponent = holder.getComponent(this.modelComponentType);
            if (modelComponent == null) {
                String appearance = role.getAppearanceName();
                if (appearance == null || appearance.isEmpty()) {
                    this.fail(holder);
                    npcPlugin.getLogger().at(Level.SEVERE).log("Appearance can't be initially empty for role %s", npcComponent.getRoleName());
                    return;
                }
                ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(appearance);
                if (modelAsset == null) {
                    this.fail(holder);
                    npcPlugin.getLogger().at(Level.SEVERE).log("Model asset not found: %s for role %s", (Object)appearance, (Object)npcComponent.getRoleName());
                    return;
                }
                float scale = modelAsset.generateRandomScale();
                npcComponent.setInitialModelScale(scale);
                Model scaledModel = Model.createScaledModel(modelAsset, scale);
                holder.putComponent(this.persistentModelComponentType, new PersistentModel(scaledModel.toReference()));
                holder.putComponent(this.modelComponentType, new ModelComponent(scaledModel));
            }
            role.spawned(holder, npcComponent);
            if (roleBuilder instanceof SpawnEffect) {
                SpawnEffect spawnEffect = (SpawnEffect)((Object)roleBuilder);
                TransformComponent transformComponent = holder.getComponent(this.transformComponentType);
                assert (transformComponent != null);
                spawnEffect.spawnEffect(transformComponent.getPosition(), transformComponent.getRotation(), store);
            }
        }
    }

    @Override
    public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
    }

    private void fail(@Nonnull Holder<EntityStore> holder) {
        Archetype<EntityStore> archetype = holder.getArchetype();
        if (archetype == null) {
            return;
        }
        for (int i = archetype.getMinIndex(); i < archetype.length(); ++i) {
            ComponentType<EntityStore, ?> componentType = archetype.get(i);
            if (componentType == null) continue;
            holder.removeComponent(componentType);
        }
        holder.ensureComponent(FailedSpawnComponent.getComponentType());
    }
}

