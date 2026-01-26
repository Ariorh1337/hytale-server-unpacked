/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.worldlocationproviders;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.builtin.adventure.objectives.config.worldlocationproviders.WorldLocationProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LookBlocksBelowProvider
extends WorldLocationProvider {
    public static final BuilderCodec<LookBlocksBelowProvider> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(LookBlocksBelowProvider.class, LookBlocksBelowProvider::new, BASE_CODEC).append(new KeyedCodec<T[]>("BlockTags", Codec.STRING_ARRAY), (lookBlocksBelowCondition, strings) -> {
        lookBlocksBelowCondition.blockTags = strings;
    }, lookBlocksBelowCondition -> lookBlocksBelowCondition.blockTags).addValidator(Validators.nonEmptyArray()).addValidator(Validators.uniqueInArray()).add()).append(new KeyedCodec<Integer>("Count", Codec.INTEGER), (lookBlocksBelowCondition, integer) -> {
        lookBlocksBelowCondition.count = integer;
    }, lookBlocksBelowCondition -> lookBlocksBelowCondition.count).addValidator(Validators.greaterThan(0)).add()).append(new KeyedCodec<Integer>("MinRange", Codec.INTEGER), (lookBlocksBelowCondition, integer) -> {
        lookBlocksBelowCondition.minRange = integer;
    }, lookBlocksBelowCondition -> lookBlocksBelowCondition.minRange).addValidator(Validators.greaterThanOrEqual(0)).add()).append(new KeyedCodec<Integer>("MaxRange", Codec.INTEGER), (lookBlocksBelowCondition, integer) -> {
        lookBlocksBelowCondition.maxRange = integer;
    }, lookBlocksBelowCondition -> lookBlocksBelowCondition.maxRange).addValidator(Validators.greaterThanOrEqual(1)).add()).afterDecode(lookBlocksBelowCondition -> {
        if (lookBlocksBelowCondition.blockTags == null) {
            return;
        }
        lookBlocksBelowCondition.blockTagsIndexes = new int[lookBlocksBelowCondition.blockTags.length];
        for (int i = 0; i < lookBlocksBelowCondition.blockTags.length; ++i) {
            String blockTag = lookBlocksBelowCondition.blockTags[i];
            lookBlocksBelowCondition.blockTagsIndexes[i] = AssetRegistry.getOrCreateTagIndex(blockTag);
        }
        if (lookBlocksBelowCondition.minRange > lookBlocksBelowCondition.maxRange) {
            throw new IllegalArgumentException("LookBlocksBelowCondition.MinRange (" + lookBlocksBelowCondition.minRange + ") needs to be greater than LookBlocksBelowCondition.MaxRange (" + lookBlocksBelowCondition.maxRange + ")");
        }
    })).build();
    protected String[] blockTags;
    protected int count = 1;
    protected int minRange = 0;
    protected int maxRange = 10;
    private int[] blockTagsIndexes;

    public LookBlocksBelowProvider(@Nonnull String[] blockTags, int count, int minRange, int maxRange) {
        this.blockTags = blockTags;
        this.count = count;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.blockTagsIndexes = new int[blockTags.length];
        for (int i = 0; i < blockTags.length; ++i) {
            String blockTag = blockTags[i];
            this.blockTagsIndexes[i] = AssetRegistry.getOrCreateTagIndex(blockTag);
        }
    }

    protected LookBlocksBelowProvider() {
    }

    @Override
    @Nullable
    public Vector3i runCondition(@Nonnull World world, @Nonnull Vector3i position) {
        int y;
        Vector3i newPosition = position.clone();
        Object worldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(newPosition.x, newPosition.z));
        int baseY = newPosition.y;
        int x = newPosition.x;
        int z = newPosition.z;
        int currentCount = 0;
        for (y = newPosition.y; y >= this.minRange && baseY - y <= this.maxRange; --y) {
            String blockStateKey = worldChunk.getBlockType(x, y, z).getId();
            boolean found = false;
            for (int i = 0; i < this.blockTagsIndexes.length; ++i) {
                int blockTagId = this.blockTagsIndexes[i];
                if (!BlockType.getAssetMap().getKeysForTag(blockTagId).contains(blockStateKey)) continue;
                found = true;
                ++currentCount;
                break;
            }
            if (currentCount == this.count) break;
            if (found) continue;
            currentCount = 0;
        }
        return currentCount == this.count ? new Vector3i(x, y, z) : null;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LookBlocksBelowProvider that = (LookBlocksBelowProvider)o;
        if (this.count != that.count) {
            return false;
        }
        if (this.minRange != that.minRange) {
            return false;
        }
        if (this.maxRange != that.maxRange) {
            return false;
        }
        return Arrays.equals(this.blockTags, that.blockTags);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(this.blockTags);
        result = 31 * result + this.count;
        result = 31 * result + this.minRange;
        result = 31 * result + this.maxRange;
        return result;
    }

    @Override
    @Nonnull
    public String toString() {
        return "LookBlocksBelowProvider{blockTags=" + Arrays.toString(this.blockTags) + ", count=" + this.count + ", minRange=" + this.minRange + ", maxRange=" + this.maxRange + "} " + super.toString();
    }
}

