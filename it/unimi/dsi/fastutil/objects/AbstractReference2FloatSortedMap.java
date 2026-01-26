/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.AbstractReference2FloatMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatSortedMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatSortedMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Comparator;

public abstract class AbstractReference2FloatSortedMap<K>
extends AbstractReference2FloatMap<K>
implements Reference2FloatSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractReference2FloatSortedMap() {
    }

    @Override
    public ReferenceSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public FloatCollection values() {
        return new ValuesCollection();
    }

    protected class KeySet
    extends AbstractReferenceSortedSet<K> {
        protected KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return AbstractReference2FloatSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractReference2FloatSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2FloatSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractReference2FloatSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractReference2FloatSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractReference2FloatSortedMap.this.lastKey();
        }

        @Override
        public ReferenceSortedSet<K> headSet(K to) {
            return AbstractReference2FloatSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K from) {
            return AbstractReference2FloatSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            return AbstractReference2FloatSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractReference2FloatSortedMap.this.reference2FloatEntrySet().iterator(new AbstractReference2FloatMap.BasicEntry(from, 0.0f)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Reference2FloatSortedMaps.fastIterator(AbstractReference2FloatSortedMap.this));
        }
    }

    protected class ValuesCollection
    extends AbstractFloatCollection {
        protected ValuesCollection() {
        }

        @Override
        public FloatIterator iterator() {
            return new ValuesIterator(Reference2FloatSortedMaps.fastIterator(AbstractReference2FloatSortedMap.this));
        }

        @Override
        public boolean contains(float k) {
            return AbstractReference2FloatSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractReference2FloatSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2FloatSortedMap.this.clear();
        }
    }

    protected static class ValuesIterator<K>
    implements FloatIterator {
        protected final ObjectBidirectionalIterator<Reference2FloatMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Reference2FloatMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public float nextFloat() {
            return ((Reference2FloatMap.Entry)this.i.next()).getFloatValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Reference2FloatMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Reference2FloatMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public K next() {
            return ((Reference2FloatMap.Entry)this.i.next()).getKey();
        }

        @Override
        public K previous() {
            return ((Reference2FloatMap.Entry)this.i.previous()).getKey();
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

