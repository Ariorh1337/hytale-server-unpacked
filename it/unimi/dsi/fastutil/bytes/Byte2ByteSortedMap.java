/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2ByteMap;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Map;
import java.util.SortedMap;

public interface Byte2ByteSortedMap
extends Byte2ByteMap,
SortedMap<Byte, Byte> {
    public Byte2ByteSortedMap subMap(byte var1, byte var2);

    public Byte2ByteSortedMap headMap(byte var1);

    public Byte2ByteSortedMap tailMap(byte var1);

    public byte firstByteKey();

    public byte lastByteKey();

    @Deprecated
    default public Byte2ByteSortedMap subMap(Byte from, Byte to) {
        return this.subMap((byte)from, (byte)to);
    }

    @Deprecated
    default public Byte2ByteSortedMap headMap(Byte to) {
        return this.headMap((byte)to);
    }

    @Deprecated
    default public Byte2ByteSortedMap tailMap(Byte from) {
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
    default public ObjectSortedSet<Map.Entry<Byte, Byte>> entrySet() {
        return this.byte2ByteEntrySet();
    }

    public ObjectSortedSet<Byte2ByteMap.Entry> byte2ByteEntrySet();

    @Override
    public ByteSortedSet keySet();

    @Override
    public ByteCollection values();

    public ByteComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Byte2ByteMap.Entry>,
    Byte2ByteMap.FastEntrySet {
        public ObjectBidirectionalIterator<Byte2ByteMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Byte2ByteMap.Entry> fastIterator(Byte2ByteMap.Entry var1);
    }
}

