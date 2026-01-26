/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2LongMap;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Map;
import java.util.SortedMap;

public interface Short2LongSortedMap
extends Short2LongMap,
SortedMap<Short, Long> {
    public Short2LongSortedMap subMap(short var1, short var2);

    public Short2LongSortedMap headMap(short var1);

    public Short2LongSortedMap tailMap(short var1);

    public short firstShortKey();

    public short lastShortKey();

    @Deprecated
    default public Short2LongSortedMap subMap(Short from, Short to) {
        return this.subMap((short)from, (short)to);
    }

    @Deprecated
    default public Short2LongSortedMap headMap(Short to) {
        return this.headMap((short)to);
    }

    @Deprecated
    default public Short2LongSortedMap tailMap(Short from) {
        return this.tailMap((short)from);
    }

    @Override
    @Deprecated
    default public Short firstKey() {
        return this.firstShortKey();
    }

    @Override
    @Deprecated
    default public Short lastKey() {
        return this.lastShortKey();
    }

    @Override
    @Deprecated
    default public ObjectSortedSet<Map.Entry<Short, Long>> entrySet() {
        return this.short2LongEntrySet();
    }

    public ObjectSortedSet<Short2LongMap.Entry> short2LongEntrySet();

    @Override
    public ShortSortedSet keySet();

    @Override
    public LongCollection values();

    public ShortComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Short2LongMap.Entry>,
    Short2LongMap.FastEntrySet {
        public ObjectBidirectionalIterator<Short2LongMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Short2LongMap.Entry> fastIterator(Short2LongMap.Entry var1);
    }
}

