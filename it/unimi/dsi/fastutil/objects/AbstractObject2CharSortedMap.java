/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2CharMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import it.unimi.dsi.fastutil.objects.Object2CharSortedMap;
import it.unimi.dsi.fastutil.objects.Object2CharSortedMaps;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Comparator;

public abstract class AbstractObject2CharSortedMap<K>
extends AbstractObject2CharMap<K>
implements Object2CharSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractObject2CharSortedMap() {
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public CharCollection values() {
        return new ValuesCollection();
    }

    protected class KeySet
    extends AbstractObjectSortedSet<K> {
        protected KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return AbstractObject2CharSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractObject2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2CharSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractObject2CharSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractObject2CharSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractObject2CharSortedMap.this.lastKey();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            return AbstractObject2CharSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            return AbstractObject2CharSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            return AbstractObject2CharSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractObject2CharSortedMap.this.object2CharEntrySet().iterator(new AbstractObject2CharMap.BasicEntry(from, '\u0000')));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Object2CharSortedMaps.fastIterator(AbstractObject2CharSortedMap.this));
        }
    }

    protected class ValuesCollection
    extends AbstractCharCollection {
        protected ValuesCollection() {
        }

        @Override
        public CharIterator iterator() {
            return new ValuesIterator(Object2CharSortedMaps.fastIterator(AbstractObject2CharSortedMap.this));
        }

        @Override
        public boolean contains(char k) {
            return AbstractObject2CharSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractObject2CharSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2CharSortedMap.this.clear();
        }
    }

    protected static class ValuesIterator<K>
    implements CharIterator {
        protected final ObjectBidirectionalIterator<Object2CharMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Object2CharMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public char nextChar() {
            return ((Object2CharMap.Entry)this.i.next()).getCharValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Object2CharMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Object2CharMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public K next() {
            return ((Object2CharMap.Entry)this.i.next()).getKey();
        }

        @Override
        public K previous() {
            return ((Object2CharMap.Entry)this.i.previous()).getKey();
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

