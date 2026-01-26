/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.role.support;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.function.consumer.DoubleQuadObjectConsumer;
import com.hypixel.hytale.function.consumer.QuadConsumer;
import com.hypixel.hytale.function.consumer.TriConsumer;
import com.hypixel.hytale.function.predicate.QuadPredicate;
import com.hypixel.hytale.math.iterator.BlockIterator;
import com.hypixel.hytale.math.random.RandomExtra;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector2d;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.Opacity;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.blockset.BlockSetModule;
import com.hypixel.hytale.server.core.modules.collision.CollisionMath;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.support.EntityList;
import com.hypixel.hytale.server.npc.role.support.RoleStats;
import com.hypixel.hytale.server.npc.util.NPCPhysicsMath;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PositionCache {
    public static final BiPredicate<Ref<EntityStore>, ComponentAccessor<EntityStore>> IS_VALID_PLAYER = (ref, componentAccessor) -> {
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        if (playerComponent == null || playerComponent.isWaitingForClientReady()) {
            return false;
        }
        if (playerComponent.getGameMode() == GameMode.Adventure) {
            return true;
        }
        if (playerComponent.getGameMode() == GameMode.Creative) {
            PlayerSettings playerSettingsComponent = componentAccessor.getComponent(ref, PlayerSettings.getComponentType());
            return playerSettingsComponent != null && playerSettingsComponent.creativeSettings().allowNPCDetection();
        }
        return false;
    };
    public static final BiPredicate<Ref<EntityStore>, ComponentAccessor<EntityStore>> IS_VALID_NPC = (ref, accessor) -> accessor.getArchetype(ref).contains(NPCEntity.getComponentType());
    public static final double MIN_LOS_BLOCKING_DISTANCE_SQUARED = 1.0E-6;
    public static final String FUNCTION_CAN_BE_ONLY_CALLED_WHILE_CONFIGURING_POSITION_CACHE = "function can be only called while configuring PositionCache";
    private static final float LOS_CACHE_TTL_MIN_SECONDS = 0.09f;
    private static final float LOS_CACHE_TTL_MAX_SECONDS = 0.11f;
    private static final float POSITION_CACHE_TTL_SECONDS = 0.2f;
    private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
    private static final ComponentType<EntityStore, ItemComponent> ITEM_COMPONENT_TYPE = ItemComponent.getComponentType();
    private static final ComponentType<EntityStore, ModelComponent> MODEL_COMPONENT_TYPE = ModelComponent.getComponentType();
    protected static final ComponentType<EntityStore, BoundingBox> BOUNDING_BOX_COMPONENT_TYPE = BoundingBox.getComponentType();
    private double maxDroppedItemDistance;
    private double maxSpawnMarkerDistance;
    private int maxSpawnBeaconDistance;
    @Nonnull
    private final Role role;
    private int opaqueBlockSet;
    protected EntityList players;
    protected EntityList npcs;
    protected final List<Consumer<Role>> externalRegistrations = new ObjectArrayList<Consumer<Role>>();
    private final List<Ref<EntityStore>> droppedItems = new ObjectArrayList<Ref<EntityStore>>();
    private final List<Ref<EntityStore>> spawnMarkers = new ObjectArrayList<Ref<EntityStore>>();
    private final List<Ref<EntityStore>> spawnBeacons = new ObjectArrayList<Ref<EntityStore>>();
    private final Object2ByteMap<Ref<EntityStore>> lineOfSightCache = new Object2ByteOpenHashMap<Ref<EntityStore>>();
    private final Object2ByteMap<Ref<EntityStore>> inverseLineOfSightCache = new Object2ByteOpenHashMap<Ref<EntityStore>>();
    private final Object2ByteMap<Ref<EntityStore>> friendlyFireCache = new Object2ByteOpenHashMap<Ref<EntityStore>>();
    protected final LineOfSightBuffer lineOfSightComputeBuffer = new LineOfSightBuffer();
    protected final LineOfSightEntityBuffer lineOfSightEntityComputeBuffer = new LineOfSightEntityBuffer();
    private float cacheTTL = 0.09f;
    private float positionCacheNextUpdate;
    private boolean isBenchmarking;
    private boolean isConfiguring;
    private boolean couldBreathe = true;

    public PositionCache(@Nonnull Role role) {
        this.role = role;
        this.players = new EntityList(null, IS_VALID_PLAYER);
        this.npcs = new EntityList(null, IS_VALID_NPC);
    }

    public boolean isBenchmarking() {
        return this.isBenchmarking;
    }

    public void setBenchmarking(boolean benchmarking) {
        this.isBenchmarking = benchmarking;
    }

    public void setCouldBreathe(boolean couldBreathe) {
        this.couldBreathe = couldBreathe;
    }

    public EntityList getPlayers() {
        return this.players;
    }

    public EntityList getNpcs() {
        return this.npcs;
    }

    public boolean tickPositionCacheNextUpdate(float dt) {
        float f;
        this.positionCacheNextUpdate -= dt;
        return f <= 0.0f;
    }

    public void resetPositionCacheNextUpdate() {
        this.positionCacheNextUpdate = 0.2f;
    }

    public double getMaxDroppedItemDistance() {
        return this.maxDroppedItemDistance;
    }

    public double getMaxSpawnMarkerDistance() {
        return this.maxSpawnMarkerDistance;
    }

    public int getMaxSpawnBeaconDistance() {
        return this.maxSpawnBeaconDistance;
    }

    public void addExternalPositionCacheRegistration(Consumer<Role> registration) {
        this.externalRegistrations.add(registration);
    }

    @Nonnull
    public List<Consumer<Role>> getExternalRegistrations() {
        return this.externalRegistrations;
    }

    public void reset(boolean isConfiguring) {
        this.players.reset();
        this.npcs.reset();
        this.maxDroppedItemDistance = 0.0;
        this.droppedItems.clear();
        this.spawnMarkers.clear();
        this.spawnBeacons.clear();
        this.positionCacheNextUpdate = RandomExtra.randomRange(0.0f, 0.2f);
        this.clearLineOfSightCache();
        this.isConfiguring = isConfiguring;
    }

    public void finalizeConfiguration() {
        this.isConfiguring = false;
        this.npcs.finalizeConfiguration();
        this.players.finalizeConfiguration();
        RoleStats roleStats = this.role.getRoleStats();
        if (roleStats != null) {
            roleStats.trackBuckets(false, this.npcs.getBucketRanges());
            roleStats.trackBuckets(true, this.players.getBucketRanges());
        }
    }

    public void clear(double tickTime) {
        this.clearLineOfSightCache(tickTime);
        if (this.isBenchmarking) {
            NPCPlugin.get().collectSensorSupportTickDone(this.role.getRoleIndex());
        }
        this.isBenchmarking = false;
    }

    public boolean couldBreatheCached() {
        return this.couldBreathe;
    }

    public <T, U, V> void forEachPlayer(@Nonnull DoubleQuadObjectConsumer<Ref<EntityStore>, T, U, V> consumer, T t, U u, V v, double d, ComponentAccessor<EntityStore> componentAccessor) {
        this.players.forEachEntity(consumer, t, u, v, d, componentAccessor);
    }

    @Nullable
    public Ref<EntityStore> getClosestPlayerInRange(double minRange, double maxRange, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        return this.getClosestPlayerInRange(minRange, maxRange, p -> true, componentAccessor);
    }

    @Nullable
    public Ref<EntityStore> getClosestPlayerInRange(double minRange, double maxRange, @Nonnull Predicate<Ref<EntityStore>> filter, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        return this.players.getClosestEntityInRange(minRange, maxRange, filter, componentAccessor);
    }

    @Nullable
    public Ref<EntityStore> getClosestNPCInRange(double minRange, double maxRange, @Nonnull Predicate<Ref<EntityStore>> filter, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        return this.npcs.getClosestEntityInRange(minRange, maxRange, filter, componentAccessor);
    }

    public <S, T> void processNPCsInRange(@Nonnull Ref<EntityStore> ref, double minRange, double maxRange, boolean useProjectedDistance, Ref<EntityStore> ignoredEntityReference, @Nonnull Role role, @Nonnull QuadPredicate<S, Ref<EntityStore>, Role, T> filter, S s, T t, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        this.processEntitiesInRange(ref, this.npcs, minRange, maxRange, useProjectedDistance, ignoredEntityReference, role, filter, s, t, componentAccessor);
    }

    public <S, T> void processPlayersInRange(@Nonnull Ref<EntityStore> ref, double minRange, double maxRange, boolean useProjectedDistance, Ref<EntityStore> ignoredEntityReference, @Nonnull Role role, @Nonnull QuadPredicate<S, Ref<EntityStore>, Role, T> filter, S s, T t, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        this.processEntitiesInRange(ref, this.players, minRange, maxRange, useProjectedDistance, ignoredEntityReference, role, filter, s, t, componentAccessor);
    }

    public <S, T> void processEntitiesInRange(@Nonnull Ref<EntityStore> ref, @Nonnull EntityList entities, double minRange, double maxRange, boolean useProjectedDistance, Ref<EntityStore> ignoredEntityReference, @Nonnull Role role, @Nonnull QuadPredicate<S, Ref<EntityStore>, Role, T> filter, S s, T t, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (useProjectedDistance) {
            entities.getClosestEntityInRangeProjected(ref, ignoredEntityReference, role.getActiveMotionController(), minRange, maxRange, filter, role, s, t, componentAccessor);
        } else {
            entities.getClosestEntityInRange(ignoredEntityReference, minRange, maxRange, filter, role, s, t, componentAccessor);
        }
    }

    @Nullable
    public <S> Ref<EntityStore> getClosestDroppedItemInRange(@Nonnull Ref<EntityStore> ref, double minRange, double maxRange, @Nonnull QuadPredicate<S, Ref<EntityStore>, Role, ComponentAccessor<EntityStore>> filter, Role role, S s, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        int droppedItemsSize = this.droppedItems.size();
        if (droppedItemsSize == 0) {
            return null;
        }
        TransformComponent transformComponent = componentAccessor.getComponent(ref, TRANSFORM_COMPONENT_TYPE);
        assert (transformComponent != null);
        Vector3d position = transformComponent.getPosition();
        minRange *= minRange;
        maxRange *= maxRange;
        for (int index = 0; index < droppedItemsSize; ++index) {
            ItemComponent itemComponent;
            Ref<EntityStore> itemEntityRef = this.droppedItems.get(index);
            if (!itemEntityRef.isValid() || (itemComponent = componentAccessor.getComponent(itemEntityRef, ITEM_COMPONENT_TYPE)) == null) continue;
            TransformComponent itemEntityTransformComponent = componentAccessor.getComponent(itemEntityRef, TRANSFORM_COMPONENT_TYPE);
            assert (itemEntityTransformComponent != null);
            double squaredDistance = itemEntityTransformComponent.getPosition().distanceSquaredTo(position);
            if (squaredDistance < minRange) continue;
            if (squaredDistance >= maxRange) break;
            if (!filter.test((Role)s, itemEntityRef, role, componentAccessor)) continue;
            return itemEntityRef;
        }
        return null;
    }

    public <S> boolean isEntityCountInRange(double minRange, double maxRange, int minCount, int maxCount, boolean findPlayers, Role role, @Nonnull QuadPredicate<S, Ref<EntityStore>, Role, ComponentAccessor<EntityStore>> filter, S s, ComponentAccessor<EntityStore> componentAccessor) {
        int count = 0;
        if (findPlayers && (count = this.players.countEntitiesInRange(minRange, maxRange, maxCount + 1, filter, s, role, componentAccessor)) > maxCount) {
            return false;
        }
        return (count += this.npcs.countEntitiesInRange(minRange, maxRange, maxCount - count + 1, filter, s, role, componentAccessor)) >= minCount && count <= maxCount;
    }

    public <S, T> int countEntitiesInRange(double minRange, double maxRange, boolean findPlayers, @Nonnull QuadPredicate<S, Ref<EntityStore>, T, ComponentAccessor<EntityStore>> filter, S s, T t, ComponentAccessor<EntityStore> componentAccessor) {
        int count = 0;
        if (findPlayers) {
            count = this.players.countEntitiesInRange(minRange, maxRange, Integer.MAX_VALUE, filter, s, t, componentAccessor);
        }
        return count + this.npcs.countEntitiesInRange(minRange, maxRange, Integer.MAX_VALUE, filter, s, t, componentAccessor);
    }

    public void requirePlayerDistanceSorted(double v) {
        int value = MathUtil.ceil(v);
        if (!this.isConfiguring) {
            throw new IllegalStateException(FUNCTION_CAN_BE_ONLY_CALLED_WHILE_CONFIGURING_POSITION_CACHE);
        }
        this.players.requireDistanceSorted(value);
        RoleStats roleStats = this.role.getRoleStats();
        if (roleStats != null) {
            roleStats.trackRange(true, RoleStats.RangeType.SORTED, value);
        }
    }

    public void requirePlayerDistanceUnsorted(double v) {
        int value = MathUtil.ceil(v);
        if (!this.isConfiguring) {
            throw new IllegalStateException(FUNCTION_CAN_BE_ONLY_CALLED_WHILE_CONFIGURING_POSITION_CACHE);
        }
        this.players.requireDistanceUnsorted(value);
        RoleStats roleStats = this.role.getRoleStats();
        if (roleStats != null) {
            roleStats.trackRange(true, RoleStats.RangeType.UNSORTED, value);
        }
    }

    public void requirePlayerDistanceAvoidance(double v) {
        int value = MathUtil.ceil(v);
        if (!this.isConfiguring) {
            throw new IllegalStateException(FUNCTION_CAN_BE_ONLY_CALLED_WHILE_CONFIGURING_POSITION_CACHE);
        }
        this.players.requireDistanceAvoidance(value);
        RoleStats roleStats = this.role.getRoleStats();
        if (roleStats != null) {
            roleStats.trackRange(true, RoleStats.RangeType.AVOIDANCE, value);
        }
    }

    public void requireEntityDistanceSorted(double v) {
        int value = MathUtil.ceil(v);
        if (!this.isConfiguring) {
            throw new IllegalStateException(FUNCTION_CAN_BE_ONLY_CALLED_WHILE_CONFIGURING_POSITION_CACHE);
        }
        this.npcs.requireDistanceSorted(value);
        RoleStats roleStats = this.role.getRoleStats();
        if (roleStats != null) {
            roleStats.trackRange(false, RoleStats.RangeType.SORTED, value);
        }
    }

    public void requireEntityDistanceUnsorted(double v) {
        int value = MathUtil.ceil(v);
        if (!this.isConfiguring) {
            throw new IllegalStateException(FUNCTION_CAN_BE_ONLY_CALLED_WHILE_CONFIGURING_POSITION_CACHE);
        }
        this.npcs.requireDistanceUnsorted(value);
        RoleStats roleStats = this.role.getRoleStats();
        if (roleStats != null) {
            roleStats.trackRange(false, RoleStats.RangeType.UNSORTED, value);
        }
    }

    public void requireEntityDistanceAvoidance(double v) {
        int value = MathUtil.ceil(v);
        if (!this.isConfiguring) {
            throw new IllegalStateException(FUNCTION_CAN_BE_ONLY_CALLED_WHILE_CONFIGURING_POSITION_CACHE);
        }
        value = this.npcs.requireDistanceAvoidance(value);
        RoleStats roleStats = this.role.getRoleStats();
        if (roleStats != null) {
            roleStats.trackRange(false, RoleStats.RangeType.AVOIDANCE, value);
        }
    }

    public void requireDroppedItemDistance(double value) {
        if (this.maxDroppedItemDistance < value) {
            this.maxDroppedItemDistance = value;
        }
    }

    public void requireSpawnMarkerDistance(double value) {
        if (this.maxSpawnMarkerDistance < value) {
            this.maxSpawnMarkerDistance = value;
        }
    }

    public void requireSpawnBeaconDistance(int value) {
        if (this.maxSpawnBeaconDistance < value) {
            this.maxSpawnBeaconDistance = value;
        }
    }

    @Nonnull
    public Role getRole() {
        return this.role;
    }

    public <T, U, V, R> void forEachNPCUnordered(double maxDistance, @Nonnull QuadPredicate<Ref<EntityStore>, T, U, ComponentAccessor<EntityStore>> predicate, @Nonnull QuadConsumer<Ref<EntityStore>, T, V, R> consumer, T t, U u, V v, R r, ComponentAccessor<EntityStore> componentAccessor) {
        this.npcs.forEachEntityUnordered(maxDistance, predicate, consumer, t, u, v, r, componentAccessor);
    }

    public <T> void forEachEntityInAvoidanceRange(@Nonnull Set<Ref<EntityStore>> ignoredEntitiesForAvoidance, @Nonnull TriConsumer<Ref<EntityStore>, T, CommandBuffer<EntityStore>> consumer, T t, CommandBuffer<EntityStore> commandBuffer) {
        this.npcs.forEachEntityAvoidance(ignoredEntitiesForAvoidance, consumer, t, commandBuffer);
        this.players.forEachEntityAvoidance(ignoredEntitiesForAvoidance, consumer, t, commandBuffer);
    }

    public <T, U> void forEachEntityInAvoidanceRange(@Nonnull Set<Ref<EntityStore>> ignoredEntitiesForAvoidance, @Nonnull QuadConsumer<Ref<EntityStore>, T, U, CommandBuffer<EntityStore>> consumer, T t, U u, CommandBuffer<EntityStore> commandBuffer) {
        this.npcs.forEachEntityAvoidance(ignoredEntitiesForAvoidance, consumer, t, u, commandBuffer);
        this.players.forEachEntityAvoidance(ignoredEntitiesForAvoidance, consumer, t, u, commandBuffer);
    }

    public void setOpaqueBlockSet(int blockSet) {
        this.opaqueBlockSet = blockSet;
    }

    private static <T> boolean testLineOfSightRays(@Nonnull Ref<EntityStore> ref, @Nonnull Ref<EntityStore> targetRef, @Nonnull RayPredicate<T> predicate, @Nonnull T t, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        TransformComponent transformComponent = componentAccessor.getComponent(ref, TRANSFORM_COMPONENT_TYPE);
        assert (transformComponent != null);
        Vector3d position = transformComponent.getPosition();
        ModelComponent modelComponent = componentAccessor.getComponent(ref, MODEL_COMPONENT_TYPE);
        float eyeHeight = modelComponent != null ? modelComponent.getModel().getEyeHeight() : 0.0f;
        double sx = position.getX();
        double sy = position.getY() + (double)eyeHeight;
        double sz = position.getZ();
        TransformComponent targetTransformComponent = componentAccessor.getComponent(targetRef, TRANSFORM_COMPONENT_TYPE);
        assert (targetTransformComponent != null);
        Vector3d targetPosition = targetTransformComponent.getPosition();
        double tx = targetPosition.getX();
        double ty = targetPosition.getY();
        double tz = targetPosition.getZ();
        ModelComponent targetModelComponent = componentAccessor.getComponent(targetRef, MODEL_COMPONENT_TYPE);
        if (targetModelComponent != null) {
            return predicate.test(sx, sy, sz, tx, ty + (double)targetModelComponent.getModel().getEyeHeight(), tz, t, componentAccessor);
        }
        double ox = 0.0;
        double oy = 0.0;
        double oz = 0.0;
        BoundingBox boundingBoxComponent = componentAccessor.getComponent(targetRef, BOUNDING_BOX_COMPONENT_TYPE);
        if (boundingBoxComponent != null) {
            Box boundingBox = boundingBoxComponent.getBoundingBox();
            ox = (boundingBox.getMax().getX() + boundingBox.getMin().getX()) / 2.0;
            oy = (boundingBox.getMax().getY() + boundingBox.getMin().getY()) / 2.0;
            oz = (boundingBox.getMax().getZ() + boundingBox.getMin().getZ()) / 2.0;
        }
        return predicate.test(sx, sy, sz, tx + ox, ty + oy, tz + oz, t, componentAccessor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean hasLineOfSightInternal(@Nonnull Ref<EntityStore> ref, @Nonnull Ref<EntityStore> targetRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (ref.equals(targetRef)) {
            return false;
        }
        TransformComponent transformComponent = componentAccessor.getComponent(ref, TRANSFORM_COMPONENT_TYPE);
        assert (transformComponent != null);
        TransformComponent targetTransformComponent = componentAccessor.getComponent(targetRef, TRANSFORM_COMPONENT_TYPE);
        assert (targetTransformComponent != null);
        if (transformComponent.getPosition().distanceSquaredTo(targetTransformComponent.getPosition()) <= 1.0E-12) {
            return true;
        }
        World world = componentAccessor.getExternalData().getWorld();
        Objects.requireNonNull(world, "World can't be null in isLOS");
        Int2ObjectMap<IntSet> blockSets = BlockSetModule.getInstance().getBlockSets();
        IntSet opaqueSet = this.opaqueBlockSet >= 0 && blockSets != null ? (IntSet)blockSets.get(this.opaqueBlockSet) : null;
        try {
            this.lineOfSightComputeBuffer.result = true;
            this.lineOfSightComputeBuffer.assetMap = BlockType.getAssetMap();
            this.lineOfSightComputeBuffer.opaqueSet = opaqueSet;
            this.lineOfSightComputeBuffer.world = world;
            boolean bl = PositionCache.testLineOfSightRays(ref, targetRef, (sx, sy, sz, tx, ty, tz, buffer, accessor) -> {
                buffer.chunk = buffer.world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(sx, sz));
                if (buffer.chunk == null) {
                    return false;
                }
                BlockIterator.iterateFromTo(sx, sy, sz, tx, ty, tz, (x, y, z, px, py, pz, qx, qy, qz, iBuffer) -> {
                    int blockId;
                    if (!ChunkUtil.isInsideChunk(iBuffer.chunk.getX(), iBuffer.chunk.getZ(), x, z)) {
                        iBuffer.chunk = iBuffer.world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
                        if (iBuffer.chunk == null) {
                            iBuffer.result = false;
                            return false;
                        }
                    }
                    if ((blockId = iBuffer.chunk.getBlock(x, y, z)) == 0) {
                        return true;
                    }
                    BlockType blockType = iBuffer.assetMap.getAsset(blockId);
                    if (blockType == BlockType.UNKNOWN || blockType.getOpacity() == null || blockType.getOpacity() != Opacity.Transparent || iBuffer.opaqueSet != null && iBuffer.opaqueSet.contains(blockId)) {
                        iBuffer.result = false;
                        return false;
                    }
                    return true;
                }, buffer);
                return buffer.result;
            }, this.lineOfSightComputeBuffer, componentAccessor);
            return bl;
        }
        finally {
            this.lineOfSightComputeBuffer.clearRefs();
        }
    }

    public boolean hasLineOfSight(@Nonnull Ref<EntityStore> ref, @Nonnull Ref<EntityStore> targetRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        boolean hasLineOfSight;
        boolean cached = this.lineOfSightCache.containsKey(targetRef);
        if (cached) {
            if (this.isBenchmarking) {
                NPCPlugin.get().collectSensorSupportLosTest(this.role.getRoleIndex(), true, 0L);
            }
            return this.lineOfSightCache.getByte(targetRef) != 0;
        }
        if (this.isBenchmarking) {
            long start = System.nanoTime();
            hasLineOfSight = this.hasLineOfSightInternal(ref, targetRef, componentAccessor);
            NPCPlugin.get().collectSensorSupportLosTest(this.role.getRoleIndex(), false, System.nanoTime() - start);
        } else {
            hasLineOfSight = this.hasLineOfSightInternal(ref, targetRef, componentAccessor);
        }
        this.lineOfSightCache.put(targetRef, hasLineOfSight ? (byte)1 : 0);
        return hasLineOfSight;
    }

    public boolean hasInverseLineOfSight(@Nonnull Ref<EntityStore> ref, @Nonnull Ref<EntityStore> targetRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        boolean cached = this.inverseLineOfSightCache.containsKey(targetRef);
        if (this.isBenchmarking) {
            NPCPlugin.get().collectSensorSupportInverseLosTest(this.role.getRoleIndex(), cached);
        }
        if (cached) {
            return this.inverseLineOfSightCache.getByte(targetRef) != 0;
        }
        boolean hasLineOfSight = this.hasLineOfSightInternal(targetRef, ref, componentAccessor);
        this.inverseLineOfSightCache.put(targetRef, hasLineOfSight ? (byte)1 : 0);
        return hasLineOfSight;
    }

    public boolean isFriendlyBlockingLineOfSight(@Nonnull Ref<EntityStore> ref, @Nonnull Ref<EntityStore> targetRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        boolean cached = this.friendlyFireCache.containsKey(targetRef);
        if (this.isBenchmarking) {
            NPCPlugin.get().collectSensorSupportFriendlyBlockingTest(this.role.getRoleIndex(), cached);
        }
        if (cached) {
            return this.friendlyFireCache.getByte(targetRef) != 0;
        }
        boolean blocking = PositionCache.testLineOfSightRays(ref, targetRef, (sx, sy, sz, tx, ty, tz, _this, accessor) -> {
            LineOfSightEntityBuffer buffer = _this.lineOfSightEntityComputeBuffer;
            buffer.pos.assign(sx, sy, sz);
            buffer.dir.assign(tx - sx, ty - sy, tz - sz);
            double squaredLength = buffer.dir.squaredLength();
            if (squaredLength < 1.0E-6) {
                return false;
            }
            return _this.players.testAnyEntityDistanceSquared(squaredLength, (positionCache, targetRef1, buffer1, componentAccessor1, length2) -> positionCache.testLineOfSightEntity(ref, (Ref<EntityStore>)targetRef1, (LineOfSightEntityBuffer)buffer1, (ComponentAccessor<EntityStore>)componentAccessor1, length2), _this, buffer, accessor) || _this.npcs.testAnyEntityDistanceSquared(squaredLength, (positionCache1, targetRef2, buffer2, componentAccessor2, length3) -> positionCache1.testLineOfSightEntity(ref, (Ref<EntityStore>)targetRef2, (LineOfSightEntityBuffer)buffer2, (ComponentAccessor<EntityStore>)componentAccessor2, length3), _this, buffer, accessor);
        }, this, componentAccessor);
        this.friendlyFireCache.put(targetRef, blocking ? (byte)1 : 0);
        return blocking;
    }

    private boolean testLineOfSightEntity(@Nonnull Ref<EntityStore> ref, @Nonnull Ref<EntityStore> targetRef, @Nonnull LineOfSightEntityBuffer buffer, @Nonnull ComponentAccessor<EntityStore> componentAccessor, double length2) {
        return !targetRef.equals(ref) && this.role.isFriendly(targetRef, componentAccessor) && PositionCache.rayIsIntersectingEntity(targetRef, buffer.pos, buffer.dir, buffer.minMax, length2, componentAccessor);
    }

    private void clearLineOfSightCache(double tickTime) {
        this.cacheTTL = (float)((double)this.cacheTTL - tickTime);
        if (this.cacheTTL <= 0.0f) {
            this.clearLineOfSightCache();
        }
    }

    private void clearLineOfSightCache() {
        this.cacheTTL = RandomExtra.randomRange(0.09f, 0.11f);
        this.lineOfSightCache.clear();
        this.inverseLineOfSightCache.clear();
        this.friendlyFireCache.clear();
    }

    protected static boolean rayIsIntersectingEntity(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3d pos, @Nonnull Vector3d dir, @Nonnull Vector2d minMax, double length2, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        double pz;
        double dz;
        double py;
        double dy;
        BoundingBox boundingBoxComponent = componentAccessor.getComponent(ref, BOUNDING_BOX_COMPONENT_TYPE);
        if (boundingBoxComponent == null) {
            return false;
        }
        TransformComponent transformComponent = componentAccessor.getComponent(ref, TRANSFORM_COMPONENT_TYPE);
        assert (transformComponent != null);
        Vector3d position = transformComponent.getPosition();
        double px = position.getX();
        double dx = px - pos.x;
        double dotProduct = NPCPhysicsMath.dotProduct(dir.x, dir.y, dir.z, dx, dy = (py = position.getY()) - pos.y, dz = (pz = position.getZ()) - pos.z);
        if (dotProduct <= 0.0) {
            return false;
        }
        double dist2 = NPCPhysicsMath.dotProduct(dx, dy, dz);
        if (dotProduct * dotProduct >= dist2 * length2) {
            return false;
        }
        return CollisionMath.intersectRayAABB(pos, dir, px, py, pz, boundingBoxComponent.getBoundingBox(), minMax);
    }

    @Nonnull
    public List<Ref<EntityStore>> getDroppedItemList() {
        return this.droppedItems;
    }

    @Nonnull
    public List<Ref<EntityStore>> getSpawnMarkerList() {
        return this.spawnMarkers;
    }

    @Nonnull
    public List<Ref<EntityStore>> getSpawnBeaconList() {
        return this.spawnBeacons;
    }

    private static class LineOfSightBuffer {
        @Nullable
        public World world;
        @Nullable
        public WorldChunk chunk;
        @Nullable
        public IntSet opaqueSet;
        @Nullable
        public BlockTypeAssetMap<String, BlockType> assetMap;
        public boolean result;

        private LineOfSightBuffer() {
        }

        public void clearRefs() {
            this.world = null;
            this.chunk = null;
            this.opaqueSet = null;
            this.assetMap = null;
        }
    }

    private static class LineOfSightEntityBuffer {
        public final Vector3d pos = new Vector3d();
        public final Vector3d dir = new Vector3d();
        public final Vector2d minMax = new Vector2d();

        private LineOfSightEntityBuffer() {
        }
    }

    @FunctionalInterface
    public static interface RayPredicate<T> {
        public boolean test(double var1, double var3, double var5, double var7, double var9, double var11, T var13, @Nonnull ComponentAccessor<EntityStore> var14);
    }
}

