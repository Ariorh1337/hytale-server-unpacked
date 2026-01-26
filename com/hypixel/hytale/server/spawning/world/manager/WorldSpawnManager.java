/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.world.manager;

import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import com.hypixel.hytale.server.spawning.assets.spawns.config.RoleSpawnParameters;
import com.hypixel.hytale.server.spawning.assets.spawns.config.WorldNPCSpawn;
import com.hypixel.hytale.server.spawning.managers.SpawnManager;
import com.hypixel.hytale.server.spawning.world.WorldEnvironmentSpawnData;
import com.hypixel.hytale.server.spawning.world.component.SpawnJobData;
import com.hypixel.hytale.server.spawning.world.component.WorldSpawnData;
import com.hypixel.hytale.server.spawning.world.manager.EnvironmentSpawnParameters;
import com.hypixel.hytale.server.spawning.world.manager.WorldSpawnWrapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldSpawnManager
extends SpawnManager<WorldSpawnWrapper, WorldNPCSpawn> {
    protected final Int2ObjectConcurrentHashMap<EnvironmentSpawnParameters> environmentSpawnParametersMap = new Int2ObjectConcurrentHashMap();
    protected final Long2IntMap npcEnvCombinations = new Long2IntOpenHashMap();
    protected final Int2ObjectMap<IntSet> npcTypesPerEnvironment = new Int2ObjectOpenHashMap<IntSet>();

    public WorldSpawnManager() {
        this.npcEnvCombinations.defaultReturnValue(Integer.MIN_VALUE);
    }

    @Override
    @Nullable
    public WorldSpawnWrapper removeSpawnWrapper(int spawnConfigurationIndex) {
        WorldSpawnWrapper spawnWrapper = (WorldSpawnWrapper)super.removeSpawnWrapper(spawnConfigurationIndex);
        if (spawnWrapper == null) {
            return null;
        }
        IntSet environments = ((WorldNPCSpawn)spawnWrapper.getSpawn()).getEnvironmentIds();
        IntIterator intIterator = environments.iterator();
        while (intIterator.hasNext()) {
            int environmentIndex = (Integer)intIterator.next();
            EnvironmentSpawnParameters envConfigs = this.environmentSpawnParametersMap.get(environmentIndex);
            if (envConfigs == null) continue;
            envConfigs.getSpawnWrappers().remove(spawnWrapper);
        }
        intIterator = spawnWrapper.getRoles().keySet().iterator();
        while (intIterator.hasNext()) {
            int npcIndex = (Integer)intIterator.next();
            for (World world : Universe.get().getWorlds().values()) {
                WorldSpawnManager.onRoleRemoved(world, npcIndex, environments);
            }
            for (Integer environment : environments) {
                this.removeCombination(npcIndex, environment);
            }
        }
        return spawnWrapper;
    }

    @Override
    public boolean addSpawnWrapper(@Nonnull WorldSpawnWrapper spawnWrapper) {
        WorldNPCSpawn spawn = (WorldNPCSpawn)spawnWrapper.getSpawn();
        IndexedLookupTableAssetMap<String, Environment> assetMap = Environment.getAssetMap();
        IntSet environments = ((WorldNPCSpawn)spawnWrapper.getSpawn()).getEnvironmentIds();
        Int2ObjectMap<RoleSpawnParameters> npcs = spawnWrapper.getRoles();
        int spawnConfigIndex = spawnWrapper.getSpawnIndex();
        for (Integer npcIndex : npcs.keySet()) {
            for (Integer n : environments) {
                if (!this.haveCombination(npcIndex, n)) continue;
                SpawningPlugin.get().getLogger().at(Level.SEVERE).log("Spawning Configuration %s can't be utilised: Combination NPC %s with Environment %s defined before in configuration %s", spawn.getId(), NPCPlugin.get().getName(npcIndex), assetMap.getAsset(n).getId(), this.getCombination(npcIndex, n));
                return false;
            }
        }
        for (Integer environmentIndex : environments) {
            EnvironmentSpawnParameters environmentSpawnParameters = this.environmentSpawnParametersMap.get(environmentIndex);
            if (environmentSpawnParameters == null) {
                environmentSpawnParameters = this.createEnvironmentSpawnParameters(environmentIndex, assetMap.getAsset(environmentIndex));
            }
            environmentSpawnParameters.getSpawnWrappers().add(spawnWrapper);
        }
        for (World world : Universe.get().getWorlds().values()) {
            for (Int2ObjectMap.Entry entry : npcs.int2ObjectEntrySet()) {
                int npcIndex = entry.getIntKey();
                RoleSpawnParameters roleSpawnParameters = (RoleSpawnParameters)entry.getValue();
                WorldSpawnManager.onRoleAdded(world, npcIndex, environments, spawnWrapper, roleSpawnParameters);
            }
        }
        for (Integer npcIndex : npcs.keySet()) {
            for (Integer n : environments) {
                this.addCombination(npcIndex, n, spawnConfigIndex);
            }
        }
        super.addSpawnWrapper(spawnWrapper);
        return true;
    }

    public IntSet getRolesForEnvironment(int environment) {
        return (IntSet)this.npcTypesPerEnvironment.get(environment);
    }

    @Nonnull
    public EnvironmentSpawnParameters createEnvironmentSpawnParameters(int environmentIndex, @Nullable Environment environment) {
        EnvironmentSpawnParameters environmentSpawnParameters = new EnvironmentSpawnParameters(environment != null ? environment.getSpawnDensity() : 0.0);
        this.environmentSpawnParametersMap.put(environmentIndex, environmentSpawnParameters);
        return environmentSpawnParameters;
    }

    public EnvironmentSpawnParameters getEnvironmentSpawnParameters(int environmentIndex) {
        return this.environmentSpawnParametersMap.get(environmentIndex);
    }

    public void updateSpawnParameters(int environmentIndex, @Nullable Environment environment) {
        EnvironmentSpawnParameters spawnParameters = this.getEnvironmentSpawnParameters(environmentIndex);
        if (spawnParameters == null) {
            this.createEnvironmentSpawnParameters(environmentIndex, environment);
        } else {
            spawnParameters.setDensity(environment != null ? environment.getSpawnDensity() : 0.0);
        }
    }

    public void rebuildConfigurations(@Nullable IntSet changeSet) {
        if (changeSet == null || changeSet.isEmpty()) {
            return;
        }
        WorldSpawnManager.untrackNPCs(changeSet);
        int setupCount = 0;
        for (Integer configIndex : changeSet) {
            this.removeSpawnWrapper(configIndex);
            WorldNPCSpawn spawn = WorldNPCSpawn.getAssetMap().getAssetOrDefault(configIndex, null);
            if (spawn == null || !this.addSpawnWrapper(new WorldSpawnWrapper(spawn))) continue;
            ++setupCount;
        }
        WorldSpawnManager.trackNPCs(changeSet);
        SpawningPlugin.get().getLogger().at(Level.INFO).log("Successfully rebuilt %s world spawn configurations", setupCount);
    }

    public static void trackNPCs(@Nonnull IntSet spawnConfigs) {
        Universe.get().getWorlds().forEach((name, world) -> world.execute(() -> {
            Store<EntityStore> store = world.getEntityStore().getStore();
            WorldSpawnData worldSpawnData = store.getResource(WorldSpawnData.getResourceType());
            store.forEachChunk(NPCEntity.getComponentType(), (archetypeChunk, commandBuffer) -> {
                for (int index = 0; index < archetypeChunk.size(); ++index) {
                    boolean isTracked;
                    NPCEntity npc = archetypeChunk.getComponent(index, NPCEntity.getComponentType());
                    int spawnConfiguration = npc.getSpawnConfiguration();
                    if (spawnConfiguration < 0 || !spawnConfigs.contains(spawnConfiguration) || (isTracked = npc.updateSpawnTrackingState(true))) continue;
                    worldSpawnData.trackNPC(npc.getEnvironment(), npc.getRoleIndex(), 1, (World)world, (ComponentAccessor<EntityStore>)commandBuffer);
                }
            });
        }));
    }

    @Override
    public void untrackNPCs(int spawnConfig) {
        if (spawnConfig < 0) {
            return;
        }
        Universe.get().getWorlds().forEach((name, world) -> world.execute(() -> world.getEntityStore().getStore().forEachChunk(NPCEntity.getComponentType(), (archetypeChunk, commandBuffer) -> {
            for (int index = 0; index < archetypeChunk.size(); ++index) {
                NPCEntity npc = archetypeChunk.getComponent(index, NPCEntity.getComponentType());
                if (npc.getSpawnConfiguration() != spawnConfig) continue;
                WorldSpawnManager.untrackNPC(world, npc);
            }
        })));
    }

    public static void untrackNPCs(@Nonnull IntSet spawnConfigs) {
        Universe.get().getWorlds().forEach((name, world) -> world.execute(() -> world.getEntityStore().getStore().forEachChunk(NPCEntity.getComponentType(), (archetypeChunk, commandBuffer) -> {
            for (int index = 0; index < archetypeChunk.size(); ++index) {
                NPCEntity npc = archetypeChunk.getComponent(index, NPCEntity.getComponentType());
                int spawnConfiguration = npc.getSpawnConfiguration();
                if (spawnConfiguration < 0 || !spawnConfigs.contains(spawnConfiguration)) continue;
                WorldSpawnManager.untrackNPC(world, npc);
            }
        })));
    }

    public static void onEnvironmentChanged() {
        Universe.get().getWorlds().forEach((name, world) -> world.execute(() -> WorldSpawnManager.onEnvironmentChanged(world)));
    }

    private static void untrackNPC(@Nonnull World world, @Nonnull NPCEntity npc) {
        boolean isTracked = npc.updateSpawnTrackingState(false);
        if (!isTracked) {
            return;
        }
        WorldSpawnData worldSpawnData = world.getEntityStore().getStore().getResource(WorldSpawnData.getResourceType());
        worldSpawnData.untrackNPC(npc.getEnvironment(), npc.getRoleIndex(), 1);
    }

    private static void onEnvironmentChanged(@Nonnull World world) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
        WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
        WorldSpawnData worldSpawnData = store.getResource(WorldSpawnData.getResourceType());
        worldSpawnData.forEachEnvironmentSpawnData(worldEnvironmentSpawnData -> {
            EnvironmentSpawnParameters environmentSpawnParameters = SpawningPlugin.get().getWorldEnvironmentSpawnParameters(worldEnvironmentSpawnData.getEnvironmentIndex());
            if (environmentSpawnParameters != null) {
                worldEnvironmentSpawnData.setDensity(environmentSpawnParameters.getSpawnDensity(), chunkStore);
                for (WorldSpawnWrapper config : environmentSpawnParameters.getSpawnWrappers()) {
                    worldEnvironmentSpawnData.updateNPCs(config, world);
                }
                int moonPhase = worldTimeResource.getMoonPhase();
                worldEnvironmentSpawnData.recalculateWeight(moonPhase);
                worldEnvironmentSpawnData.resetUnspawnable();
            } else {
                worldEnvironmentSpawnData.setDensity(0.0, chunkStore);
                worldEnvironmentSpawnData.clearNPCs();
            }
        });
        worldSpawnData.recalculateWorldCount();
    }

    private static void onRoleRemoved(@Nonnull World world, int roleIndex, @Nonnull IntSet environments) {
        world.execute(() -> {
            world.getChunkStore().getStore().forEachEntityParallel(SpawnJobData.getComponentType(), (index, chunk, commandBuffer) -> {
                SpawnJobData spawnJobData = chunk.getComponent(index, SpawnJobData.getComponentType());
                assert (spawnJobData != null);
                if (spawnJobData.getRoleIndex() == roleIndex) {
                    spawnJobData.terminate();
                }
            });
            Store<EntityStore> store = world.getEntityStore().getStore();
            WorldSpawnData worldSpawnData = store.getResource(WorldSpawnData.getResourceType());
            IntIterator i$ = environments.iterator();
            while (i$.hasNext()) {
                int environmentIndex = (Integer)i$.next();
                WorldEnvironmentSpawnData worldEnvironmentSpawnStats = worldSpawnData.getWorldEnvironmentSpawnData(environmentIndex);
                if (worldEnvironmentSpawnStats == null) continue;
                worldEnvironmentSpawnStats.removeNPC(roleIndex, store);
            }
        });
    }

    private static void onRoleAdded(@Nonnull World world, int roleIndex, @Nonnull IntSet environments, WorldSpawnWrapper spawnWrapper, @Nonnull RoleSpawnParameters spawnParams) {
        world.execute(() -> {
            Store<EntityStore> store = world.getEntityStore().getStore();
            WorldSpawnData worldSpawnData = store.getResource(WorldSpawnData.getResourceType());
            IntIterator i$ = environments.iterator();
            while (i$.hasNext()) {
                int environmentIndex = (Integer)i$.next();
                worldSpawnData.getOrCreateWorldEnvironmentSpawnData(environmentIndex, world, store).addNPC(roleIndex, spawnWrapper, spawnParams, world, store);
            }
            WorldSpawnManager.onEnvironmentChanged();
        });
    }

    private static long combinedIndex(int npc, int environment) {
        return ((long)npc << 32) + (long)environment;
    }

    private boolean haveCombination(int npc, int environment) {
        return this.npcEnvCombinations.containsKey(WorldSpawnManager.combinedIndex(npc, environment));
    }

    private void addCombination(int npc, int environment, int config) {
        this.npcEnvCombinations.put(WorldSpawnManager.combinedIndex(npc, environment), config);
        this.npcTypesPerEnvironment.computeIfAbsent(environment, i -> new IntOpenHashSet()).add(npc);
    }

    private void removeCombination(int npc, int environment) {
        this.npcEnvCombinations.remove(WorldSpawnManager.combinedIndex(npc, environment));
        ((IntSet)this.npcTypesPerEnvironment.get(environment)).remove(npc);
    }

    private String getCombination(int npc, int environment) {
        return WorldNPCSpawn.getAssetMap().getAsset(this.npcEnvCombinations.get(WorldSpawnManager.combinedIndex(npc, environment))).getId();
    }
}

