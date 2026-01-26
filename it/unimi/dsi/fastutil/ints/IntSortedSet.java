/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterable;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.util.SortedSet;

public interface IntSortedSet
extends IntSet,
SortedSet<Integer>,
IntBidirectionalIterable {
    public IntBidirectionalIterator iterator(int var1);

    @Override
    public IntBidirectionalIterator iterator();

    @Override
    default public IntSpliterator spliterator() {
        return IntSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 341, this.comparator());
    }

    public IntSortedSet subSet(int var1, int var2);

    public IntSortedSet headSet(int var1);

    public IntSortedSet tailSet(int var1);

    public IntComparator comparator();

    public int firstInt();

    public int lastInt();

    @Deprecated
    default public IntSortedSet subSet(Integer from, Integer to) {
        return this.subSet((int)from, (int)to);
    }

    @Deprecated
    default public IntSortedSet headSet(Integer to) {
        return this.headSet((int)to);
    }

    @Deprecated
    default public IntSortedSet tailSet(Integer from) {
        return this.tailSet((int)from);
    }

    @Override
    @Deprecated
    default public Integer first() {
        return this.firstInt();
    }

    @Override
    @Deprecated
    default public Integer last() {
        return this.lastInt();
    }
}

