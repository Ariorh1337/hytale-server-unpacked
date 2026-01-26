/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.util.SortedSet;

public interface ReferenceSortedSet<K>
extends ReferenceSet<K>,
SortedSet<K>,
ObjectBidirectionalIterable<K> {
    public ObjectBidirectionalIterator<K> iterator(K var1);

    @Override
    public ObjectBidirectionalIterator<K> iterator();

    @Override
    default public ObjectSpliterator<K> spliterator() {
        return ObjectSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 85, this.comparator());
    }

    @Override
    public ReferenceSortedSet<K> subSet(K var1, K var2);

    @Override
    public ReferenceSortedSet<K> headSet(K var1);

    @Override
    public ReferenceSortedSet<K> tailSet(K var1);
}

