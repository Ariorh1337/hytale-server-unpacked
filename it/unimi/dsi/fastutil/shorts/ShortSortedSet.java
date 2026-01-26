/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterable;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.util.SortedSet;

public interface ShortSortedSet
extends ShortSet,
SortedSet<Short>,
ShortBidirectionalIterable {
    public ShortBidirectionalIterator iterator(short var1);

    @Override
    public ShortBidirectionalIterator iterator();

    @Override
    default public ShortSpliterator spliterator() {
        return ShortSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 341, this.comparator());
    }

    public ShortSortedSet subSet(short var1, short var2);

    public ShortSortedSet headSet(short var1);

    public ShortSortedSet tailSet(short var1);

    public ShortComparator comparator();

    public short firstShort();

    public short lastShort();

    @Deprecated
    default public ShortSortedSet subSet(Short from, Short to) {
        return this.subSet((short)from, (short)to);
    }

    @Deprecated
    default public ShortSortedSet headSet(Short to) {
        return this.headSet((short)to);
    }

    @Deprecated
    default public ShortSortedSet tailSet(Short from) {
        return this.tailSet((short)from);
    }

    @Override
    @Deprecated
    default public Short first() {
        return this.firstShort();
    }

    @Override
    @Deprecated
    default public Short last() {
        return this.lastShort();
    }
}

