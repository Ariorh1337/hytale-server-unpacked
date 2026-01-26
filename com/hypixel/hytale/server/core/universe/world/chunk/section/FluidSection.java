/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.chunk.section;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.CachedPacket;
import com.hypixel.hytale.protocol.packets.world.SetFluids;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.modules.LegacyModule;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.EmptySectionPalette;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.ISectionPalette;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.PaletteTypeEnum;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.io.ByteBufUtil;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.lang.ref.SoftReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.StampedLock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidSection
implements Component<ChunkStore> {
    public static final int LEVEL_DATA_SIZE = 16384;
    public static final int VERSION = 0;
    public static final BuilderCodec<FluidSection> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FluidSection.class, FluidSection::new).versioned()).codecVersion(0)).append(new KeyedCodec<byte[]>("Data", Codec.BYTE_ARRAY), FluidSection::deserialize, FluidSection::serialize).add()).build();
    private final StampedLock lock = new StampedLock();
    private int x;
    private int y;
    private int z;
    private boolean loaded = false;
    private ISectionPalette typePalette = EmptySectionPalette.INSTANCE;
    @Nullable
    private byte[] levelData = null;
    private int nonZeroLevels = 0;
    @Nonnull
    private IntOpenHashSet changedPositions = new IntOpenHashSet(0);
    @Nonnull
    private IntOpenHashSet swapChangedPositions = new IntOpenHashSet(0);
    @Nullable
    private transient SoftReference<CompletableFuture<CachedPacket<SetFluids>>> cachedPacket = null;

    public static ComponentType<ChunkStore, FluidSection> getComponentType() {
        return LegacyModule.get().getFluidSectionComponentType();
    }

    public void preload(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void load(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.loaded = true;
    }

    private boolean setFluidRaw(int x, int y, int z, int fluidId) {
        return this.setFluidRaw(ChunkUtil.indexBlock(x, y, z), fluidId);
    }

    private boolean setFluidRaw(int index, int fluidId) {
        ISectionPalette.SetResult result = this.typePalette.set(index, fluidId);
        if (result == ISectionPalette.SetResult.REQUIRES_PROMOTE) {
            this.typePalette = this.typePalette.promote();
            result = this.typePalette.set(index, fluidId);
            if (result != ISectionPalette.SetResult.ADDED_OR_REMOVED) {
                throw new IllegalStateException("Promoted fluid section failed to correctly add the new fluid");
            }
        } else if (this.typePalette.shouldDemote()) {
            this.typePalette = this.typePalette.demote();
        }
        return result != ISectionPalette.SetResult.UNCHANGED;
    }

    public boolean setFluid(int x, int y, int z, @Nonnull Fluid fluid, byte level) {
        return this.setFluid(ChunkUtil.indexBlock(x, y, z), Fluid.getAssetMap().getIndex(fluid.getId()), level);
    }

    public boolean setFluid(int x, int y, int z, int fluidId, byte level) {
        return this.setFluid(ChunkUtil.indexBlock(x, y, z), fluidId, level);
    }

    public boolean setFluid(int index, @Nonnull Fluid fluid, byte level) {
        return this.setFluid(index, Fluid.getAssetMap().getIndex(fluid.getId()), level);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setFluid(int index, int fluidId, byte level) {
        if ((level = (byte)(level & 0xF)) == 0) {
            fluidId = 0;
        }
        if (fluidId == 0) {
            level = 0;
        }
        long stamp = this.lock.writeLock();
        try {
            boolean changed = this.setFluidRaw(index, fluidId);
            if ((changed |= this.setFluidLevel(index, level)) && this.loaded) {
                this.cachedPacket = null;
                this.changedPositions.add(index);
            }
            boolean bl = changed;
            return bl;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    private boolean setFluidRaw(int x, int y, int z, @Nonnull Fluid fluid) {
        return this.setFluidRaw(ChunkUtil.indexBlock(x, y, z), fluid);
    }

    private boolean setFluidRaw(int index, @Nonnull Fluid fluid) {
        IndexedLookupTableAssetMap<String, Fluid> assetMap = Fluid.getAssetMap();
        return this.setFluidRaw(index, assetMap.getIndex(fluid.getId()));
    }

    public int getFluidId(int x, int y, int z) {
        return this.getFluidId(ChunkUtil.indexBlock(x, y, z));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getFluidId(int index) {
        long stamp = this.lock.tryOptimisticRead();
        int currentId = this.typePalette.get(index);
        if (!this.lock.validate(stamp)) {
            stamp = this.lock.readLock();
            try {
                currentId = this.typePalette.get(index);
            }
            finally {
                this.lock.unlockRead(stamp);
            }
        }
        return currentId;
    }

    @Nullable
    public Fluid getFluid(int x, int y, int z) {
        return this.getFluid(ChunkUtil.indexBlock(x, y, z));
    }

    @Nullable
    public Fluid getFluid(int index) {
        IndexedLookupTableAssetMap<String, Fluid> assetMap = Fluid.getAssetMap();
        return assetMap.getAsset(this.getFluidId(index));
    }

    private boolean setFluidLevel(int x, int y, int z, byte level) {
        return this.setFluidLevel(ChunkUtil.indexBlock(x, y, z), level);
    }

    private boolean setFluidLevel(int index, byte level) {
        int byteIndex;
        byte byteValue;
        int value;
        level = (byte)(level & 0xF);
        if (this.levelData == null) {
            if (level == 0) {
                return false;
            }
            this.levelData = new byte[16384];
        }
        if ((value = (byteValue = this.levelData[byteIndex = index >> 1]) >> (index & 1) * 4 & 0xF) == level) {
            return false;
        }
        if (value == 0) {
            ++this.nonZeroLevels;
        } else if (level == 0) {
            --this.nonZeroLevels;
            if (this.nonZeroLevels <= 0) {
                this.levelData = null;
                return true;
            }
        }
        this.levelData[byteIndex] = (index & 1) == 0 ? (byte)(byteValue & 0xF0 | level) : (byte)(byteValue & 0xF | level << 4);
        return true;
    }

    public byte getFluidLevel(int x, int y, int z) {
        return this.getFluidLevel(ChunkUtil.indexBlock(x, y, z));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte getFluidLevel(int index) {
        byte byteValue;
        byte byteIndex;
        long stamp = this.lock.tryOptimisticRead();
        byte[] localData = this.levelData;
        byte result = 0;
        if (localData != null) {
            byteIndex = index >> 1;
            byteValue = localData[byteIndex];
            result = (byte)(byteValue >> (index & 1) * 4 & 0xF);
        }
        if (!this.lock.validate(stamp)) {
            stamp = this.lock.readLock();
            try {
                if (this.levelData == null) {
                    byteIndex = 0;
                    return byteIndex;
                }
                byteIndex = index >> 1;
                byteValue = this.levelData[byteIndex];
                byte by = (byte)(byteValue >> (index & 1) * 4 & 0xF);
                return by;
            }
            finally {
                this.lock.unlockRead(stamp);
            }
        }
        return result;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public IntOpenHashSet getAndClearChangedPositions() {
        long stamp = this.lock.writeLock();
        try {
            this.swapChangedPositions.clear();
            IntOpenHashSet tmp = this.changedPositions;
            this.changedPositions = this.swapChangedPositions;
            this.swapChangedPositions = tmp;
            IntOpenHashSet intOpenHashSet = tmp;
            return intOpenHashSet;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    @Override
    @Nonnull
    public Component<ChunkStore> clone() {
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void serializeForPacket(@Nonnull ByteBuf buf) {
        long stamp = this.lock.readLock();
        try {
            buf.writeByte(this.typePalette.getPaletteType().ordinal());
            this.typePalette.serializeForPacket(buf);
            if (this.levelData != null) {
                buf.writeBoolean(true);
                buf.writeBytes(this.levelData);
            } else {
                buf.writeBoolean(false);
            }
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    private byte[] serialize(ExtraInfo extraInfo) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        long stamp = this.lock.readLock();
        try {
            buf.writeByte(this.typePalette.getPaletteType().ordinal());
            this.typePalette.serialize(Fluid.KEY_SERIALIZER, buf);
            if (this.levelData != null) {
                buf.writeBoolean(true);
                buf.writeBytes(this.levelData);
            } else {
                buf.writeBoolean(false);
            }
            byte[] byArray = ByteBufUtil.getBytesRelease(buf);
            return byArray;
        }
        catch (Throwable e) {
            buf.release();
            throw SneakyThrow.sneakyThrow(e);
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    private void deserialize(@Nonnull byte[] bytes, ExtraInfo extraInfo) {
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        PaletteTypeEnum type = PaletteTypeEnum.get(buf.readByte());
        this.typePalette = type.getConstructor().get();
        this.typePalette.deserialize(Fluid.KEY_DESERIALIZER, buf, 0);
        if (buf.readBoolean()) {
            this.levelData = new byte[16384];
            buf.readBytes(this.levelData);
            this.nonZeroLevels = 0;
            for (int i = 0; i < 16384; ++i) {
                byte v = this.levelData[i];
                if ((v & 0xF) != 0) {
                    ++this.nonZeroLevels;
                }
                if ((v & 0xF0) == 0) continue;
                ++this.nonZeroLevels;
            }
        } else {
            this.levelData = null;
        }
    }

    @Nonnull
    public CompletableFuture<CachedPacket<SetFluids>> getCachedPacket() {
        CompletableFuture<CachedPacket<SetFluids>> future;
        SoftReference<CompletableFuture<CachedPacket<SetFluids>>> ref = this.cachedPacket;
        CompletableFuture<CachedPacket<SetFluids>> completableFuture = future = ref != null ? ref.get() : null;
        if (future != null) {
            return future;
        }
        future = CompletableFuture.supplyAsync(() -> {
            ByteBuf buf = Unpooled.buffer(65536);
            this.serializeForPacket(buf);
            byte[] data = ByteBufUtil.getBytesRelease(buf);
            SetFluids packet = new SetFluids(this.x, this.y, this.z, data);
            return CachedPacket.cache(packet);
        });
        this.cachedPacket = new SoftReference<CompletableFuture<CachedPacket<SetFluids>>>(future);
        return future;
    }

    public boolean isEmpty() {
        return this.typePalette.isSolid(0) && this.nonZeroLevels == 0;
    }
}

