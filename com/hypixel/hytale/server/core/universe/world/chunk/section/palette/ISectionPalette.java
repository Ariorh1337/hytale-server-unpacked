/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.chunk.section.palette;

import com.hypixel.hytale.protocol.packets.world.PaletteType;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.ByteSectionPalette;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.EmptySectionPalette;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.HalfByteSectionPalette;
import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.ShortSectionPalette;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;
import javax.annotation.Nonnull;

public interface ISectionPalette {
    public PaletteType getPaletteType();

    public SetResult set(int var1, int var2);

    public int get(int var1);

    public boolean contains(int var1);

    public boolean containsAny(IntList var1);

    default public boolean isSolid(int id) {
        return this.count() == 1 && this.contains(id);
    }

    public int count();

    public int count(int var1);

    public IntSet values();

    public void forEachValue(IntConsumer var1);

    public Int2ShortMap valueCounts();

    public void find(IntList var1, IntSet var2, IntConsumer var3);

    public boolean shouldDemote();

    public ISectionPalette demote();

    public ISectionPalette promote();

    public void serializeForPacket(ByteBuf var1);

    public void serialize(KeySerializer var1, ByteBuf var2);

    public void deserialize(ToIntFunction<ByteBuf> var1, ByteBuf var2, int var3);

    @Nonnull
    public static ISectionPalette from(@Nonnull int[] data, int[] unique, int count) {
        if (count == 1 && unique[0] == 0) {
            return EmptySectionPalette.INSTANCE;
        }
        if (count < 16) {
            return new HalfByteSectionPalette(data, unique, count);
        }
        if (count < 256) {
            return new ByteSectionPalette(data, unique, count);
        }
        if (count < 65536) {
            return new ShortSectionPalette(data, unique, count);
        }
        throw new UnsupportedOperationException("Too many block types for palette.");
    }

    @FunctionalInterface
    public static interface KeySerializer {
        public void serialize(ByteBuf var1, int var2);
    }

    public static enum SetResult {
        ADDED_OR_REMOVED,
        CHANGED,
        UNCHANGED,
        REQUIRES_PROMOTE;

    }
}

