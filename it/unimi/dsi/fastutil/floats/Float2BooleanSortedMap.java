/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.floats.Float2BooleanMap;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Map;
import java.util.SortedMap;

public interface Float2BooleanSortedMap
extends Float2BooleanMap,
SortedMap<Float, Boolean> {
    public Float2BooleanSortedMap subMap(float var1, float var2);

    public Float2BooleanSortedMap headMap(float var1);

    public Float2BooleanSortedMap tailMap(float var1);

    public float firstFloatKey();

    public float lastFloatKey();

    @Deprecated
    default public Float2BooleanSortedMap subMap(Float from, Float to) {
        return this.subMap(from.floatValue(), to.floatValue());
    }

    @Deprecated
    default public Float2BooleanSortedMap headMap(Float to) {
        return this.headMap(to.floatValue());
    }

    @Deprecated
    default public Float2BooleanSortedMap tailMap(Float from) {
        return this.tailMap(from.floatValue());
    }

    @Override
    @Deprecated
    default public Float firstKey() {
        return Float.valueOf(this.firstFloatKey());
    }

    @Override
    @Deprecated
    default public Float lastKey() {
        return Float.valueOf(this.lastFloatKey());
    }

    @Override
    @Deprecated
    default public ObjectSortedSet<Map.Entry<Float, Boolean>> entrySet() {
        return this.float2BooleanEntrySet();
    }

    public ObjectSortedSet<Float2BooleanMap.Entry> float2BooleanEntrySet();

    @Override
    public FloatSortedSet keySet();

    @Override
    public BooleanCollection values();

    public FloatComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Float2BooleanMap.Entry>,
    Float2BooleanMap.FastEntrySet {
        public ObjectBidirectionalIterator<Float2BooleanMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Float2BooleanMap.Entry> fastIterator(Float2BooleanMap.Entry var1);
    }
}

