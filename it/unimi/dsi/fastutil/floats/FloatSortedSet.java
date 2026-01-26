/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterable;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import java.util.SortedSet;

public interface FloatSortedSet
extends FloatSet,
SortedSet<Float>,
FloatBidirectionalIterable {
    public FloatBidirectionalIterator iterator(float var1);

    @Override
    public FloatBidirectionalIterator iterator();

    @Override
    default public FloatSpliterator spliterator() {
        return FloatSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 341, this.comparator());
    }

    public FloatSortedSet subSet(float var1, float var2);

    public FloatSortedSet headSet(float var1);

    public FloatSortedSet tailSet(float var1);

    public FloatComparator comparator();

    public float firstFloat();

    public float lastFloat();

    @Deprecated
    default public FloatSortedSet subSet(Float from, Float to) {
        return this.subSet(from.floatValue(), to.floatValue());
    }

    @Deprecated
    default public FloatSortedSet headSet(Float to) {
        return this.headSet(to.floatValue());
    }

    @Deprecated
    default public FloatSortedSet tailSet(Float from) {
        return this.tailSet(from.floatValue());
    }

    @Override
    @Deprecated
    default public Float first() {
        return Float.valueOf(this.firstFloat());
    }

    @Override
    @Deprecated
    default public Float last() {
        return Float.valueOf(this.lastFloat());
    }
}

