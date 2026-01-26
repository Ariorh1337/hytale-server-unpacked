/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.prefab;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.loader.prefab.BlockPlacementMaskRegistry;
import com.hypixel.hytale.server.worldgen.loader.util.ResolvedBlockArrayJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.util.ResolvedVariantsBlockArrayLoader;
import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class BlockPlacementMaskJsonLoader
extends JsonLoader<SeedStringResource, BlockMaskCondition> {
    private String fileName;

    public BlockPlacementMaskJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".BlockMaskCondition"), dataFolder, json);
    }

    @Override
    public BlockMaskCondition load() {
        BlockMaskCondition.Mask defaultMask;
        BlockMaskCondition mask;
        BlockPlacementMaskRegistry registry = ((SeedStringResource)this.seed.get()).getBlockMaskRegistry();
        if (this.fileName != null && (mask = (BlockMaskCondition)registry.getIfPresentFileMask(this.fileName)) != null) {
            return mask;
        }
        Long2ObjectMap<BlockMaskCondition.Mask> specificMasks = Long2ObjectMaps.emptyMap();
        if (this.json == null || this.json.isJsonNull()) {
            defaultMask = BlockMaskCondition.DEFAULT_MASK;
        } else {
            defaultMask = this.has("Default") ? new BlockMaskCondition.Mask(this.loadEntries(this.get("Default").getAsJsonArray())) : BlockMaskCondition.DEFAULT_MASK;
            if (this.has("Specific")) {
                BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
                specificMasks = new Long2ObjectOpenHashMap<BlockMaskCondition.Mask>();
                JsonArray array = this.get("Specific").getAsJsonArray();
                for (int i = 0; i < array.size(); ++i) {
                    try {
                        JsonObject specificObject = array.get(i).getAsJsonObject();
                        JsonElement blocksElement = specificObject.get("Block");
                        ResolvedBlockArray blocks = new ResolvedBlockArrayJsonLoader(this.seed, this.dataFolder, blocksElement).load();
                        for (BlockFluidEntry blockEntry : blocks.getEntries()) {
                            String key = assetMap.getAsset(blockEntry.blockId()).getId();
                            for (String variant : assetMap.getSubKeys(key)) {
                                int index = assetMap.getIndex(variant);
                                if (index == Integer.MIN_VALUE) {
                                    throw new IllegalArgumentException("Unknown key! " + variant);
                                }
                                JsonArray rule = specificObject.getAsJsonArray("Rule");
                                specificMasks.put(MathUtil.packLong(index, blockEntry.fluidId()), new BlockMaskCondition.Mask(this.loadEntries(rule)));
                            }
                        }
                        continue;
                    }
                    catch (Throwable e) {
                        throw new Error(String.format("Error while reading specific block mask #%s!", i), e);
                    }
                }
            }
        }
        BlockMaskCondition mask2 = registry.retainOrAllocateMask(defaultMask, specificMasks);
        if (this.fileName != null) {
            registry.putFileMask(this.fileName, mask2);
        }
        return mask2;
    }

    @Nonnull
    protected BlockMaskCondition.MaskEntry[] loadEntries(@Nonnull JsonArray jsonArray) {
        BlockMaskCondition.MaskEntry[] entries = new BlockMaskCondition.MaskEntry[jsonArray.size()];
        int head = 0;
        int tail = entries.length;
        for (JsonElement element : jsonArray) {
            boolean replace;
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                replace = true;
                if (obj.has("Replace")) {
                    replace = obj.get("Replace").getAsBoolean();
                }
                ResolvedBlockArray blocks = ResolvedVariantsBlockArrayLoader.loadSingleBlock(obj);
                entries[head++] = ((SeedStringResource)this.seed.get()).getBlockMaskRegistry().retainOrAllocateEntry(blocks, replace);
                continue;
            }
            String string = element.getAsString();
            replace = true;
            int beginIndex = 0;
            if (string.charAt(0) == '!') {
                replace = false;
                beginIndex = 1;
            }
            if (string.length() == beginIndex + 1 && string.charAt(beginIndex) == '*') {
                if (tail < entries.length) {
                    System.arraycopy(entries, tail, entries, tail - 1, entries.length - tail);
                }
                entries[entries.length - 1] = replace ? BlockMaskCondition.MaskEntry.WILDCARD_TRUE : BlockMaskCondition.MaskEntry.WILDCARD_FALSE;
                --tail;
                continue;
            }
            string = string.substring(beginIndex);
            ResolvedBlockArray blocks = ResolvedVariantsBlockArrayLoader.loadSingleBlock(string);
            entries[head++] = ((SeedStringResource)this.seed.get()).getBlockMaskRegistry().retainOrAllocateEntry(blocks, replace);
        }
        return entries;
    }

    @Override
    protected JsonElement loadFileConstructor(String filePath) {
        this.fileName = filePath;
        return ((SeedStringResource)this.seed.get()).getBlockMaskRegistry().cachedFile(filePath, file -> super.loadFileConstructor((String)file));
    }

    public static interface Constants {
        public static final String KEY_DEFAULT = "Default";
        public static final String KEY_SPECIFIC = "Specific";
        public static final String KEY_BLOCK = "Block";
        public static final String KEY_RULE = "Rule";
        public static final String ERROR_FAIL_SPECIFIC = "Error while reading specific block mask #%s!";
        public static final String ERROR_BLOCK_INVALID = "Failed to resolve block \"%s\"";
    }
}

