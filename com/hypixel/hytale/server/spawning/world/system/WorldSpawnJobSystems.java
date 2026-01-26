/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.world.system;

import com.hypixel.fastutil.longs.Long2ObjectConcurrentHashMap;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.NonTicking;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.FlockPlugin;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.spawning.ISpawnableWithModel;
import com.hypixel.hytale.server.spawning.SpawnRejection;
import com.hypixel.hytale.server.spawning.SpawnTestResult;
import com.hypixel.hytale.server.spawning.SpawningContext;
import com.hypixel.hytale.server.spawning.assets.spawns.config.NPCSpawn;
import com.hypixel.hytale.server.spawning.suppression.SuppressionSpanHelper;
import com.hypixel.hytale.server.spawning.suppression.component.ChunkSuppressionEntry;
import com.hypixel.hytale.server.spawning.suppression.component.SpawnSuppressionController;
import com.hypixel.hytale.server.spawning.util.RandomChunkColumnIterator;
import com.hypixel.hytale.server.spawning.world.ChunkEnvironmentSpawnData;
import com.hypixel.hytale.server.spawning.world.WorldEnvironmentSpawnData;
import com.hypixel.hytale.server.spawning.world.component.ChunkSpawnData;
import com.hypixel.hytale.server.spawning.world.component.ChunkSpawnedNPCData;
import com.hypixel.hytale.server.spawning.world.component.SpawnJobData;
import com.hypixel.hytale.server.spawning.world.component.WorldSpawnData;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldSpawnJobSystems {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Query<ChunkStore> QUERY = Archetype.of(SpawnJobData.getComponentType(), WorldChunk.getComponentType());
    private static final Query<ChunkStore> TICKING_QUERY = Query.and(Query.not(ChunkStore.REGISTRY.getNonTickingComponentType()), QUERY);
    private static final int JOB_BUDGET = 64;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    private static Result run(@Nonnull SpawnJobData spawnJobData, @Nonnull WorldChunk chunk, @Nonnull ChunkEnvironmentSpawnData chunkEnvironmentSpawnData, @Nonnull WorldSpawnData worldSpawnData, @Nonnull SpawnSuppressionController spawnSuppressionController) {
        ISpawnableWithModel spawnable;
        int roleIndex = spawnJobData.getRoleIndex();
        try {
            spawnable = WorldSpawnJobSystems.getSpawnable(roleIndex);
        }
        catch (IllegalArgumentException e) {
            WorldSpawnJobSystems.endProbing(Result.PERMANENT_FAILURE, spawnJobData, chunk, worldSpawnData);
            throw e;
        }
        if (spawnable == null) {
            HytaleLogger.Api context = LOGGER.at(Level.FINEST);
            if (context.isEnabled()) {
                context.log("Spawn job %s: Terminated, spawnable %s gone", spawnJobData.getJobId(), (Object)WorldSpawnJobSystems.getSpawnableName(roleIndex));
            }
            return WorldSpawnJobSystems.endProbing(Result.FAILED, spawnJobData, chunk, worldSpawnData);
        }
        SpawningContext spawningContext = spawnJobData.getSpawningContext();
        if (!spawningContext.setSpawnable(spawnable)) {
            HytaleLogger.Api context = LOGGER.at(Level.FINEST);
            if (context.isEnabled()) {
                context.log("Spawn job %s: Terminated, Unable to set spawnable %s", spawnJobData.getJobId(), (Object)WorldSpawnJobSystems.getSpawnableName(roleIndex));
            }
            return WorldSpawnJobSystems.endProbing(Result.FAILED, spawnJobData, chunk, worldSpawnData);
        }
        spawningContext.setChunk(chunk, spawnJobData.getEnvironmentIndex());
        SuppressionSpanHelper suppressionSpanHelper = spawnJobData.getSuppressionSpanHelper();
        Long2ObjectConcurrentHashMap<ChunkSuppressionEntry> chunkSuppressionMap = spawnSuppressionController.getChunkSuppressionMap();
        suppressionSpanHelper.optimiseSuppressedSpans(roleIndex, chunkSuppressionMap.get(chunk.getIndex()));
        try {
            IntSet spawnBlockSet = spawnJobData.getSpawnConfig().getSpawnBlockSet(roleIndex);
            int spawnFluidTag = spawnJobData.getSpawnConfig().getSpawnFluidTag(roleIndex);
            RandomChunkColumnIterator iterator = chunkEnvironmentSpawnData.getRandomChunkColumnIterator();
            spawnJobData.setBudgetUsed(0);
            while (spawnJobData.getBudgetUsed() < 64) {
                iterator.nextPositionAvoidBorders();
                spawnJobData.adjustBudgetUsed(3);
                spawningContext.setColumn(iterator.getCurrentX(), iterator.getCurrentZ(), suppressionSpanHelper);
                Result result = WorldSpawnJobSystems.trySpawn(spawnable, spawnBlockSet, spawnFluidTag, spawnJobData, chunk, chunkEnvironmentSpawnData, worldSpawnData);
                if (result == Result.TRY_AGAIN) continue;
                Result result2 = result;
                return result2;
            }
        }
        finally {
            spawningContext.release();
        }
        return Result.TRY_AGAIN;
    }

    @Nullable
    private static ISpawnableWithModel getSpawnable(int roleIndex) {
        Builder<Role> role = NPCPlugin.get().tryGetCachedValidRole(roleIndex);
        if (role == null) {
            return null;
        }
        if (!role.isSpawnable()) {
            throw new IllegalArgumentException("Spawn job: Role must be a spawnable (non-abstract) type for spawning: " + NPCPlugin.get().getName(roleIndex));
        }
        if (!(role instanceof ISpawnableWithModel)) {
            throw new IllegalArgumentException("Spawn job: Need ISpawnableWithModel interface for spawning: " + NPCPlugin.get().getName(roleIndex));
        }
        return (ISpawnableWithModel)((Object)role);
    }

    @Nonnull
    private static Result trySpawn(@Nonnull ISpawnableWithModel spawnable, IntSet spawnBlockSet, int spawnFluidTag, @Nonnull SpawnJobData spawnJobData, @Nonnull WorldChunk worldChunk, @Nonnull ChunkEnvironmentSpawnData environmentSpawnData, @Nonnull WorldSpawnData worldSpawnData) {
        spawnJobData.incrementTotalColumnsTested();
        SpawningContext spawningContext = spawnJobData.getSpawningContext();
        try {
            int spansBlocked = 0;
            int spansTested = 0;
            while (spawningContext.selectRandomSpawnSpan()) {
                spawnJobData.adjustBudgetUsed(1);
                spawnJobData.incrementSpansTried();
                ++spansTested;
                if (!spawnJobData.getSpawnConfig().withinLightRange(spawningContext)) {
                    WorldSpawnJobSystems.rejectSpan(spawnJobData.getRejectionMap(), SpawnRejection.OUTSIDE_LIGHT_RANGE);
                } else if (!WorldSpawnJobSystems.canSpawnOnBlock(spawnBlockSet, spawnFluidTag, spawningContext)) {
                    ++spansBlocked;
                    WorldSpawnJobSystems.rejectSpan(spawnJobData.getRejectionMap(), SpawnRejection.INVALID_SPAWN_BLOCK);
                } else {
                    SpawnTestResult spawnTestResult = spawningContext.canSpawn();
                    if (spawnTestResult == SpawnTestResult.TEST_OK) {
                        return WorldSpawnJobSystems.spawn(spawnJobData, worldChunk, worldSpawnData);
                    }
                    if (spawnTestResult == SpawnTestResult.FAIL_INVALID_POSITION) {
                        WorldSpawnJobSystems.rejectSpan(spawnJobData.getRejectionMap(), SpawnRejection.INVALID_POSITION);
                        ++spansBlocked;
                    } else if (spawnTestResult == SpawnTestResult.FAIL_NO_POSITION) {
                        WorldSpawnJobSystems.rejectSpan(spawnJobData.getRejectionMap(), SpawnRejection.NO_POSITION);
                        ++spansBlocked;
                    } else if (spawnTestResult == SpawnTestResult.FAIL_NOT_BREATHABLE) {
                        WorldSpawnJobSystems.rejectSpan(spawnJobData.getRejectionMap(), SpawnRejection.NOT_BREATHABLE);
                        ++spansBlocked;
                    } else {
                        WorldSpawnJobSystems.rejectSpan(spawnJobData.getRejectionMap(), SpawnRejection.OTHER);
                    }
                }
                spawningContext.deleteCurrentSpawnSpan();
            }
            if (spansBlocked > 0 && spansTested == spansBlocked) {
                spawnJobData.incrementTotalColumnsBlocked();
            }
        }
        catch (IllegalStateException | NullPointerException e) {
            LOGGER.at(Level.WARNING).log("%s with spawnable=%s spwnCfg=%s X/Y/Z=%s/%s/%s", e.getMessage(), WorldSpawnJobSystems.getSpawnableName(spawnJobData.getRoleIndex()), ((NPCSpawn)spawnJobData.getSpawnConfig().getSpawn()).getId(), spawningContext.xSpawn, spawningContext.ySpawn, spawningContext.zSpawn);
            spawnable.markNeedsReload();
            return WorldSpawnJobSystems.endProbing(Result.FAILED, spawnJobData, worldChunk, worldSpawnData);
        }
        if (environmentSpawnData.getRandomChunkColumnIterator().isAtSavedIteratorPosition()) {
            if (spawnJobData.getTotalColumnsBlocked() == spawnJobData.getTotalColumnsTested()) {
                environmentSpawnData.markRoleAsUnspawnable(spawnJobData.getRoleIndex());
                HytaleLogger.Api context = LOGGER.at(Level.FINEST);
                if (context.isEnabled()) {
                    context.log("Spawn job %s: No column to create %s (env %s) at chunk %s/%s, columns probed %s", spawnJobData.getJobId(), WorldSpawnJobSystems.getSpawnableName(spawnJobData.getRoleIndex()), spawnJobData.getEnvironment().getId(), worldChunk.getX(), worldChunk.getZ(), spawnJobData.getTotalColumnsTested());
                }
                if (environmentSpawnData.allRolesUnspawnable()) {
                    worldSpawnData.queueUnspawnableChunk(spawnJobData.getEnvironmentIndex(), worldChunk.getIndex());
                    if (context.isEnabled()) {
                        context.log("Spawn job %s: All roles unspawnable. Queued for processing.", spawnJobData.getJobId());
                    }
                }
            }
            return WorldSpawnJobSystems.endProbing(Result.FAILED, spawnJobData, worldChunk, worldSpawnData);
        }
        return Result.TRY_AGAIN;
    }

    @Nonnull
    private static Result spawn(@Nonnull SpawnJobData spawnJobData, @Nonnull WorldChunk worldChunk, @Nonnull WorldSpawnData worldSpawnData) {
        NPCPlugin npcModule = NPCPlugin.get();
        SpawningContext spawningContext = spawnJobData.getSpawningContext();
        Vector3d position = spawningContext.newPosition();
        Vector3f rotation = spawningContext.newRotation();
        int roleIndex = spawnJobData.getRoleIndex();
        try {
            Store<EntityStore> store = spawningContext.world.getEntityStore().getStore();
            Pair<Ref<EntityStore>, NPCEntity> npcPair = npcModule.spawnEntity(store, roleIndex, position, rotation, spawningContext.getModel(), (_npc, _holder, _store) -> WorldSpawnJobSystems.preAddToWorld(_npc, _holder, roleIndex, spawnJobData), null);
            NPCEntity npcComponent = npcPair.right();
            Ref<EntityStore> npcRef = npcPair.left();
            FlockPlugin.trySpawnFlock(npcRef, npcComponent, roleIndex, position, rotation, spawnJobData.getFlockSize(), spawnJobData.getFlockAsset(), (_npc, _holder, _store) -> WorldSpawnJobSystems.preAddToWorld(_npc, _holder, roleIndex, spawnJobData), null, store);
        }
        catch (RuntimeException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Spawn job %s: Failed to create %s: %s", spawnJobData.getJobId(), npcModule.getName(roleIndex), e.getMessage());
            WorldSpawnJobSystems.rejectSpan(spawnJobData.getRejectionMap(), SpawnRejection.OTHER);
            return WorldSpawnJobSystems.endProbing(Result.FAILED, spawnJobData, worldChunk, worldSpawnData);
        }
        HytaleLogger.Api context = LOGGER.at(Level.FINEST);
        if (context.isEnabled()) {
            context.log("Spawn job %s: Created %s with flock size %s (env %s) at chunk %s/%s, columns probed %s", spawnJobData.getJobId(), NPCPlugin.get().getName(roleIndex), spawnJobData.getFlockSize(), spawnJobData.getEnvironment().getId(), worldChunk.getX(), worldChunk.getZ(), spawnJobData.getTotalColumnsTested());
        }
        spawnJobData.incrementSpansSuccess();
        return WorldSpawnJobSystems.endProbing(Result.SUCCESS, spawnJobData, worldChunk, worldSpawnData);
    }

    private static void preAddToWorld(@Nonnull NPCEntity npc, @Nonnull Holder<EntityStore> holder, int roleIndex, @Nonnull SpawnJobData spawnJobData) {
        npc.setSpawnRoleIndex(roleIndex);
        if (spawnJobData.isSpawnFrozen()) {
            holder.ensureComponent(Frozen.getComponentType());
        }
        npc.setEnvironment(spawnJobData.getEnvironmentIndex());
        npc.setSpawnConfiguration(spawnJobData.getSpawnConfigIndex());
    }

    private static boolean canSpawnOnBlock(@Nullable IntSet spawnBlockSet, int spawnFluidTag, @Nonnull SpawningContext spawningContext) {
        if (spawnBlockSet == null && spawnFluidTag == Integer.MIN_VALUE) {
            return true;
        }
        if (spawnBlockSet != null && spawnBlockSet.contains(spawningContext.groundBlockId)) {
            return true;
        }
        return spawnFluidTag != Integer.MIN_VALUE && Fluid.getAssetMap().getIndexesForTag(spawnFluidTag).contains(spawningContext.groundFluidId);
    }

    private static void rejectSpan(@Nonnull Object2IntMap<SpawnRejection> rejectionMap, SpawnRejection rejection) {
        rejectionMap.mergeInt(rejection, 1, Integer::sum);
    }

    protected static Result endProbing(Result result, @Nonnull SpawnJobData spawnJobData, @Nonnull WorldChunk worldChunk, @Nonnull WorldSpawnData worldSpawnData) {
        HytaleLogger.Api context = LOGGER.at(Level.FINEST);
        if (context.isEnabled()) {
            context.log("Term Spawnjob id=%s env=%s role=%s chunk=[%s/%s] tested=%s result=%s budgetUsed=%s", spawnJobData.getJobId(), spawnJobData.getEnvironment().getId(), WorldSpawnJobSystems.getSpawnableName(spawnJobData.getRoleIndex()), worldChunk.getX(), worldChunk.getZ(), spawnJobData.getTotalColumnsTested(), (Object)result, spawnJobData.getTotalBudgetUsed());
        }
        worldSpawnData.untrackNPC(spawnJobData.getEnvironmentIndex(), spawnJobData.getRoleIndex(), spawnJobData.getFlockSize());
        WorldSpawnJobSystems.updateSpawnStats(worldSpawnData, spawnJobData, result);
        worldSpawnData.adjustActiveSpawnJobs(-1, -spawnJobData.getFlockSize());
        return result;
    }

    private static void updateSpawnStats(@Nonnull WorldSpawnData worldSpawnData, @Nonnull SpawnJobData spawnJobData, Result result) {
        boolean success = result == Result.SUCCESS;
        WorldEnvironmentSpawnData worldEnvironmentSpawnStats = worldSpawnData.getWorldEnvironmentSpawnData(spawnJobData.getEnvironmentIndex());
        worldEnvironmentSpawnStats.updateSpawnStats(spawnJobData.getRoleIndex(), spawnJobData.getSpansTried(), spawnJobData.getSpansSuccess(), spawnJobData.getTotalBudgetUsed(), spawnJobData.getRejectionMap(), success);
        worldSpawnData.addCompletedSpawnJob(spawnJobData.getTotalBudgetUsed());
    }

    @Nullable
    private static String getSpawnableName(int roleIndex) {
        return NPCPlugin.get().getName(roleIndex);
    }

    protected static enum Result {
        SUCCESS,
        FAILED,
        TRY_AGAIN,
        PERMANENT_FAILURE;

    }

    public static class Ticking
    extends EntityTickingSystem<ChunkStore> {
        private final ResourceType<EntityStore, WorldSpawnData> worldSpawnDataResourceType;
        private final ResourceType<EntityStore, SpawnSuppressionController> spawnSuppressionControllerResourceType;
        private final ComponentType<ChunkStore, SpawnJobData> spawnJobDataComponentType;
        private final ComponentType<ChunkStore, WorldChunk> worldChunkComponentType;
        private final ComponentType<ChunkStore, ChunkSpawnData> chunkSpawnDataComponentType;
        private final ComponentType<ChunkStore, ChunkSpawnedNPCData> chunkSpawnedNPCDataComponentType;

        public Ticking(ResourceType<EntityStore, WorldSpawnData> worldSpawnDataResourceType, ResourceType<EntityStore, SpawnSuppressionController> spawnSuppressionControllerResourceType, ComponentType<ChunkStore, SpawnJobData> spawnJobDataComponentType, ComponentType<ChunkStore, ChunkSpawnData> chunkSpawnDataComponentType, ComponentType<ChunkStore, ChunkSpawnedNPCData> chunkSpawnedNPCDataComponentType) {
            this.worldSpawnDataResourceType = worldSpawnDataResourceType;
            this.spawnSuppressionControllerResourceType = spawnSuppressionControllerResourceType;
            this.spawnJobDataComponentType = spawnJobDataComponentType;
            this.worldChunkComponentType = WorldChunk.getComponentType();
            this.chunkSpawnDataComponentType = chunkSpawnDataComponentType;
            this.chunkSpawnedNPCDataComponentType = chunkSpawnedNPCDataComponentType;
        }

        @Override
        public Query<ChunkStore> getQuery() {
            return QUERY;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return false;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
            World world = store.getExternalData().getWorld();
            Store<EntityStore> entityStore = world.getEntityStore().getStore();
            SpawnJobData spawnJobData = archetypeChunk.getComponent(index, this.spawnJobDataComponentType);
            WorldSpawnData worldSpawnData = entityStore.getResource(this.worldSpawnDataResourceType);
            WorldChunk worldChunk = archetypeChunk.getComponent(index, this.worldChunkComponentType);
            if (spawnJobData.isTerminated() || (double)worldSpawnData.getActualNPCs() > worldSpawnData.getExpectedNPCs()) {
                WorldSpawnJobSystems.endProbing(Result.FAILED, spawnJobData, worldChunk, worldSpawnData);
                commandBuffer.removeComponent(archetypeChunk.getReferenceTo(index), this.spawnJobDataComponentType);
                return;
            }
            ChunkSpawnData chunkSpawnData = archetypeChunk.getComponent(index, this.chunkSpawnDataComponentType);
            ChunkSpawnedNPCData chunkSpawnedNPCData = archetypeChunk.getComponent(index, this.chunkSpawnedNPCDataComponentType);
            int environmentIndex = spawnJobData.getEnvironmentIndex();
            double spawnedNPCs = chunkSpawnedNPCData.getEnvironmentSpawnCount(environmentIndex);
            ChunkEnvironmentSpawnData chunkEnvironmentSpawnData = chunkSpawnData.getEnvironmentSpawnData(environmentIndex);
            if (chunkEnvironmentSpawnData.allRolesUnspawnable() || !spawnJobData.isIgnoreFullyPopulated() && chunkEnvironmentSpawnData.isFullyPopulated(spawnedNPCs)) {
                WorldSpawnJobSystems.endProbing(Result.FAILED, spawnJobData, worldChunk, worldSpawnData);
                commandBuffer.removeComponent(archetypeChunk.getReferenceTo(index), this.spawnJobDataComponentType);
                return;
            }
            SpawnSuppressionController spawnSuppressionController = entityStore.getResource(this.spawnSuppressionControllerResourceType);
            Result result = WorldSpawnJobSystems.run(spawnJobData, worldChunk, chunkEnvironmentSpawnData, worldSpawnData, spawnSuppressionController);
            if (result == Result.SUCCESS) {
                chunkSpawnData.setLastSpawn(System.nanoTime());
            }
            if (result != Result.TRY_AGAIN) {
                commandBuffer.removeComponent(archetypeChunk.getReferenceTo(index), this.spawnJobDataComponentType);
            }
        }
    }

    public static class TickingState
    extends RefChangeSystem<ChunkStore, NonTicking<ChunkStore>> {
        private final ResourceType<EntityStore, WorldSpawnData> worldSpawnDataResourceType;
        private final ComponentType<ChunkStore, SpawnJobData> spawnJobDataComponentType;
        private final ComponentType<ChunkStore, WorldChunk> worldChunkComponentType;

        public TickingState(ResourceType<EntityStore, WorldSpawnData> worldSpawnDataResourceType, ComponentType<ChunkStore, SpawnJobData> spawnJobDataComponentType) {
            this.worldSpawnDataResourceType = worldSpawnDataResourceType;
            this.spawnJobDataComponentType = spawnJobDataComponentType;
            this.worldChunkComponentType = WorldChunk.getComponentType();
        }

        @Override
        public Query<ChunkStore> getQuery() {
            return QUERY;
        }

        @Override
        @Nonnull
        public ComponentType<ChunkStore, NonTicking<ChunkStore>> componentType() {
            return ChunkStore.REGISTRY.getNonTickingComponentType();
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull NonTicking<ChunkStore> component, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
            SpawnJobData spawnJobData = store.getComponent(ref, this.spawnJobDataComponentType);
            WorldChunk worldChunk = store.getComponent(ref, this.worldChunkComponentType);
            WorldSpawnData worldSpawnData = store.getExternalData().getWorld().getEntityStore().getStore().getResource(this.worldSpawnDataResourceType);
            WorldSpawnJobSystems.endProbing(Result.FAILED, spawnJobData, worldChunk, worldSpawnData);
            commandBuffer.removeComponent(ref, this.spawnJobDataComponentType);
        }

        @Override
        public void onComponentSet(@Nonnull Ref<ChunkStore> ref, NonTicking<ChunkStore> oldComponent, @Nonnull NonTicking<ChunkStore> newComponent, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        }

        @Override
        public void onComponentRemoved(@Nonnull Ref<ChunkStore> ref, @Nonnull NonTicking<ChunkStore> component, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        }
    }

    public static class EntityRemoved
    extends HolderSystem<ChunkStore> {
        private final ResourceType<EntityStore, WorldSpawnData> worldSpawnDataResourceType;
        private final ComponentType<ChunkStore, SpawnJobData> spawnJobDataComponentType;
        private final ComponentType<ChunkStore, WorldChunk> worldChunkComponentType;

        public EntityRemoved(ResourceType<EntityStore, WorldSpawnData> worldSpawnDataResourceType, ComponentType<ChunkStore, SpawnJobData> spawnJobDataComponentType) {
            this.worldSpawnDataResourceType = worldSpawnDataResourceType;
            this.spawnJobDataComponentType = spawnJobDataComponentType;
            this.worldChunkComponentType = WorldChunk.getComponentType();
        }

        @Override
        @Nonnull
        public Query<ChunkStore> getQuery() {
            return TICKING_QUERY;
        }

        @Override
        public void onEntityAdd(@Nonnull Holder<ChunkStore> holder, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store) {
        }

        @Override
        public void onEntityRemoved(@Nonnull Holder<ChunkStore> entityHolder, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store) {
            SpawnJobData spawnJobData = entityHolder.getComponent(this.spawnJobDataComponentType);
            WorldChunk worldChunk = entityHolder.getComponent(this.worldChunkComponentType);
            WorldSpawnData worldSpawnData = store.getExternalData().getWorld().getEntityStore().getStore().getResource(this.worldSpawnDataResourceType);
            WorldSpawnJobSystems.endProbing(Result.FAILED, spawnJobData, worldChunk, worldSpawnData);
            entityHolder.removeComponent(this.spawnJobDataComponentType);
        }
    }
}

