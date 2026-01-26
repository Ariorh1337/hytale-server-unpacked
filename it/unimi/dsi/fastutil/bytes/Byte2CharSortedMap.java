/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2CharMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Map;
import java.util.SortedMap;

public interface Byte2CharSortedMap
extends Byte2CharMap,
SortedMap<Byte, Character> {
    public Byte2CharSortedMap subMap(byte var1, byte var2);

    public Byte2CharSortedMap headMap(byte var1);

    public Byte2CharSortedMap tailMap(byte var1);

    public byte firstByteKey();

    public byte lastByteKey();

    @Deprecated
    default public Byte2CharSortedMap subMap(Byte from, Byte to) {
        return this.subMap((byte)from, (byte)to);
    }

    @Deprecated
    default public Byte2CharSortedMap headMap(Byte to) {
        return this.headMap((byte)to);
    }

    @Deprecated
    default public Byte2CharSortedMap tailMap(Byte from) {
        return this.tailMap((byte)from);
    }

    @Override
    @Deprecated
    default public Byte firstKey() {
        return this.firstByteKey();
    }

    @Override
    @Deprecated
    default public Byte lastKey() {
        return this.lastByteKey();
    }

    @Override
    @Deprecated
    default public ObjectSortedSet<Map.Entry<Byte, Character>> entrySet() {
        return this.byte2CharEntrySet();
    }

    public ObjectSortedSet<Byte2CharMap.Entry> byte2CharEntrySet();

    @Override
    public ByteSortedSet keySet();

    @Override
    public CharCollection values();

    public ByteComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Byte2CharMap.Entry>,
    Byte2CharMap.FastEntrySet {
        public ObjectBidirectionalIterator<Byte2CharMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Byte2CharMap.Entry> fastIterator(Byte2CharMap.Entry var1);
    }
}

