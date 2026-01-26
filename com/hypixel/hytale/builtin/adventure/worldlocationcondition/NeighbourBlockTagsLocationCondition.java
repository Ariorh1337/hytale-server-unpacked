/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.worldlocationcondition;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.range.IntRange;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.tagpattern.config.TagPattern;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.accessor.BlockAccessor;
import com.hypixel.hytale.server.core.universe.world.accessor.ChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.worldlocationcondition.WorldLocationCondition;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NeighbourBlockTagsLocationCondition
extends WorldLocationCondition {
    public static final BuilderCodec<NeighbourBlockTagsLocationCondition> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(NeighbourBlockTagsLocationCondition.class, NeighbourBlockTagsLocationCondition::new, WorldLocationCondition.BASE_CODEC).append(new KeyedCodec<String>("TagPattern", Codec.STRING), (neighbourBlockTagsLocationCondition, s) -> {
        neighbourBlockTagsLocationCondition.tagPatternId = s;
    }, neighbourBlockTagsLocationCondition -> neighbourBlockTagsLocationCondition.tagPatternId).documentation("A TagPattern can be used if the block at the chosen location needs to fulfill specific conditions.").addValidator(Validators.nonNull()).add()).append(new KeyedCodec<NeighbourDirection>("NeighbourBlock", new EnumCodec<NeighbourDirection>(NeighbourDirection.class)), (neighbourBlockTagsLocationCondition, neighbourDirection) -> {
        neighbourBlockTagsLocationCondition.neighbourDirection = neighbourDirection;
    }, neighbourBlockTagsLocationCondition -> neighbourBlockTagsLocationCondition.neighbourDirection).documentation("Defines which block has to be checked related to original location. Possible values: Above, Below, Sideways.").addValidator(Validators.nonNull()).add()).append(new KeyedCodec<IntRange>("Support", IntRange.CODEC), (neighbourBlockTagsLocationCondition, blockSupport) -> {
        neighbourBlockTagsLocationCondition.support = blockSupport;
    }, neighbourBlockTagsLocationCondition -> neighbourBlockTagsLocationCondition.support).documentation("Additional field used if NeighbourBlock is set to Sideways.").add()).build();
    protected String tagPatternId;
    protected NeighbourDirection neighbourDirection;
    protected IntRange support = new IntRange(1, 4);

    @Override
    public boolean test(World world, int worldX, int worldY, int worldZ) {
        if (worldY <= 0) {
            return false;
        }
        Object worldChunk = world.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(worldX, worldZ));
        if (worldChunk == null) {
            return false;
        }
        if (this.neighbourDirection == NeighbourDirection.SIDEWAYS) {
            int count = 0;
            ChunkAccessor chunkAccessor = ((WorldChunk)worldChunk).getChunkAccessor();
            if (this.checkBlockHasTag(worldX - 1, worldY, worldZ, (BlockAccessor)chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(worldX - 1, worldZ)))) {
                ++count;
            }
            if (this.checkBlockHasTag(worldX + 1, worldY, worldZ, (BlockAccessor)chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(worldX + 1, worldZ)))) {
                ++count;
            }
            if (this.checkBlockHasTag(worldX, worldY, worldZ - 1, (BlockAccessor)chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(worldX, worldZ - 1)))) {
                ++count;
            }
            if (this.checkBlockHasTag(worldX, worldY, worldZ + 1, (BlockAccessor)chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(worldX, worldZ + 1)))) {
                ++count;
            }
            return this.support.includes(count);
        }
        int yPos = worldY;
        switch (this.neighbourDirection.ordinal()) {
            case 0: {
                ++yPos;
                break;
            }
            case 1: {
                --yPos;
            }
        }
        return this.checkBlockHasTag(worldX, yPos, worldZ, (BlockAccessor)worldChunk);
    }

    private boolean checkBlockHasTag(int x, int y, int z, @Nonnull BlockAccessor worldChunk) {
        int blockIndex = worldChunk.getBlock(x, y, z);
        TagPattern tagPattern = (TagPattern)TagPattern.getAssetMap().getAsset(this.tagPatternId);
        if (tagPattern != null) {
            AssetExtraInfo.Data data = BlockType.getAssetMap().getAsset(blockIndex).getData();
            if (data == null) {
                return false;
            }
            return tagPattern.test(data.getTags());
        }
        HytaleLogger.getLogger().at(Level.WARNING).log("No TagPattern asset found for id: " + this.tagPatternId);
        return false;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NeighbourBlockTagsLocationCondition that = (NeighbourBlockTagsLocationCondition)o;
        if (!this.tagPatternId.equals(that.tagPatternId)) {
            return false;
        }
        if (this.neighbourDirection != that.neighbourDirection) {
            return false;
        }
        return this.support != null ? this.support.equals(that.support) : that.support == null;
    }

    @Override
    public int hashCode() {
        int result = this.tagPatternId.hashCode();
        result = 31 * result + this.neighbourDirection.hashCode();
        result = 31 * result + (this.support != null ? this.support.hashCode() : 0);
        return result;
    }

    @Override
    @Nonnull
    public String toString() {
        return "NeighbourBlockTagsLocationCondition{tagPatternId='" + this.tagPatternId + "', neighbourDirection=" + String.valueOf((Object)this.neighbourDirection) + ", support=" + String.valueOf(this.support) + "} " + super.toString();
    }

    private static enum NeighbourDirection {
        ABOVE,
        BELOW,
        SIDEWAYS;

    }
}

