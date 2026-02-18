/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.farming.config.modifiers;

import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
import com.hypixel.hytale.builtin.weather.resources.WeatherResource;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.GrowthModifierAsset;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.time.Instant;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WaterGrowthModifierAsset
extends GrowthModifierAsset {
    @Nonnull
    public static final BuilderCodec<WaterGrowthModifierAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WaterGrowthModifierAsset.class, WaterGrowthModifierAsset::new, ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Fluids", new ArrayCodec<String>(Codec.STRING, String[]::new)), (asset, blocks) -> {
        asset.fluids = blocks;
    }, asset -> asset.fluids).addValidator(Fluid.VALIDATOR_CACHE.getArrayValidator().late()).add()).append(new KeyedCodec<T[]>("Weathers", Codec.STRING_ARRAY), (asset, weathers) -> {
        asset.weathers = weathers;
    }, asset -> asset.weathers).addValidator(Weather.VALIDATOR_CACHE.getArrayValidator()).add()).addField(new KeyedCodec<Integer>("RainDuration", Codec.INTEGER), (asset, duration) -> {
        asset.rainDuration = duration;
    }, asset -> asset.rainDuration)).afterDecode(asset -> {
        int i;
        if (asset.fluids != null) {
            asset.fluidIds = new IntOpenHashSet();
            for (i = 0; i < asset.fluids.length; ++i) {
                asset.fluidIds.add(Fluid.getAssetMap().getIndex(asset.fluids[i]));
            }
        }
        if (asset.weathers != null) {
            asset.weatherIds = new IntOpenHashSet();
            for (i = 0; i < asset.weathers.length; ++i) {
                asset.weatherIds.add(Weather.getAssetMap().getIndex(asset.weathers[i]));
            }
        }
    })).build();
    protected String[] fluids;
    protected IntOpenHashSet fluidIds;
    protected String[] weathers;
    protected IntOpenHashSet weatherIds;
    protected int rainDuration;

    public String[] getFluids() {
        return this.fluids;
    }

    public IntOpenHashSet getFluidIds() {
        return this.fluidIds;
    }

    public String[] getWeathers() {
        return this.weathers;
    }

    public IntOpenHashSet getWeatherIds() {
        return this.weatherIds;
    }

    public int getRainDuration() {
        return this.rainDuration;
    }

    @Override
    public double getCurrentGrowthMultiplier(@Nonnull CommandBuffer<ChunkStore> commandBuffer, @Nonnull Ref<ChunkStore> sectionRef, @Nonnull Ref<ChunkStore> blockRef, int x, int y, int z, boolean initialTick) {
        boolean hasWaterBlock = this.checkIfWaterSource(commandBuffer, sectionRef, x, y, z);
        boolean isRaining = this.checkIfRaining(commandBuffer, sectionRef, x, y, z);
        boolean active = hasWaterBlock || isRaining;
        TilledSoilBlock soil = WaterGrowthModifierAsset.getSoil(commandBuffer, sectionRef, x, y, z);
        if (soil != null) {
            if (soil.hasExternalWater() != active) {
                soil.setExternalWater(active);
                BlockSection blockSectionComponent = commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
                if (blockSectionComponent != null) {
                    blockSectionComponent.setTicking(x, y, z, true);
                }
            }
            active |= WaterGrowthModifierAsset.isSoilWaterExpiring(commandBuffer.getExternalData().getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()), soil);
        }
        if (!active) {
            return 1.0;
        }
        return super.getCurrentGrowthMultiplier(commandBuffer, sectionRef, blockRef, x, y, z, initialTick);
    }

    @Nullable
    private static TilledSoilBlock getSoil(@Nonnull CommandBuffer<ChunkStore> commandBuffer, @Nonnull Ref<ChunkStore> sectionRef, int x, int y, int z) {
        ChunkSection chunkSectionComponent = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
        if (chunkSectionComponent == null) {
            return null;
        }
        Ref<ChunkStore> chunkRef = chunkSectionComponent.getChunkColumnReference();
        if (chunkRef == null || !chunkRef.isValid()) {
            return null;
        }
        BlockComponentChunk blockComponentChunk = commandBuffer.getComponent(chunkRef, BlockComponentChunk.getComponentType());
        if (blockComponentChunk == null) {
            return null;
        }
        int blockBelowIndex = ChunkUtil.indexBlockInColumn(x, y - 1, z);
        Ref<ChunkStore> blockBelowRef = blockComponentChunk.getEntityReference(blockBelowIndex);
        if (blockBelowRef == null) {
            return null;
        }
        return commandBuffer.getComponent(blockBelowRef, TilledSoilBlock.getComponentType());
    }

    protected boolean checkIfWaterSource(@Nonnull CommandBuffer<ChunkStore> commandBuffer, @Nonnull Ref<ChunkStore> sectionRef, int x, int y, int z) {
        IntOpenHashSet waterBlocks = this.fluidIds;
        if (waterBlocks == null) {
            return false;
        }
        TilledSoilBlock soil = WaterGrowthModifierAsset.getSoil(commandBuffer, sectionRef, x, y, z);
        if (soil == null) {
            return false;
        }
        int[] fluids = WaterGrowthModifierAsset.getNeighbourFluids(commandBuffer, sectionRef, x, y - 1, z);
        if (fluids == null) {
            return false;
        }
        for (int block : fluids) {
            if (!waterBlocks.contains(block)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    private static int[] getNeighbourFluids(@Nonnull CommandBuffer<ChunkStore> commandBuffer, @Nonnull Ref<ChunkStore> sectionRef, int x, int y, int z) {
        ChunkSection chunkSectionComponent = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
        if (chunkSectionComponent == null) {
            return null;
        }
        return new int[]{WaterGrowthModifierAsset.getFluidAtPos(x - 1, y, z, sectionRef, chunkSectionComponent, commandBuffer), WaterGrowthModifierAsset.getFluidAtPos(x + 1, y, z, sectionRef, chunkSectionComponent, commandBuffer), WaterGrowthModifierAsset.getFluidAtPos(x, y, z - 1, sectionRef, chunkSectionComponent, commandBuffer), WaterGrowthModifierAsset.getFluidAtPos(x, y, z + 1, sectionRef, chunkSectionComponent, commandBuffer)};
    }

    private static int getFluidAtPos(int posX, int posY, int posZ, @Nonnull Ref<ChunkStore> sectionRef, @Nonnull ChunkSection currentChunkSection, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        int chunkZ;
        int chunkY;
        Ref<ChunkStore> chunkToUse = sectionRef;
        int chunkX = ChunkUtil.worldCoordFromLocalCoord(currentChunkSection.getX(), posX);
        if (ChunkUtil.isSameChunkSection(chunkX, chunkY = ChunkUtil.worldCoordFromLocalCoord(currentChunkSection.getY(), posY), chunkZ = ChunkUtil.worldCoordFromLocalCoord(currentChunkSection.getZ(), posZ), currentChunkSection.getX(), currentChunkSection.getY(), currentChunkSection.getZ())) {
            chunkToUse = commandBuffer.getExternalData().getChunkSectionReference(chunkX, chunkY, chunkZ);
        }
        if (chunkToUse == null) {
            return Integer.MIN_VALUE;
        }
        FluidSection fluidSectionComponent = commandBuffer.getComponent(chunkToUse, FluidSection.getComponentType());
        if (fluidSectionComponent == null) {
            return Integer.MIN_VALUE;
        }
        return fluidSectionComponent.getFluidId(posX, posY, posZ);
    }

    protected boolean checkIfRaining(@Nonnull CommandBuffer<ChunkStore> commandBuffer, @Nonnull Ref<ChunkStore> sectionRef, int x, int y, int z) {
        if (this.weatherIds == null) {
            return false;
        }
        ChunkSection chunkSectionComponent = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
        if (chunkSectionComponent == null) {
            return false;
        }
        Ref<ChunkStore> chunkRef = chunkSectionComponent.getChunkColumnReference();
        BlockChunk blockChunkComponent = commandBuffer.getComponent(chunkRef, BlockChunk.getComponentType());
        if (blockChunkComponent == null) {
            return false;
        }
        int blockId = blockChunkComponent.getBlock(x, y, z);
        Store<EntityStore> entityStore = commandBuffer.getExternalData().getWorld().getEntityStore().getStore();
        WeatherResource weatherResource = entityStore.getResource(WeatherResource.getResourceType());
        int environment = blockChunkComponent.getEnvironment(x, y, z);
        int weatherId = weatherResource.getForcedWeatherIndex() != 0 ? weatherResource.getForcedWeatherIndex() : weatherResource.getWeatherIndexForEnvironment(environment);
        if (this.weatherIds.contains(weatherId)) {
            boolean unobstructed = true;
            for (int searchY = y + 1; searchY < 320; ++searchY) {
                int block = blockChunkComponent.getBlock(x, searchY, z);
                if (block == 0 || block == blockId) continue;
                unobstructed = false;
                break;
            }
            return unobstructed;
        }
        return false;
    }

    private static boolean isSoilWaterExpiring(@Nonnull WorldTimeResource worldTimeResource, @Nonnull TilledSoilBlock soilBlock) {
        Instant until = soilBlock.getWateredUntil();
        if (until == null) {
            return false;
        }
        Instant now = worldTimeResource.getGameTime();
        if (now.isAfter(until)) {
            soilBlock.setWateredUntil(null);
            return false;
        }
        return true;
    }

    @Override
    @Nonnull
    public String toString() {
        return "WaterGrowthModifierAsset{blocks=" + Arrays.toString(this.fluids) + ", blockIds=" + String.valueOf(this.fluidIds) + ", weathers=" + Arrays.toString(this.weathers) + ", weatherIds=" + String.valueOf(this.weatherIds) + ", rainDuration=" + this.rainDuration + "} " + super.toString();
    }
}

