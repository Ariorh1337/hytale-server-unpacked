/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.chunk.section.palette;

import com.hypixel.hytale.protocol.packets.world.PaletteType;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.HalfByteSectionPalette;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.ISectionPalette;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;
import javax.annotation.Nonnull;

public class EmptySectionPalette
implements ISectionPalette {
    public static final int EMPTY_ID = 0;
    public static final EmptySectionPalette INSTANCE = new EmptySectionPalette();

    private EmptySectionPalette() {
    }

    @Override
    @Nonnull
    public PaletteType getPaletteType() {
        return PaletteType.Empty;
    }

    @Override
    @Nonnull
    public ISectionPalette.SetResult set(int index, int id) {
        return id == 0 ? ISectionPalette.SetResult.UNCHANGED : ISectionPalette.SetResult.REQUIRES_PROMOTE;
    }

    @Override
    public int get(int index) {
        return 0;
    }

    @Override
    public boolean shouldDemote() {
        return false;
    }

    @Override
    public ISectionPalette demote() {
        throw new UnsupportedOperationException("Cannot demote empty chunk section!");
    }

    @Override
    @Nonnull
    public ISectionPalette promote() {
        return new HalfByteSectionPalette();
    }

    @Override
    public boolean contains(int id) {
        return id == 0;
    }

    @Override
    public boolean containsAny(@Nonnull IntList ids) {
        return ids.contains(0);
    }

    @Override
    public boolean isSolid(int id) {
        return id == 0;
    }

    @Override
    public int count() {
        return 1;
    }

    @Override
    public int count(int id) {
        return id == 0 ? 32768 : 0;
    }

    @Override
    @Nonnull
    public IntSet values() {
        IntOpenHashSet set = new IntOpenHashSet();
        set.add(0);
        return set;
    }

    @Override
    public void forEachValue(@Nonnull IntConsumer consumer) {
        consumer.accept(0);
    }

    @Override
    @Nonnull
    public Int2ShortMap valueCounts() {
        Int2ShortOpenHashMap map = new Int2ShortOpenHashMap();
        map.put(0, (short)Short.MIN_VALUE);
        return map;
    }

    @Override
    public void find(@Nonnull IntList ids, IntSet internalIdHolder, @Nonnull IntConsumer indexConsumer) {
        if (ids.contains(0)) {
            for (int i = 0; i < 32768; ++i) {
                indexConsumer.accept(i);
            }
        }
    }

    @Override
    public void serializeForPacket(ByteBuf buf) {
    }

    @Override
    public void serialize(ISectionPalette.KeySerializer keySerializer, ByteBuf buf) {
    }

    @Override
    public void deserialize(ToIntFunction<ByteBuf> deserializer, ByteBuf buf, int version) {
    }
}

