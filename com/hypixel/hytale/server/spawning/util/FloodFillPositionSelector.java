/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.util;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.accessor.ChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.accessor.LocalCachedChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.spawning.ISpawnableWithModel;
import com.hypixel.hytale.server.spawning.SpawnTestResult;
import com.hypixel.hytale.server.spawning.SpawningContext;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import com.hypixel.hytale.server.spawning.assets.spawns.config.BeaconNPCSpawn;
import com.hypixel.hytale.server.spawning.assets.spawns.config.RoleSpawnParameters;
import com.hypixel.hytale.server.spawning.suppression.component.ChunkSuppressionEntry;
import com.hypixel.hytale.server.spawning.suppression.component.SpawnSuppressionController;
import com.hypixel.hytale.server.spawning.util.FloodFillEntryPoolSimple;
import com.hypixel.hytale.server.spawning.wrappers.BeaconSpawnWrapper;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Deque;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FloodFillPositionSelector
implements Component<EntityStore> {
    private static final int MAX_SPAWN_POSITIONS_HINT = 30;
    private static final double SPAWN_POSITION_DENSITY = 0.1;
    private static final int CONCURRENT_POSITION_OPTION_MULTIPLIER = 3;
    private static final double MAX_FAILED_SPAWN_POSITION_RATIO = 0.25;
    private static final double IRREGULAR_MIN_SPAWNS_MULTIPLIER = 0.3;
    private static final double IRREGULAR_MAX_SPAWNS_MULTIPLIER = 5.0;
    private static final int NOT_CHECKED = -1;
    private static final int BLOCKED = -2;
    private static final int TOO_HIGH = Integer.MAX_VALUE;
    private static final int TOO_LOW = Integer.MIN_VALUE;
    private static final int MAX_RESOLUTION_DIVISOR = 8;
    private static final ThreadLocal<SortBufferProvider> sortBufferProvider = ThreadLocal.withInitial(SortBufferProvider::new);
    private static final Comparator<Object> WEIGHTED_POSITION_COMPARATOR = (entry1, entry2) -> Double.compare(((WeightedPosition)entry2).getWeight(), ((WeightedPosition)entry1).getWeight());
    private final World world;
    @Nullable
    private ChunkAccessor<WorldChunk> chunkAccessor;
    private final int size;
    private int minY;
    private int maxY;
    @Nonnull
    private final BeaconSpawnWrapper spawnWrapper;
    @Nonnull
    private final int[] roleIndexes;
    @Nonnull
    private final int[] heightGrid;
    @Nonnull
    private final Int2ObjectMap<BitSet> resolutionMaps;
    private final BitSet fullResolutionMap;
    private int desiredPositionCount;
    private final IntArrayList highResolutionOptions = new IntArrayList(4);
    private final Deque<int[]> floodFillQueue = new ArrayDeque<int[]>();
    private final SpawningContext spawningContext = new SpawningContext();
    @Nullable
    private WorldChunk chunk;
    private final IntSet positionIndexes = new IntOpenHashSet();
    private final Int2ObjectMap<ObjectArrayList<WeightedPosition>> positionsByRole = new Int2ObjectOpenHashMap<ObjectArrayList<WeightedPosition>>();
    private final Int2IntMap failedSpawnsByRole = new Int2IntOpenHashMap();
    private boolean hasRun;
    private Debug debug;
    private boolean irregularCase;
    private IntSet failedPositionTestIndexes;
    private double calculatePositionsAfter;

    public static ComponentType<EntityStore, FloodFillPositionSelector> getComponentType() {
        return SpawningPlugin.get().getFloodFillPositionSelectorComponentType();
    }

    public FloodFillPositionSelector(World world, @Nonnull BeaconSpawnWrapper spawnWrapper) {
        this.world = world;
        int baseSize = MathUtil.ceil(spawnWrapper.getSpawnRadius()) * 2;
        this.size = (baseSize + 8 - 1) / 8 * 8;
        this.heightGrid = new int[this.size * this.size];
        Arrays.fill(this.heightGrid, -1);
        this.resolutionMaps = new Int2ObjectOpenHashMap<BitSet>();
        for (int i = 1; i <= 8; i *= 2) {
            this.resolutionMaps.put(i, new BitSet(this.heightGrid.length / i));
        }
        this.fullResolutionMap = (BitSet)this.resolutionMaps.get(1);
        this.spawnWrapper = spawnWrapper;
        RoleSpawnParameters[] roleSpawnParameters = ((BeaconNPCSpawn)spawnWrapper.getSpawn()).getNPCs();
        this.roleIndexes = new int[roleSpawnParameters.length];
        for (int i = 0; i < this.roleIndexes.length; ++i) {
            int roleIndex;
            this.roleIndexes[i] = roleIndex = NPCPlugin.get().getIndex(roleSpawnParameters[i].getId());
            this.positionsByRole.put(roleIndex, (ObjectArrayList<WeightedPosition>)new ObjectArrayList());
        }
        this.debug = ((BeaconNPCSpawn)spawnWrapper.getSpawn()).getDebug();
        if (this.debug != Debug.DISABLED) {
            this.failedPositionTestIndexes = new IntOpenHashSet();
        }
    }

    public void setCalculatePositionsAfter(double calculatePositionsAfter) {
        this.calculatePositionsAfter = calculatePositionsAfter;
    }

    public boolean tickCalculatePositionsAfter(float dt) {
        double d;
        this.calculatePositionsAfter -= (double)dt;
        return d <= 0.0;
    }

    public boolean hasPositionsForRole(int roleIndex) {
        return !((ObjectArrayList)this.positionsByRole.get(roleIndex)).isEmpty();
    }

    public boolean prepareSpawnContext(@Nonnull Vector3d playerPosition, int spawnsThisRound, int roleIndex, @Nonnull SpawningContext spawningContext, @Nonnull BeaconSpawnWrapper spawnWrapper) {
        WeightedPosition entry;
        ObjectArrayList positions = (ObjectArrayList)this.positionsByRole.get(roleIndex);
        if (this.positionsByRole.isEmpty()) {
            return false;
        }
        double minDistanceFromPlayerSquared = spawnWrapper.getMinDistanceFromPlayerSquared();
        double targetDistanceFromPlayerSquared = spawnWrapper.getTargetDistanceFromPlayerSquared();
        for (int i = 0; i < positions.size(); ++i) {
            WeightedPosition entry2 = (WeightedPosition)positions.get(i);
            double distance = playerPosition.distanceSquaredTo(entry2.position);
            entry2.weight = distance < minDistanceFromPlayerSquared ? 0.0 : Math.max(0.0, targetDistanceFromPlayerSquared - Math.abs(distance - targetDistanceFromPlayerSquared));
        }
        int targetNumberOfOptions = spawnsThisRound * 3;
        int size = positions.size();
        WeightedPosition[] sortBuffer = sortBufferProvider.get().getBuffer(size);
        System.arraycopy(positions.elements(), 0, sortBuffer, 0, size);
        ObjectArrays.mergeSort(positions.elements(), 0, size, WEIGHTED_POSITION_COMPARATOR, sortBuffer);
        double sum = 0.0;
        for (int i = 0; i < targetNumberOfOptions && i < positions.size(); ++i) {
            entry = (WeightedPosition)positions.get(i);
            if (entry.weight == 0.0) break;
            sum += entry.weight;
        }
        if (sum == 0.0) {
            return false;
        }
        sum = ThreadLocalRandom.current().nextDouble(sum);
        int selectedIndex = -1;
        entry = null;
        int i = 0;
        while (i < targetNumberOfOptions && i < positions.size()) {
            entry = (WeightedPosition)positions.get(i);
            selectedIndex = i++;
            if ((sum -= entry.weight) < 0.0) break;
        }
        if (entry == null) {
            return false;
        }
        Vector3i position = entry.position;
        SpawnSuppressionController suppressionController = this.world.getEntityStore().getStore().getResource(SpawnSuppressionController.getResourceType());
        long indexChunk = ChunkUtil.indexChunk(ChunkUtil.chunkCoordinate(position.x), ChunkUtil.chunkCoordinate(position.z));
        ChunkSuppressionEntry suppressionEntry = suppressionController.getChunkSuppressionMap().get(indexChunk);
        if (suppressionEntry != null && suppressionEntry.isSuppressingRoleAt(roleIndex, position.y) || !spawningContext.set(this.world, position.x, position.y, position.z) || spawningContext.canSpawn() != SpawnTestResult.TEST_OK) {
            positions.remove(selectedIndex);
            int totalFailed = this.failedSpawnsByRole.mergeInt(roleIndex, 1, Integer::sum);
            if ((double)totalFailed > (double)positions.size() * 0.25) {
                this.hasRun = false;
            }
            return false;
        }
        return true;
    }

    public boolean shouldRebuildCache() {
        return !this.hasRun;
    }

    public void forceRebuildCache() {
        this.hasRun = false;
    }

    public void init() {
        Arrays.fill(this.heightGrid, -1);
        for (int i = 1; i <= 8; i *= 2) {
            ((BitSet)this.resolutionMaps.get(i)).clear();
        }
        for (int role : this.roleIndexes) {
            ((ObjectArrayList)this.positionsByRole.get(role)).clear();
            this.failedSpawnsByRole.put(role, 0);
        }
        this.irregularCase = false;
    }

    public void buildPositionCache(@Nonnull Vector3d origin, @Nonnull FloodFillEntryPoolSimple pool) {
        int sizeHalf = this.size / 2;
        int worldX = MathUtil.floor(origin.getX());
        int worldY = MathUtil.floor(origin.getY());
        int worldZ = MathUtil.floor(origin.getZ());
        this.chunkAccessor = LocalCachedChunkAccessor.atWorldCoords(this.world, worldX, worldZ, sizeHalf);
        this.chunk = (WorldChunk)this.chunkAccessor.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(worldX, worldZ));
        if (this.chunk == null) {
            return;
        }
        int[] yRange = ((BeaconNPCSpawn)this.spawnWrapper.getSpawn()).getYRange();
        this.minY = Math.max(0, worldY + yRange[0]);
        this.maxY = Math.min(319, worldY + yRange[1]);
        this.floodFill(worldX, worldY, worldZ, sizeHalf, sizeHalf, pool);
        this.desiredPositionCount = Math.min(MathUtil.ceil((double)this.fullResolutionMap.cardinality() * 0.1), 30);
        if (this.desiredPositionCount > 0) {
            this.findPositions(worldX, worldZ);
        } else {
            SpawningPlugin.get().getLogger().at(Level.WARNING).log("Spawn beacon at: " + String.valueOf(origin) + " unable to find any suitable positions to check");
        }
        if (this.debug != Debug.DISABLED && (this.irregularCase || this.debug == Debug.ALL)) {
            for (int i = 2; i <= 8; i *= 2) {
                BitSet map = (BitSet)this.resolutionMaps.get(i);
                if (map.cardinality() <= 0) continue;
                SpawningPlugin.get().getLogger().at(Level.WARNING).log(this.debugDumpLowResolutionMap(map, this.size / i));
            }
            SpawningPlugin.get().getLogger().at(Level.WARNING).log("Spawn beacon at: " + String.valueOf(origin) + (this.irregularCase ? " is an irregular case" : ""));
        }
        this.chunkAccessor = null;
        this.chunk = null;
        this.spawningContext.releaseFull();
        this.hasRun = true;
    }

    private void floodFill(int worldX, int worldY, int worldZ, int setX, int setZ, @Nonnull FloodFillEntryPoolSimple pool) {
        if (this.chunk == null) {
            return;
        }
        this.floodFillQueue.clear();
        int[] initialEntry = pool.allocate();
        initialEntry[0] = worldX;
        initialEntry[1] = worldY;
        initialEntry[2] = worldZ;
        initialEntry[3] = setX;
        initialEntry[4] = setZ;
        this.floodFillQueue.add(initialEntry);
        int chunkX = this.chunk.getX();
        int chunkZ = this.chunk.getZ();
        block0: while (!this.floodFillQueue.isEmpty()) {
            int block;
            int[] state = this.floodFillQueue.poll();
            worldX = state[0];
            worldY = state[1];
            worldZ = state[2];
            setX = state[3];
            setZ = state[4];
            if (setX < 0 || setX >= this.size || setZ < 0 || setZ >= this.size) {
                pool.deallocate(state);
                continue;
            }
            int index = FloodFillPositionSelector.getPositionIndex(setX, setZ, this.size);
            if (this.heightGrid[index] != -1) {
                pool.deallocate(state);
                continue;
            }
            if (!ChunkUtil.isInsideChunk(chunkX, chunkZ, worldX, worldZ)) {
                WorldChunk newChunk = (WorldChunk)this.chunkAccessor.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(worldX, worldZ));
                if (newChunk == null) {
                    this.heightGrid[index] = -2;
                    pool.deallocate(state);
                    continue;
                }
                this.chunk = newChunk;
                chunkX = this.chunk.getX();
                chunkZ = this.chunk.getZ();
            }
            if ((block = this.chunk.getBlock(worldX, worldY, worldZ)) == 0 || BlockType.getAssetMap().getAsset(block).getMaterial() != BlockMaterial.Solid) {
                while (block == 0) {
                    block = this.chunk.getBlock(worldX, --worldY, worldZ);
                    if (worldY >= this.minY) continue;
                    this.heightGrid[index] = Integer.MIN_VALUE;
                    pool.deallocate(state);
                    continue block0;
                }
            } else {
                while (block != 0) {
                    block = this.chunk.getBlock(worldX, ++worldY, worldZ);
                    if (worldY <= this.maxY) continue;
                    this.heightGrid[index] = Integer.MAX_VALUE;
                    pool.deallocate(state);
                    continue block0;
                }
            }
            this.heightGrid[index] = ++worldY;
            this.fullResolutionMap.set(index);
            state[0] = worldX + 1;
            state[1] = worldY;
            state[2] = worldZ;
            state[3] = setX + 1;
            state[4] = setZ;
            this.floodFillQueue.add(state);
            int[] entry2 = pool.allocate();
            entry2[0] = worldX - 1;
            entry2[1] = worldY;
            entry2[2] = worldZ;
            entry2[3] = setX - 1;
            entry2[4] = setZ;
            this.floodFillQueue.add(entry2);
            int[] entry3 = pool.allocate();
            entry3[0] = worldX;
            entry3[1] = worldY;
            entry3[2] = worldZ + 1;
            entry3[3] = setX;
            entry3[4] = setZ + 1;
            this.floodFillQueue.add(entry3);
            int[] entry4 = pool.allocate();
            entry4[0] = worldX;
            entry4[1] = worldY;
            entry4[2] = worldZ - 1;
            entry4[3] = setX;
            entry4[4] = setZ - 1;
            this.floodFillQueue.add(entry4);
        }
    }

    /*
     * Unable to fully structure code
     */
    private void findPositions(int originX, int originZ) {
        resolution = 1;
        do {
            segments = this.buildLowerResolutionMap((BitSet)this.resolutionMaps.get(resolution *= 2), this.size / resolution, (BitSet)this.resolutionMaps.get(resolution / 2), this.size / (resolution / 2));
        } while (resolution < 8 && segments / this.desiredPositionCount > 1);
        lowestResolutionMap = (BitSet)this.resolutionMaps.get(resolution);
        openSpots = lowestResolutionMap.cardinality();
        if (openSpots < this.desiredPositionCount) {
            lowestResolutionMap = (BitSet)this.resolutionMaps.get(resolution /= 2);
        }
        sizeHalf = this.size / 2;
        offsetOriginX = originX - sizeHalf;
        offsetOriginZ = originZ - sizeHalf;
        suppressionController = this.world.getEntityStore().getStore().getResource(SpawnSuppressionController.getResourceType());
        chunkIndex = ChunkUtil.NOT_FOUND;
        suppressionEntry = null;
        chunkSuppressionMap = suppressionController.getChunkSuppressionMap();
        for (int roleIndex : this.roleIndexes) {
            spawnable = NPCPlugin.get().tryGetCachedValidRole(roleIndex);
            if (spawnable == null || !spawnable.isSpawnable() || !(spawnable instanceof ISpawnableWithModel) || !this.spawningContext.setSpawnable((ISpawnableWithModel)spawnable, true)) continue;
            positionList = (ObjectArrayList)this.positionsByRole.get(roleIndex);
            i = lowestResolutionMap.nextSetBit(0);
            while (i >= 0) {
                block14: {
                    block13: {
                        chosenIndex = i;
                        for (currentResolution = resolution; currentResolution > 1; currentResolution /= 2) {
                            nextResolution = currentResolution / 2;
                            chosenIndex = this.pickOpenSegment(chosenIndex, this.size / currentResolution, (BitSet)this.resolutionMaps.get(nextResolution), this.size / nextResolution);
                        }
                        x = offsetOriginX + FloodFillPositionSelector.xFromIndex(chosenIndex, this.size);
                        y = this.heightGrid[chosenIndex];
                        z = offsetOriginZ + FloodFillPositionSelector.zFromIndex(chosenIndex, this.size);
                        newChunkIndex = ChunkUtil.indexChunk(ChunkUtil.chunkCoordinate(x), ChunkUtil.chunkCoordinate(z));
                        if (chunkIndex != newChunkIndex) {
                            suppressionEntry = chunkSuppressionMap.get(newChunkIndex);
                            chunkIndex = newChunkIndex;
                        }
                        if (this.canSpawn(x, y, z, roleIndex, suppressionEntry)) break block13;
                        if (this.debug != Debug.DISABLED) {
                            this.failedPositionTestIndexes.add(chosenIndex);
                        }
                        originalIndex = chosenIndex;
                        if ((chosenIndex = this.shiftIndexAwayFromWall(chosenIndex)) == originalIndex) break block14;
                        x = offsetOriginX + FloodFillPositionSelector.xFromIndex(chosenIndex, this.size);
                        y = this.heightGrid[chosenIndex];
                        z = offsetOriginZ + FloodFillPositionSelector.zFromIndex(chosenIndex, this.size);
                        newChunkIndex = ChunkUtil.indexChunk(ChunkUtil.chunkCoordinate(x), ChunkUtil.chunkCoordinate(z));
                        if (chunkIndex != newChunkIndex) {
                            suppressionEntry = chunkSuppressionMap.get(newChunkIndex);
                            chunkIndex = newChunkIndex;
                        }
                        if (!this.canSpawn(x, y, z, roleIndex, suppressionEntry)) {
                            if (this.debug != Debug.DISABLED) {
                                this.failedPositionTestIndexes.add(chosenIndex);
                                ** GOTO lbl59
                            } else {
                                ** GOTO lbl53
                            }
                        }
                        break block13;
lbl53:
                        // 2 sources

                        break block14;
                    }
                    if (this.positionIndexes.add(chosenIndex)) {
                        positionList.add(new WeightedPosition(x, y, z));
                    }
                    if (i == 0x7FFFFFFF) break;
                }
                i = lowestResolutionMap.nextSetBit(i + 1);
            }
            positionCount = this.positionIndexes.size();
            if (this.debug != Debug.DISABLED && ((double)positionCount < (double)this.desiredPositionCount * 0.3 || (double)positionCount > (double)this.desiredPositionCount * 5.0)) {
                this.irregularCase = true;
            }
            if (this.irregularCase || this.debug == Debug.ALL) {
                SpawningPlugin.get().getLogger().at(Level.WARNING).log("Role: " + NPCPlugin.get().getName(roleIndex));
                SpawningPlugin.get().getLogger().at(Level.WARNING).log(this.debugDumpBaseFloodFill());
                this.failedPositionTestIndexes.clear();
            }
            this.positionIndexes.clear();
        }
    }

    private int buildLowerResolutionMap(@Nonnull BitSet targetMap, int mapSize, @Nonnull BitSet parentMap, int parentMapSize) {
        for (int x = 0; x < mapSize; ++x) {
            for (int z = 0; z < mapSize; ++z) {
                int parentX = x * 2;
                int parentZ = z * 2;
                int index = FloodFillPositionSelector.getPositionIndex(parentX, parentZ, parentMapSize);
                if (!parentMap.get(index) && !parentMap.get(index + 1) && !parentMap.get(index + parentMapSize) && !parentMap.get(index + parentMapSize + 1)) continue;
                targetMap.set(FloodFillPositionSelector.getPositionIndex(x, z, mapSize));
            }
        }
        return targetMap.cardinality();
    }

    private int pickOpenSegment(int lowResolutionIndex, int lowResolutionMapSize, @Nonnull BitSet higherResolutionMap, int highResolutionMapSize) {
        int index;
        int z;
        int parentZ;
        int x = FloodFillPositionSelector.xFromIndex(lowResolutionIndex, lowResolutionMapSize);
        int parentX = x * 2;
        int originIndex = FloodFillPositionSelector.getPositionIndex(parentX, parentZ = (z = FloodFillPositionSelector.zFromIndex(lowResolutionIndex, lowResolutionMapSize)) * 2, highResolutionMapSize);
        if (higherResolutionMap.get(originIndex)) {
            this.highResolutionOptions.add(originIndex);
        }
        if (higherResolutionMap.get(index = originIndex + 1)) {
            this.highResolutionOptions.add(index);
        }
        if (higherResolutionMap.get(index = originIndex + highResolutionMapSize)) {
            this.highResolutionOptions.add(index);
        }
        if (higherResolutionMap.get(index = originIndex + highResolutionMapSize + 1)) {
            this.highResolutionOptions.add(index);
        }
        if (this.highResolutionOptions.size() > 1) {
            IntLists.shuffle(this.highResolutionOptions, ThreadLocalRandom.current());
        }
        index = this.highResolutionOptions.getInt(0);
        this.highResolutionOptions.clear();
        return index;
    }

    private int shiftIndexAwayFromWall(int index) {
        int newIndex = index;
        int checkIndex = index - 1;
        if (checkIndex < 0 || !this.fullResolutionMap.get(checkIndex)) {
            ++newIndex;
        }
        if ((checkIndex = index + 1) >= this.fullResolutionMap.size() || !this.fullResolutionMap.get(checkIndex)) {
            --newIndex;
        }
        if ((checkIndex = index - this.size) < 0 || !this.fullResolutionMap.get(checkIndex)) {
            newIndex += this.size;
        }
        if ((checkIndex = index + this.size) >= this.fullResolutionMap.size() || !this.fullResolutionMap.get(checkIndex)) {
            newIndex -= this.size;
        }
        return this.fullResolutionMap.get(newIndex) ? newIndex : index;
    }

    private boolean canSpawn(int x, int y, int z, int roleIndex, @Nullable ChunkSuppressionEntry suppressionEntry) {
        if (!((BeaconNPCSpawn)this.spawnWrapper.getSpawn()).isOverrideSpawnSuppressors() && suppressionEntry != null && suppressionEntry.isSuppressingRoleAt(roleIndex, y)) {
            return false;
        }
        if (!this.spawningContext.set(this.world, x, y, z) || this.spawningContext.canSpawn() != SpawnTestResult.TEST_OK || !this.spawnWrapper.withinLightRange(this.spawningContext)) {
            return false;
        }
        if (this.spawningContext.ySpawn > (double)this.maxY) {
            return false;
        }
        IntSet spawnBlockSet = this.spawnWrapper.getSpawnBlockSet(roleIndex);
        int spawnFluidTag = this.spawnWrapper.getSpawnFluidTag(roleIndex);
        if (spawnBlockSet == null && spawnFluidTag == Integer.MIN_VALUE) {
            return true;
        }
        if (spawnBlockSet != null && spawnBlockSet.contains(this.spawningContext.groundBlockId)) {
            return true;
        }
        return spawnFluidTag != Integer.MIN_VALUE && Fluid.getAssetMap().getIndexesForTag(spawnFluidTag).contains(this.spawningContext.groundFluidId);
    }

    @Nonnull
    private String debugDumpBaseFloodFill() {
        StringBuilder sb = new StringBuilder();
        int sizeHalf = this.size / 2;
        int centre = FloodFillPositionSelector.getPositionIndex(sizeHalf, sizeHalf, this.size);
        for (int z = 0; z < this.size; ++z) {
            for (int x = 0; x < this.size; ++x) {
                int index = FloodFillPositionSelector.getPositionIndex(x, z, this.size);
                if (index == centre) {
                    sb.append('B');
                } else if (this.positionIndexes.contains(index)) {
                    sb.append('S');
                } else if (this.failedPositionTestIndexes.contains(index)) {
                    sb.append('F');
                } else {
                    switch (this.heightGrid[index]) {
                        case 0x7FFFFFFF: {
                            sb.append('^');
                            break;
                        }
                        case -2147483648: {
                            sb.append('v');
                            break;
                        }
                        case -2: 
                        case -1: {
                            sb.append('X');
                            break;
                        }
                        default: {
                            sb.append('.');
                        }
                    }
                }
                sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @Nonnull
    private String debugDumpLowResolutionMap(@Nonnull BitSet map, int size) {
        StringBuilder sb = new StringBuilder();
        for (int z = 0; z < size; ++z) {
            for (int x = 0; x < size; ++x) {
                sb.append(map.get(FloodFillPositionSelector.getPositionIndex(x, z, size)) ? (char)'.' : 'X').append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static int getPositionIndex(int x, int z, int size) {
        return x * size + z;
    }

    public static int xFromIndex(int index, int size) {
        return index / size;
    }

    public static int zFromIndex(int index, int size) {
        return index % size;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        FloodFillPositionSelector selector = new FloodFillPositionSelector(this.world, this.spawnWrapper);
        selector.init();
        return selector;
    }

    public static enum Debug {
        DISABLED,
        IRREGULARITIES,
        ALL;

    }

    private static class WeightedPosition {
        @Nonnull
        private final Vector3i position;
        private double weight;

        private WeightedPosition(int x, int y, int z) {
            this.position = new Vector3i(x, y, z);
        }

        public double getWeight() {
            return this.weight;
        }
    }

    public static class SortBufferProvider {
        protected WeightedPosition[] buffer = new WeightedPosition[10];

        public WeightedPosition[] getBuffer(int size) {
            if (size <= this.buffer.length) {
                return this.buffer;
            }
            this.buffer = ObjectArrays.grow(this.buffer, size);
            return this.buffer;
        }
    }
}

