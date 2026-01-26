/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.chunk.section.palette;

import com.hypixel.hytale.math.util.NumberUtil;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.ISectionPalette;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.shorts.Short2IntMap;
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortOpenHashMap;
import java.util.BitSet;
import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;
import javax.annotation.Nonnull;

public abstract class AbstractShortSectionPalette
implements ISectionPalette {
    protected final Int2ShortMap externalToInternal;
    protected final Short2IntMap internalToExternal;
    protected final BitSet internalIdSet;
    protected final Short2ShortMap internalIdCount;
    protected final short[] blocks;

    public AbstractShortSectionPalette(short[] blocks) {
        this(new Int2ShortOpenHashMap(), new Short2IntOpenHashMap(), new BitSet(), new Short2ShortOpenHashMap(), blocks);
        this.externalToInternal.put(0, (short)0);
        this.internalToExternal.put((short)0, 0);
        this.internalIdSet.set(0);
        this.internalIdCount.put((short)0, (short)Short.MIN_VALUE);
    }

    public AbstractShortSectionPalette(short[] blocks, @Nonnull int[] data, int[] unique, int count) {
        this(new Int2ShortOpenHashMap(count), new Short2IntOpenHashMap(count), new BitSet(count), new Short2ShortOpenHashMap(count), blocks);
        for (int internalId = 0; internalId < count; ++internalId) {
            int blockId = unique[internalId];
            this.internalToExternal.put((short)internalId, blockId);
            this.externalToInternal.put(blockId, (short)internalId);
            this.internalIdSet.set(internalId);
            this.internalIdCount.put((short)internalId, (short)0);
        }
        for (int index = 0; index < data.length; ++index) {
            int id = data[index];
            short internalId = this.externalToInternal.get(id);
            this.incrementBlockCount(internalId);
            this.set0(index, internalId);
        }
    }

    protected AbstractShortSectionPalette(Int2ShortMap externalToInternal, Short2IntMap internalToExternal, BitSet internalIdSet, Short2ShortMap internalIdCount, short[] blocks) {
        this.externalToInternal = externalToInternal;
        this.internalToExternal = internalToExternal;
        this.internalIdSet = internalIdSet;
        this.internalIdCount = internalIdCount;
        this.blocks = blocks;
    }

    @Override
    public int get(int index) {
        short internalId = this.get0(index);
        return this.internalToExternal.get(internalId);
    }

    @Override
    @Nonnull
    public ISectionPalette.SetResult set(int index, int id) {
        short oldInternalId = this.get0(index);
        if (this.externalToInternal.containsKey(id)) {
            short newInternalId = this.externalToInternal.get(id);
            if (newInternalId == oldInternalId) {
                return ISectionPalette.SetResult.UNCHANGED;
            }
            boolean removed = this.decrementBlockCount(oldInternalId);
            this.incrementBlockCount(newInternalId);
            this.set0(index, newInternalId);
            if (removed) {
                return ISectionPalette.SetResult.ADDED_OR_REMOVED;
            }
            return ISectionPalette.SetResult.CHANGED;
        }
        int nextInternalId = this.nextInternalId(oldInternalId);
        if (!this.isValidInternalId(nextInternalId)) {
            return ISectionPalette.SetResult.REQUIRES_PROMOTE;
        }
        this.decrementBlockCount(oldInternalId);
        short newInternalId = (short)nextInternalId;
        this.createBlockId(newInternalId, id);
        this.set0(index, newInternalId);
        return ISectionPalette.SetResult.ADDED_OR_REMOVED;
    }

    protected abstract short get0(int var1);

    protected abstract void set0(int var1, short var2);

    @Override
    public boolean contains(int id) {
        return this.externalToInternal.containsKey(id);
    }

    @Override
    public boolean containsAny(@Nonnull IntList ids) {
        for (int i = 0; i < ids.size(); ++i) {
            if (!this.externalToInternal.containsKey(ids.getInt(i))) continue;
            return true;
        }
        return false;
    }

    @Override
    public int count() {
        return this.internalIdCount.size();
    }

    @Override
    public int count(int id) {
        if (this.externalToInternal.containsKey(id)) {
            short internalId = this.externalToInternal.get(id);
            return this.internalIdCount.get(internalId);
        }
        return 0;
    }

    @Override
    @Nonnull
    public IntSet values() {
        return new IntOpenHashSet(this.externalToInternal.keySet());
    }

    @Override
    public void forEachValue(IntConsumer consumer) {
        this.externalToInternal.keySet().forEach(consumer);
    }

    @Override
    @Nonnull
    public Int2ShortMap valueCounts() {
        Int2ShortOpenHashMap map = new Int2ShortOpenHashMap();
        for (Short2ShortMap.Entry entry : this.internalIdCount.short2ShortEntrySet()) {
            short internalId = entry.getShortKey();
            short count = entry.getShortValue();
            int externalId = this.internalToExternal.get(internalId);
            map.put(externalId, count);
        }
        return map;
    }

    private void createBlockId(short internalId, int blockId) {
        this.internalToExternal.put(internalId, blockId);
        this.externalToInternal.put(blockId, internalId);
        this.internalIdSet.set(internalId);
        this.internalIdCount.put(internalId, (short)1);
    }

    private boolean decrementBlockCount(short internalId) {
        short oldCount = this.internalIdCount.get(internalId);
        if (oldCount == 1) {
            this.internalIdCount.remove(internalId);
            int externalId = this.internalToExternal.remove(internalId);
            this.externalToInternal.remove(externalId);
            this.internalIdSet.clear(internalId);
            return true;
        }
        this.internalIdCount.mergeShort(internalId, (short)1, NumberUtil::subtract);
        return false;
    }

    private void incrementBlockCount(short internalId) {
        this.internalIdCount.mergeShort(internalId, (short)1, NumberUtil::sum);
    }

    private int nextInternalId(short oldInternalId) {
        if (this.internalIdCount.get(oldInternalId) == 1) {
            return oldInternalId;
        }
        return this.internalIdSet.nextClearBit(0);
    }

    protected abstract boolean isValidInternalId(int var1);

    @Override
    public void serializeForPacket(@Nonnull ByteBuf buf) {
        buf.writeShortLE(this.internalToExternal.size());
        for (Short2IntMap.Entry entry : this.internalToExternal.short2IntEntrySet()) {
            short internalId = entry.getShortKey();
            int externalId = entry.getIntValue();
            buf.writeShortLE(internalId & 0xFFFF);
            buf.writeIntLE(externalId);
            buf.writeShortLE(this.internalIdCount.get(internalId));
        }
        for (int i = 0; i < this.blocks.length; ++i) {
            buf.writeShortLE(this.blocks[i]);
        }
    }

    @Override
    public void serialize(@Nonnull ISectionPalette.KeySerializer keySerializer, @Nonnull ByteBuf buf) {
        buf.writeShort(this.internalToExternal.size());
        for (Short2IntMap.Entry entry : this.internalToExternal.short2IntEntrySet()) {
            short internalId = entry.getShortKey();
            int externalId = entry.getIntValue();
            buf.writeShort(internalId & 0xFFFF);
            keySerializer.serialize(buf, externalId);
            buf.writeShort(this.internalIdCount.get(internalId));
        }
        for (int i = 0; i < this.blocks.length; ++i) {
            buf.writeShort(this.blocks[i]);
        }
    }

    @Override
    public void deserialize(@Nonnull ToIntFunction<ByteBuf> deserializer, @Nonnull ByteBuf buf, int version) {
        int i;
        this.externalToInternal.clear();
        this.internalToExternal.clear();
        this.internalIdSet.clear();
        this.internalIdCount.clear();
        Short2ShortMap internalIdRemapping = null;
        int blockCount = buf.readShort();
        for (i = 0; i < blockCount; ++i) {
            short internalId = buf.readShort();
            int externalId = deserializer.applyAsInt(buf);
            short count = buf.readShort();
            if (this.externalToInternal.containsKey(externalId)) {
                short existingInternalId = this.externalToInternal.get(externalId);
                if (internalIdRemapping == null) {
                    internalIdRemapping = new Short2ShortOpenHashMap();
                }
                internalIdRemapping.put(internalId, existingInternalId);
                this.internalIdCount.mergeShort(existingInternalId, count, NumberUtil::sum);
                continue;
            }
            this.externalToInternal.put(externalId, internalId);
            this.internalToExternal.put(internalId, externalId);
            this.internalIdSet.set(internalId);
            this.internalIdCount.put(internalId, count);
        }
        for (i = 0; i < this.blocks.length; ++i) {
            this.blocks[i] = buf.readShort();
        }
        if (internalIdRemapping != null) {
            for (i = 0; i < 32768; ++i) {
                short oldInternalId = this.get0(i);
                if (!internalIdRemapping.containsKey(oldInternalId)) continue;
                this.set0(i, internalIdRemapping.get(oldInternalId));
            }
        }
    }

    @Override
    public void find(@Nonnull IntList ids, @Nonnull IntSet internalIdHolder, @Nonnull IntConsumer indexConsumer) {
        int i;
        for (i = 0; i < ids.size(); ++i) {
            short internal = this.externalToInternal.getOrDefault(ids.getInt(i), (short)Short.MIN_VALUE);
            if (internal == Short.MIN_VALUE) continue;
            internalIdHolder.add(internal);
        }
        for (i = 0; i < 32768; ++i) {
            short type = this.get0(i);
            if (!internalIdHolder.contains(type)) continue;
            indexConsumer.accept(i);
        }
    }
}

