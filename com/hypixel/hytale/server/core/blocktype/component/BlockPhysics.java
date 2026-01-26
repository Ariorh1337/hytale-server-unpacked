/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.blocktype.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.util.BitUtil;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.blocktype.BlockTypeModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.io.ByteBufUtil;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import java.util.Arrays;
import java.util.concurrent.locks.StampedLock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockPhysics
implements Component<ChunkStore> {
    public static final int VERSION = 0;
    public static final BuilderCodec<BlockPhysics> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockPhysics.class, BlockPhysics::new).versioned()).codecVersion(0)).append(new KeyedCodec<byte[]>("Data", Codec.BYTE_ARRAY), BlockPhysics::deserialize, BlockPhysics::serialize).add()).build();
    public static final int SUPPORT_DATA_SIZE = 16384;
    public static final int IS_DECO_VALUE = 15;
    public static final int NULL_SUPPORT = 0;
    private final StampedLock lock = new StampedLock();
    @Nullable
    private byte[] supportData = null;
    private int nonZeroCount = 0;

    public static ComponentType<ChunkStore, BlockPhysics> getComponentType() {
        return BlockTypeModule.get().getBlockPhysicsComponentType();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean set(int index, int support) {
        long stamp = this.lock.writeLock();
        try {
            byte previousValue;
            support &= 0xF;
            if (this.supportData == null) {
                if (support == 0) {
                    boolean bl = false;
                    return bl;
                }
                this.supportData = new byte[16384];
            }
            if ((previousValue = BitUtil.getAndSetNibble(this.supportData, index, (byte)support)) == support) {
                boolean bl = false;
                return bl;
            }
            if (previousValue == 0) {
                ++this.nonZeroCount;
            } else if (support == 0) {
                --this.nonZeroCount;
                if (this.nonZeroCount <= 0) {
                    this.supportData = null;
                }
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    public boolean set(int x, int y, int z, int support) {
        return this.set(ChunkUtil.indexBlock(x, y, z), support);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int get(int index) {
        long stamp = this.lock.readLock();
        try {
            if (this.supportData == null) {
                int n = 0;
                return n;
            }
            byte by = BitUtil.getNibble(this.supportData, index);
            return by;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    public int get(int x, int y, int z) {
        return this.get(ChunkUtil.indexBlock(x, y, z));
    }

    public boolean isDeco(int x, int y, int z) {
        return this.isDeco(ChunkUtil.indexBlock(x, y, z));
    }

    public boolean isDeco(int index) {
        return this.get(index) == 15;
    }

    @Override
    @Nonnull
    public Component<ChunkStore> clone() {
        BlockPhysics decoBlocks = new BlockPhysics();
        if (this.supportData != null) {
            decoBlocks.supportData = Arrays.copyOf(this.supportData, this.supportData.length);
            decoBlocks.nonZeroCount = this.nonZeroCount;
        }
        return decoBlocks;
    }

    private byte[] serialize(ExtraInfo extraInfo) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        try {
            buf.writeBoolean(this.supportData != null);
            if (this.supportData != null) {
                buf.writeBytes(this.supportData);
            }
            return ByteBufUtil.getBytesRelease(buf);
        }
        catch (Throwable e) {
            buf.release();
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    private void deserialize(@Nonnull byte[] bytes, ExtraInfo extraInfo) {
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        if (buf.readBoolean()) {
            this.supportData = new byte[16384];
            buf.readBytes(this.supportData);
            this.nonZeroCount = 0;
            for (int i = 0; i < 16384; ++i) {
                byte v = this.supportData[i];
                if ((v & 0xF) != 0) {
                    ++this.nonZeroCount;
                }
                if ((v & 0xF0) == 0) continue;
                ++this.nonZeroCount;
            }
        } else {
            this.supportData = null;
            this.nonZeroCount = 0;
        }
    }

    public static void clear(@Nonnull Store<ChunkStore> store, @Nonnull Ref<ChunkStore> section, int x, int y, int z) {
        BlockPhysics blockPhysics = store.getComponent(section, BlockPhysics.getComponentType());
        if (blockPhysics == null) {
            return;
        }
        blockPhysics.set(ChunkUtil.indexBlock(x, y, z), 0);
    }

    public static void clear(@Nonnull Holder<ChunkStore> section, int x, int y, int z) {
        BlockPhysics blockPhysics = section.getComponent(BlockPhysics.getComponentType());
        if (blockPhysics == null) {
            return;
        }
        blockPhysics.set(ChunkUtil.indexBlock(x, y, z), 0);
    }

    public static void reset(@Nonnull Store<ChunkStore> store, @Nonnull Ref<ChunkStore> section, int x, int y, int z) {
        BlockPhysics blockPhysics = store.getComponent(section, BlockPhysics.getComponentType());
        if (blockPhysics == null) {
            blockPhysics = store.ensureAndGetComponent(section, BlockPhysics.getComponentType());
        }
        blockPhysics.set(ChunkUtil.indexBlock(x, y, z), 0);
    }

    public static void reset(@Nonnull Holder<ChunkStore> section, int x, int y, int z) {
        BlockPhysics.setSupportValue(section, x, y, z, 0);
    }

    public static void markDeco(@Nonnull ComponentAccessor<ChunkStore> store, @Nonnull Ref<ChunkStore> section, int x, int y, int z) {
        BlockPhysics blockPhysics = store.getComponent(section, BlockPhysics.getComponentType());
        if (blockPhysics == null) {
            blockPhysics = store.ensureAndGetComponent(section, BlockPhysics.getComponentType());
        }
        blockPhysics.set(ChunkUtil.indexBlock(x, y, z), 15);
    }

    public static void setSupportValue(@Nonnull Store<ChunkStore> store, @Nonnull Ref<ChunkStore> section, int x, int y, int z, int value) {
        BlockPhysics blockPhysics = store.getComponent(section, BlockPhysics.getComponentType());
        if (blockPhysics == null) {
            blockPhysics = store.ensureAndGetComponent(section, BlockPhysics.getComponentType());
        }
        blockPhysics.set(ChunkUtil.indexBlock(x, y, z), value);
    }

    public static void setSupportValue(@Nonnull Holder<ChunkStore> section, int x, int y, int z, int value) {
        BlockPhysics blockPhysics = section.getComponent(BlockPhysics.getComponentType());
        if (blockPhysics == null) {
            blockPhysics = section.ensureAndGetComponent(BlockPhysics.getComponentType());
        }
        blockPhysics.set(ChunkUtil.indexBlock(x, y, z), value);
    }
}

