/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab.selection.mask;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.function.FunctionCodec;
import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.common.map.WeightedMap;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BlockTypeListAsset;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockPattern {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final Codec<BlockPattern> CODEC = new FunctionCodec<String, BlockPattern>(Codec.STRING, BlockPattern::parse, BlockPattern::toString);
    public static final BlockPattern EMPTY = new BlockPattern(BlockPattern.parseBlockPattern("Empty"));
    public static final BlockPattern[] EMPTY_ARRAY = new BlockPattern[0];
    private static final Pattern FILLER_TEMP_REMOVER_PATTERN = Pattern.compile("(Filler=-?\\d+),(-?\\d+),(-?\\d+)");
    private static final String BLOCK_SEPARATOR = ",";
    private static final String ALT_BLOCK_SEPARATOR = ";";
    private static final String CHANCE_SUFFIX = "%";
    private static final double DEFAULT_CHANCE = 100.0;
    private final IWeightedMap<String> weightedMap;
    private final transient String toString0;
    private IWeightedMap<Integer> resolvedWeightedMap;
    private IWeightedMap<BlockEntry> resolvedWeightedMapBtk;

    public BlockPattern(IWeightedMap<String> weightedMap) {
        this.weightedMap = weightedMap;
        this.toString0 = this.toString0();
    }

    public Integer[] getResolvedKeys() {
        this.resolve();
        return this.resolvedWeightedMap.internalKeys();
    }

    public void resolve() {
        if (this.resolvedWeightedMap != null) {
            return;
        }
        WeightedMap.Builder<Integer> mapBuilder = WeightedMap.builder(ArrayUtil.EMPTY_INTEGER_ARRAY);
        WeightedMap.Builder<BlockEntry> mapBuilderKey = WeightedMap.builder(new BlockEntry[0]);
        this.weightedMap.forEachEntry((blockName, weight) -> {
            BlockTypeListAsset blockTypeListAsset;
            int blockId = BlockPattern.parseBlock(blockName);
            BlockEntry key = BlockPattern.tryParseBlockTypeKey(blockName);
            BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
            if (blockType != null && blockType.getBlockListAssetId() != null && (blockTypeListAsset = BlockTypeListAsset.getAssetMap().getAsset(blockType.getBlockListAssetId())) != null && blockTypeListAsset.getBlockPattern() != null) {
                for (String resolvedKey : blockTypeListAsset.getBlockTypeKeys()) {
                    int resolvedId = BlockType.getAssetMap().getIndex(resolvedKey);
                    if (resolvedId == Integer.MIN_VALUE) {
                        LOGGER.at(Level.WARNING).log("BlockTypeList '%s' contains invalid block '%s' - skipping", (Object)blockType.getBlockListAssetId(), (Object)resolvedKey);
                        continue;
                    }
                    mapBuilder.put(resolvedId, weight / (double)blockTypeListAsset.getBlockTypeKeys().size());
                }
                return;
            }
            mapBuilder.put(blockId, weight);
            if (key != null) {
                mapBuilderKey.put(key, weight);
            }
        });
        this.resolvedWeightedMap = mapBuilder.build();
        this.resolvedWeightedMapBtk = mapBuilderKey.build();
    }

    public boolean isEmpty() {
        return this.weightedMap.size() == 0;
    }

    public int nextBlock(Random random) {
        this.resolve();
        return this.resolvedWeightedMap.get(random);
    }

    @Nullable
    public BlockEntry nextBlockTypeKey(Random random) {
        this.resolve();
        return this.resolvedWeightedMapBtk.get(random);
    }

    @Deprecated
    public int firstBlock() {
        this.resolve();
        return this.resolvedWeightedMap.size() > 0 ? this.resolvedWeightedMap.internalKeys()[0] : 0;
    }

    public String toString() {
        return this.toString0;
    }

    private String toString0() {
        if (this.weightedMap.size() == 1) {
            return this.weightedMap.internalKeys()[0];
        }
        ObjectArrayList blocks = new ObjectArrayList();
        this.weightedMap.forEachEntry((k, v) -> blocks.add(v + CHANCE_SUFFIX + k));
        return String.join((CharSequence)BLOCK_SEPARATOR, blocks);
    }

    public static BlockPattern parse(@Nonnull String str) {
        if (str.isEmpty() || str.equals("Empty")) {
            return EMPTY;
        }
        if (str.toLowerCase().contains("filler=")) {
            str = FILLER_TEMP_REMOVER_PATTERN.matcher(str).replaceAll("$1;$2;$3");
        }
        str = str.replace(ALT_BLOCK_SEPARATOR, BLOCK_SEPARATOR);
        return new BlockPattern(BlockPattern.parseBlockPattern(str.split(BLOCK_SEPARATOR)));
    }

    @Nonnull
    private static IWeightedMap<String> parseBlockPattern(String ... blocksArgs) {
        WeightedMap.Builder<String> builder = WeightedMap.builder(ArrayUtil.EMPTY_STRING_ARRAY);
        for (String blockArg : blocksArgs) {
            if (blockArg.isEmpty()) continue;
            double chance = 100.0;
            String[] blockArr = blockArg.split(CHANCE_SUFFIX);
            if (blockArr.length > 1) {
                try {
                    chance = Double.parseDouble(blockArr[0]);
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid Chance Value: " + blockArr[0], e);
                }
                blockArg = blockArr[1];
            }
            builder.put(blockArg, chance);
        }
        return builder.build();
    }

    public static int parseBlock(@Nonnull String blockText) {
        int blockId;
        block4: {
            try {
                blockId = Integer.parseInt(blockText);
                if (BlockType.getAssetMap().getAsset(blockId) == null) {
                    throw new IllegalArgumentException("Block with id '" + blockText + "' doesn't exist!");
                }
            }
            catch (NumberFormatException ignored) {
                blockText = blockText.replace(ALT_BLOCK_SEPARATOR, BLOCK_SEPARATOR);
                int oldData = blockText.indexOf(124);
                if (oldData != -1) {
                    blockText = blockText.substring(0, oldData);
                }
                if ((blockId = BlockType.getAssetMap().getIndex(blockText)) != Integer.MIN_VALUE) break block4;
                LOGGER.at(Level.WARNING).log("Invalid block name '%s' - using empty block", blockText);
                return 0;
            }
        }
        return blockId;
    }

    @Nullable
    public static BlockEntry tryParseBlockTypeKey(String blockText) {
        try {
            blockText = blockText.replace(ALT_BLOCK_SEPARATOR, BLOCK_SEPARATOR);
            return BlockEntry.decode(blockText);
        }
        catch (Exception e) {
            return null;
        }
    }

    public record BlockEntry(String blockTypeKey, int rotation, int filler) {
        @Deprecated(forRemoval=true)
        public static Codec<BlockEntry> CODEC = new FunctionCodec<String, BlockEntry>(Codec.STRING, BlockEntry::decode, BlockEntry::encode);

        @Deprecated(forRemoval=true)
        private String encode() {
            if (this.filler == 0 && this.rotation == 0) {
                return this.blockTypeKey;
            }
            StringBuilder out = new StringBuilder(this.blockTypeKey);
            RotationTuple rot = RotationTuple.get(this.rotation);
            if (rot.yaw() != Rotation.None) {
                out.append("|Yaw=").append(rot.yaw().getDegrees());
            }
            if (rot.pitch() != Rotation.None) {
                out.append("|Pitch=").append(rot.pitch().getDegrees());
            }
            if (rot.roll() != Rotation.None) {
                out.append("|Roll=").append(rot.roll().getDegrees());
            }
            if (this.filler != 0) {
                int fillerX = FillerBlockUtil.unpackX(this.filler);
                int fillerY = FillerBlockUtil.unpackY(this.filler);
                int fillerZ = FillerBlockUtil.unpackZ(this.filler);
                out.append("|Filler=").append(fillerX).append(BlockPattern.BLOCK_SEPARATOR).append(fillerY).append(BlockPattern.BLOCK_SEPARATOR).append(fillerZ);
            }
            return out.toString();
        }

        @Deprecated(forRemoval=true)
        public static BlockEntry decode(String key) {
            int end;
            int start;
            int end2;
            int filler = 0;
            if (key.contains("|Filler=")) {
                int start2 = key.indexOf("|Filler=") + "|Filler=".length();
                int firstComma = key.indexOf(44, start2);
                if (firstComma == -1) {
                    throw new IllegalArgumentException("Invalid filler metadata! Missing comma");
                }
                int secondComma = key.indexOf(44, firstComma + 1);
                if (secondComma == -1) {
                    throw new IllegalArgumentException("Invalid filler metadata! Missing second comma");
                }
                end2 = key.indexOf(124, start2);
                if (end2 == -1) {
                    end2 = key.length();
                }
                int fillerX = Integer.parseInt(key, start2, firstComma, 10);
                int fillerY = Integer.parseInt(key, firstComma + 1, secondComma, 10);
                int fillerZ = Integer.parseInt(key, secondComma + 1, end2, 10);
                filler = FillerBlockUtil.pack(fillerX, fillerY, fillerZ);
            }
            Rotation rotationYaw = Rotation.None;
            Rotation rotationPitch = Rotation.None;
            Rotation rotationRoll = Rotation.None;
            if (key.contains("|Yaw=")) {
                start = key.indexOf("|Yaw=") + "|Yaw=".length();
                end = key.indexOf(124, start);
                if (end == -1) {
                    end = key.length();
                }
                rotationYaw = Rotation.ofDegrees(Integer.parseInt(key, start, end, 10));
            }
            if (key.contains("|Pitch=")) {
                start = key.indexOf("|Pitch=") + "|Pitch=".length();
                end = key.indexOf(124, start);
                if (end == -1) {
                    end = key.length();
                }
                rotationPitch = Rotation.ofDegrees(Integer.parseInt(key, start, end, 10));
            }
            if (key.contains("|Roll=")) {
                start = key.indexOf("|Roll=") + "|Roll=".length();
                end = key.indexOf(124, start);
                if (end == -1) {
                    end = key.length();
                }
                rotationRoll = Rotation.ofDegrees(Integer.parseInt(key, start, end, 10));
            }
            if ((end2 = key.indexOf(124)) == -1) {
                end2 = key.length();
            }
            String name = key.substring(0, end2);
            return new BlockEntry(name, RotationTuple.of(rotationYaw, rotationPitch, rotationRoll).index(), filler);
        }
    }
}

