/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.AbstractReference2DoubleMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleSortedMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleSortedMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Comparator;

public abstract class AbstractReference2DoubleSortedMap<K>
extends AbstractReference2DoubleMap<K>
implements Reference2DoubleSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractReference2DoubleSortedMap() {
    }

    @Override
    public ReferenceSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public DoubleCollection values() {
        return new ValuesCollection();
    }

    protected class KeySet
    extends AbstractReferenceSortedSet<K> {
        protected KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return AbstractReference2DoubleSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractReference2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2DoubleSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractReference2DoubleSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractReference2DoubleSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractReference2DoubleSortedMap.this.lastKey();
        }

        @Override
        public ReferenceSortedSet<K> headSet(K to) {
            return AbstractReference2DoubleSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K from) {
            return AbstractReference2DoubleSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            return AbstractReference2DoubleSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractReference2DoubleSortedMap.this.reference2DoubleEntrySet().iterator(new AbstractReference2DoubleMap.BasicEntry(from, 0.0)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Reference2DoubleSortedMaps.fastIterator(AbstractReference2DoubleSortedMap.this));
        }
    }

    protected class ValuesCollection
    extends AbstractDoubleCollection {
        protected ValuesCollection() {
        }

        @Override
        public DoubleIterator iterator() {
            return new ValuesIterator(Reference2DoubleSortedMaps.fastIterator(AbstractReference2DoubleSortedMap.this));
        }

        @Override
        public boolean contains(double k) {
            return AbstractReference2DoubleSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractReference2DoubleSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2DoubleSortedMap.this.clear();
        }
    }

    protected static class ValuesIterator<K>
    implements DoubleIterator {
        protected final ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public double nextDouble() {
            return ((Reference2DoubleMap.Entry)this.i.next()).getDoubleValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public K next() {
            return ((Reference2DoubleMap.Entry)this.i.next()).getKey();
        }

        @Override
        public K previous() {
            return ((Reference2DoubleMap.Entry)this.i.previous()).getKey();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }
    }
}

