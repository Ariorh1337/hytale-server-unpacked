/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2BooleanMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanSortedMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanSortedMaps;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Comparator;

public abstract class AbstractObject2BooleanSortedMap<K>
extends AbstractObject2BooleanMap<K>
implements Object2BooleanSortedMap<K> {
    private static final long serialVersionUID = -1773560792952436569L;

    protected AbstractObject2BooleanSortedMap() {
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        return new KeySet();
    }

    @Override
    public BooleanCollection values() {
        return new ValuesCollection();
    }

    protected class KeySet
    extends AbstractObjectSortedSet<K> {
        protected KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return AbstractObject2BooleanSortedMap.this.containsKey(k);
        }

        @Override
        public int size() {
            return AbstractObject2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2BooleanSortedMap.this.clear();
        }

        @Override
        public Comparator<? super K> comparator() {
            return AbstractObject2BooleanSortedMap.this.comparator();
        }

        @Override
        public K first() {
            return AbstractObject2BooleanSortedMap.this.firstKey();
        }

        @Override
        public K last() {
            return AbstractObject2BooleanSortedMap.this.lastKey();
        }

        @Override
        public ObjectSortedSet<K> headSet(K to) {
            return AbstractObject2BooleanSortedMap.this.headMap(to).keySet();
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            return AbstractObject2BooleanSortedMap.this.tailMap(from).keySet();
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to) {
            return AbstractObject2BooleanSortedMap.this.subMap(from, to).keySet();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeySetIterator(AbstractObject2BooleanSortedMap.this.object2BooleanEntrySet().iterator(new AbstractObject2BooleanMap.BasicEntry(from, false)));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeySetIterator(Object2BooleanSortedMaps.fastIterator(AbstractObject2BooleanSortedMap.this));
        }
    }

    protected class ValuesCollection
    extends AbstractBooleanCollection {
        protected ValuesCollection() {
        }

        @Override
        public BooleanIterator iterator() {
            return new ValuesIterator(Object2BooleanSortedMaps.fastIterator(AbstractObject2BooleanSortedMap.this));
        }

        @Override
        public boolean contains(boolean k) {
            return AbstractObject2BooleanSortedMap.this.containsValue(k);
        }

        @Override
        public int size() {
            return AbstractObject2BooleanSortedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractObject2BooleanSortedMap.this.clear();
        }
    }

    protected static class ValuesIterator<K>
    implements BooleanIterator {
        protected final ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> i;

        public ValuesIterator(ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public boolean nextBoolean() {
            return ((Object2BooleanMap.Entry)this.i.next()).getBooleanValue();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }

    protected static class KeySetIterator<K>
    implements ObjectBidirectionalIterator<K> {
        protected final ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> i;

        public KeySetIterator(ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> i) {
            this.i = i;
        }

        @Override
        public K next() {
            return ((Object2BooleanMap.Entry)this.i.next()).getKey();
        }

        @Override
        public K previous() {
            return ((Object2BooleanMap.Entry)this.i.previous()).getKey();
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

