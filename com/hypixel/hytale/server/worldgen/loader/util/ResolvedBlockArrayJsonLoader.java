/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class ResolvedBlockArrayJsonLoader
extends JsonLoader<SeedStringResource, ResolvedBlockArray> {
    public ResolvedBlockArrayJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
        super(seed.append("ResolvedBlockArray"), dataFolder, json);
    }

    @Override
    public ResolvedBlockArray load() {
        if (this.json == null || this.json.isJsonNull()) {
            return ResolvedBlockArray.EMPTY;
        }
        if (!this.json.isJsonArray()) {
            if (this.json.isJsonObject()) {
                return ResolvedBlockArrayJsonLoader.loadSingleBlock(this.json.getAsJsonObject());
            }
            return this.loadSingleBlock(this.json.getAsString());
        }
        JsonArray jsonArray = this.json.getAsJsonArray();
        if (jsonArray.size() == 1) {
            if (jsonArray.get(0).isJsonObject()) {
                return ResolvedBlockArrayJsonLoader.loadSingleBlock(jsonArray.get(0).getAsJsonObject());
            }
            return this.loadSingleBlock(jsonArray.get(0).getAsString());
        }
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        IndexedLookupTableAssetMap<String, Fluid> fluidMap = Fluid.getAssetMap();
        BlockFluidEntry[] blocks = new BlockFluidEntry[jsonArray.size()];
        for (int k = 0; k < blocks.length; ++k) {
            JsonElement elm = jsonArray.get(k);
            if (elm.isJsonObject()) {
                int index;
                Object key;
                JsonObject obj = elm.getAsJsonObject();
                int blockIndex = 0;
                int rotation = 0;
                int fluidIndex = 0;
                if (obj.has("Block")) {
                    key = BlockPattern.BlockEntry.decode(obj.get("Block").getAsString());
                    index = BlockType.getBlockIdOrUnknown(((BlockPattern.BlockEntry)key).blockTypeKey(), "Failed to find block '%s' in resolved block array!", ((BlockPattern.BlockEntry)key).blockTypeKey());
                    if (index == Integer.MIN_VALUE) {
                        throw new IllegalArgumentException("Unknown key! " + String.valueOf(key));
                    }
                    blockIndex = index;
                    rotation = ((BlockPattern.BlockEntry)key).rotation();
                }
                if (obj.has("Fluid")) {
                    key = obj.get("Fluid").getAsString();
                    index = Fluid.getFluidIdOrUnknown((String)key, "Failed to find fluid '%s' in resolved block array!", key);
                    if (index == Integer.MIN_VALUE) {
                        throw new IllegalArgumentException("Unknown key! " + (String)key);
                    }
                    fluidIndex = index;
                }
                blocks[k] = new BlockFluidEntry(blockIndex, rotation, fluidIndex);
                continue;
            }
            String blockName = elm.getAsString();
            try {
                BlockPattern.BlockEntry key = BlockPattern.BlockEntry.decode(blockName);
                int index = BlockType.getBlockIdOrUnknown(key.blockTypeKey(), "Failed to find block '%s' in resolved block array!", key.blockTypeKey());
                if (index == Integer.MIN_VALUE) {
                    throw new IllegalArgumentException("Unknown key! " + String.valueOf(key));
                }
                blocks[k] = new BlockFluidEntry(index, key.rotation(), 0);
                continue;
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("BlockLayer " + blockName + " does not exist in BlockTypes", e);
            }
        }
        return new ResolvedBlockArray(blocks);
    }

    @Nonnull
    public ResolvedBlockArray loadSingleBlock(@Nonnull String blockName) {
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        try {
            ResolvedBlockArray cachedResolvedBlockArray;
            BlockPattern.BlockEntry key = BlockPattern.BlockEntry.decode(blockName);
            int index = assetMap.getIndex(key.blockTypeKey());
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + String.valueOf(key));
            }
            long mapIndex = MathUtil.packLong(index, 0);
            if (key.rotation() == 0 && (cachedResolvedBlockArray = (ResolvedBlockArray)ResolvedBlockArray.RESOLVED_BLOCKS.get(mapIndex)) != null) {
                return cachedResolvedBlockArray;
            }
            ResolvedBlockArray resolvedBlockArray = new ResolvedBlockArray(new BlockFluidEntry[]{new BlockFluidEntry(index, key.rotation(), 0)});
            if (key.rotation() == 0) {
                ResolvedBlockArray.RESOLVED_BLOCKS.put(mapIndex, resolvedBlockArray);
            }
            return resolvedBlockArray;
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("BlockLayer does not exist in BlockTypes", e);
        }
    }

    @Nonnull
    public static ResolvedBlockArray loadSingleBlock(@Nonnull JsonObject obj) {
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        IndexedLookupTableAssetMap<String, Fluid> fluidMap = Fluid.getAssetMap();
        try {
            ResolvedBlockArray cachedResolvedBlockArray;
            int index;
            Object key;
            int blockIndex = 0;
            int rotation = 0;
            int fluidIndex = 0;
            if (obj.has("Block")) {
                key = BlockPattern.BlockEntry.decode(obj.get("Block").getAsString());
                index = assetMap.getIndex(((BlockPattern.BlockEntry)key).blockTypeKey());
                if (index == Integer.MIN_VALUE) {
                    throw new IllegalArgumentException("Unknown key! " + String.valueOf(key));
                }
                blockIndex = index;
                rotation = ((BlockPattern.BlockEntry)key).rotation();
            }
            if (obj.has("Fluid")) {
                key = obj.get("Fluid").getAsString();
                index = fluidMap.getIndex((String)key);
                if (index == Integer.MIN_VALUE) {
                    throw new IllegalArgumentException("Unknown key! " + (String)key);
                }
                fluidIndex = index;
            }
            long mapIndex = MathUtil.packLong(blockIndex, fluidIndex);
            if (rotation == 0 && (cachedResolvedBlockArray = (ResolvedBlockArray)ResolvedBlockArray.RESOLVED_BLOCKS.get(mapIndex)) != null) {
                return cachedResolvedBlockArray;
            }
            ResolvedBlockArray resolvedBlockArray = new ResolvedBlockArray(new BlockFluidEntry[]{new BlockFluidEntry(blockIndex, rotation, fluidIndex)});
            if (rotation == 0) {
                ResolvedBlockArray.RESOLVED_BLOCKS.put(mapIndex, resolvedBlockArray);
            }
            return resolvedBlockArray;
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("BlockLayer does not exist in BlockTypes", e);
        }
    }
}

