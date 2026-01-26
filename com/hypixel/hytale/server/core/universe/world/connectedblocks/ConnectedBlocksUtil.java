/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.connectedblocks;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockRuleSet;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class ConnectedBlocksUtil {
    private static final int MAX_UPDATE_DEPTH = 3;

    public static void setConnectedBlockAndNotifyNeighbors(int blockTypeId, @Nonnull RotationTuple blockTypeRotation, @Nonnull Vector3i placementNormal, @Nonnull Vector3i blockPosition, @Nonnull WorldChunk worldChunkComponent, @Nonnull BlockChunk blockChunkComponent) {
        Vector3i coordinate = new Vector3i(blockPosition);
        BlockType blockType = BlockType.getAssetMap().getAsset(blockTypeId);
        if (blockType == null) {
            return;
        }
        BlockSection sectionAtY = blockChunkComponent.getSectionAtBlockY(blockPosition.y);
        int filler = sectionAtY.getFiller(blockPosition.x, blockPosition.y, blockPosition.z);
        int settings = 132;
        if (blockType.getConnectedBlockRuleSet() != null && filler == 0) {
            int rotationIndex = blockTypeRotation.index();
            Optional<ConnectedBlockResult> foundPattern = ConnectedBlocksUtil.getDesiredConnectedBlockType(worldChunkComponent.getWorld(), coordinate, blockType, rotationIndex, placementNormal, true);
            if (foundPattern.isPresent() && (!foundPattern.get().blockTypeKey().equals(blockType.getId()) || foundPattern.get().rotationIndex != rotationIndex)) {
                ConnectedBlockResult result = foundPattern.get();
                int id = BlockType.getAssetMap().getIndex(result.blockTypeKey());
                int rotation = result.rotationIndex();
                worldChunkComponent.setBlock(coordinate.x, coordinate.y, coordinate.z, id, BlockType.getAssetMap().getAsset(id), rotation, 0, settings);
            }
        }
        ConnectedBlocksUtil.updateNeighborsWithDepth(worldChunkComponent, coordinate, placementNormal, settings);
    }

    private static void updateNeighborsWithDepth(@Nonnull WorldChunk worldChunkComponent, @Nonnull Vector3i startCoordinate, @Nonnull Vector3i placementNormal, int settings) {
        ArrayDeque<QueueEntry> queue = new ArrayDeque<QueueEntry>();
        ObjectOpenHashSet visited = new ObjectOpenHashSet();
        record QueueEntry(Vector3i coordinate, int depth) {
        }
        queue.add(new QueueEntry(new Vector3i(startCoordinate), 0));
        while (!queue.isEmpty()) {
            QueueEntry entry = (QueueEntry)queue.poll();
            Vector3i coordinate = entry.coordinate;
            int depth = entry.depth;
            Object2ObjectOpenHashMap<Vector3i, ConnectedBlockResult> desiredChanges = new Object2ObjectOpenHashMap<Vector3i, ConnectedBlockResult>();
            ConnectedBlocksUtil.notifyNeighborsAndCollectChanges(worldChunkComponent.getWorld(), coordinate, desiredChanges, placementNormal);
            for (Map.Entry result : desiredChanges.entrySet()) {
                Vector3i location = (Vector3i)result.getKey();
                ConnectedBlockResult connectedBlockResult = (ConnectedBlockResult)result.getValue();
                if (!visited.add(location.clone()) || location.x == coordinate.x && location.y == coordinate.y && location.z == coordinate.z) continue;
                WorldChunk newWorldChunk = worldChunkComponent;
                long chunkIndex = ChunkUtil.indexChunkFromBlock(location.x, location.z);
                if (chunkIndex != newWorldChunk.getIndex() && (newWorldChunk = worldChunkComponent.getWorld().getChunkIfLoaded(chunkIndex)) == null) continue;
                int blockId = BlockType.getAssetMap().getIndex(connectedBlockResult.blockTypeKey());
                BlockType block = BlockType.getAssetMap().getAsset(blockId);
                newWorldChunk.setBlock(location.x, location.y, location.z, blockId, block, connectedBlockResult.rotationIndex(), 0, settings);
                for (Map.Entry<Vector3i, ObjectIntPair<String>> additionalEntry : connectedBlockResult.getAdditionalConnectedBlocks().entrySet()) {
                    Vector3i offset = additionalEntry.getKey();
                    ObjectIntPair<String> blockData = additionalEntry.getValue();
                    Vector3i additionalLocation = new Vector3i(location).add(offset);
                    WorldChunk additionalChunk = newWorldChunk;
                    long additionalChunkIndex = ChunkUtil.indexChunkFromBlock(additionalLocation.x, additionalLocation.z);
                    if (additionalChunkIndex != newWorldChunk.getIndex() && (additionalChunk = worldChunkComponent.getWorld().getChunkIfLoaded(additionalChunkIndex)) == null) continue;
                    int additionalBlockId = BlockType.getAssetMap().getIndex((String)blockData.first());
                    BlockType additionalBlock = BlockType.getAssetMap().getAsset(additionalBlockId);
                    if (additionalBlock == null) continue;
                    additionalChunk.setBlock(additionalLocation.x, additionalLocation.y, additionalLocation.z, additionalBlockId, additionalBlock, blockData.rightInt(), 0, settings);
                }
                if (depth + 1 >= 3) continue;
                queue.add(new QueueEntry(location.clone(), depth + 1));
            }
        }
    }

    public static void notifyNeighborsAndCollectChanges(@Nonnull World world, @Nonnull Vector3i origin, @Nonnull Map<Vector3i, ConnectedBlockResult> desiredChanges, Vector3i placementNormal) {
        Vector3i coordinate = origin.clone();
        long chunkIndex = ChunkUtil.indexChunkFromBlock(origin.x, origin.z);
        WorldChunk chunk = world.getChunkIfLoaded(chunkIndex);
        for (int x1 = -1; x1 <= 1; ++x1) {
            for (int z1 = -1; z1 <= 1; ++z1) {
                for (int y1 = -1; y1 <= 1; ++y1) {
                    Optional<ConnectedBlockResult> output;
                    ConnectedBlockRuleSet ruleSet;
                    BlockSection blockSection;
                    BlockChunk blockChunk;
                    if (x1 == 0 && y1 == 0 && z1 == 0) continue;
                    coordinate.assign(origin).add(x1, y1, z1);
                    if (coordinate.y < 0 || coordinate.y >= 320 || desiredChanges.containsKey(coordinate)) continue;
                    long neighborChunkIndex = ChunkUtil.indexChunkFromBlock(coordinate.x, coordinate.z);
                    if (neighborChunkIndex != chunkIndex) {
                        chunkIndex = neighborChunkIndex;
                        chunk = world.getChunkIfLoaded(neighborChunkIndex);
                    }
                    if (chunk == null || (blockChunk = chunk.getBlockChunk()) == null || (blockSection = blockChunk.getSectionAtBlockY(coordinate.y)) == null) continue;
                    int neighborBlockId = blockSection.get(coordinate.x, coordinate.y, coordinate.z);
                    BlockType neighborBlockType = BlockType.getAssetMap().getAsset(neighborBlockId);
                    if (neighborBlockType == null || (ruleSet = neighborBlockType.getConnectedBlockRuleSet()) == null || ruleSet.onlyUpdateOnPlacement()) continue;
                    int filler = blockSection.getFiller(coordinate.x, coordinate.y, coordinate.z);
                    int existingRotation = blockSection.getRotationIndex(coordinate.x, coordinate.y, coordinate.z);
                    if (filler != 0) {
                        int originX = coordinate.x - FillerBlockUtil.unpackX(filler);
                        int originY = coordinate.y - FillerBlockUtil.unpackY(filler);
                        int originZ = coordinate.z - FillerBlockUtil.unpackZ(filler);
                        coordinate.assign(originX, originY, originZ);
                    }
                    if (!(output = ConnectedBlocksUtil.getDesiredConnectedBlockType(world, coordinate, neighborBlockType, existingRotation, placementNormal, false)).isPresent() || neighborBlockType.getId().equals(output.get().blockTypeKey()) && output.get().rotationIndex == existingRotation) continue;
                    desiredChanges.put(coordinate.clone(), output.get());
                }
            }
        }
    }

    @Nonnull
    public static Optional<ConnectedBlockResult> getDesiredConnectedBlockType(@Nonnull World world, @Nonnull Vector3i coordinate, @Nonnull BlockType currentBlockType, int currentRotation, @Nonnull Vector3i placementNormal, boolean isPlacement) {
        ConnectedBlockRuleSet ruleSet = currentBlockType.getConnectedBlockRuleSet();
        if (ruleSet == null) {
            return Optional.empty();
        }
        return ruleSet.getConnectedBlockType(world, coordinate, currentBlockType, currentRotation, placementNormal, isPlacement);
    }

    public static final class ConnectedBlockResult {
        private final String blockTypeKey;
        private final int rotationIndex;
        private final Map<Vector3i, ObjectIntPair<String>> additionalConnectedBlocks = new Object2ObjectOpenHashMap<Vector3i, ObjectIntPair<String>>();

        public ConnectedBlockResult(@Nonnull String blockTypeKey, int rotationIndex) {
            this.blockTypeKey = blockTypeKey;
            this.rotationIndex = rotationIndex;
        }

        @Nonnull
        public String blockTypeKey() {
            return this.blockTypeKey;
        }

        public int rotationIndex() {
            return this.rotationIndex;
        }

        @Nonnull
        public Map<Vector3i, ObjectIntPair<String>> getAdditionalConnectedBlocks() {
            return this.additionalConnectedBlocks;
        }

        public void addAdditionalBlock(@Nonnull Vector3i offset, @Nonnull String blockTypeKey, int rotationIndex) {
            this.additionalConnectedBlocks.put(offset, ObjectIntPair.of(blockTypeKey, rotationIndex));
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            ConnectedBlockResult that = (ConnectedBlockResult)obj;
            return Objects.equals(this.blockTypeKey, that.blockTypeKey) && this.rotationIndex == that.rotationIndex && Objects.equals(this.additionalConnectedBlocks, that.additionalConnectedBlocks);
        }

        public int hashCode() {
            return Objects.hash(this.blockTypeKey, this.rotationIndex, this.additionalConnectedBlocks);
        }

        public String toString() {
            return "ConnectedBlockResult[blockTypeKey=" + this.blockTypeKey + ", rotationIndex=" + this.rotationIndex + ", additionalConnectedBlocks=" + String.valueOf(this.additionalConnectedBlocks) + "]";
        }
    }
}

