/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.entities;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.random.RandomExtra;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.ApplicationEffects;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.ActiveAnimationComponent;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.blackboard.Blackboard;
import com.hypixel.hytale.server.npc.blackboard.view.blocktype.BlockTypeView;
import com.hypixel.hytale.server.npc.blackboard.view.event.EntityEventNotification;
import com.hypixel.hytale.server.npc.blackboard.view.event.EventNotification;
import com.hypixel.hytale.server.npc.blackboard.view.event.block.BlockEventType;
import com.hypixel.hytale.server.npc.blackboard.view.event.block.BlockEventView;
import com.hypixel.hytale.server.npc.blackboard.view.event.entity.EntityEventType;
import com.hypixel.hytale.server.npc.blackboard.view.event.entity.EntityEventView;
import com.hypixel.hytale.server.npc.components.messaging.EntityEventSupport;
import com.hypixel.hytale.server.npc.components.messaging.EventSupport;
import com.hypixel.hytale.server.npc.components.messaging.NPCBlockEventSupport;
import com.hypixel.hytale.server.npc.components.messaging.NPCEntityEventSupport;
import com.hypixel.hytale.server.npc.components.messaging.PlayerBlockEventSupport;
import com.hypixel.hytale.server.npc.components.messaging.PlayerEntityEventSupport;
import com.hypixel.hytale.server.npc.entities.PathManager;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
import com.hypixel.hytale.server.npc.storage.AlarmStore;
import com.hypixel.hytale.server.npc.util.DamageData;
import com.hypixel.hytale.server.spawning.assets.spawns.config.WorldNPCSpawn;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NPCEntity
extends LivingEntity
implements INonPlayerCharacter {
    public static final BuilderCodec<NPCEntity> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(NPCEntity.class, NPCEntity::new, LivingEntity.CODEC).addField(new KeyedCodec<String>("Env", Codec.STRING), (npcEntity, s) -> {
        npcEntity.environmentIndex = Environment.getAssetMap().getIndex((String)s);
    }, npcEntity -> {
        Environment environment = Environment.getAssetMap().getAssetOrDefault(npcEntity.environmentIndex, null);
        return environment != null ? environment.getId() : null;
    })).addField(new KeyedCodec<Double>("HvrPhs", Codec.DOUBLE), (npcEntity, d) -> {
        npcEntity.hoverPhase = d.floatValue();
    }, npcEntity -> npcEntity.hoverPhase)).addField(new KeyedCodec<Double>("HvrHght", Codec.DOUBLE), (npcEntity, d) -> {
        npcEntity.hoverHeight = d;
    }, npcEntity -> npcEntity.hoverHeight)).addField(new KeyedCodec<String>("SpawnName", Codec.STRING), (npcEntity, s) -> {
        npcEntity.spawnRoleName = s;
        npcEntity.spawnRoleIndex = NPCPlugin.get().getIndex((String)s);
    }, npcEntity -> npcEntity.spawnRoleName)).addField(new KeyedCodec<Double>("MdlScl", Codec.DOUBLE), (npcEntity, d) -> {
        npcEntity.initialModelScale = d.floatValue();
    }, npcEntity -> npcEntity.initialModelScale)).addField(new KeyedCodec<String>("SpawnConfig", Codec.STRING), (npcEntity, s) -> {
        npcEntity.spawnConfigurationName = s;
        npcEntity.spawnConfigurationIndex = WorldNPCSpawn.getAssetMap().getIndex((String)s);
    }, npcEntity -> npcEntity.spawnConfigurationName)).addField(new KeyedCodec("SpawnInstant", Codec.INSTANT), (npcEntity, instant) -> {
        npcEntity.spawnInstant = instant;
    }, npcEntity -> npcEntity.spawnInstant)).append(new KeyedCodec<AlarmStore>("AlarmStore", AlarmStore.CODEC), (npcEntity, alarmStore) -> {
        npcEntity.alarmStore = alarmStore;
    }, npcEntity -> npcEntity.alarmStore).add()).addField(new KeyedCodec<Integer>("WorldgenId", Codec.INTEGER), (npcEntity, i) -> {
        npcEntity.worldgenId = i;
    }, npcEntity -> npcEntity.worldgenId)).append(new KeyedCodec<PathManager>("PathManager", PathManager.CODEC), (npcEntity, manager) -> {
        npcEntity.pathManager = manager;
    }, npcEntity -> npcEntity.pathManager).add()).addField(new KeyedCodec<Vector3d>("LeashPos", Vector3d.CODEC), (npcEntity, v) -> {
        npcEntity.leashPoint.assign((Vector3d)v);
        npcEntity.hasLeashPosition = true;
    }, npcEntity -> npcEntity.requiresLeashPosition() ? npcEntity.leashPoint : null)).addField(new KeyedCodec<Double>("LeashHdg", Codec.DOUBLE), (npcEntity, v) -> {
        npcEntity.leashHeading = v.floatValue();
    }, npcEntity -> npcEntity.requiresLeashPosition() ? Double.valueOf(npcEntity.leashHeading) : null)).addField(new KeyedCodec<Double>("LeashPtch", Codec.DOUBLE), (npcEntity, v) -> {
        npcEntity.leashPitch = v.floatValue();
    }, npcEntity -> npcEntity.requiresLeashPosition() ? Double.valueOf(npcEntity.leashPitch) : null)).addField(new KeyedCodec<String>("RoleName", Codec.STRING), (npcEntity, s) -> {
        npcEntity.roleName = s;
    }, npcEntity -> npcEntity.roleName)).build();
    private String roleName;
    private int roleIndex = Integer.MIN_VALUE;
    @Nullable
    private Role role = null;
    private int spawnRoleIndex = Integer.MIN_VALUE;
    @Nullable
    private String spawnRoleName;
    @Nullable
    private String spawnConfigurationName;
    private int environmentIndex = Integer.MIN_VALUE;
    private int spawnConfigurationIndex = Integer.MIN_VALUE;
    private boolean isSpawnTracked;
    private boolean isDespawning;
    private boolean isPlayingDespawnAnim;
    private float despawnRemainingSeconds;
    private float despawnCheckRemainingSeconds = RandomExtra.randomRange(1.0f, 5.0f);
    private float despawnAnimationRemainingSeconds;
    private float cachedEntityHorizontalSpeedMultiplier = Float.MAX_VALUE;
    private final Vector3d leashPoint = new Vector3d();
    private float leashHeading;
    private float leashPitch;
    private boolean hasLeashPosition;
    private float hoverPhase;
    private double hoverHeight;
    private float initialModelScale = 1.0f;
    private Instant spawnInstant;
    @Nonnull
    private PathManager pathManager = new PathManager();
    private final DamageData damageData = new DamageData();
    @Nullable
    private BlockTypeView blackboardBlockTypeView;
    private IntList blackboardBlockTypeSets;
    private BlockEventView blackboardBlockChangeView;
    private Map<BlockEventType, IntSet> blackboardBlockChangeSets;
    private EntityEventView blackboardEntityEventView;
    private Map<EntityEventType, IntSet> blackboardEntityEventSets;
    private AlarmStore alarmStore;
    @Deprecated(forRemoval=true)
    private int worldgenId = 0;
    @Nonnull
    private final Set<UUID> reservedBy = new HashSet<UUID>();
    private final Vector3d oldPosition = new Vector3d();

    @Nullable
    public static ComponentType<EntityStore, NPCEntity> getComponentType() {
        return EntityModule.get().getComponentType(NPCEntity.class);
    }

    public NPCEntity() {
    }

    public NPCEntity(@Nonnull World world) {
        super(world);
    }

    @Nonnull
    public AlarmStore getAlarmStore() {
        if (this.alarmStore == null) {
            this.alarmStore = new AlarmStore();
        }
        return this.alarmStore;
    }

    @Override
    @Nonnull
    protected Inventory createDefaultInventory() {
        return new Inventory(0, Inventory.DEFAULT_ARMOR_CAPACITY, 3, 0, 0);
    }

    @Nullable
    public Role getRole() {
        return this.role;
    }

    public void invalidateCachedHorizontalSpeedMultiplier() {
        this.cachedEntityHorizontalSpeedMultiplier = Float.MAX_VALUE;
    }

    public void storeTickStartPosition(@Nonnull Vector3d position) {
        this.oldPosition.assign(position);
    }

    public boolean tickDespawnAnimationRemainingSeconds(float dt) {
        float f;
        this.despawnAnimationRemainingSeconds -= dt;
        return f <= 0.0f;
    }

    public void setDespawnAnimationRemainingSeconds(float seconds) {
        this.despawnAnimationRemainingSeconds = seconds;
    }

    public boolean tickDespawnRemainingSeconds(float dt) {
        float f;
        this.despawnRemainingSeconds -= dt;
        return f <= 0.0f;
    }

    public void setDespawnRemainingSeconds(float seconds) {
        this.despawnRemainingSeconds = seconds;
    }

    public void setDespawning(boolean despawning) {
        this.isDespawning = despawning;
    }

    public void setPlayingDespawnAnim(boolean playingDespawnAnim) {
        this.isPlayingDespawnAnim = playingDespawnAnim;
    }

    public boolean tickDespawnCheckRemainingSeconds(float dt) {
        float f;
        this.despawnCheckRemainingSeconds -= dt;
        return f <= 0.0f;
    }

    public void setDespawnCheckRemainingSeconds(float seconds) {
        this.despawnCheckRemainingSeconds = seconds;
    }

    public void setInitialModelScale(float scale) {
        this.initialModelScale = scale;
    }

    public Vector3d getOldPosition() {
        return this.oldPosition;
    }

    public void playAnimation(@Nonnull Ref<EntityStore> ref, @Nonnull AnimationSlot animationSlot, @Nullable String animationId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Model model = null;
        ModelComponent modelComponent = componentAccessor.getComponent(ref, ModelComponent.getComponentType());
        if (modelComponent != null) {
            model = modelComponent.getModel();
        }
        if (animationSlot != AnimationSlot.Action && animationId != null && model != null && !model.getAnimationSetMap().containsKey(animationId)) {
            ((HytaleLogger.Api)Entity.LOGGER.at(Level.WARNING).atMostEvery(1, TimeUnit.MINUTES)).log("Missing animation '%s' for Model '%s'", (Object)animationId, (Object)model.getModelAssetId());
            return;
        }
        ActiveAnimationComponent activeAnimationComponent = componentAccessor.getComponent(ref, ActiveAnimationComponent.getComponentType());
        if (activeAnimationComponent == null) {
            ((HytaleLogger.Api)Entity.LOGGER.at(Level.WARNING).atMostEvery(1, TimeUnit.MINUTES)).log("Missing active animation component for entity: %s", this.roleName);
            return;
        }
        String[] activeAnimations = activeAnimationComponent.getActiveAnimations();
        if (animationSlot != AnimationSlot.Action && Objects.equals(activeAnimations[animationSlot.ordinal()], animationId)) {
            return;
        }
        activeAnimations[animationSlot.ordinal()] = animationId;
        activeAnimationComponent.setPlayingAnimation(animationSlot, animationId);
        AnimationUtils.playAnimation(ref, animationSlot, animationId, componentAccessor);
    }

    public void clearDamageData() {
        this.damageData.reset();
    }

    public void setToDespawn() {
        this.isDespawning = true;
    }

    public void setDespawnTime(float time) {
        if (this.isDespawning) {
            this.despawnRemainingSeconds = time;
        }
    }

    public double getDespawnTime() {
        return this.despawnRemainingSeconds;
    }

    @Override
    public boolean canBreathe(@Nonnull Ref<EntityStore> ref, @Nonnull BlockMaterial breathingMaterial, int fluidId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        return this.role.canBreathe(breathingMaterial, fluidId);
    }

    public DamageData getDamageData() {
        return this.damageData;
    }

    public boolean getCanCauseDamage(@Nonnull Ref<EntityStore> attackerRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        return this.role.getCombatSupport().getCanCauseDamage(attackerRef, componentAccessor);
    }

    public void onFlockSetState(@Nonnull Ref<EntityStore> ref, @Nonnull String state, @Nullable String subState, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        this.role.getStateSupport().setState(ref, state, subState, componentAccessor);
    }

    public void onFlockSetTarget(@Nonnull String targetSlot, @Nonnull Ref<EntityStore> target) {
        this.role.setMarkedTarget(targetSlot, target);
    }

    public void saveLeashInformation(@Nonnull Vector3d position, @Nonnull Vector3f rotation) {
        this.leashPoint.assign(position);
        this.leashHeading = rotation.getYaw();
        this.leashPitch = rotation.getPitch();
        this.saveLeashBlockType();
    }

    public void saveLeashBlockType() {
    }

    public boolean requiresLeashPosition() {
        if (this.role != null) {
            return this.role.requiresLeashPosition();
        }
        return this.hasLeashPosition;
    }

    public Vector3d getLeashPoint() {
        return this.leashPoint;
    }

    public void setLeashPoint(@Nonnull Vector3d leashPoint) {
        this.leashPoint.assign(leashPoint);
    }

    public float getLeashHeading() {
        return this.leashHeading;
    }

    public void setLeashHeading(float leashHeading) {
        this.leashHeading = leashHeading;
    }

    public float getLeashPitch() {
        return this.leashPitch;
    }

    public void setLeashPitch(float leashPitch) {
        this.leashPitch = leashPitch;
    }

    public float getHoverPhase() {
        return this.hoverPhase;
    }

    public void setHoverPhase(float hoverPhase) {
        this.hoverPhase = hoverPhase;
    }

    public double getHoverHeight() {
        return this.hoverHeight;
    }

    public void setHoverHeight(double hoverHeight) {
        this.hoverHeight = hoverHeight;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getRoleIndex() {
        return this.roleIndex;
    }

    public void setRoleIndex(int roleIndex) {
        this.roleIndex = roleIndex;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getSpawnRoleIndex() {
        return this.spawnRoleIndex != Integer.MIN_VALUE ? this.spawnRoleIndex : this.roleIndex;
    }

    public void setSpawnRoleIndex(int spawnRoleIndex) {
        if (spawnRoleIndex == this.roleIndex) {
            spawnRoleIndex = Integer.MIN_VALUE;
        }
        this.spawnRoleIndex = spawnRoleIndex;
        if (spawnRoleIndex == Integer.MIN_VALUE) {
            this.spawnRoleName = null;
            return;
        }
        this.spawnRoleName = NPCPlugin.get().getName(spawnRoleIndex);
    }

    @Nonnull
    public BlockTypeView getBlockTypeBlackboardView(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        if (this.blackboardBlockTypeView == null) {
            this.initBlockTypeBlackboardView(ref, store);
        }
        if (this.blackboardBlockTypeView.isOutdated(ref, store)) {
            this.blackboardBlockTypeView = this.blackboardBlockTypeView.getUpdatedView((Ref)ref, store);
        }
        return this.blackboardBlockTypeView;
    }

    @Nullable
    public BlockTypeView removeBlockTypeBlackboardView() {
        BlockTypeView view = this.blackboardBlockTypeView;
        this.blackboardBlockTypeView = null;
        return view;
    }

    public void initBlockTypeBlackboardView(@Nonnull Ref<EntityStore> ref, ComponentAccessor<EntityStore> componentAccessor) {
        if (this.blackboardBlockTypeSets != null) {
            this.blackboardBlockTypeView = componentAccessor.getResource(Blackboard.getResourceType()).getView(BlockTypeView.class, ref, componentAccessor);
            this.blackboardBlockTypeView.initialiseEntity(ref, this);
        }
    }

    public void initBlockChangeBlackboardView(@Nonnull Ref<EntityStore> ref, ComponentAccessor<EntityStore> componentAccessor) {
        if (this.blackboardBlockChangeSets != null) {
            this.blackboardBlockChangeView = componentAccessor.getResource(Blackboard.getResourceType()).getView(BlockEventView.class, ref, componentAccessor);
            this.blackboardBlockChangeView.initialiseEntity(ref, this);
        }
        if (this.blackboardEntityEventSets != null) {
            this.blackboardEntityEventView = componentAccessor.getResource(Blackboard.getResourceType()).getView(EntityEventView.class, ref, componentAccessor);
            this.blackboardEntityEventView.initialiseEntity(ref, this);
        }
    }

    public void addBlackboardBlockTypeSets(IntList blackboardBlockSets) {
        this.blackboardBlockTypeSets = blackboardBlockSets;
    }

    public IntList getBlackboardBlockTypeSets() {
        return this.blackboardBlockTypeSets;
    }

    public void addBlackboardBlockChangeSets(@Nonnull BlockEventType type, @Nonnull IntSet sets) {
        if (this.blackboardBlockChangeSets == null) {
            this.blackboardBlockChangeSets = new EnumMap<BlockEventType, IntSet>(BlockEventType.class);
        }
        this.blackboardBlockChangeSets.put(type, sets);
    }

    public IntSet getBlackboardBlockChangeSet(BlockEventType type) {
        return this.blackboardBlockChangeSets.getOrDefault(type, null);
    }

    public Map<BlockEventType, IntSet> getBlackboardBlockChangeSets() {
        return this.blackboardBlockChangeSets;
    }

    public void notifyBlockChange(@Nonnull BlockEventType type, @Nonnull EventNotification notification) {
        Ref<EntityStore> initiator;
        Store<EntityStore> store = this.world.getEntityStore().getStore();
        boolean isPlayer = store.getArchetype(initiator = notification.getInitiator()).contains(Player.getComponentType());
        EventSupport support = isPlayer ? (EventSupport)store.getComponent(this.reference, PlayerBlockEventSupport.getComponentType()) : (EventSupport)store.getComponent(this.reference, NPCBlockEventSupport.getComponentType());
        if (support != null) {
            support.postMessage(type, notification, this.getReference(), store);
        }
    }

    public void addBlackboardEntityEventSets(@Nonnull EntityEventType type, @Nonnull IntSet sets) {
        if (this.blackboardEntityEventSets == null) {
            this.blackboardEntityEventSets = new EnumMap<EntityEventType, IntSet>(EntityEventType.class);
        }
        this.blackboardEntityEventSets.put(type, sets);
    }

    public IntSet getBlackboardEntityEventSet(@Nonnull EntityEventType type) {
        return this.blackboardEntityEventSets.getOrDefault(type, null);
    }

    public Map<EntityEventType, IntSet> getBlackboardEntityEventSets() {
        return this.blackboardEntityEventSets;
    }

    public void notifyEntityEvent(@Nonnull EntityEventType type, @Nonnull EntityEventNotification notification) {
        Ref<EntityStore> initiator;
        Store<EntityStore> store = this.world.getEntityStore().getStore();
        boolean isPlayer = store.getArchetype(initiator = notification.getInitiator()).contains(Player.getComponentType());
        EntityEventSupport support = isPlayer ? (EntityEventSupport)store.getComponent(this.reference, PlayerEntityEventSupport.getComponentType()) : (EntityEventSupport)store.getComponent(this.reference, NPCEntityEventSupport.getComponentType());
        if (support != null) {
            support.postMessage(type, notification, (Ref<EntityStore>)this.reference, store);
        }
    }

    public void setEnvironment(int env) {
        this.environmentIndex = env;
    }

    public int getEnvironment() {
        return this.environmentIndex;
    }

    public int getSpawnConfiguration() {
        return this.spawnConfigurationIndex;
    }

    public void setSpawnConfiguration(int spawnConfigurationIndex) {
        if (spawnConfigurationIndex == Integer.MIN_VALUE) {
            this.spawnConfigurationIndex = Integer.MIN_VALUE;
            this.spawnConfigurationName = null;
            return;
        }
        String name = WorldNPCSpawn.getAssetMap().getAsset(spawnConfigurationIndex).getId();
        if (name == null) {
            throw new IllegalArgumentException("setSpawnConfiguration: Cannot find spawn configuration name for index: " + spawnConfigurationIndex);
        }
        this.spawnConfigurationIndex = spawnConfigurationIndex;
        this.spawnConfigurationName = name;
    }

    public boolean updateSpawnTrackingState(boolean newState) {
        boolean oldState = this.isSpawnTracked;
        this.isSpawnTracked = newState;
        return oldState;
    }

    public boolean isDespawning() {
        return this.isDespawning;
    }

    public boolean isPlayingDespawnAnim() {
        return this.isPlayingDespawnAnim;
    }

    public EnumSet<RoleDebugFlags> getRoleDebugFlags() {
        return this.role.getDebugSupport().getDebugFlags();
    }

    public void setRoleDebugFlags(@Nonnull EnumSet<RoleDebugFlags> flags) {
        this.role.getDebugSupport().setDebugFlags(flags);
    }

    public void setSpawnInstant(@Nonnull Instant spawned) {
        this.spawnInstant = spawned;
    }

    public Instant getSpawnInstant() {
        return this.spawnInstant;
    }

    public void setInventorySize(int hotbarCapacity, int inventoryCapacity, int offHandCapacity) {
        this.setInventory(new Inventory((short)inventoryCapacity, Inventory.DEFAULT_ARMOR_CAPACITY, (short)hotbarCapacity, (short)offHandCapacity, 0));
    }

    @Deprecated(forRemoval=true)
    public int getLegacyWorldgenId() {
        return this.worldgenId;
    }

    @Nonnull
    public PathManager getPathManager() {
        return this.pathManager;
    }

    public static boolean setAppearance(@Nonnull Ref<EntityStore> ref, @Nonnull String name, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Appearance can't be changed to empty");
        }
        ModelComponent modelComponent = componentAccessor.getComponent(ref, ModelComponent.getComponentType());
        if (modelComponent == null) {
            return false;
        }
        Model model = modelComponent.getModel();
        if (name.equals(model.getModelAssetId())) {
            return true;
        }
        NPCEntity npcComponent = componentAccessor.getComponent(ref, NPCEntity.getComponentType());
        assert (npcComponent != null);
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(name);
        if (modelAsset == null) {
            NPCPlugin.get().getLogger().at(Level.SEVERE).log("Role '%s': Cannot find model '%s'", (Object)npcComponent.roleName, (Object)name);
            return false;
        }
        npcComponent.setAppearance(ref, modelAsset, componentAccessor);
        return true;
    }

    public void setAppearance(@Nonnull Ref<EntityStore> ref, @Nonnull ModelAsset modelAsset, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Model model = Model.createScaledModel(modelAsset, this.initialModelScale);
        componentAccessor.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(model));
        this.role.updateMotionControllers(ref, model, model.getBoundingBox(), componentAccessor);
    }

    public float getCurrentHorizontalSpeedMultiplier(@Nullable Ref<EntityStore> ref, @Nullable ComponentAccessor<EntityStore> componentAccessor) {
        if (this.cachedEntityHorizontalSpeedMultiplier != Float.MAX_VALUE) {
            return this.cachedEntityHorizontalSpeedMultiplier;
        }
        this.cachedEntityHorizontalSpeedMultiplier = 1.0f;
        if (ref == null || componentAccessor == null) {
            return this.cachedEntityHorizontalSpeedMultiplier;
        }
        EffectControllerComponent effectControllerComponent = componentAccessor.getComponent(ref, EffectControllerComponent.getComponentType());
        if (effectControllerComponent == null) {
            return this.cachedEntityHorizontalSpeedMultiplier;
        }
        int[] cachedEffectIndexes = effectControllerComponent.getActiveEffectIndexes();
        if (cachedEffectIndexes == null) {
            return this.cachedEntityHorizontalSpeedMultiplier;
        }
        for (int cachedEffectIndex : cachedEffectIndexes) {
            float multiplier;
            ApplicationEffects applicationEffects;
            EntityEffect effect = EntityEffect.getAssetMap().getAsset(cachedEffectIndex);
            if (effect == null || (applicationEffects = effect.getApplicationEffects()) == null || !((multiplier = applicationEffects.getHorizontalSpeedMultiplier()) >= 0.0f)) continue;
            this.cachedEntityHorizontalSpeedMultiplier *= multiplier;
        }
        return this.cachedEntityHorizontalSpeedMultiplier;
    }

    @Override
    @Nonnull
    public String toString() {
        return "NPCEntity{role=" + String.valueOf(this.role) + ", spawnRoleIndex=" + this.spawnRoleIndex + ", spawnPoint=" + String.valueOf(this.leashPoint) + ", spawnHeading=" + this.leashHeading + ", spawnPitch=" + this.leashPitch + ", environmentIndex='" + this.environmentIndex + "'} " + super.toString();
    }

    @Override
    public String getNPCTypeId() {
        return this.roleName;
    }

    @Override
    public int getNPCTypeIndex() {
        return this.roleIndex;
    }

    public void addReservation(@Nonnull UUID playerUUID) {
        this.reservedBy.add(playerUUID);
    }

    public void removeReservation(@Nonnull UUID playerUUID) {
        this.reservedBy.remove(playerUUID);
    }

    public boolean isReserved() {
        return !this.reservedBy.isEmpty();
    }

    public boolean isReservedBy(@Nonnull UUID playerUUID) {
        return this.reservedBy.contains(playerUUID);
    }
}

