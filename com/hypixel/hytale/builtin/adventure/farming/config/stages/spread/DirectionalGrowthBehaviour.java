/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.farming.config.stages.spread;

import com.hypixel.hytale.builtin.adventure.farming.config.stages.spread.SpreadGrowthBehaviour;
import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
import com.hypixel.hytale.builtin.blockphysics.BlockPhysicsSystems;
import com.hypixel.hytale.builtin.blockphysics.BlockPhysicsUtil;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.map.IWeightedElement;
import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.range.IntRange;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
import com.hypixel.hytale.server.core.codec.WeightedMapCodec;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.accessor.LocalCachedChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;

public class DirectionalGrowthBehaviour
extends SpreadGrowthBehaviour {
    public static final BuilderCodec<DirectionalGrowthBehaviour> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DirectionalGrowthBehaviour.class, DirectionalGrowthBehaviour::new, BASE_CODEC).append(new KeyedCodec("GrowthBlockTypes", new WeightedMapCodec(BlockTypeWeight.CODEC, (IWeightedElement[])new BlockTypeWeight[0])), (directionalGrowthBehaviour, blockTypeWeightIWeightedMap) -> {
        directionalGrowthBehaviour.blockTypes = blockTypeWeightIWeightedMap;
    }, directionalGrowthBehaviour -> directionalGrowthBehaviour.blockTypes).documentation("Defines a map of the possible BlockType to spread.").addValidator(Validators.nonNull()).add()).append(new KeyedCodec<IntRange>("Horizontal", IntRange.CODEC), (directionalGrowthBehaviour, intRange) -> {
        directionalGrowthBehaviour.horizontalRange = intRange;
    }, directionalGrowthBehaviour -> directionalGrowthBehaviour.horizontalRange).documentation("Defines if the spread can happen horizontally. The range must be set with positive integers.").add()).append(new KeyedCodec<IntRange>("Vertical", IntRange.CODEC), (directionalGrowthBehaviour, intRange) -> {
        directionalGrowthBehaviour.verticalRange = intRange;
    }, directionalGrowthBehaviour -> directionalGrowthBehaviour.verticalRange).documentation("Defines if the spread can happen vertically. The range must be set with positive integers.").add()).append(new KeyedCodec<VerticalDirection>("VerticalDirection", new EnumCodec<VerticalDirection>(VerticalDirection.class)), (directionalGrowthBehaviour, verticalDirection) -> {
        directionalGrowthBehaviour.verticalDirection = verticalDirection;
    }, directionalGrowthBehaviour -> directionalGrowthBehaviour.verticalDirection).documentation("Defines in which direction the vertical spread should happen. Possible values are: 'Upwards' and 'Downwards', default value: 'Upwards'.").addValidator(Validators.nonNull()).add()).build();
    private static final int PLACE_BLOCK_TRIES = 100;
    protected IWeightedMap<BlockTypeWeight> blockTypes;
    protected IntRange horizontalRange;
    protected IntRange verticalRange;
    protected VerticalDirection verticalDirection = VerticalDirection.BOTH;

    public IWeightedMap<BlockTypeWeight> getBlockTypes() {
        return this.blockTypes;
    }

    public IntRange getHorizontalRange() {
        return this.horizontalRange;
    }

    public IntRange getVerticalRange() {
        return this.verticalRange;
    }

    public VerticalDirection getVerticalDirection() {
        return this.verticalDirection;
    }

    @Override
    public void execute(ComponentAccessor<ChunkStore> commandBuffer, Ref<ChunkStore> sectionRef, Ref<ChunkStore> blockRef, int worldX, int worldY, int worldZ, float newSpreadRate) {
        int x = 0;
        int z = 0;
        FastRandom random = new FastRandom();
        String blockTypeKey = this.blockTypes.get(random).getBlockTypeKey();
        World world = commandBuffer.getExternalData().getWorld();
        LocalCachedChunkAccessor chunkAccessor = LocalCachedChunkAccessor.atWorldCoords(world, worldX, worldZ, 1);
        for (int i = 0; i < 100; ++i) {
            int chunkZ;
            if (this.horizontalRange != null) {
                double angle = (float)Math.PI * 2 * random.nextFloat();
                int radius = this.horizontalRange.getInt(random.nextFloat());
                x = MathUtil.fastRound((float)radius * TrigMathUtil.cos(angle));
                z = MathUtil.fastRound((float)radius * TrigMathUtil.sin(angle));
            }
            int targetX = worldX + x;
            int targetY = worldY;
            int targetZ = worldZ + z;
            int chunkX = ChunkUtil.chunkCoordinate(targetX);
            WorldChunk chunk = chunkAccessor.getChunkIfInMemory(ChunkUtil.indexChunk(chunkX, chunkZ = ChunkUtil.chunkCoordinate(targetZ)));
            if (chunk == null) continue;
            if (this.verticalRange != null) {
                int directionValue = switch (this.verticalDirection.ordinal()) {
                    default -> throw new MatchException(null, null);
                    case 0, 2 -> this.verticalDirection.getValue();
                    case 1 -> random.nextBoolean() ? 1 : -1;
                };
                targetY += this.verticalRange.getInt(random.nextFloat()) * directionValue;
            } else {
                targetY = chunk.getHeight(targetX, targetZ) + 1;
            }
            if (!this.tryPlaceBlock(world, chunk, targetX, targetY, targetZ, blockTypeKey, 0)) continue;
            int finalTargetY = targetY;
            world.execute(() -> {
                WorldChunk loadedChunk = chunkAccessor.getChunk(ChunkUtil.indexChunk(chunkX, chunkZ));
                if (loadedChunk == null) {
                    return;
                }
                loadedChunk.placeBlock(targetX, finalTargetY, targetZ, blockTypeKey, Rotation.None, Rotation.None, Rotation.None);
                DirectionalGrowthBehaviour.decaySpread(commandBuffer, loadedChunk.getBlockComponentChunk(), targetX, finalTargetY, targetZ, newSpreadRate);
            });
            return;
        }
    }

    private static void decaySpread(ComponentAccessor<ChunkStore> commandBuffer, BlockComponentChunk blockComponentChunk, int worldX, int worldY, int worldZ, float newSpreadRate) {
        Ref<ChunkStore> blockRefPlaced = blockComponentChunk.getEntityReference(ChunkUtil.indexBlockInColumn(worldX, worldY, worldZ));
        if (blockRefPlaced == null) {
            return;
        }
        FarmingBlock farmingPlaced = commandBuffer.getComponent(blockRefPlaced, FarmingBlock.getComponentType());
        if (farmingPlaced == null) {
            return;
        }
        farmingPlaced.setSpreadRate(newSpreadRate);
    }

    private boolean tryPlaceBlock(@Nonnull World world, @Nonnull WorldChunk chunk, int worldX, int worldY, int worldZ, String blockTypeKey, int rotation) {
        if (chunk.getBlock(worldX, worldY, worldZ) != 0) {
            return false;
        }
        if (!this.validatePosition(world, worldX, worldY, worldZ)) {
            return false;
        }
        BlockType blockType = (BlockType)BlockType.getAssetMap().getAsset(blockTypeKey);
        if (blockType == null) {
            return false;
        }
        if (!chunk.testPlaceBlock(worldX, worldY, worldZ, blockType, rotation)) {
            return false;
        }
        int cx = chunk.getX();
        int cz = chunk.getZ();
        int cy = ChunkUtil.indexSection(worldY);
        Ref<ChunkStore> sectionRef = world.getChunkStore().getChunkSectionReference(cx, cy, cz);
        if (sectionRef == null) {
            return false;
        }
        Store<ChunkStore> store = world.getChunkStore().getStore();
        BlockPhysics blockPhysics = store.getComponent(sectionRef, BlockPhysics.getComponentType());
        FluidSection fluidSection = store.getComponent(sectionRef, FluidSection.getComponentType());
        BlockSection blockSection = store.getComponent(sectionRef, BlockSection.getComponentType());
        int filler = blockSection.getFiller(worldX, worldY, worldZ);
        BlockPhysicsSystems.CachedAccessor cachedAccessor = BlockPhysicsSystems.CachedAccessor.of(store, blockSection, blockPhysics, fluidSection, cx, cy, cz, 14);
        return BlockPhysicsUtil.testBlockPhysics(cachedAccessor, blockSection, blockPhysics, fluidSection, worldX, worldY, worldZ, blockType, rotation, filler) != 0;
    }

    @Nonnull
    public String toString() {
        return "DirectionalGrowthBehaviour{blockTypes=" + String.valueOf(this.blockTypes) + ", horizontalRange=" + String.valueOf(this.horizontalRange) + ", verticalRange=" + String.valueOf(this.verticalRange) + ", verticalDirection=" + String.valueOf((Object)this.verticalDirection) + "} " + super.toString();
    }

    private static enum VerticalDirection {
        DOWNWARDS(-1),
        BOTH(0),
        UPWARDS(1);

        private final int value;

        private VerticalDirection(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static class BlockTypeWeight
    implements IWeightedElement {
        @Nonnull
        public static BuilderCodec<BlockTypeWeight> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockTypeWeight.class, BlockTypeWeight::new).append(new KeyedCodec<Double>("Weight", Codec.DOUBLE), (blockTypeWeight, integer) -> {
            blockTypeWeight.weight = integer;
        }, blockTypeWeight -> blockTypeWeight.weight).documentation("Defines the probability to have this entry.").addValidator(Validators.greaterThan(0.0)).add()).append(new KeyedCodec<String>("BlockType", Codec.STRING), (blockTypeWeight, blockTypeKey) -> {
            blockTypeWeight.blockTypeKey = blockTypeKey;
        }, blockTypeWeight -> blockTypeWeight.blockTypeKey).documentation("Defines the BlockType that'll be spread").addValidator(Validators.nonNull()).add()).build();
        protected double weight = 1.0;
        protected String blockTypeKey;

        @Override
        public double getWeight() {
            return this.weight;
        }

        public String getBlockTypeKey() {
            return this.blockTypeKey;
        }

        @Nonnull
        public String toString() {
            return "BlockTypeWeight{weight=" + this.weight + ", blockTypeKey=" + this.blockTypeKey + "}";
        }
    }
}

