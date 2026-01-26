/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractObject2ReferenceMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceSortedMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceSortedMaps;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Comparator;

public abstract class AbstractObject2ReferenceSortedMap<K, V>
extends AbstractObject2ReferenceMap<K, V>
implements Object2ReferenceSortedMap<K, V> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractObject2ReferenceSortedMap() {
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public ReferenceCollection<V> values() {
        return new ValuesCollection();
    }

    protected class KeySet
    extends AbstractObjectSortedSet<K> {
        protected KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return AbstractObject2ReferenceSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractObject2ReferenceSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2ReferenceSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractObject2ReferenceSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractObject2ReferenceSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractObject2ReferenceSortedMap.this.lastKey();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            return AbstractObject2ReferenceSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            return AbstractObject2ReferenceSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            return AbstractObject2ReferenceSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractObject2ReferenceSortedMap.this.object2ReferenceEntrySet().iterator(new AbstractObject2ReferenceMap.BasicEntry(from, null)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Object2ReferenceSortedMaps.fastIterator(AbstractObject2ReferenceSortedMap.this));
        }
    }

    protected class ValuesCollection
    extends AbstractReferenceCollection<V> {
        protected ValuesCollection() {
        }

        @Override
        public ObjectIterator<V> iterator() {
            return new ValuesIterator(Object2ReferenceSortedMaps.fastIterator(AbstractObject2ReferenceSortedMap.this));
        }

        @Override
        public boolean contains(Object k) {
            return AbstractObject2ReferenceSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractObject2ReferenceSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2ReferenceSortedMap.this.clear();
        }
    }

    protected static class ValuesIterator<K, V>
    implements ObjectIterator<V> {
        protected final ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> i) {
            this.i = i;
        }

        @Override
        public V next() {
            return ((Object2ReferenceMap.Entry)this.i.next()).getValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected static class KeySetIterator<K, V>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> i) {
            this.i = i;
        }

        @Override
        public K next() {
            return ((Object2ReferenceMap.Entry)this.i.next()).getKey();
        }

        @Override
        public K previous() {
            return ((Object2ReferenceMap.Entry)this.i.previous()).getKey();
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

