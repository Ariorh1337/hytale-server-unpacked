/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Map;
import java.util.SortedMap;

public interface Long2LongSortedMap
extends Long2LongMap,
SortedMap<Long, Long> {
    public Long2LongSortedMap subMap(long var1, long var3);

    public Long2LongSortedMap headMap(long var1);

    public Long2LongSortedMap tailMap(long var1);

    public long firstLongKey();

    public long lastLongKey();

    @Deprecated
    default public Long2LongSortedMap subMap(Long from, Long to) {
        return this.subMap((long)from, (long)to);
    }

    @Deprecated
    default public Long2LongSortedMap headMap(Long to) {
        return this.headMap((long)to);
    }

    @Deprecated
    default public Long2LongSortedMap tailMap(Long from) {
        return this.tailMap((long)from);
    }

    @Override
    @Deprecated
    default public Long firstKey() {
        return this.firstLongKey();
    }

    @Override
    @Deprecated
    default public Long lastKey() {
        return this.lastLongKey();
    }

    @Override
    @Deprecated
    default public ObjectSortedSet<Map.Entry<Long, Long>> entrySet() {
        return this.long2LongEntrySet();
    }

    public ObjectSortedSet<Long2LongMap.Entry> long2LongEntrySet();

    @Override
    public LongSortedSet keySet();

    @Override
    public LongCollection values();

    public LongComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Long2LongMap.Entry>,
    Long2LongMap.FastEntrySet {
        public ObjectBidirectionalIterator<Long2LongMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Long2LongMap.Entry> fastIterator(Long2LongMap.Entry var1);
    }
}

