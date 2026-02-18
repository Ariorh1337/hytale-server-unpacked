/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.fluid;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.fluid.FluidTicker;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.asset.type.tagpattern.config.TagPattern;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FireFluidTicker
extends FluidTicker {
    public static final BuilderCodec<FireFluidTicker> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FireFluidTicker.class, FireFluidTicker::new, BASE_CODEC).appendInherited(new KeyedCodec<String>("SpreadFluid", Codec.STRING), (ticker, o) -> {
        ticker.spreadFluid = o;
    }, ticker -> ticker.spreadFluid, (ticker, parent) -> {
        ticker.spreadFluid = parent.spreadFluid;
    }).addValidator(Fluid.VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<T[]>("Flammability", new ArrayCodec<FlammabilityConfig>(FlammabilityConfig.CODEC, FlammabilityConfig[]::new)), (ticker, o) -> {
        ArrayList combined = new ArrayList();
        if (ticker.rawFlammabilityConfigs != null) {
            Collections.addAll(combined, ticker.rawFlammabilityConfigs);
        }
        Collections.addAll(combined, o);
        ticker.rawFlammabilityConfigs = combined.toArray(new FlammabilityConfig[0]);
    }, ticker -> ticker.rawFlammabilityConfigs, (ticker, parent) -> {
        ticker.rawFlammabilityConfigs = parent.rawFlammabilityConfigs;
    }).documentation("Defines flammability configs per tag pattern").add()).build();
    public static final FireFluidTicker INSTANCE = new FireFluidTicker();
    private static final Vector3i[] OFFSETS = new Vector3i[]{new Vector3i(0, -1, 0), new Vector3i(0, 1, 0), new Vector3i(0, 0, -1), new Vector3i(0, 0, 1), new Vector3i(-1, 0, 0), new Vector3i(1, 0, 0)};
    private String spreadFluid;
    private int spreadFluidId;
    private FlammabilityConfig[] rawFlammabilityConfigs = new FlammabilityConfig[0];
    @Nullable
    private transient List<FlammabilityConfig> sortedFlammabilityConfigs = null;

    @Override
    @Nonnull
    protected FluidTicker.AliveStatus isAlive(@Nonnull FluidTicker.Accessor accessor, @Nonnull FluidSection fluidSection, @Nonnull BlockSection blockSection, Fluid fluid, int fluidId, byte fluidLevel, int worldX, int worldY, int worldZ) {
        return FluidTicker.AliveStatus.ALIVE;
    }

    @Override
    @Nonnull
    protected BlockTickStrategy spread(@Nonnull World world, long tick, @Nonnull FluidTicker.Accessor accessor, @Nonnull FluidSection fluidSection, BlockSection blockSection, @Nonnull Fluid fluid, int fluidId, byte fluidLevel, int worldX, int worldY, int worldZ) {
        BlockTypeAssetMap<String, BlockType> blockMap = BlockType.getAssetMap();
        int spreadFluidId = this.getSpreadFluidId(fluidId);
        BlockType currentBlock = blockMap.getAsset(blockSection.get(worldX, worldY, worldZ));
        int maxLevel = fluid.getMaxFluidLevel();
        if (fluidLevel < maxLevel) {
            fluidLevel = (byte)(fluidLevel + 1);
            fluidSection.setFluid(worldX, worldY, worldZ, fluidId, fluidLevel);
        }
        for (Vector3i offset : OFFSETS) {
            boolean isFlammable;
            BlockSection otherBlockSection;
            int x = offset.x;
            int y = offset.y;
            int z = offset.z;
            int blockX = worldX + x;
            int blockY = worldY + y;
            int blockZ = worldZ + z;
            if (blockY < 0 || blockY >= 320) continue;
            boolean isDifferentSection = !ChunkUtil.isSameChunkSection(worldX, worldY, worldZ, blockX, blockY, blockZ);
            FluidSection otherFluidSection = isDifferentSection ? accessor.getFluidSectionByBlock(blockX, blockY, blockZ) : fluidSection;
            BlockSection blockSection2 = otherBlockSection = isDifferentSection ? accessor.getBlockSectionByBlock(blockX, blockY, blockZ) : blockSection;
            if (otherFluidSection == null || otherBlockSection == null) {
                return BlockTickStrategy.WAIT_FOR_ADJACENT_CHUNK_LOAD;
            }
            int otherBlockId = otherBlockSection.get(blockX, blockY, blockZ);
            BlockType otherBlock = blockMap.getAsset(otherBlockId);
            int otherFluidId = otherFluidSection.getFluidId(blockX, blockY, blockZ);
            if (otherFluidId != 0) continue;
            boolean bl = isFlammable = this.getFlammabilityForBlock(otherBlock) != null;
            if (!isFlammable) continue;
            otherFluidSection.setFluid(blockX, blockY, blockZ, spreadFluidId, (byte)1);
            otherBlockSection.setTicking(blockX, blockY, blockZ, true);
        }
        FlammabilityConfig currentFlammability = this.getFlammabilityForBlock(currentBlock);
        if (currentFlammability == null) {
            fluidSection.setFluid(worldX, worldY, worldZ, 0, (byte)0);
            return BlockTickStrategy.SLEEP;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (fluidLevel >= currentFlammability.getBurnLevel() && random.nextFloat() < currentFlammability.getBurnChance()) {
            this.tryBurn(world, accessor, fluidSection, blockSection, currentFlammability, worldX, worldY, worldZ);
            return BlockTickStrategy.SLEEP;
        }
        return BlockTickStrategy.CONTINUE;
    }

    @Nullable
    private FlammabilityConfig getFlammabilityForBlock(@Nonnull BlockType block) {
        List<FlammabilityConfig> configs = this.getSortedFlammabilityConfigs();
        AssetExtraInfo.Data data = block.getData();
        if (data == null) {
            return null;
        }
        Int2ObjectMap<IntSet> blockTags = data.getTags();
        for (FlammabilityConfig config : configs) {
            TagPattern tagPattern = config.getTagPattern();
            if (tagPattern == null || !tagPattern.test(blockTags)) continue;
            return config;
        }
        return null;
    }

    @Override
    public boolean canOccupySolidBlocks() {
        return true;
    }

    private boolean tryBurn(@Nonnull World world, @Nonnull FluidTicker.Accessor accessor, @Nonnull FluidSection fluidSection, BlockSection blockSection, @Nonnull FlammabilityConfig config, int blockX, int blockY, int blockZ) {
        int soundEvent;
        int resultingBlockIndex = config.getResultingBlockIndex();
        if (resultingBlockIndex != Integer.MIN_VALUE) {
            int originalRotation = blockSection.getRotationIndex(blockX, blockY, blockZ);
            int originalFiller = blockSection.getFiller(blockX, blockY, blockZ);
            blockSection.set(blockX, blockY, blockZ, resultingBlockIndex, originalRotation, originalFiller);
            FireFluidTicker.setTickingSurrounding(accessor, blockSection, blockX, blockY, blockZ);
        }
        if ((soundEvent = config.getSoundEventIndex()) != Integer.MIN_VALUE) {
            world.execute(() -> SoundUtil.playSoundEvent3d(soundEvent, SoundCategory.SFX, (double)blockX, (double)blockY, (double)blockZ, world.getEntityStore().getStore()));
        }
        fluidSection.setFluid(blockX, blockY, blockZ, 0, (byte)0);
        return true;
    }

    @Override
    public boolean isSelfFluid(int selfFluidId, int otherFluidId) {
        return super.isSelfFluid(selfFluidId, otherFluidId) || otherFluidId == this.getSpreadFluidId(selfFluidId);
    }

    private int getSpreadFluidId(int fluidId) {
        if (this.spreadFluidId == 0) {
            this.spreadFluidId = this.spreadFluid != null ? Fluid.getAssetMap().getIndex(this.spreadFluid) : Integer.MIN_VALUE;
        }
        if (this.spreadFluidId == Integer.MIN_VALUE) {
            return fluidId;
        }
        return this.spreadFluidId;
    }

    @Nonnull
    public List<FlammabilityConfig> getSortedFlammabilityConfigs() {
        if (this.sortedFlammabilityConfigs == null) {
            ArrayList<FlammabilityConfig> configs = new ArrayList<FlammabilityConfig>();
            if (this.rawFlammabilityConfigs != null) {
                Collections.addAll(configs, this.rawFlammabilityConfigs);
            }
            configs.sort(Comparator.comparingInt(FlammabilityConfig::getPriority).reversed());
            this.sortedFlammabilityConfigs = configs;
        }
        return this.sortedFlammabilityConfigs;
    }

    public static class FlammabilityConfig {
        public static final BuilderCodec<FlammabilityConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FlammabilityConfig.class, FlammabilityConfig::new).appendInherited(new KeyedCodec<String>("TagPattern", TagPattern.CHILD_ASSET_CODEC), (o, v) -> {
            o.tagPatternId = v;
        }, o -> o.tagPatternId, (o, p) -> {
            o.tagPatternId = p.tagPatternId;
        }).addValidator(TagPattern.VALIDATOR_CACHE.getValidator()).documentation("TagPattern to match blocks that this config applies to").add()).appendInherited(new KeyedCodec<Integer>("Priority", Codec.INTEGER), (o, v) -> {
            o.priority = v;
        }, o -> o.priority, (o, p) -> {
            o.priority = p.priority;
        }).documentation("Priority for pattern matching - higher values are checked first").add()).appendInherited(new KeyedCodec<Byte>("BurnLevel", Codec.BYTE), (o, v) -> {
            o.burnLevel = v;
        }, o -> o.burnLevel, (o, p) -> {
            o.burnLevel = p.burnLevel;
        }).documentation("The fluid level the fluid has to be greater than or equal to to burn this block").add()).appendInherited(new KeyedCodec<Float>("BurnChance", Codec.FLOAT), (o, v) -> {
            o.burnChance = v.floatValue();
        }, o -> Float.valueOf(o.burnChance), (o, p) -> {
            o.burnChance = p.burnChance;
        }).documentation("Probability (0.0 to 1.0) that the block will burn each tick when above the burn level").add()).appendInherited(new KeyedCodec<String>("ResultingBlock", Codec.STRING), (o, v) -> {
            o.resultingBlock = v;
        }, o -> o.resultingBlock, (o, p) -> {
            o.resultingBlock = p.resultingBlock;
        }).documentation("The block to place after burning, if any").add()).appendInherited(new KeyedCodec<String>("SoundEvent", Codec.STRING), (o, v) -> {
            o.soundEvent = v;
        }, o -> o.soundEvent, (o, p) -> {
            o.soundEvent = p.soundEvent;
        }).addValidator(SoundEvent.VALIDATOR_CACHE.getValidator()).add()).build();
        private String tagPatternId;
        @Nullable
        private transient TagPattern tagPattern = null;
        private int priority;
        private byte burnLevel = 1;
        private float burnChance = 0.1f;
        private String resultingBlock = "Empty";
        private int resultingBlockIndex = Integer.MIN_VALUE;
        private String soundEvent;
        private int soundEventIndex = Integer.MIN_VALUE;

        @Nullable
        public TagPattern getTagPattern() {
            if (this.tagPattern == null && this.tagPatternId != null) {
                this.tagPattern = (TagPattern)TagPattern.getAssetMap().getAsset(this.tagPatternId);
            }
            return this.tagPattern;
        }

        public int getPriority() {
            return this.priority;
        }

        public byte getBurnLevel() {
            return this.burnLevel;
        }

        public float getBurnChance() {
            return this.burnChance;
        }

        public int getResultingBlockIndex() {
            if (this.resultingBlockIndex == Integer.MIN_VALUE && this.resultingBlock != null) {
                this.resultingBlockIndex = BlockType.getBlockIdOrUnknown(this.resultingBlock, "Unknown block type %s", this.resultingBlock);
            }
            return this.resultingBlockIndex;
        }

        public int getSoundEventIndex() {
            if (this.soundEventIndex == Integer.MIN_VALUE && this.soundEvent != null) {
                this.soundEventIndex = SoundEvent.getAssetMap().getIndex(this.soundEvent);
            }
            return this.soundEventIndex;
        }
    }
}

