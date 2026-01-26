/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.spawnmarkers;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.function.consumer.TriConsumer;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.group.EntityGroup;
import com.hypixel.hytale.server.core.entity.reference.InvalidatablePersistentRef;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.component.WorldGenId;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.FlockPlugin;
import com.hypixel.hytale.server.flock.StoredFlock;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderInfo;
import com.hypixel.hytale.server.npc.components.SpawnMarkerReference;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.spawning.ISpawnableWithModel;
import com.hypixel.hytale.server.spawning.SpawnTestResult;
import com.hypixel.hytale.server.spawning.SpawningContext;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import com.hypixel.hytale.server.spawning.assets.spawnmarker.config.SpawnMarker;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpawnMarkerEntity
implements Component<EntityStore> {
    private static final double SPAWN_LOST_TIMEOUT = 35.0;
    @Nonnull
    private static final InvalidatablePersistentRef[] EMPTY_REFERENCES = new InvalidatablePersistentRef[0];
    public static final ArrayCodec<InvalidatablePersistentRef> NPC_REFERENCES_CODEC = new ArrayCodec<InvalidatablePersistentRef>(InvalidatablePersistentRef.CODEC, InvalidatablePersistentRef[]::new);
    @Nonnull
    public static final BuilderCodec<SpawnMarkerEntity> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SpawnMarkerEntity.class, SpawnMarkerEntity::new).addField(new KeyedCodec<String>("SpawnMarker", Codec.STRING), (spawnMarkerEntity, s) -> {
        spawnMarkerEntity.spawnMarkerId = s;
    }, spawnMarkerEntity -> spawnMarkerEntity.spawnMarkerId)).addField(new KeyedCodec<Double>("RespawnTime", Codec.DOUBLE), (spawnMarkerEntity, d) -> {
        spawnMarkerEntity.respawnCounter = d;
    }, spawnMarkerEntity -> spawnMarkerEntity.respawnCounter)).addField(new KeyedCodec<Integer>("SpawnCount", Codec.INTEGER), (spawnMarkerEntity, i) -> {
        spawnMarkerEntity.spawnCount = i;
    }, spawnMarkerEntity -> spawnMarkerEntity.spawnCount)).addField(new KeyedCodec("GameTimeRespawn", Codec.DURATION), (spawnMarkerEntity, duration) -> {
        spawnMarkerEntity.gameTimeRespawn = duration;
    }, spawnMarkerEntity -> spawnMarkerEntity.gameTimeRespawn)).addField(new KeyedCodec("SpawnAfter", Codec.INSTANT), (spawnMarkerEntity, instant) -> {
        spawnMarkerEntity.spawnAfter = instant;
    }, spawnMarkerEntity -> spawnMarkerEntity.spawnAfter)).addField(new KeyedCodec<T[]>("NPCReferences", NPC_REFERENCES_CODEC), (spawnMarkerEntity, array) -> {
        spawnMarkerEntity.npcReferences = array;
    }, spawnMarkerEntity -> spawnMarkerEntity.npcReferences)).addField(new KeyedCodec<StoredFlock>("PersistedFlock", StoredFlock.CODEC), (spawnMarkerEntity, o) -> {
        spawnMarkerEntity.storedFlock = o;
    }, spawnMarkerEntity -> spawnMarkerEntity.storedFlock)).addField(new KeyedCodec<Vector3d>("SpawnPosition", Vector3d.CODEC), (spawnMarkerEntity, v) -> spawnMarkerEntity.spawnPosition.assign((Vector3d)v), spawnMarkerEntity -> spawnMarkerEntity.storedFlock == null ? null : spawnMarkerEntity.spawnPosition)).build();
    private static final int MAX_FAILED_SPAWNS = 5;
    private String spawnMarkerId;
    private SpawnMarker cachedMarker;
    private double respawnCounter;
    @Nullable
    private Duration gameTimeRespawn;
    @Nullable
    private Instant spawnAfter;
    private int spawnCount;
    @Nullable
    private Set<UUID> suppressedBy;
    private int failedSpawns;
    @Nonnull
    private final SpawningContext context;
    private final Vector3d spawnPosition = new Vector3d();
    private InvalidatablePersistentRef[] npcReferences;
    @Nullable
    private StoredFlock storedFlock;
    @Nullable
    private List<Pair<Ref<EntityStore>, NPCEntity>> tempStorageList;
    private double timeToDeactivation;
    private boolean despawnStarted;
    private double spawnLostTimeoutCounter;

    public static ComponentType<EntityStore, SpawnMarkerEntity> getComponentType() {
        return SpawningPlugin.get().getSpawnMarkerComponentType();
    }

    public SpawnMarkerEntity() {
        this.context = new SpawningContext();
        this.npcReferences = EMPTY_REFERENCES;
    }

    public SpawnMarker getCachedMarker() {
        return this.cachedMarker;
    }

    public void setCachedMarker(@Nonnull SpawnMarker marker) {
        this.cachedMarker = marker;
    }

    public int getSpawnCount() {
        return this.spawnCount;
    }

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }

    public void setRespawnCounter(double respawnCounter) {
        this.respawnCounter = respawnCounter;
    }

    public void setSpawnAfter(@Nullable Instant spawnAfter) {
        this.spawnAfter = spawnAfter;
    }

    @Nullable
    public Instant getSpawnAfter() {
        return this.spawnAfter;
    }

    public void setGameTimeRespawn(@Nullable Duration gameTimeRespawn) {
        this.gameTimeRespawn = gameTimeRespawn;
    }

    @Nullable
    public Duration pollGameTimeRespawn() {
        Duration ret = this.gameTimeRespawn;
        this.gameTimeRespawn = null;
        return ret;
    }

    public boolean tickRespawnTimer(float dt) {
        double d;
        this.respawnCounter -= (double)dt;
        return d <= 0.0;
    }

    @Nullable
    public Set<UUID> getSuppressedBy() {
        return this.suppressedBy;
    }

    public void setStoredFlock(@Nonnull StoredFlock storedFlock) {
        this.storedFlock = storedFlock;
    }

    @Nullable
    public StoredFlock getStoredFlock() {
        return this.storedFlock;
    }

    public double getTimeToDeactivation() {
        return this.timeToDeactivation;
    }

    public void setTimeToDeactivation(double timeToDeactivation) {
        this.timeToDeactivation = timeToDeactivation;
    }

    public boolean tickTimeToDeactivation(float dt) {
        double d;
        this.timeToDeactivation -= (double)dt;
        return d <= 0.0;
    }

    public boolean tickSpawnLostTimeout(float dt) {
        double d;
        this.spawnLostTimeoutCounter -= (double)dt;
        return d <= 0.0;
    }

    @Nonnull
    public Vector3d getSpawnPosition() {
        return this.spawnPosition;
    }

    public InvalidatablePersistentRef[] getNpcReferences() {
        return this.npcReferences;
    }

    public void setNpcReferences(@Nullable InvalidatablePersistentRef[] npcReferences) {
        this.npcReferences = npcReferences != null ? npcReferences : EMPTY_REFERENCES;
    }

    @Nullable
    public List<Pair<Ref<EntityStore>, NPCEntity>> getTempStorageList() {
        return this.tempStorageList;
    }

    public void setTempStorageList(@Nonnull List<Pair<Ref<EntityStore>, NPCEntity>> tempStorageList) {
        this.tempStorageList = tempStorageList;
    }

    public boolean isDespawnStarted() {
        return this.despawnStarted;
    }

    public void setDespawnStarted(boolean despawnStarted) {
        this.despawnStarted = despawnStarted;
    }

    public void refreshTimeout() {
        this.spawnLostTimeoutCounter = 35.0;
    }

    public boolean spawnNPC(@Nonnull Ref<EntityStore> ref, @Nonnull SpawnMarker marker, @Nonnull Store<EntityStore> store) {
        NPCEntity npcComponent;
        boolean hasPlayersInRange;
        Builder<?> role;
        SpawnMarker.SpawnConfiguration spawn = marker.getWeightedConfigurations().get(ThreadLocalRandom.current());
        if (spawn == null) {
            SpawningPlugin.get().getLogger().at(Level.SEVERE).log("Marker %s has no spawn configuration to spawn", ref);
            this.refreshTimeout();
            return false;
        }
        boolean realtime = marker.isRealtimeRespawn();
        if (realtime) {
            this.respawnCounter = spawn.getRealtimeRespawnTime();
        } else {
            this.spawnAfter = null;
            this.gameTimeRespawn = spawn.getSpawnAfterGameTime();
        }
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        assert (uuidComponent != null);
        UUID uuid = uuidComponent.getUuid();
        String roleName = spawn.getNpc();
        if (roleName == null || roleName.isEmpty()) {
            SpawningPlugin.get().getLogger().at(Level.FINE).log("Marker %s performed noop spawn and set repawn to %s", (Object)uuid, (Object)(realtime ? Double.valueOf(this.respawnCounter) : this.gameTimeRespawn));
            this.refreshTimeout();
            return true;
        }
        NPCPlugin npcModule = NPCPlugin.get();
        int roleIndex = npcModule.getIndex(roleName);
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        assert (transformComponent != null);
        Vector3d position = transformComponent.getPosition();
        BuilderInfo builderInfo = npcModule.getRoleBuilderInfo(roleIndex);
        if (builderInfo == null) {
            SpawningPlugin.get().getLogger().at(Level.SEVERE).log("Marker %s attempted to spawn non-existent NPC role '%s'", (Object)uuid, (Object)roleName);
            this.fail(ref, uuid, roleName, position, store, FailReason.NONEXISTENT_ROLE);
            return false;
        }
        Builder<?> builder = role = builderInfo.isValid() ? builderInfo.getBuilder() : null;
        if (role == null) {
            SpawningPlugin.get().getLogger().at(Level.SEVERE).log("Marker %s attempted to spawn invalid NPC role '%s'", (Object)uuid, (Object)roleName);
            this.fail(ref, uuid, roleName, position, store, FailReason.INVALID_ROLE);
            return false;
        }
        if (!role.isSpawnable()) {
            SpawningPlugin.get().getLogger().at(Level.SEVERE).log("Marker %s attempted to spawn a non-spawnable (abstract) role '%s'", (Object)uuid, (Object)roleName);
            this.fail(ref, uuid, roleName, position, store, FailReason.INVALID_ROLE);
            return false;
        }
        if (!this.context.setSpawnable((ISpawnableWithModel)((Object)role))) {
            SpawningPlugin.get().getLogger().at(Level.SEVERE).log("Marker %s failed to spawn NPC role '%s' due to failed role validation", (Object)uuid, (Object)roleName);
            this.fail(ref, uuid, roleName, position, store, FailReason.FAILED_ROLE_VALIDATION);
            return false;
        }
        ObjectList results = SpatialResource.getThreadLocalReferenceList();
        SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = store.getResource(EntityModule.get().getPlayerSpatialResourceType());
        spatialResource.getSpatialStructure().collect(position, marker.getExclusionRadius(), results);
        boolean bl = hasPlayersInRange = !results.isEmpty();
        if (hasPlayersInRange) {
            this.refreshTimeout();
            return false;
        }
        World world = store.getExternalData().getWorld();
        if (!this.context.set(world, position.x, position.y, position.z)) {
            SpawningPlugin.get().getLogger().at(Level.FINE).log("Marker %s attempted to spawn NPC '%s' at %s but could not fit", uuid, roleName, position);
            this.fail(ref, uuid, roleName, position, store, FailReason.NO_ROOM);
            return false;
        }
        SpawnTestResult testResult = this.context.canSpawn(true, false);
        if (testResult != SpawnTestResult.TEST_OK) {
            SpawningPlugin.get().getLogger().at(Level.FINE).log("Marker %s attempted to spawn NPC '%s' at %s but could not fit: %s", uuid, roleName, position, (Object)testResult);
            this.fail(ref, uuid, roleName, position, store, FailReason.NO_ROOM);
            return false;
        }
        this.spawnPosition.assign(this.context.xSpawn, this.context.ySpawn, this.context.zSpawn);
        if (this.spawnPosition.distanceSquaredTo(position) > marker.getMaxDropHeightSquared()) {
            SpawningPlugin.get().getLogger().at(Level.FINE).log("Marker %s attempted to spawn NPC '%s' but was offset too far from the ground at %s", uuid, roleName, position);
            this.fail(ref, uuid, roleName, position, store, FailReason.TOO_HIGH);
            return false;
        }
        TriConsumer<NPCEntity, Ref<EntityStore>, Store<EntityStore>> postSpawn = (_entity, _ref, _store) -> {
            SpawnMarkerReference spawnMarkerReference = _store.ensureAndGetComponent(_ref, SpawnMarkerReference.getComponentType());
            spawnMarkerReference.getReference().setEntity(ref, (ComponentAccessor<EntityStore>)_store);
            spawnMarkerReference.refreshTimeoutCounter();
            WorldGenId worldGenIdComponent = _store.getComponent(ref, WorldGenId.getComponentType());
            int worldGenId = worldGenIdComponent != null ? worldGenIdComponent.getWorldGenId() : 0;
            _store.putComponent(_ref, WorldGenId.getComponentType(), new WorldGenId(worldGenId));
        };
        Vector3f rotation = transformComponent.getRotation();
        Pair<Ref<EntityStore>, NPCEntity> npcPair = npcModule.spawnEntity(store, roleIndex, this.spawnPosition, rotation, null, postSpawn);
        if (npcPair == null) {
            SpawningPlugin.get().getLogger().at(Level.SEVERE).log("Marker %s failed to spawn NPC role '%s' due to an internal error", (Object)uuid, (Object)roleName);
            this.fail(ref, uuid, roleName, position, store, FailReason.INVALID_ROLE);
            return false;
        }
        Ref<EntityStore> npcRef = npcPair.first();
        Ref<EntityStore> flockReference = FlockPlugin.trySpawnFlock(npcRef, npcComponent = npcPair.second(), store, roleIndex, this.spawnPosition, rotation, spawn.getFlockDefinition(), postSpawn);
        EntityGroup group = flockReference == null ? null : store.getComponent(flockReference, EntityGroup.getComponentType());
        int n = this.spawnCount = group != null ? group.size() : 1;
        if (this.storedFlock != null) {
            this.despawnStarted = false;
            this.npcReferences = new InvalidatablePersistentRef[this.spawnCount];
            if (group != null) {
                group.forEachMember((index, member, referenceArray) -> {
                    InvalidatablePersistentRef reference = new InvalidatablePersistentRef();
                    reference.setEntity((Ref<EntityStore>)member, store);
                    referenceArray[index] = reference;
                }, this.npcReferences);
            } else {
                InvalidatablePersistentRef reference = new InvalidatablePersistentRef();
                reference.setEntity(npcRef, store);
                this.npcReferences[0] = reference;
            }
            this.storedFlock.clear();
        }
        SpawningPlugin.get().getLogger().at(Level.FINE).log("Marker %s spawned %s and set respawn to %s", uuid, npcComponent.getRoleName(), realtime ? Double.valueOf(this.respawnCounter) : this.gameTimeRespawn);
        this.refreshTimeout();
        return true;
    }

    private void fail(@Nonnull Ref<EntityStore> self, @Nonnull UUID uuid, @Nonnull String role, @Nonnull Vector3d position, @Nonnull Store<EntityStore> store, @Nonnull FailReason reason) {
        if (++this.failedSpawns >= 5) {
            SpawningPlugin.get().getLogger().at(Level.WARNING).log("Marker %s at %s removed due to repeated spawning fails of %s with reason: %s", uuid, position, role, (Object)reason);
            store.removeEntity(self, RemoveReason.REMOVE);
            return;
        }
        this.refreshTimeout();
    }

    public void setSpawnMarker(@Nonnull SpawnMarker marker) {
        this.spawnMarkerId = marker.getId();
        this.cachedMarker = marker;
        if (this.cachedMarker.getDeactivationDistance() > 0.0) {
            this.storedFlock = new StoredFlock();
            this.tempStorageList = new ObjectArrayList<Pair<Ref<EntityStore>, NPCEntity>>();
        } else {
            this.storedFlock = null;
            this.tempStorageList = null;
        }
    }

    public int decrementAndGetSpawnCount() {
        return --this.spawnCount;
    }

    public String getSpawnMarkerId() {
        return this.spawnMarkerId;
    }

    public boolean isManualTrigger() {
        return this.cachedMarker.isManualTrigger();
    }

    public boolean trigger(@Nonnull Ref<EntityStore> markerRef, @Nonnull Store<EntityStore> store) {
        if (!this.cachedMarker.isManualTrigger() || this.spawnCount > 0) {
            return false;
        }
        return this.spawnNPC(markerRef, this.cachedMarker, store);
    }

    public void suppress(@Nonnull UUID suppressor) {
        if (this.suppressedBy == null) {
            this.suppressedBy = new HashSet<UUID>();
        }
        this.suppressedBy.add(suppressor);
    }

    public void releaseSuppression(@Nonnull UUID suppressor) {
        if (this.suppressedBy != null) {
            this.suppressedBy.remove(suppressor);
        }
    }

    public void clearAllSuppressions() {
        if (this.suppressedBy != null) {
            this.suppressedBy.clear();
        }
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        SpawnMarkerEntity spawnMarker = new SpawnMarkerEntity();
        spawnMarker.spawnMarkerId = this.spawnMarkerId;
        spawnMarker.cachedMarker = this.cachedMarker;
        spawnMarker.respawnCounter = this.respawnCounter;
        spawnMarker.gameTimeRespawn = this.gameTimeRespawn;
        spawnMarker.spawnAfter = this.spawnAfter;
        spawnMarker.spawnCount = this.spawnCount;
        spawnMarker.suppressedBy = this.suppressedBy != null ? new HashSet<UUID>(this.suppressedBy) : null;
        spawnMarker.failedSpawns = this.failedSpawns;
        spawnMarker.spawnPosition.assign(this.spawnPosition);
        spawnMarker.npcReferences = this.npcReferences;
        spawnMarker.storedFlock = this.storedFlock != null ? this.storedFlock.clone() : null;
        spawnMarker.timeToDeactivation = this.timeToDeactivation;
        spawnMarker.despawnStarted = this.despawnStarted;
        spawnMarker.spawnLostTimeoutCounter = this.spawnLostTimeoutCounter;
        return spawnMarker;
    }

    @Nonnull
    public String toString() {
        return "SpawnMarkerEntity{spawnMarkerId='" + this.spawnMarkerId + "', cachedMarker=" + String.valueOf(this.cachedMarker) + ", respawnCounter=" + this.respawnCounter + ", gameTimeRespawn=" + String.valueOf(this.gameTimeRespawn) + ", spawnAfter=" + String.valueOf(this.spawnAfter) + ", spawnCount=" + this.spawnCount + ", spawnLostTimeoutCounter=" + this.spawnLostTimeoutCounter + ", failedSpawns=" + this.failedSpawns + ", context=" + String.valueOf(this.context) + ", spawnPosition=" + String.valueOf(this.spawnPosition) + ", storedFlock=" + String.valueOf(this.storedFlock) + "} " + super.toString();
    }

    public static Model getModel(@Nonnull SpawnMarker marker) {
        String modelName = marker.getModel();
        ModelAsset modelAsset = null;
        if (modelName != null && !modelName.isEmpty()) {
            modelAsset = ModelAsset.getAssetMap().getAsset(modelName);
        }
        Model model = modelAsset == null ? SpawningPlugin.get().getSpawnMarkerModel() : Model.createUnitScaleModel(modelAsset);
        return model;
    }

    private static enum FailReason {
        INVALID_ROLE,
        NONEXISTENT_ROLE,
        FAILED_ROLE_VALIDATION,
        NO_ROOM,
        TOO_HIGH;

    }
}

