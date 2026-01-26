/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab.selection.mask;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.function.FunctionCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BlockTypeListAsset;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.PlaceFluidInteraction;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import com.hypixel.hytale.server.core.universe.world.accessor.ChunkAccessor;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockFilter {
    public static final BlockFilter[] EMPTY_ARRAY = new BlockFilter[0];
    public static final Codec<BlockFilter> CODEC = new FunctionCodec<String, BlockFilter>(Codec.STRING, BlockFilter::parse, BlockFilter::toString);
    public static final String BLOCK_SEPARATOR = "|";
    public static final Pattern BLOCK_SEPARATOR_PATTERN = Pattern.compile(Pattern.quote("|"));
    @Nonnull
    private final FilterType blockFilterType;
    @Nonnull
    private final String[] blocks;
    private final boolean inverted;
    @Nonnull
    private final transient String toString0;
    private IntSet resolvedBlocks;
    private IntSet resolvedFluids;

    public BlockFilter(@Nonnull FilterType blockFilterType, @Nonnull String[] blocks, boolean inverted) {
        Objects.requireNonNull(blockFilterType);
        Objects.requireNonNull(blocks);
        this.blockFilterType = blockFilterType;
        this.blocks = blocks;
        this.inverted = inverted;
        this.toString0 = this.toString0();
    }

    public void resolve() {
        if (this.resolvedBlocks == null) {
            BlocksAndFluids result = BlockFilter.parseBlocksAndFluids(this.blocks);
            this.resolvedBlocks = result.blocks;
            this.resolvedFluids = result.fluids;
        }
    }

    @Nonnull
    public FilterType getBlockFilterType() {
        return this.blockFilterType;
    }

    @Nonnull
    public String[] getBlocks() {
        return this.blocks;
    }

    public boolean isInverted() {
        return this.inverted;
    }

    public boolean isExcluded(@Nonnull ChunkAccessor accessor, int x, int y, int z, Vector3i min, Vector3i max, int blockId) {
        return this.isExcluded(accessor, x, y, z, min, max, blockId, -1);
    }

    public boolean isExcluded(@Nonnull ChunkAccessor accessor, int x, int y, int z, Vector3i min, Vector3i max, int blockId, int fluidId) {
        boolean exclude = !this.isIncluded(accessor, x, y, z, min, max, blockId, fluidId);
        return this.inverted != exclude;
    }

    private boolean isIncluded(@Nonnull ChunkAccessor accessor, int x, int y, int z, @Nullable Vector3i min, @Nullable Vector3i max, int blockId) {
        return this.isIncluded(accessor, x, y, z, min, max, blockId, -1);
    }

    private boolean isIncluded(@Nonnull ChunkAccessor accessor, int x, int y, int z, @Nullable Vector3i min, @Nullable Vector3i max, int blockId, int fluidId) {
        switch (this.blockFilterType.ordinal()) {
            case 0: {
                this.resolve();
                boolean matchesBlock = this.resolvedBlocks.contains(blockId);
                boolean matchesFluid = fluidId >= 0 && this.resolvedFluids != null && this.resolvedFluids.contains(fluidId);
                return matchesBlock || matchesFluid;
            }
            case 1: {
                return this.matchesAt(accessor, x, y - 1, z);
            }
            case 2: {
                return this.matchesAt(accessor, x, y + 1, z);
            }
            case 3: {
                return this.matchesAt(accessor, x - 1, y, z) || this.matchesAt(accessor, x + 1, y, z) || this.matchesAt(accessor, x, y, z - 1) || this.matchesAt(accessor, x, y, z + 1);
            }
            case 4: {
                for (int xo = -1; xo < 2; ++xo) {
                    for (int yo = -1; yo < 2; ++yo) {
                        for (int zo = -1; zo < 2; ++zo) {
                            if (xo == 0 && yo == 0 && zo == 0 || !this.matchesAt(accessor, x + xo, y + yo, z + zo)) continue;
                            return true;
                        }
                    }
                }
                return false;
            }
            case 5: {
                return this.matchesAt(accessor, x, y, z - 1);
            }
            case 6: {
                return this.matchesAt(accessor, x, y, z + 1);
            }
            case 7: {
                return this.matchesAt(accessor, x, y, z + 1);
            }
            case 8: {
                return this.matchesAt(accessor, x, y, z - 1);
            }
            case 9: {
                return this.matchesAt(accessor, x - 1, y + 1, z) || this.matchesAt(accessor, x - 1, y - 1, z) || this.matchesAt(accessor, x + 1, y + 1, z) || this.matchesAt(accessor, x + 1, y - 1, z);
            }
            case 10: {
                return this.matchesAt(accessor, x - 1, y, z + 1) || this.matchesAt(accessor, x - 1, y, z - 1) || this.matchesAt(accessor, x + 1, y, z + 1) || this.matchesAt(accessor, x + 1, y, z - 1);
            }
            case 11: {
                return this.matchesAt(accessor, x, y - 1, z + 1) || this.matchesAt(accessor, x, y - 1, z - 1) || this.matchesAt(accessor, x, y + 1, z + 1) || this.matchesAt(accessor, x, y + 1, z - 1);
            }
            case 12: {
                if (min == null || max == null) {
                    return false;
                }
                return x >= min.x && y >= min.y && z >= min.z && x <= max.x && y <= max.y && z <= max.z;
            }
        }
        throw new IllegalArgumentException("Unknown filter type: " + String.valueOf((Object)this.blockFilterType));
    }

    private boolean matchesAt(@Nonnull ChunkAccessor accessor, int x, int y, int z) {
        this.resolve();
        if (this.resolvedBlocks.contains(accessor.getBlock(x, y, z))) {
            return true;
        }
        return this.resolvedFluids != null && this.resolvedFluids.contains(accessor.getFluidId(x, y, z));
    }

    @Nonnull
    public String toString() {
        return this.toString0;
    }

    @Nonnull
    public String toString0() {
        return (this.inverted ? "!" : "") + this.blockFilterType.getPrefix() + String.join((CharSequence)BLOCK_SEPARATOR, this.blocks);
    }

    @Nonnull
    public String informativeToString() {
        StringBuilder builder = new StringBuilder();
        String prefix = (this.inverted ? "!" : "") + this.blockFilterType.getPrefix();
        if (this.blocks.length > 1) {
            builder.append("(");
        }
        for (int i = 0; i < this.blocks.length; ++i) {
            builder.append(prefix).append(this.blocks[i]);
            if (i == this.blocks.length - 1) continue;
            builder.append(" OR ");
        }
        if (this.blocks.length > 1) {
            builder.append(")");
        }
        return builder.toString();
    }

    @Nonnull
    public static BlockFilter parse(@Nonnull String str) {
        ParsedFilterParts parts = BlockFilter.parseComponents(str);
        String[] blocks = parts.type.hasBlocks() ? BLOCK_SEPARATOR_PATTERN.split(parts.blocks) : ArrayUtil.EMPTY_STRING_ARRAY;
        return new BlockFilter(parts.type, blocks, parts.inverted);
    }

    @Nonnull
    public static ParsedFilterParts parseComponents(@Nonnull String str) {
        boolean invert = str.startsWith("!");
        int index = invert ? 1 : 0;
        FilterType filterType = FilterType.parse(str, index);
        String blocks = str.substring(index += filterType.getPrefix().length());
        return new ParsedFilterParts(filterType, invert, blocks);
    }

    @Nonnull
    public static IntSet parseBlocks(@Nonnull String[] blocksArgs) {
        return BlockFilter.parseBlocksAndFluids((String[])blocksArgs).blocks;
    }

    @Nonnull
    private static BlocksAndFluids parseBlocksAndFluids(@Nonnull String[] blocksArgs) {
        IntOpenHashSet blocks = new IntOpenHashSet();
        IntOpenHashSet fluids = new IntOpenHashSet();
        for (String blockArg : blocksArgs) {
            BlockTypeListAsset blockTypeListAsset;
            int fluidId;
            Item item = Item.getAssetMap().getAsset(blockArg);
            if (item != null && (fluidId = BlockFilter.getFluidIdFromItem(item)) >= 0) {
                fluids.add(fluidId);
                continue;
            }
            int blockId = BlockPattern.parseBlock(blockArg);
            BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
            if (blockType != null && blockType.getBlockListAssetId() != null && (blockTypeListAsset = BlockTypeListAsset.getAssetMap().getAsset(blockType.getBlockListAssetId())) != null && blockTypeListAsset.getBlockPattern() != null) {
                Integer[] integerArray = blockTypeListAsset.getBlockPattern().getResolvedKeys();
                int n = integerArray.length;
                for (int i = 0; i < n; ++i) {
                    int resolvedKey = integerArray[i];
                    blocks.add(resolvedKey);
                }
                continue;
            }
            blocks.add(blockId);
        }
        return new BlocksAndFluids(IntSets.unmodifiable(blocks), fluids.isEmpty() ? null : IntSets.unmodifiable(fluids));
    }

    private static int getFluidIdFromItem(@Nonnull Item item) {
        Map<InteractionType, String> interactions = item.getInteractions();
        String secondaryRootId = interactions.get((Object)InteractionType.Secondary);
        if (secondaryRootId == null) {
            return -1;
        }
        RootInteraction rootInteraction = (RootInteraction)RootInteraction.getAssetMap().getAsset(secondaryRootId);
        if (rootInteraction == null) {
            return -1;
        }
        for (String interactionId : rootInteraction.getInteractionIds()) {
            int fluidId;
            PlaceFluidInteraction placeFluidInteraction;
            String fluidKey;
            Interaction interaction = (Interaction)Interaction.getAssetMap().getAsset(interactionId);
            if (!(interaction instanceof PlaceFluidInteraction) || (fluidKey = (placeFluidInteraction = (PlaceFluidInteraction)interaction).getFluidKey()) == null || (fluidId = Fluid.getAssetMap().getIndex(fluidKey)) < 0) continue;
            return fluidId;
        }
        return -1;
    }

    public static enum FilterType {
        TargetBlock(""),
        AboveBlock(">"),
        BelowBlock("<"),
        AdjacentBlock("~"),
        NeighborBlock("^"),
        NorthBlock("+n"),
        EastBlock("+e"),
        SouthBlock("+s"),
        WestBlock("+w"),
        DiagonalXy("%xy"),
        DiagonalXz("%xz"),
        DiagonalZy("%zy"),
        Selection("#", false);

        public static final String INVERT_PREFIX = "!";
        public static final String TARGET_BLOCK_PREFIX = "";
        public static final String ABOVE_BLOCK_PREFIX = ">";
        public static final String BELOW_BLOCK_PREFIX = "<";
        public static final String ADJACENT_BLOCK_PREFIX = "~";
        public static final String NEIGHBOR_BLOCK_PREFIX = "^";
        public static final String SELECTION_PREFIX = "#";
        public static final String CARDINAL_NORTH_PREFIX = "+n";
        public static final String CARDINAL_EAST_PREFIX = "+e";
        public static final String CARDINAL_SOUTH_PREFIX = "+s";
        public static final String CARDINAL_WEST_PREFIX = "+w";
        public static final String DIAGONAL_XY_PREFIX = "%xy";
        public static final String DIAGONAL_XZ_PREFIX = "%xz";
        public static final String DIAGONAL_ZY_PREFIX = "%zy";
        @Nonnull
        private static final FilterType[] VALUES_TO_PARSE;
        private final String prefix;
        private final boolean hasBlocks;

        private FilterType(String prefix) {
            this.prefix = prefix;
            this.hasBlocks = true;
        }

        private FilterType(String prefix, boolean hasBlocks) {
            this.prefix = prefix;
            this.hasBlocks = hasBlocks;
        }

        public boolean hasBlocks() {
            return this.hasBlocks;
        }

        public String getPrefix() {
            return this.prefix;
        }

        @Nonnull
        public static FilterType parse(@Nonnull String str, int index) {
            for (FilterType filterType : VALUES_TO_PARSE) {
                if (!str.startsWith(filterType.prefix, index)) continue;
                return filterType;
            }
            return TargetBlock;
        }

        static {
            FilterType[] values = FilterType.values();
            FilterType[] valuesToParse = new FilterType[values.length - 1];
            int i = 0;
            for (FilterType value : values) {
                if (value == TargetBlock) continue;
                valuesToParse[i++] = value;
            }
            VALUES_TO_PARSE = valuesToParse;
        }
    }

    private static class BlocksAndFluids {
        final IntSet blocks;
        final IntSet fluids;

        BlocksAndFluids(IntSet blocks, IntSet fluids) {
            this.blocks = blocks;
            this.fluids = fluids;
        }
    }

    public record ParsedFilterParts(FilterType type, boolean inverted, String blocks) {
    }
}

