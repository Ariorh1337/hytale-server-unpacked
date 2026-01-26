/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.local;

import com.hypixel.hytale.builtin.weather.components.WeatherTracker;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.function.function.TriIntObjectDoubleToByteFunction;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import com.hypixel.hytale.server.spawning.assets.spawns.LightType;
import com.hypixel.hytale.server.spawning.assets.spawns.config.BeaconNPCSpawn;
import com.hypixel.hytale.server.spawning.beacons.LegacySpawnBeaconEntity;
import com.hypixel.hytale.server.spawning.local.LocalSpawnBeacon;
import com.hypixel.hytale.server.spawning.local.LocalSpawnController;
import com.hypixel.hytale.server.spawning.local.LocalSpawnState;
import com.hypixel.hytale.server.spawning.util.LightRangePredicate;
import com.hypixel.hytale.server.spawning.wrappers.BeaconSpawnWrapper;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class LocalSpawnControllerSystem
extends TickingSystem<EntityStore> {
    public static final double RUN_FREQUENCY_SECONDS = 5.0;
    private static final int LIGHT_LEVEL_EVALUATION_RADIUS = 4;
    private final Archetype<EntityStore> controllerArchetype;
    private final ComponentType<EntityStore, LocalSpawnController> spawnControllerComponentType;
    private final ComponentType<EntityStore, TransformComponent> transformComponentype;
    private final ComponentType<EntityStore, WeatherTracker> weatherTrackerComponentType;
    private final ComponentType<EntityStore, LocalSpawnBeacon> localSpawnBeaconComponentType;
    private final ComponentType<EntityStore, LegacySpawnBeaconEntity> spawnBeaconComponentType;
    private final ResourceType<EntityStore, LocalSpawnState> localSpawnStateResourceType;
    private final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> beaconSpatialComponent;

    public LocalSpawnControllerSystem(ComponentType<EntityStore, LocalSpawnController> spawnControllerComponentType, ComponentType<EntityStore, TransformComponent> transformComponentype, ComponentType<EntityStore, WeatherTracker> weatherTrackerComponentType, ComponentType<EntityStore, LocalSpawnBeacon> localSpawnBeaconComponentType, ComponentType<EntityStore, LegacySpawnBeaconEntity> spawnBeaconComponentType, ResourceType<EntityStore, LocalSpawnState> localSpawnStateResourceType, ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> beaconSpatialComponent) {
        this.spawnControllerComponentType = spawnControllerComponentType;
        this.transformComponentype = transformComponentype;
        this.weatherTrackerComponentType = weatherTrackerComponentType;
        this.localSpawnBeaconComponentType = localSpawnBeaconComponentType;
        this.spawnBeaconComponentType = spawnBeaconComponentType;
        this.localSpawnStateResourceType = localSpawnStateResourceType;
        this.beaconSpatialComponent = beaconSpatialComponent;
        this.controllerArchetype = Archetype.of(spawnControllerComponentType, PlayerRef.getComponentType(), transformComponentype, weatherTrackerComponentType);
    }

    @Override
    public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
        LocalSpawnState localSpawnState = store.getResource(this.localSpawnStateResourceType);
        List<Ref<EntityStore>> controllers = localSpawnState.getLocalControllerList();
        store.forEachChunk(this.controllerArchetype, (archetypeChunk, commandBuffer) -> {
            for (int index = 0; index < archetypeChunk.size(); ++index) {
                LocalSpawnController spawnControllerComponent = archetypeChunk.getComponent(index, this.spawnControllerComponentType);
                assert (spawnControllerComponent != null);
                if (!spawnControllerComponent.tickTimeToNextRunSeconds(dt)) continue;
                controllers.add(archetypeChunk.getReferenceTo(index));
            }
        });
        if (controllers.isEmpty()) {
            return;
        }
        World world = store.getExternalData().getWorld();
        List<LegacySpawnBeaconEntity> pendingSpawns = localSpawnState.getLocalPendingSpawns();
        ObjectList existingBeacons = SpatialResource.getThreadLocalReferenceList();
        for (int index = 0; index < controllers.size(); ++index) {
            Ref<EntityStore> reference = controllers.get(index);
            LocalSpawnController spawnControllerComponent = store.getComponent(reference, this.spawnControllerComponentType);
            assert (spawnControllerComponent != null);
            PlayerRef playerRefComponent = store.getComponent(reference, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            SpawningPlugin.get().getLogger().at(Level.FINE).log("Running local spawn controller for player %s", playerRefComponent.getUsername());
            TransformComponent transformComponent = store.getComponent(reference, this.transformComponentype);
            assert (transformComponent != null);
            WeatherTracker weatherTrackerComponent = store.getComponent(reference, this.weatherTrackerComponentType);
            assert (weatherTrackerComponent != null);
            weatherTrackerComponent.updateEnvironment(transformComponent, store);
            int environmentIndex = weatherTrackerComponent.getEnvironmentId();
            List<BeaconSpawnWrapper> possibleBeacons = SpawningPlugin.get().getBeaconSpawnsForEnvironment(environmentIndex);
            if (possibleBeacons == null || possibleBeacons.isEmpty()) {
                spawnControllerComponent.setTimeToNextRunSeconds(5.0);
                continue;
            }
            BeaconSpawnWrapper firstBeacon = possibleBeacons.getFirst();
            double largestDistance = firstBeacon.getBeaconRadius();
            int[] firstRange = ((BeaconNPCSpawn)firstBeacon.getSpawn()).getYRange();
            int lowestY = firstRange[0];
            int highestY = firstRange[1];
            for (int i = 1; i < possibleBeacons.size(); ++i) {
                int[] yRange;
                BeaconSpawnWrapper beacon = possibleBeacons.get(i);
                double radius = beacon.getBeaconRadius();
                if (radius > largestDistance) {
                    largestDistance = radius;
                }
                if ((yRange = ((BeaconNPCSpawn)beacon.getSpawn()).getYRange())[0] < lowestY) {
                    lowestY = yRange[0];
                }
                if (yRange[1] <= highestY) continue;
                highestY = yRange[1];
            }
            Vector3d position = transformComponent.getPosition();
            double largestDistanceSquared = (largestDistance *= 2.0) * largestDistance;
            int yDistance = Math.abs(lowestY) + Math.abs(highestY);
            int y = MathUtil.floor(position.getY());
            int minY = Math.max(0, y - yDistance);
            int maxY = Math.min(319, y + yDistance);
            SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = store.getResource(this.beaconSpatialComponent);
            spatialResource.getSpatialStructure().ordered(position, largestDistance, existingBeacons);
            WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
            double sunlightFactor = worldTimeResource.getSunlightFactor();
            int xPos = MathUtil.floor(position.getX());
            int yPos = MathUtil.floor(position.getY());
            int zPos = MathUtil.floor(position.getZ());
            Object2ByteOpenHashMap<LightType> averageLightValues = new Object2ByteOpenHashMap<LightType>();
            averageLightValues.defaultReturnValue((byte)-1);
            block2: for (int i = 0; i < possibleBeacons.size(); ++i) {
                Pair<Ref<EntityStore>, LegacySpawnBeaconEntity> beaconEntityPair;
                Ref<EntityStore> beaconRef;
                int j;
                BeaconSpawnWrapper possibleBeacon = possibleBeacons.get(i);
                if (!possibleBeacon.spawnParametersMatch(store)) continue;
                for (j = 0; j < existingBeacons.size(); ++j) {
                    int existingBeaconIndex;
                    Ref existingBeaconReference = (Ref)existingBeacons.get(j);
                    LegacySpawnBeaconEntity existingBeaconComponent = store.getComponent(existingBeaconReference, this.spawnBeaconComponentType);
                    assert (existingBeaconComponent != null);
                    TransformComponent existingBeaconTransformComponent = store.getComponent(existingBeaconReference, this.transformComponentype);
                    assert (existingBeaconTransformComponent != null);
                    double existingY = existingBeaconTransformComponent.getPosition().getY();
                    if (!(existingY > (double)maxY) && !(existingY < (double)minY) && (existingBeaconIndex = existingBeaconComponent.getSpawnWrapper().getSpawnIndex()) == possibleBeacon.getSpawnIndex()) continue block2;
                }
                for (j = 0; j < pendingSpawns.size(); ++j) {
                    int existingBeaconIndex;
                    double zDiff;
                    double xDiff;
                    double distSquared;
                    LegacySpawnBeaconEntity pending = pendingSpawns.get(j);
                    Ref<EntityStore> pendingReference = pending.getReference();
                    TransformComponent pendingTransformComponent = store.getComponent(pendingReference, TransformComponent.getComponentType());
                    assert (pendingTransformComponent != null);
                    Vector3d pendingPosition = pendingTransformComponent.getPosition();
                    double pendingY = pendingPosition.getY();
                    if (!(pendingY > (double)maxY || pendingY < (double)minY || (distSquared = (xDiff = position.x - pendingPosition.x) * xDiff + (zDiff = position.z - pendingPosition.z) * zDiff) > largestDistanceSquared || (existingBeaconIndex = pending.getSpawnWrapper().getSpawnIndex()) != possibleBeacon.getSpawnIndex())) continue block2;
                }
                if (!LocalSpawnControllerSystem.spawnLightLevelMatches(world, xPos, yPos, zPos, sunlightFactor, possibleBeacon, averageLightValues) || (beaconRef = (beaconEntityPair = LegacySpawnBeaconEntity.create(possibleBeacon, transformComponent.getPosition(), transformComponent.getRotation(), store)).first()) == null || !beaconRef.isValid()) continue;
                store.ensureComponent(beaconRef, this.localSpawnBeaconComponentType);
                SpawningPlugin.get().getLogger().at(Level.FINE).log("Placed spawn beacon of type %s at position %s for player %s", ((BeaconNPCSpawn)possibleBeacon.getSpawn()).getId(), position, playerRefComponent.getUsername());
                pendingSpawns.add(beaconEntityPair.second());
            }
            existingBeacons.clear();
            averageLightValues.clear();
            spawnControllerComponent.setTimeToNextRunSeconds(5.0);
        }
        controllers.clear();
        pendingSpawns.clear();
    }

    private static boolean spawnLightLevelMatches(@Nonnull World world, int x, int y, int z, double sunlightFactor, @Nonnull BeaconSpawnWrapper wrapper, @Nonnull Object2ByteMap<LightType> averageValues) {
        byte lightValue;
        LightRangePredicate lightRangePredicate = wrapper.getLightRangePredicate();
        if (lightRangePredicate.isTestLightValue() && !lightRangePredicate.testLight(lightValue = LocalSpawnControllerSystem.getCachedAverageLightValue(LightType.Light, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> LightRangePredicate.calculateLightValue(_chunk, _x, _y, _z, _sunlightFactor), averageValues))) {
            return false;
        }
        if (lightRangePredicate.isTestSkyLightValue() && !lightRangePredicate.testSkyLight(lightValue = LocalSpawnControllerSystem.getCachedAverageLightValue(LightType.SkyLight, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> _chunk.getSkyLight(_x, _y, _z), averageValues))) {
            return false;
        }
        if (lightRangePredicate.isTestSunlightValue() && !lightRangePredicate.testSunlight(lightValue = LocalSpawnControllerSystem.getCachedAverageLightValue(LightType.Sunlight, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> (byte)((double)_chunk.getSkyLight(_x, _y, _z) * _sunlightFactor), averageValues))) {
            return false;
        }
        if (lightRangePredicate.isTestRedLightValue() && !lightRangePredicate.testRedLight(lightValue = LocalSpawnControllerSystem.getCachedAverageLightValue(LightType.RedLight, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> _chunk.getRedBlockLight(_x, _y, _z), averageValues))) {
            return false;
        }
        if (lightRangePredicate.isTestGreenLightValue() && !lightRangePredicate.testGreenLight(lightValue = LocalSpawnControllerSystem.getCachedAverageLightValue(LightType.GreenLight, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> _chunk.getGreenBlockLight(_x, _y, _z), averageValues))) {
            return false;
        }
        if (lightRangePredicate.isTestBlueLightValue()) {
            lightValue = LocalSpawnControllerSystem.getCachedAverageLightValue(LightType.BlueLight, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> _chunk.getBlueBlockLight(_x, _y, _z), averageValues);
            return lightRangePredicate.testBlueLight(lightValue);
        }
        return true;
    }

    private static byte getCachedAverageLightValue(LightType lightType, @Nonnull World world, int x, int y, int z, double sunlightFactor, @Nonnull TriIntObjectDoubleToByteFunction<BlockChunk> valueCalculator, @Nonnull Object2ByteMap<LightType> averageValues) {
        byte cachedValue = averageValues.getByte((Object)lightType);
        if (cachedValue < 0) {
            int counted = 0;
            int total = 0;
            for (int xOffset = x - 4; xOffset < x + 4; ++xOffset) {
                for (int zOffset = z - 4; zOffset < z + 4; ++zOffset) {
                    WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(xOffset, zOffset));
                    if (chunk == null) continue;
                    BlockChunk blockChunk = chunk.getBlockChunk();
                    for (int yOffset = y; yOffset < y + 4; ++yOffset) {
                        int blockId = chunk.getBlock(xOffset, yOffset, zOffset);
                        if (blockId != 0 && BlockType.getAssetMap().getAsset(blockId).getMaterial() == BlockMaterial.Solid) continue;
                        ++counted;
                        total += valueCalculator.apply(xOffset, yOffset, zOffset, blockChunk, sunlightFactor);
                    }
                }
            }
            cachedValue = counted > 0 ? (byte)((float)total / (float)counted) : (byte)0;
            averageValues.put(lightType, cachedValue);
        }
        return cachedValue;
    }
}

