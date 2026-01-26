/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.EntityMatcherType;
import com.hypixel.hytale.protocol.FailOnType;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.InteractionChainData;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.SelectedHitEntity;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.EntitySnapshot;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.meta.DynamicMetaStore;
import com.hypixel.hytale.server.core.meta.MetaKey;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.ClientSourcedSelector;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.Selector;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.SelectorType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.PositionUtil;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SelectInteraction
extends SimpleInteraction {
    public static boolean SHOW_VISUAL_DEBUG;
    @Nonnull
    public static SnapshotSource SNAPSHOT_SOURCE;
    @Nonnull
    public static final BuilderCodec<SelectInteraction> CODEC;
    @Nonnull
    public static final MetaKey<IntSet> HIT_ENTITIES;
    @Nonnull
    public static final MetaKey<Set<BlockPosition>> HIT_BLOCKS;
    @Nonnull
    public static final MetaKey<DynamicMetaStore<Interaction>> SELECT_META_STORE;
    private static final MetaKey<Selector> ENTITY_SELECTOR;
    protected SelectorType selector;
    @Nullable
    protected String hitEntity;
    @Nullable
    protected HitEntity[] hitEntityRules;
    @Nullable
    protected String hitBlock;
    @Nonnull
    protected FailOnType failOn = FailOnType.Neither;
    protected boolean ignoreOwner = true;

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Client;
    }

    @Override
    public boolean needsRemoteSync() {
        return true;
    }

    @Override
    protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        boolean checkEntities;
        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        DynamicMetaStore<Interaction> instanceStore = context.getInstanceStore();
        Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
        if (firstRun) {
            Selector selector = this.selector.newSelector();
            if (playerComponent != null && SNAPSHOT_SOURCE == SnapshotSource.CLIENT) {
                selector = new ClientSourcedSelector(selector, context);
            }
            instanceStore.putMetaObject(ENTITY_SELECTOR, selector);
            if ((playerComponent == null || SNAPSHOT_SOURCE != SnapshotSource.CLIENT) && time <= 0.0f && this.getRunTime() > 0.0f) {
                return;
            }
        }
        World world = commandBuffer.getExternalData().getWorld();
        Selector selector = instanceStore.getMetaObject(ENTITY_SELECTOR);
        selector.tick(commandBuffer, context.getEntity(), Math.min(time, this.getRunTime()), this.getRunTime());
        boolean bl = checkEntities = this.hitEntity != null || this.hitEntityRules != null;
        if (checkEntities) {
            IntSet hitEntities = instanceStore.getMetaObject(HIT_ENTITIES);
            selector.selectTargetEntities(commandBuffer, context.getEntity(), (targetRef, hit) -> {
                NetworkId networkIdComponent = targetRef.getStore().getComponent((Ref<EntityStore>)targetRef, NetworkId.getComponentType());
                if (networkIdComponent == null) {
                    return;
                }
                int networkId = networkIdComponent.getId();
                if (!hitEntities.add(networkId)) {
                    return;
                }
                String hitEntity = this.hitEntity;
                if (hitEntity != null) {
                    PlayerSettings playerSettingsComponent;
                    Player targetPlayerComponent;
                    Archetype<EntityStore> archetype = commandBuffer.getArchetype((Ref<EntityStore>)targetRef);
                    boolean targetDead = archetype.contains(DeathComponent.getComponentType());
                    boolean targetInvulnerable = archetype.contains(Invulnerable.getComponentType());
                    if (targetInvulnerable && (targetPlayerComponent = commandBuffer.getComponent((Ref<EntityStore>)targetRef, Player.getComponentType())) != null && targetPlayerComponent.getGameMode() == GameMode.Creative && (playerSettingsComponent = commandBuffer.getComponent((Ref<EntityStore>)targetRef, PlayerSettings.getComponentType())) != null && playerSettingsComponent.creativeSettings().respondToHit()) {
                        targetInvulnerable = false;
                    }
                    if (targetDead || targetInvulnerable || targetRef.equals(ref)) {
                        hitEntity = null;
                    }
                }
                if (this.hitEntityRules != null) {
                    block0: for (HitEntity rule : this.hitEntityRules) {
                        for (EntityMatcher matcher : rule.matchers) {
                            if (!matcher.test(ref, (Ref<EntityStore>)targetRef, commandBuffer)) continue block0;
                        }
                        hitEntity = rule.next;
                    }
                }
                if (hitEntity == null) {
                    return;
                }
                RootInteraction hitEntityInteraction = RootInteraction.getRootInteractionOrUnknown(hitEntity);
                InteractionContext subCtx = context.duplicate();
                DynamicMetaStore<InteractionContext> metaStore = subCtx.getMetaStore();
                metaStore.putMetaObject(TARGET_ENTITY, targetRef);
                metaStore.putMetaObject(HIT_LOCATION, hit);
                metaStore.putMetaObject(SELECT_META_STORE, instanceStore);
                metaStore.removeMetaObject(TARGET_BLOCK);
                metaStore.removeMetaObject(TARGET_BLOCK_RAW);
                if (playerComponent != null && SNAPSHOT_SOURCE == SnapshotSource.CLIENT) {
                    InteractionSyncData currentState = context.getClientState();
                    subCtx.setSnapshotProvider((cBuffer, attacker, targetNetworkId) -> {
                        int attackerNetworkId = cBuffer.getComponent(attacker, NetworkId.getComponentType()).getId();
                        if (targetNetworkId == attackerNetworkId) {
                            return new EntitySnapshot(PositionUtil.toVector3d(currentState.attackerPos), PositionUtil.toRotation(currentState.attackerRot));
                        }
                        for (SelectedHitEntity e : currentState.hitEntities) {
                            if (e.networkId != targetNetworkId) continue;
                            return new EntitySnapshot(PositionUtil.toVector3d(e.position), PositionUtil.toRotation(e.bodyRotation));
                        }
                        throw new IllegalArgumentException("No entity " + targetNetworkId + " in client state");
                    });
                }
                context.fork(new InteractionChainData(), context.getChain().getType(), subCtx, hitEntityInteraction, false);
            }, e -> {
                if (this.ignoreOwner && e.equals(ref)) {
                    return false;
                }
                return !e.equals(context.getEntity());
            });
            if (context.hasLabels() && hitEntities.isEmpty() && context.getState().state == InteractionState.Finished && (this.failOn == FailOnType.Entity || this.failOn == FailOnType.Either)) {
                context.getState().state = InteractionState.Failed;
            }
        }
        if (this.hitBlock != null) {
            Set<BlockPosition> hitBlocks = instanceStore.getMetaObject(HIT_BLOCKS);
            RootInteraction hitBlock = RootInteraction.getRootInteractionOrUnknown(this.hitBlock);
            selector.selectTargetBlocks(commandBuffer, context.getEntity(), (x, y, z) -> {
                BlockPosition rawBlock = new BlockPosition(x, y, z);
                BlockPosition targetBlock = world.getBaseBlock(rawBlock);
                if (!hitBlocks.add(targetBlock)) {
                    return;
                }
                InteractionContext subCtx = context.duplicate();
                DynamicMetaStore<InteractionContext> metaStore = subCtx.getMetaStore();
                metaStore.putMetaObject(TARGET_BLOCK, targetBlock);
                metaStore.putMetaObject(TARGET_BLOCK_RAW, rawBlock);
                metaStore.putMetaObject(SELECT_META_STORE, instanceStore);
                metaStore.removeMetaObject(TARGET_ENTITY);
                context.fork(new InteractionChainData(), context.getChain().getType(), subCtx, hitBlock, false);
            });
            if (context.hasLabels() && hitBlocks.isEmpty() && context.getState().state == InteractionState.Finished && (this.failOn == FailOnType.Block || this.failOn == FailOnType.Either)) {
                context.getState().state = InteractionState.Failed;
            }
        }
        if (playerComponent != null && SNAPSHOT_SOURCE == SnapshotSource.CLIENT && context.getState().state != InteractionState.Failed) {
            context.getState().state = context.getClientState().state;
        }
        super.tick0(firstRun, time, type, context, cooldownHandler);
    }

    @Override
    protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
    }

    @Override
    @Nullable
    public InteractionChain mapForkChain(@Nonnull InteractionContext context, @Nonnull InteractionChainData data) {
        if (data.blockPosition != null) {
            return null;
        }
        Long2ObjectMap<InteractionChain> chains = context.getChain().getForkedChains();
        for (InteractionChain chain : chains.values()) {
            if (chain.getBaseForkedChainId().entryIndex != context.getEntry().getIndex()) continue;
            InteractionChainData otherData = chain.getChainData();
            if (otherData.entityId != data.entityId) continue;
            return chain;
        }
        return null;
    }

    @Override
    @Nonnull
    protected com.hypixel.hytale.protocol.Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.SelectInteraction();
    }

    @Override
    protected void configurePacket(com.hypixel.hytale.protocol.Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.SelectInteraction p = (com.hypixel.hytale.protocol.SelectInteraction)packet;
        p.hitEntity = RootInteraction.getRootInteractionIdOrUnknown(this.hitEntity);
        p.failOn = this.failOn;
        p.ignoreOwner = this.ignoreOwner;
        p.selector = (com.hypixel.hytale.protocol.Selector)this.selector.toPacket();
        if (this.hitEntityRules != null) {
            com.hypixel.hytale.protocol.HitEntity[] protoHits = new com.hypixel.hytale.protocol.HitEntity[this.hitEntityRules.length];
            for (int i = 0; i < this.hitEntityRules.length; ++i) {
                protoHits[i] = this.hitEntityRules[i].toPacket();
            }
            p.hitEntityRules = protoHits;
        }
    }

    @Override
    @Nonnull
    public String toString() {
        return "SelectInteraction{selector=" + String.valueOf(this.selector) + ", hitEntity='" + this.hitEntity + "', hitBlock='" + this.hitBlock + "', ignoreOwner='" + this.ignoreOwner + "'} " + super.toString();
    }

    static {
        SNAPSHOT_SOURCE = SnapshotSource.CLIENT;
        CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SelectInteraction.class, SelectInteraction::new, SimpleInteraction.CODEC).documentation("An interaction that can be used to find entities/blocks within a given area.\n\nThis runs the given `Selector` every tick this interactions runs for, the selector may change the search area over time (based on `RunTime`). e.g. to trace out an arc of a sword swing.\n\nWhen an entity/block is found this interaction will run a set of interactions (as defined by `HitEntity`/`HitBlock`) **per a entity/block**, this will not interrupt the selector and it will continue searching until the select interaction completes.\n\nThis interaction does not wait for any forked interaction chains from `HitEntity`/`HitBlock` to complete before finishing itself.")).appendInherited(new KeyedCodec<SelectorType>("Selector", SelectorType.CODEC), (i, o) -> {
            i.selector = o;
        }, i -> i.selector, (i, p) -> {
            i.selector = p.selector;
        }).documentation("The selector to use to find entities and blocks in an area.\nThe selector may be spread over the duration `RunTime`.").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("HitEntity", RootInteraction.CHILD_ASSET_CODEC), (o, i) -> {
            o.hitEntity = i;
        }, o -> o.hitEntity, (o, p) -> {
            o.hitEntity = p.hitEntity;
        }).documentation("The interactions to fork into when an entity is hit by the selector.\nThe hit entity will be the target of the interaction chain.\n\nAn entity cannot be hit multiple times by a single selector.").addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<T[]>("HitEntityRules", new ArrayCodec<HitEntity>(HitEntity.CODEC, HitEntity[]::new)), (o, i) -> {
            o.hitEntityRules = i;
        }, o -> o.hitEntityRules, (o, p) -> {
            o.hitEntityRules = p.hitEntityRules;
        }).documentation("Tests any hit entity with the given rules, running a fork for the last one matched.\nThis overrides `HitEntity` if any match.").add()).appendInherited(new KeyedCodec("HitBlock", RootInteraction.CHILD_ASSET_CODEC), (o, i) -> {
            o.hitBlock = i;
        }, o -> o.hitBlock, (o, p) -> {
            o.hitBlock = p.hitBlock;
        }).documentation("The interactions to fork into when a block is hit by the selector.\nThe hit block will be the target of the interaction chain.\n\nA block cannot be hit multiple times by a single selector.").addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getValidator().late()).add()).append(new KeyedCodec<FailOnType>("FailOn", new EnumCodec<FailOnType>(FailOnType.class)), (o, v) -> {
            o.failOn = v;
        }, o -> o.failOn).documentation("Changes what causes the Failed case to run").add()).appendInherited(new KeyedCodec<Boolean>("IgnoreOwner", Codec.BOOLEAN), (activationEffects, s) -> {
            activationEffects.ignoreOwner = s;
        }, activationEffects -> activationEffects.ignoreOwner, (activationEffects, parent) -> {
            activationEffects.ignoreOwner = parent.ignoreOwner;
        }).documentation("Determines whether the owner of the affiliated entity should be ignored in the selection.\n\nFor example, ignoring the thrower of a projectile.").add()).build();
        HIT_ENTITIES = META_REGISTRY.registerMetaObject(i -> new IntOpenHashSet());
        HIT_BLOCKS = META_REGISTRY.registerMetaObject(i -> new HashSet());
        SELECT_META_STORE = CONTEXT_META_REGISTRY.registerMetaObject(data -> null);
        ENTITY_SELECTOR = META_REGISTRY.registerMetaObject(data -> null);
    }

    public static enum SnapshotSource {
        SERVER,
        CLIENT;

    }

    public static class HitEntity
    implements NetworkSerializable<com.hypixel.hytale.protocol.HitEntity> {
        @Nonnull
        public static final BuilderCodec<HitEntity> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HitEntity.class, HitEntity::new).appendInherited(new KeyedCodec("Next", RootInteraction.CHILD_ASSET_CODEC), (o, i) -> {
            o.next = i;
        }, o -> o.next, (o, p) -> {
            o.next = p.next;
        }).addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getValidator().late()).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<T[]>("Matchers", new ArrayCodec<EntityMatcher>(EntityMatcher.CODEC, EntityMatcher[]::new)), (o, i) -> {
            o.matchers = i;
        }, o -> o.matchers, (o, p) -> {
            o.matchers = p.matchers;
        }).addValidator(Validators.nonNull()).add()).build();
        protected String next;
        protected EntityMatcher[] matchers;

        @Override
        @Nonnull
        public com.hypixel.hytale.protocol.HitEntity toPacket() {
            com.hypixel.hytale.protocol.EntityMatcher[] protoMatchers = new com.hypixel.hytale.protocol.EntityMatcher[this.matchers.length];
            for (int i = 0; i < this.matchers.length; ++i) {
                protoMatchers[i] = this.matchers[i].toPacket();
            }
            return new com.hypixel.hytale.protocol.HitEntity(RootInteraction.getRootInteractionIdOrUnknown(this.next), protoMatchers);
        }
    }

    public static abstract class EntityMatcher
    implements NetworkSerializable<com.hypixel.hytale.protocol.EntityMatcher> {
        @Nonnull
        public static final CodecMapCodec<EntityMatcher> CODEC = new CodecMapCodec("Type");
        @Nonnull
        public static final BuilderCodec<EntityMatcher> BASE_CODEC = ((BuilderCodec.Builder)BuilderCodec.abstractBuilder(EntityMatcher.class).appendInherited(new KeyedCodec<Boolean>("Invert", Codec.BOOLEAN), (o, i) -> {
            o.invert = i;
        }, o -> o.invert, (o, p) -> {
            o.invert = p.invert;
        }).documentation("Inverts the result of the matcher").add()).build();
        protected boolean invert;

        public final boolean test(@Nonnull Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> targetRef, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            return this.test0(sourceRef, targetRef, commandBuffer) ^ this.invert;
        }

        public abstract boolean test0(@Nonnull Ref<EntityStore> var1, @Nonnull Ref<EntityStore> var2, @Nonnull CommandBuffer<EntityStore> var3);

        @Override
        @Nonnull
        public com.hypixel.hytale.protocol.EntityMatcher toPacket() {
            com.hypixel.hytale.protocol.EntityMatcher packet = new com.hypixel.hytale.protocol.EntityMatcher();
            packet.type = EntityMatcherType.Server;
            packet.invert = this.invert;
            return packet;
        }
    }
}

