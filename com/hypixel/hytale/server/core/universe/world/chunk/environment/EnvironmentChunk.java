/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.chunk.environment;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.modules.LegacyModule;
import com.hypixel.hytale.server.core.universe.world.chunk.environment.EnvironmentColumn;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.io.ByteBufUtil;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import javax.annotation.Nonnull;

public class EnvironmentChunk
implements Component<ChunkStore> {
    public static final BuilderCodec<EnvironmentChunk> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(EnvironmentChunk.class, EnvironmentChunk::new).addField(new KeyedCodec<byte[]>("Data", Codec.BYTE_ARRAY), EnvironmentChunk::deserialize, EnvironmentChunk::serialize)).build();
    private final EnvironmentColumn[] columns = new EnvironmentColumn[1024];
    private final Int2LongMap counts = new Int2LongOpenHashMap();

    public static ComponentType<ChunkStore, EnvironmentChunk> getComponentType() {
        return LegacyModule.get().getEnvironmentChunkComponentType();
    }

    public EnvironmentChunk() {
        this(0);
    }

    public EnvironmentChunk(int defaultId) {
        for (int i = 0; i < this.columns.length; ++i) {
            this.columns[i] = new EnvironmentColumn(defaultId);
        }
    }

    @Override
    @Nonnull
    public Component<ChunkStore> clone() {
        EnvironmentChunk chunk = new EnvironmentChunk();
        for (int i = 0; i < this.columns.length; ++i) {
            chunk.columns[i].copyFrom(this.columns[i]);
        }
        chunk.counts.putAll(this.counts);
        return chunk;
    }

    public int get(int x, int y, int z) {
        return this.columns[EnvironmentChunk.idx(x, z)].get(y);
    }

    public EnvironmentColumn get(int x, int z) {
        return this.columns[EnvironmentChunk.idx(x, z)];
    }

    public void setColumn(int x, int z, int environmentId) {
        int maxY;
        EnvironmentColumn column = this.columns[EnvironmentChunk.idx(x, z)];
        column.set(environmentId);
        int minY = Integer.MIN_VALUE;
        do {
            int id = column.get(minY);
            maxY = column.getMax(minY);
            int count = maxY - minY + 1;
            this.decrementBlockCount(id, count);
        } while (maxY < Integer.MAX_VALUE);
        this.createIfNotExist(environmentId);
        this.incrementBlockCount(environmentId, Integer.MAX_VALUE);
        column.set(environmentId);
    }

    public boolean set(int x, int y, int z, int environmentId) {
        EnvironmentColumn column = this.columns[EnvironmentChunk.idx(x, z)];
        int oldInternalId = column.get(y);
        if (environmentId != oldInternalId) {
            this.decrementBlockCount(oldInternalId, 1L);
            this.createIfNotExist(environmentId);
            this.incrementBlockCount(environmentId);
            column.set(y, environmentId);
            return true;
        }
        return false;
    }

    public boolean contains(int environmentId) {
        return this.counts.containsKey(environmentId);
    }

    private void createIfNotExist(int environmentId) {
        if (!this.counts.containsKey(environmentId)) {
            this.counts.put(environmentId, 0L);
        }
    }

    private void incrementBlockCount(int internalId) {
        this.counts.mergeLong(internalId, 1L, Long::sum);
    }

    private void incrementBlockCount(int internalId, int count) {
        long oldCount = this.counts.get(internalId);
        this.counts.put(internalId, oldCount + (long)count);
    }

    private boolean decrementBlockCount(int environmentId, long count) {
        long oldCount = this.counts.get(environmentId);
        if (oldCount <= count) {
            this.counts.remove(environmentId);
            return true;
        }
        this.counts.put(environmentId, oldCount - count);
        return false;
    }

    private byte[] serialize() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        try {
            buf.writeInt(this.counts.size());
            for (Int2LongMap.Entry entry : this.counts.int2LongEntrySet()) {
                int environmentId2 = entry.getIntKey();
                Environment environment = Environment.getAssetMap().getAsset(environmentId2);
                String key = environment != null ? environment.getId() : Environment.UNKNOWN.getId();
                buf.writeInt(environmentId2);
                ByteBufUtil.writeUTF(buf, key);
            }
            for (int i = 0; i < this.columns.length; ++i) {
                this.columns[i].serialize(buf, (environmentId, buf0) -> buf0.writeInt(environmentId));
            }
            return ByteBufUtil.getBytesRelease(buf);
        }
        catch (Throwable t) {
            buf.release();
            throw SneakyThrow.sneakyThrow(t);
        }
    }

    private void deserialize(@Nonnull byte[] bytes) {
        int i;
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        this.counts.clear();
        int mappingCount = buf.readInt();
        Int2IntOpenHashMap idMapping = new Int2IntOpenHashMap(mappingCount);
        for (i = 0; i < mappingCount; ++i) {
            int serialId = buf.readInt();
            String key = ByteBufUtil.readUTF(buf);
            int environmentId = Environment.getIndexOrUnknown(key, "Failed to find environment '%s' when deserializing environment chunk", key);
            idMapping.put(serialId, environmentId);
            this.counts.put(environmentId, 0L);
        }
        for (i = 0; i < this.columns.length; ++i) {
            EnvironmentColumn column = this.columns[i];
            column.deserialize(buf, buf0 -> idMapping.get(buf0.readInt()));
            for (int x = 0; x < column.size(); ++x) {
                this.counts.mergeLong(column.getValue(x), 1L, Long::sum);
            }
        }
    }

    public byte[] serializeProtocol() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        for (int i = 0; i < this.columns.length; ++i) {
            this.columns[i].serializeProtocol(buf);
        }
        return ByteBufUtil.getBytesRelease(buf);
    }

    public void trim() {
        for (int i = 0; i < this.columns.length; ++i) {
            this.columns[i].trim();
        }
    }

    private static int idx(int x, int z) {
        return ChunkUtil.indexColumn(x, z);
    }
}

