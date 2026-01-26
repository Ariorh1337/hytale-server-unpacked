/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.loader.util.ResolvedBlockArrayJsonLoader;
import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nonnull;

public class ResolvedVariantsBlockArrayLoader
extends JsonLoader<SeedStringResource, ResolvedBlockArray> {
    public ResolvedVariantsBlockArrayLoader(SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public ResolvedBlockArray load() {
        if (this.json == null || this.json.isJsonNull()) {
            return ResolvedBlockArray.EMPTY;
        }
        if (!this.json.isJsonArray()) {
            return ResolvedVariantsBlockArrayLoader.loadSingleBlock(this.json.getAsString());
        }
        JsonArray jsonArray = this.json.getAsJsonArray();
        if (jsonArray.size() == 1) {
            return ResolvedVariantsBlockArrayLoader.loadSingleBlock(jsonArray.get(0).getAsString());
        }
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        ArrayList<BlockFluidEntry[]> resolvedBlocksList = new ArrayList<BlockFluidEntry[]>();
        int size = 0;
        for (int k = 0; k < jsonArray.size(); ++k) {
            String blockName = jsonArray.get(k).getAsString();
            try {
                if (assetMap.getAsset(blockName) == null) {
                    throw new IllegalArgumentException(String.valueOf(blockName));
                }
                int index = assetMap.getIndex(blockName);
                if (index == Integer.MIN_VALUE) {
                    throw new IllegalArgumentException("Unknown key! " + blockName);
                }
                ResolvedBlockArray cachedResolvedBlockArray = (ResolvedBlockArray)ResolvedBlockArray.RESOLVED_BLOCKS_WITH_VARIANTS.get(index);
                BlockFluidEntry[] blockVariantArray = cachedResolvedBlockArray != null ? cachedResolvedBlockArray.getEntries() : ResolvedVariantsBlockArrayLoader.resolveBlockArrayWithVariants(blockName, assetMap, 0);
                resolvedBlocksList.add(blockVariantArray);
                size += blockVariantArray.length;
                continue;
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("BlockLayer does not exist in BlockTypes", e);
            }
        }
        BlockFluidEntry[] blocks = new BlockFluidEntry[size];
        Iterator iterator = resolvedBlocksList.iterator();
        while (iterator.hasNext()) {
            BlockFluidEntry[] blockArray;
            for (BlockFluidEntry block : blockArray = (BlockFluidEntry[])iterator.next()) {
                blocks[--size] = block;
            }
        }
        return new ResolvedBlockArray(blocks);
    }

    @Nonnull
    public static ResolvedBlockArray loadSingleBlock(@Nonnull String blockName) {
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        try {
            if (assetMap.getAsset(blockName) == null) {
                throw new IllegalArgumentException(String.valueOf(blockName));
            }
            int blockId = assetMap.getIndex(blockName);
            if (blockId == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown block! " + blockName);
            }
            long mapIndex = MathUtil.packLong(blockId, 0);
            ResolvedBlockArray cachedResolvedBlockArray = (ResolvedBlockArray)ResolvedBlockArray.RESOLVED_BLOCKS_WITH_VARIANTS.get(mapIndex);
            if (cachedResolvedBlockArray != null) {
                return cachedResolvedBlockArray;
            }
            BlockFluidEntry[] blocks = ResolvedVariantsBlockArrayLoader.resolveBlockArrayWithVariants(blockName, assetMap, 0);
            ResolvedBlockArray resolvedBlockArray = new ResolvedBlockArray(blocks);
            ResolvedBlockArray.RESOLVED_BLOCKS_WITH_VARIANTS.put(mapIndex, resolvedBlockArray);
            return resolvedBlockArray;
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("BlockLayer does not exist in BlockTypes", e);
        }
    }

    @Nonnull
    public static ResolvedBlockArray loadSingleBlock(@Nonnull JsonObject object) {
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        try {
            if (object.has("Block")) {
                long mapIndex;
                ResolvedBlockArray cachedResolvedBlockArray;
                String blockName = object.get("Block").getAsString();
                if (assetMap.getAsset(blockName) == null) {
                    throw new IllegalArgumentException(String.valueOf(blockName));
                }
                int blockId = assetMap.getIndex(blockName);
                if (blockId == Integer.MIN_VALUE) {
                    throw new IllegalArgumentException("Unknown block! " + blockName);
                }
                int fluidId = 0;
                if (object.has("Fluid")) {
                    String fluidName = object.get("Fluid").getAsString();
                    fluidId = Fluid.getAssetMap().getIndex(fluidName);
                    if (fluidId == Integer.MIN_VALUE) {
                        throw new IllegalArgumentException("Unknown fluid! " + fluidName);
                    }
                }
                if ((cachedResolvedBlockArray = (ResolvedBlockArray)ResolvedBlockArray.RESOLVED_BLOCKS_WITH_VARIANTS.get(mapIndex = MathUtil.packLong(blockId, fluidId))) != null) {
                    return cachedResolvedBlockArray;
                }
                BlockFluidEntry[] blocks = ResolvedVariantsBlockArrayLoader.resolveBlockArrayWithVariants(blockName, assetMap, fluidId);
                ResolvedBlockArray resolvedBlockArray = new ResolvedBlockArray(blocks);
                ResolvedBlockArray.RESOLVED_BLOCKS_WITH_VARIANTS.put(mapIndex, resolvedBlockArray);
                return resolvedBlockArray;
            }
            if (object.has("Fluid")) {
                return ResolvedBlockArrayJsonLoader.loadSingleBlock(object);
            }
            throw new IllegalArgumentException("Required either Block or Fluid key");
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("BlockLayer does not exist in BlockTypes", e);
        }
    }

    @Nonnull
    public static BlockFluidEntry[] resolveBlockArrayWithVariants(String baseKey, @Nonnull BlockTypeAssetMap<String, BlockType> assetMap, int fluidId) {
        ArrayList<String> variants = new ArrayList<String>(assetMap.getSubKeys(baseKey));
        BlockFluidEntry[] blocks = new BlockFluidEntry[variants.size()];
        for (int i = 0; i < variants.size(); ++i) {
            String key = (String)variants.get(i);
            int index = assetMap.getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + key);
            }
            blocks[i] = new BlockFluidEntry(index, 0, fluidId);
        }
        return blocks;
    }
}

