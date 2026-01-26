/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2CharMap;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Map;
import java.util.SortedMap;

public interface Short2CharSortedMap
extends Short2CharMap,
SortedMap<Short, Character> {
    public Short2CharSortedMap subMap(short var1, short var2);

    public Short2CharSortedMap headMap(short var1);

    public Short2CharSortedMap tailMap(short var1);

    public short firstShortKey();

    public short lastShortKey();

    @Deprecated
    default public Short2CharSortedMap subMap(Short from, Short to) {
        return this.subMap((short)from, (short)to);
    }

    @Deprecated
    default public Short2CharSortedMap headMap(Short to) {
        return this.headMap((short)to);
    }

    @Deprecated
    default public Short2CharSortedMap tailMap(Short from) {
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
    default public ObjectSortedSet<Map.Entry<Short, Character>> entrySet() {
        return this.short2CharEntrySet();
    }

    public ObjectSortedSet<Short2CharMap.Entry> short2CharEntrySet();

    @Override
    public ShortSortedSet keySet();

    @Override
    public CharCollection values();

    public ShortComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Short2CharMap.Entry>,
    Short2CharMap.FastEntrySet {
        public ObjectBidirectionalIterator<Short2CharMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Short2CharMap.Entry> fastIterator(Short2CharMap.Entry var1);
    }
}

