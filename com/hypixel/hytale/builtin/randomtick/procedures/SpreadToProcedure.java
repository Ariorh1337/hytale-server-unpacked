/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.randomtick.procedures;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.protocol.DrawType;
import com.hypixel.hytale.server.core.asset.type.blocktick.config.RandomTickProcedure;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import it.unimi.dsi.fastutil.ints.IntSet;

public class SpreadToProcedure
implements RandomTickProcedure {
    public static final BuilderCodec<SpreadToProcedure> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SpreadToProcedure.class, SpreadToProcedure::new).appendInherited(new KeyedCodec<T[]>("SpreadDirections", new ArrayCodec<Vector3i>(Vector3i.CODEC, Vector3i[]::new)), (o, i) -> {
        o.spreadDirections = i;
    }, o -> o.spreadDirections, (o, p) -> {
        o.spreadDirections = p.spreadDirections;
    }).documentation("The directions this block can spread in.").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Integer>("MinY", Codec.INTEGER), (o, i) -> {
        o.minY = i;
    }, o -> o.minY, (o, p) -> {
        o.minY = p.minY;
    }).documentation("The minimum Y level this block can spread to, relative to the current block. For example, a value of -1 means the block can spread to blocks one level below it.").add()).appendInherited(new KeyedCodec<Integer>("MaxY", Codec.INTEGER), (o, i) -> {
        o.maxY = i;
    }, o -> o.maxY, (o, p) -> {
        o.maxY = p.maxY;
    }).documentation("The maximum Y level this block can spread to, relative to the current block. For example, a value of 1 means the block can spread to blocks one level above it.").add()).appendInherited(new KeyedCodec<String>("AllowedTag", Codec.STRING), (o, i) -> {
        o.allowedTag = i;
        o.allowedTagIndex = AssetRegistry.getOrCreateTagIndex(i);
    }, o -> o.allowedTag, (o, p) -> {
        o.allowedTag = p.allowedTag;
        o.allowedTagIndex = p.allowedTagIndex;
    }).documentation("The asset tag that the block can spread to.").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Boolean>("RequireEmptyAboveTarget", Codec.BOOLEAN), (o, i) -> {
        o.requireEmptyAboveTarget = i;
    }, o -> o.requireEmptyAboveTarget, (o, p) -> {
        o.requireEmptyAboveTarget = p.requireEmptyAboveTarget;
    }).documentation("Whether the block requires an empty block above the target block to spread.").add()).appendInherited(new KeyedCodec<Integer>("RequiredLightLevel", Codec.INTEGER), (o, i) -> {
        o.requiredLightLevel = i;
    }, o -> o.requiredLightLevel, (o, p) -> {
        o.requiredLightLevel = p.requiredLightLevel;
    }).documentation("The minimum light level required for the block to spread.").addValidator(Validators.range(0, 15)).add()).appendInherited(new KeyedCodec<String>("RevertBlock", Codec.STRING), (o, i) -> {
        o.revertBlock = i;
    }, o -> o.revertBlock, (o, p) -> {
        o.revertBlock = p.revertBlock;
    }).documentation("If specified, the block will revert to this block if it is covered by another block.").addValidatorLate(() -> BlockType.VALIDATOR_CACHE.getValidator().late()).add()).build();
    private Vector3i[] spreadDirections;
    private int minY = 0;
    private int maxY = 0;
    private String allowedTag;
    private int allowedTagIndex = Integer.MIN_VALUE;
    private boolean requireEmptyAboveTarget = true;
    private int requiredLightLevel = 6;
    private String revertBlock;

    @Override
    public void onRandomTick(Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer, BlockSection blockSection, int worldX, int worldY, int worldZ, int blockId, BlockType blockType) {
        byte blockLevel;
        int skyLight;
        int lightLevel;
        IntSet validSpreadTargets = BlockType.getAssetMap().getIndexesForTag(this.allowedTagIndex);
        WorldTimeResource worldTimeResource = commandBuffer.getExternalData().getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());
        double sunlightFactor = worldTimeResource.getSunlightFactor();
        BlockSection aboveSection = blockSection;
        if (!ChunkUtil.isSameChunkSection(worldX, worldY, worldZ, worldX, worldY + 1, worldZ)) {
            Ref<ChunkStore> aboveChunk = store.getExternalData().getChunkSectionReference(commandBuffer, ChunkUtil.chunkCoordinate(worldX), ChunkUtil.chunkCoordinate(worldY + 1), ChunkUtil.chunkCoordinate(worldZ));
            if (aboveChunk == null) {
                return;
            }
            BlockSection aboveBlockSection = commandBuffer.getComponent(aboveChunk, BlockSection.getComponentType());
            if (aboveBlockSection == null) {
                return;
            }
            aboveSection = aboveBlockSection;
        }
        int aboveIndex = ChunkUtil.indexBlock(worldX, worldY + 1, worldZ);
        if (this.revertBlock != null) {
            int revert;
            int blockAtAboveId = aboveSection.get(aboveIndex);
            BlockType blockAtAbove = BlockType.getAssetMap().getAsset(blockAtAboveId);
            if (blockAtAbove != null && (blockAtAbove.getDrawType() == DrawType.Cube || blockAtAbove.getDrawType() == DrawType.CubeWithModel) && (revert = BlockType.getAssetMap().getIndex(this.revertBlock)) != Integer.MIN_VALUE) {
                blockSection.set(worldX, worldY, worldZ, revert, 0, 0);
                return;
            }
        }
        if ((lightLevel = Math.max(skyLight = (int)((double)aboveSection.getLocalLight().getSkyLight(aboveIndex) * sunlightFactor), blockLevel = aboveSection.getLocalLight().getBlockLightIntensity(aboveIndex))) < this.requiredLightLevel) {
            return;
        }
        for (int y = this.minY; y <= this.maxY; ++y) {
            for (Vector3i direction : this.spreadDirections) {
                int targetIndex;
                int targetBlockId;
                Ref<ChunkStore> otherChunk;
                int targetX = worldX + direction.x;
                int targetY = worldY + direction.y + y;
                int targetZ = worldZ + direction.z;
                BlockSection targetBlockSection = blockSection;
                if (!ChunkUtil.isSameChunkSection(worldX, worldY, worldZ, targetX, targetY, targetZ) && ((otherChunk = store.getExternalData().getChunkSectionReference(commandBuffer, ChunkUtil.chunkCoordinate(targetX), ChunkUtil.chunkCoordinate(targetY), ChunkUtil.chunkCoordinate(targetZ))) == null || (targetBlockSection = commandBuffer.getComponent(otherChunk, BlockSection.getComponentType())) == null) || !validSpreadTargets.contains(targetBlockId = targetBlockSection.get(targetIndex = ChunkUtil.indexBlock(targetX, targetY, targetZ)))) continue;
                if (this.requireEmptyAboveTarget) {
                    int aboveTargetBlockId;
                    if (ChunkUtil.isSameChunkSection(targetX, targetY, targetZ, targetX, targetY + 1, targetZ)) {
                        aboveTargetBlockId = targetBlockSection.get(ChunkUtil.indexBlock(targetX, targetY + 1, targetZ));
                    } else {
                        BlockSection aboveBlockSection;
                        Ref<ChunkStore> aboveChunk = store.getExternalData().getChunkSectionReference(commandBuffer, ChunkUtil.chunkCoordinate(targetX), ChunkUtil.chunkCoordinate(targetY + 1), ChunkUtil.chunkCoordinate(targetZ));
                        if (aboveChunk == null || (aboveBlockSection = commandBuffer.getComponent(aboveChunk, BlockSection.getComponentType())) == null) continue;
                        aboveTargetBlockId = aboveBlockSection.get(ChunkUtil.indexBlock(targetX, targetY + 1, targetZ));
                    }
                    BlockType aboveBlockType = BlockType.getAssetMap().getAsset(aboveTargetBlockId);
                    if (aboveBlockType == null || aboveBlockType.getMaterial() != BlockMaterial.Empty) continue;
                }
                targetBlockSection.set(targetIndex, blockId, 0, 0);
            }
        }
    }
}

