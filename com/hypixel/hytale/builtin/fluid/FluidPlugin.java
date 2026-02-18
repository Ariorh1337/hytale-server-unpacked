/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.fluid;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.builtin.fluid.FluidCommand;
import com.hypixel.hytale.builtin.fluid.FluidSystems;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.lookup.Priority;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.DefaultFluidTicker;
import com.hypixel.hytale.server.core.asset.type.fluid.FiniteFluidTicker;
import com.hypixel.hytale.server.core.asset.type.fluid.FireFluidTicker;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.fluid.FluidTicker;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.events.ChunkPreLoadProcessEvent;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.time.Instant;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidPlugin
extends JavaPlugin {
    @Nonnull
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static FluidPlugin instance;

    public static FluidPlugin get() {
        return instance;
    }

    public FluidPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        ComponentRegistryProxy<ChunkStore> chunkStoreRegistry = this.getChunkStoreRegistry();
        FluidTicker.CODEC.register(Priority.DEFAULT, "Default", (Class)DefaultFluidTicker.class, (Codec)DefaultFluidTicker.CODEC);
        FluidTicker.CODEC.register("Fire", (Class<FluidTicker>)FireFluidTicker.class, (Codec<FluidTicker>)FireFluidTicker.CODEC);
        FluidTicker.CODEC.register("Finite", (Class<FluidTicker>)FiniteFluidTicker.class, (Codec<FluidTicker>)FiniteFluidTicker.CODEC);
        ComponentType<ChunkStore, ChunkSection> chunkSectionComponentType = ChunkSection.getComponentType();
        ComponentType<ChunkStore, FluidSection> fluidSectionComponentType = FluidSection.getComponentType();
        ComponentType<ChunkStore, ChunkColumn> chunkColumnComponentType = ChunkColumn.getComponentType();
        ComponentType<ChunkStore, BlockChunk> blockChunkComponentType = BlockChunk.getComponentType();
        ComponentType<ChunkStore, WorldChunk> worldChunkComponentType = WorldChunk.getComponentType();
        chunkStoreRegistry.registerSystem(new FluidSystems.EnsureFluidSection(chunkSectionComponentType, fluidSectionComponentType));
        chunkStoreRegistry.registerSystem(new FluidSystems.MigrateFromColumn(chunkColumnComponentType, blockChunkComponentType, fluidSectionComponentType));
        chunkStoreRegistry.registerSystem(new FluidSystems.SetupSection(chunkSectionComponentType, fluidSectionComponentType));
        chunkStoreRegistry.registerSystem(new FluidSystems.LoadPacketGenerator(chunkColumnComponentType, fluidSectionComponentType));
        chunkStoreRegistry.registerSystem(new FluidSystems.ReplicateChanges(chunkSectionComponentType, fluidSectionComponentType, worldChunkComponentType));
        chunkStoreRegistry.registerSystem(new FluidSystems.Ticking(chunkSectionComponentType, fluidSectionComponentType, blockChunkComponentType));
        this.getEventRegistry().registerGlobal(EventPriority.FIRST, ChunkPreLoadProcessEvent.class, FluidPlugin::onChunkPreProcess);
        this.getCommandRegistry().registerCommand(new FluidCommand());
    }

    private static void onChunkPreProcess(@Nonnull ChunkPreLoadProcessEvent event) {
        if (!event.isNewlyGenerated()) {
            return;
        }
        WorldChunk worldChunk = event.getChunk();
        Holder<ChunkStore> holder = event.getHolder();
        ChunkColumn chunkColumnComponent = holder.getComponent(ChunkColumn.getComponentType());
        if (chunkColumnComponent == null) {
            return;
        }
        BlockChunk blockChunkComponent = holder.getComponent(BlockChunk.getComponentType());
        if (blockChunkComponent == null) {
            return;
        }
        IndexedLookupTableAssetMap<String, Fluid> fluidMap = Fluid.getAssetMap();
        BlockTypeAssetMap<String, BlockType> blockMap = BlockType.getAssetMap();
        Holder<ChunkStore>[] sections = chunkColumnComponent.getSectionHolders();
        if (sections == null) {
            return;
        }
        for (int i = 0; i < sections.length && i < 10; ++i) {
            Holder<ChunkStore> section = sections[i];
            FluidSection fluidSectionComponent = section.getComponent(FluidSection.getComponentType());
            if (fluidSectionComponent == null || fluidSectionComponent.isEmpty()) continue;
            BlockSection blockSectionComponent = section.ensureAndGetComponent(BlockSection.getComponentType());
            for (int idx = 0; idx < 32768; ++idx) {
                int fluidId = fluidSectionComponent.getFluidId(idx);
                if (fluidId == 0) continue;
                Fluid fluidType = fluidMap.getAsset(fluidId);
                if (fluidType == null) {
                    LOGGER.at(Level.WARNING).log("Invalid fluid found in chunk section: %d, %d %d with id %d", fluidSectionComponent.getX(), fluidSectionComponent.getY(), fluidSectionComponent.getZ(), fluidSectionComponent);
                    fluidSectionComponent.setFluid(idx, 0, (byte)0);
                    continue;
                }
                FluidTicker ticker = fluidType.getTicker();
                if (FluidTicker.isSolid(blockMap.getAsset(blockSectionComponent.get(idx)))) {
                    fluidSectionComponent.setFluid(idx, 0, (byte)0);
                    continue;
                }
                if (!ticker.canDemote()) {
                    boolean canSpread;
                    int x2 = ChunkUtil.minBlock(fluidSectionComponent.getX()) + ChunkUtil.xFromIndex(idx);
                    int y2 = ChunkUtil.minBlock(fluidSectionComponent.getY()) + ChunkUtil.yFromIndex(idx);
                    int z2 = ChunkUtil.minBlock(fluidSectionComponent.getZ()) + ChunkUtil.zFromIndex(idx);
                    boolean bl = canSpread = ChunkUtil.isBorderBlock(x2, z2) || fluidSectionComponent.getFluidId(x2 - 1, y2, z2) != fluidId && !FluidTicker.isSolid(blockMap.getAsset(blockSectionComponent.get(x2 - 1, y2, z2))) || fluidSectionComponent.getFluidId(x2 + 1, y2, z2) != fluidId && !FluidTicker.isSolid(blockMap.getAsset(blockSectionComponent.get(x2 + 1, y2, z2))) || fluidSectionComponent.getFluidId(x2, y2, z2 - 1) != fluidId && !FluidTicker.isSolid(blockMap.getAsset(blockSectionComponent.get(x2, y2, z2 - 1))) || fluidSectionComponent.getFluidId(x2, y2, z2 + 1) != fluidId && !FluidTicker.isSolid(blockMap.getAsset(blockSectionComponent.get(x2, y2, z2 + 1)));
                    if (y2 > 0) {
                        FluidSection fluidSection2;
                        canSpread = ChunkUtil.chunkCoordinate(y2) == ChunkUtil.chunkCoordinate(y2 - 1) ? (canSpread |= fluidSectionComponent.getFluidId(x2, y2 - 1, z2) != fluidId && !FluidTicker.isSolid(blockMap.getAsset(blockSectionComponent.get(x2, y2 - 1, z2)))) : (canSpread |= (fluidSection2 = sections[i - 1].getComponent(FluidSection.getComponentType())).getFluidId(x2, y2 - 1, z2) != fluidId && !FluidTicker.isSolid(blockMap.getAsset(blockChunkComponent.getBlock(x2, y2 - 1, z2))));
                    }
                    if (!canSpread) {
                        blockSectionComponent.setTicking(idx, false);
                        continue;
                    }
                }
                blockSectionComponent.setTicking(idx, true);
            }
        }
        int tickingBlocks = blockChunkComponent.getTickingBlocksCount();
        if (tickingBlocks == 0) {
            return;
        }
        PreprocesorAccessor accessor = new PreprocesorAccessor(worldChunk, blockChunkComponent, sections);
        do {
            blockChunkComponent.preTick(Instant.MIN);
            for (int i = 0; i < sections.length; ++i) {
                Holder<ChunkStore> section = sections[i];
                FluidSection fluidSectionComponent = section.getComponent(FluidSection.getComponentType());
                if (fluidSectionComponent == null || fluidSectionComponent.isEmpty()) continue;
                BlockSection blockSectionComponent = section.ensureAndGetComponent(BlockSection.getComponentType());
                fluidSectionComponent.preload(worldChunk.getX(), i, worldChunk.getZ());
                accessor.blockSection = blockSectionComponent;
                blockSectionComponent.forEachTicking(accessor, fluidSectionComponent, i, (preprocesorAccessor, fluidSection1, x, y, z, block) -> {
                    int fluidId = fluidSection1.getFluidId(x, y, z);
                    if (fluidId == 0) {
                        return BlockTickStrategy.IGNORED;
                    }
                    int blockX = fluidSection1.getX() << 5 | x;
                    int blockZ = fluidSection1.getZ() << 5 | z;
                    Fluid fluid = Fluid.getAssetMap().getAsset(fluidId);
                    return fluid.getTicker().process(preprocesorAccessor.worldChunk.getWorld(), preprocesorAccessor.tick, (FluidTicker.Accessor)preprocesorAccessor, (FluidSection)fluidSection1, accessor.blockSection, fluid, fluidId, blockX, y, blockZ);
                });
            }
            tickingBlocks = blockChunkComponent.getTickingBlocksCount();
            ++accessor.tick;
        } while (tickingBlocks != 0 && accessor.tick <= 100L);
        blockChunkComponent.mergeTickingBlocks();
    }

    public static class PreprocesorAccessor
    implements FluidTicker.Accessor {
        @Nonnull
        private final WorldChunk worldChunk;
        @Nonnull
        private final BlockChunk blockChunk;
        @Nonnull
        private final Holder<ChunkStore>[] sections;
        public long tick;
        public BlockSection blockSection;

        public PreprocesorAccessor(@Nonnull WorldChunk worldChunk, @Nonnull BlockChunk blockChunk, @Nonnull Holder<ChunkStore>[] sections) {
            this.worldChunk = worldChunk;
            this.blockChunk = blockChunk;
            this.sections = sections;
        }

        @Override
        @Nullable
        public FluidSection getFluidSection(int cx, int cy, int cz) {
            if (this.blockChunk.getX() == cx && this.blockChunk.getZ() == cz && cy >= 0 && cy < this.sections.length) {
                return this.sections[cy].getComponent(FluidSection.getComponentType());
            }
            return null;
        }

        @Override
        @Nullable
        public BlockSection getBlockSection(int cx, int cy, int cz) {
            if (cy < 0 || cy >= 10) {
                return null;
            }
            if (this.blockChunk.getX() == cx && this.blockChunk.getZ() == cz) {
                return this.blockChunk.getSectionAtIndex(cy);
            }
            return null;
        }

        @Override
        public void setBlock(int x, int y, int z, int blockId) {
            if (this.worldChunk.getX() != ChunkUtil.chunkCoordinate(x) && this.worldChunk.getZ() != ChunkUtil.chunkCoordinate(z)) {
                return;
            }
            this.worldChunk.setBlock(x, y, z, blockId, 157);
        }
    }
}

