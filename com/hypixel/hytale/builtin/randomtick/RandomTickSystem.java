/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.randomtick;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.builtin.randomtick.RandomTick;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.server.core.asset.type.blocktick.config.RandomTickProcedure;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RandomTickSystem
extends EntityTickingSystem<ChunkStore> {
    private final ComponentType<ChunkStore, BlockSection> blockSelectionComponentType = BlockSection.getComponentType();
    private final ComponentType<ChunkStore, ChunkSection> chunkSectionComponentType = ChunkSection.getComponentType();
    private final Query<ChunkStore> query = Query.and(this.blockSelectionComponentType, this.chunkSectionComponentType);

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        BlockSection blockSection = archetypeChunk.getComponent(index, this.blockSelectionComponentType);
        assert (blockSection != null);
        if (blockSection.isSolidAir()) {
            return;
        }
        ChunkSection chunkSection = archetypeChunk.getComponent(index, this.chunkSectionComponentType);
        assert (chunkSection != null);
        RandomTick config = commandBuffer.getResource(RandomTick.getResourceType());
        World world = store.getExternalData().getWorld();
        int interval = 32768 / config.getBlocksPerSectionPerTickStable();
        long baseSeed = HashUtil.hash(world.getTick() / (long)interval, chunkSection.getX(), chunkSection.getY(), chunkSection.getZ());
        long randomSeed = (baseSeed << 1 | 1L) & 0x7FFFL;
        long randomSeed2 = baseSeed >> 16 & 0x7FFFL;
        long startIndex = world.getTick() % (long)interval * (long)config.getBlocksPerSectionPerTickStable();
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        for (int i = 0; i < config.getBlocksPerSectionPerTickStable(); ++i) {
            RandomTickProcedure randomTickProcedure;
            BlockType blockType;
            int blockIndex = (int)((startIndex + (long)i) * randomSeed + randomSeed2 & 0x7FFFL);
            int localX = ChunkUtil.xFromIndex(blockIndex);
            int localY = ChunkUtil.yFromIndex(blockIndex);
            int localZ = ChunkUtil.zFromIndex(blockIndex);
            int blockId = blockSection.get(blockIndex);
            if (blockId == 0 || (blockType = assetMap.getAsset(blockId)) == null || (randomTickProcedure = blockType.getRandomTickProcedure()) == null) continue;
            randomTickProcedure.onRandomTick(store, commandBuffer, blockSection, ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), localX), ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), localY), ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), localZ), blockId, blockType);
        }
        Random rng = config.getRandom();
        for (int i = 0; i < config.getBlocksPerSectionPerTickUnstable(); ++i) {
            RandomTickProcedure randomTickProcedure;
            BlockType blockType;
            int blockIndex = rng.nextInt(32768);
            int localX = ChunkUtil.xFromIndex(blockIndex);
            int localY = ChunkUtil.yFromIndex(blockIndex);
            int localZ = ChunkUtil.zFromIndex(blockIndex);
            int blockId = blockSection.get(blockIndex);
            if (blockId == 0 || (blockType = assetMap.getAsset(blockId)) == null || (randomTickProcedure = blockType.getRandomTickProcedure()) == null) continue;
            randomTickProcedure.onRandomTick(store, commandBuffer, blockSection, ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), localX), ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), localY), ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), localZ), blockId, blockType);
        }
    }

    @Override
    @Nullable
    public Query<ChunkStore> getQuery() {
        return this.query;
    }
}

