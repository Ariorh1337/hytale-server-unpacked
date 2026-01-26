/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.AbstractReference2ByteMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteSortedMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteSortedMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Comparator;

public abstract class AbstractReference2ByteSortedMap<K>
extends AbstractReference2ByteMap<K>
implements Reference2ByteSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractReference2ByteSortedMap() {
    }

    @Override
    public ReferenceSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public ByteCollection values() {
        return new ValuesCollection();
    }

    protected class KeySet
    extends AbstractReferenceSortedSet<K> {
        protected KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return AbstractReference2ByteSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractReference2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2ByteSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractReference2ByteSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractReference2ByteSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractReference2ByteSortedMap.this.lastKey();
        }

        @Override
        public ReferenceSortedSet<K> headSet(K to) {
            return AbstractReference2ByteSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K from) {
            return AbstractReference2ByteSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            return AbstractReference2ByteSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractReference2ByteSortedMap.this.reference2ByteEntrySet().iterator(new AbstractReference2ByteMap.BasicEntry(from, 0)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Reference2ByteSortedMaps.fastIterator(AbstractReference2ByteSortedMap.this));
        }
    }

    protected class ValuesCollection
    extends AbstractByteCollection {
        protected ValuesCollection() {
        }

        @Override
        public ByteIterator iterator() {
            return new ValuesIterator(Reference2ByteSortedMaps.fastIterator(AbstractReference2ByteSortedMap.this));
        }

        @Override
        public boolean contains(byte k) {
            return AbstractReference2ByteSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractReference2ByteSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractReference2ByteSortedMap.this.clear();
        }
    }

    protected static class ValuesIterator<K>
    implements ByteIterator {
        protected final ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public byte nextByte() {
            return ((Reference2ByteMap.Entry)this.i.next()).getByteValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public K next() {
            return ((Reference2ByteMap.Entry)this.i.next()).getKey();
        }

        @Override
        public K previous() {
            return ((Reference2ByteMap.Entry)this.i.previous()).getKey();
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

