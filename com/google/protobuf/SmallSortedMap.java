/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.FieldSet;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

class SmallSortedMap<K extends Comparable<K>, V>
extends AbstractMap<K, V> {
    static final int DEFAULT_FIELD_MAP_ARRAY_SIZE = 16;
    private Object[] entries;
    private int entriesSize;
    private Map<K, V> overflowEntries = Collections.emptyMap();
    private boolean isImmutable;
    private volatile EntrySet lazyEntrySet;
    private Map<K, V> overflowEntriesDescending = Collections.emptyMap();

    static <FieldDescriptorT extends FieldSet.FieldDescriptorLite<FieldDescriptorT>> SmallSortedMap<FieldDescriptorT, Object> newFieldMap() {
        return new SmallSortedMap<FieldDescriptorT, Object>(){

            @Override
            public void makeImmutable() {
                if (!this.isImmutable()) {
                    List value;
                    for (int i = 0; i < this.getNumArrayEntries(); ++i) {
                        Map.Entry entry = this.getArrayEntryAt(i);
                        if (!((FieldSet.FieldDescriptorLite)entry.getKey()).isRepeated()) continue;
                        value = (List)entry.getValue();
                        entry.setValue(Collections.unmodifiableList(value));
                    }
                    for (Map.Entry entry : this.getOverflowEntries()) {
                        if (!((FieldSet.FieldDescriptorLite)entry.getKey()).isRepeated()) continue;
                        value = (List)entry.getValue();
                        entry.setValue(Collections.unmodifiableList(value));
                    }
                }
                super.makeImmutable();
            }
        };
    }

    static <K extends Comparable<K>, V> SmallSortedMap<K, V> newInstanceForTest() {
        return new SmallSortedMap<K, V>();
    }

    private SmallSortedMap() {
    }

    public void makeImmutable() {
        if (!this.isImmutable) {
            this.overflowEntries = this.overflowEntries.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.overflowEntries);
            this.overflowEntriesDescending = this.overflowEntriesDescending.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.overflowEntriesDescending);
            this.isImmutable = true;
        }
    }

    public boolean isImmutable() {
        return this.isImmutable;
    }

    public int getNumArrayEntries() {
        return this.entriesSize;
    }

    public Map.Entry<K, V> getArrayEntryAt(int index) {
        if (index >= this.entriesSize) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        Entry e = (Entry)this.entries[index];
        return e;
    }

    public int getNumOverflowEntries() {
        return this.overflowEntries.size();
    }

    public Iterable<Map.Entry<K, V>> getOverflowEntries() {
        return this.overflowEntries.isEmpty() ? Collections.emptySet() : this.overflowEntries.entrySet();
    }

    @Override
    public int size() {
        return this.entriesSize + this.overflowEntries.size();
    }

    @Override
    public boolean containsKey(Object o) {
        Comparable key = (Comparable)o;
        return this.binarySearchInArray(key) >= 0 || this.overflowEntries.containsKey(key);
    }

    @Override
    public V get(Object o) {
        Comparable key = (Comparable)o;
        int index = this.binarySearchInArray(key);
        if (index >= 0) {
            Entry e = (Entry)this.entries[index];
            return e.getValue();
        }
        return this.overflowEntries.get(key);
    }

    @Override
    public V put(K key, V value) {
        this.checkMutable();
        int index = this.binarySearchInArray(key);
        if (index >= 0) {
            Entry e = (Entry)this.entries[index];
            return e.setValue(value);
        }
        this.ensureEntryArrayMutable();
        int insertionPoint = -(index + 1);
        if (insertionPoint >= 16) {
            return this.getOverflowEntriesMutable().put(key, value);
        }
        if (this.entriesSize == 16) {
            Entry lastEntryInArray = (Entry)this.entries[15];
            --this.entriesSize;
            this.getOverflowEntriesMutable().put(lastEntryInArray.getKey(), lastEntryInArray.getValue());
        }
        System.arraycopy(this.entries, insertionPoint, this.entries, insertionPoint + 1, this.entries.length - insertionPoint - 1);
        this.entries[insertionPoint] = new Entry(this, key, value);
        ++this.entriesSize;
        return null;
    }

    @Override
    public void clear() {
        this.checkMutable();
        if (this.entriesSize != 0) {
            this.entries = null;
            this.entriesSize = 0;
        }
        if (!this.overflowEntries.isEmpty()) {
            this.overflowEntries.clear();
        }
    }

    @Override
    public V remove(Object o) {
        this.checkMutable();
        Comparable key = (Comparable)o;
        int index = this.binarySearchInArray(key);
        if (index >= 0) {
            return this.removeArrayEntryAt(index);
        }
        if (this.overflowEntries.isEmpty()) {
            return null;
        }
        return this.overflowEntries.remove(key);
    }

    private V removeArrayEntryAt(int index) {
        this.checkMutable();
        Object removed = ((Entry)this.entries[index]).getValue();
        System.arraycopy(this.entries, index + 1, this.entries, index, this.entriesSize - index - 1);
        --this.entriesSize;
        if (!this.overflowEntries.isEmpty()) {
            Iterator<Map.Entry<K, V>> iterator = this.getOverflowEntriesMutable().entrySet().iterator();
            this.entries[this.entriesSize] = new Entry(this, iterator.next());
            ++this.entriesSize;
            iterator.remove();
        }
        return removed;
    }

    private int binarySearchInArray(K key) {
        int left = 0;
        int right = this.entriesSize - 1;
        if (right >= 0) {
            int cmp = key.compareTo((Object)((Entry)this.entries[right]).getKey());
            if (cmp > 0) {
                return -(right + 2);
            }
            if (cmp == 0) {
                return right;
            }
        }
        while (left <= right) {
            int mid = (left + right) / 2;
            int cmp = key.compareTo((Object)((Entry)this.entries[mid]).getKey());
            if (cmp < 0) {
                right = mid - 1;
                continue;
            }
            if (cmp > 0) {
                left = mid + 1;
                continue;
            }
            return mid;
        }
        return -(left + 1);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.lazyEntrySet == null) {
            this.lazyEntrySet = new EntrySet();
        }
        return this.lazyEntrySet;
    }

    Set<Map.Entry<K, V>> descendingEntrySet() {
        return new DescendingEntrySet();
    }

    private void checkMutable() {
        if (this.isImmutable) {
            throw new UnsupportedOperationException();
        }
    }

    private SortedMap<K, V> getOverflowEntriesMutable() {
        this.checkMutable();
        if (this.overflowEntries.isEmpty() && !(this.overflowEntries instanceof TreeMap)) {
            this.overflowEntries = new TreeMap();
            this.overflowEntriesDescending = ((TreeMap)this.overflowEntries).descendingMap();
        }
        return (SortedMap)this.overflowEntries;
    }

    private void ensureEntryArrayMutable() {
        this.checkMutable();
        if (this.entries == null) {
            this.entries = new Object[16];
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SmallSortedMap)) {
            return super.equals(o);
        }
        SmallSortedMap other = (SmallSortedMap)o;
        int size = this.size();
        if (size != other.size()) {
            return false;
        }
        int numArrayEntries = this.getNumArrayEntries();
        if (numArrayEntries != other.getNumArrayEntries()) {
            return this.entrySet().equals(other.entrySet());
        }
        for (int i = 0; i < numArrayEntries; ++i) {
            if (this.getArrayEntryAt(i).equals(other.getArrayEntryAt(i))) continue;
            return false;
        }
        if (numArrayEntries != size) {
            return this.overflowEntries.equals(other.overflowEntries);
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int listSize = this.getNumArrayEntries();
        for (int i = 0; i < listSize; ++i) {
            h += this.entries[i].hashCode();
        }
        if (this.getNumOverflowEntries() > 0) {
            h += this.overflowEntries.hashCode();
        }
        return h;
    }

    private static class Entry
    implements Map.Entry<K, V>,
    Comparable<Entry> {
        private final K key;
        private V value;
        final /* synthetic */ SmallSortedMap this$0;

        Entry(SmallSortedMap smallSortedMap, Map.Entry<K, V> copy) {
            this(smallSortedMap, (Comparable)copy.getKey(), copy.getValue());
        }

        Entry(K key, V value) {
            this.this$0 = var1_1;
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public int compareTo(Entry other) {
            return this.getKey().compareTo(other.getKey());
        }

        @Override
        public V setValue(V newValue) {
            this.this$0.checkMutable();
            Object oldValue = this.value;
            this.value = newValue;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry other = (Map.Entry)o;
            return this.equals(this.key, other.getKey()) && this.equals(this.value, other.getValue());
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "=" + this.value;
        }

        private boolean equals(Object o1, Object o2) {
            return o1 == null ? o2 == null : o1.equals(o2);
        }
    }

    private class EntrySet
    extends AbstractSet<Map.Entry<K, V>> {
        private EntrySet() {
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return SmallSortedMap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            Object value;
            Map.Entry entry = (Map.Entry)o;
            Object existing = SmallSortedMap.this.get(entry.getKey());
            return existing == (value = entry.getValue()) || existing != null && existing.equals(value);
        }

        @Override
        public boolean add(Map.Entry<K, V> entry) {
            if (!this.contains(entry)) {
                SmallSortedMap.this.put((Comparable)entry.getKey(), entry.getValue());
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            Map.Entry entry = (Map.Entry)o;
            if (this.contains(entry)) {
                SmallSortedMap.this.remove(entry.getKey());
                return true;
            }
            return false;
        }

        @Override
        public void clear() {
            SmallSortedMap.this.clear();
        }
    }

    private class DescendingEntrySet
    extends EntrySet {
        private DescendingEntrySet() {
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new DescendingEntryIterator();
        }
    }

    private class DescendingEntryIterator
    implements Iterator<Map.Entry<K, V>> {
        private int pos;
        private Iterator<Map.Entry<K, V>> lazyOverflowIterator;

        private DescendingEntryIterator() {
            this.pos = SmallSortedMap.this.entriesSize;
        }

        @Override
        public boolean hasNext() {
            return this.pos > 0 && this.pos <= SmallSortedMap.this.entriesSize || this.getOverflowIterator().hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            if (this.getOverflowIterator().hasNext()) {
                return this.getOverflowIterator().next();
            }
            Entry e = (Entry)SmallSortedMap.this.entries[--this.pos];
            return e;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private Iterator<Map.Entry<K, V>> getOverflowIterator() {
            if (this.lazyOverflowIterator == null) {
                this.lazyOverflowIterator = SmallSortedMap.this.overflowEntriesDescending.entrySet().iterator();
            }
            return this.lazyOverflowIterator;
        }
    }

    private class EntryIterator
    implements Iterator<Map.Entry<K, V>> {
        private int pos = -1;
        private boolean nextCalledBeforeRemove;
        private Iterator<Map.Entry<K, V>> lazyOverflowIterator;

        private EntryIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.pos + 1 < SmallSortedMap.this.entriesSize || !SmallSortedMap.this.overflowEntries.isEmpty() && this.getOverflowIterator().hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            this.nextCalledBeforeRemove = true;
            if (++this.pos < SmallSortedMap.this.entriesSize) {
                Entry e = (Entry)SmallSortedMap.this.entries[this.pos];
                return e;
            }
            return this.getOverflowIterator().next();
        }

        @Override
        public void remove() {
            if (!this.nextCalledBeforeRemove) {
                throw new IllegalStateException("remove() was called before next()");
            }
            this.nextCalledBeforeRemove = false;
            SmallSortedMap.this.checkMutable();
            if (this.pos < SmallSortedMap.this.entriesSize) {
                SmallSortedMap.this.removeArrayEntryAt(this.pos--);
            } else {
                this.getOverflowIterator().remove();
            }
        }

        private Iterator<Map.Entry<K, V>> getOverflowIterator() {
            if (this.lazyOverflowIterator == null) {
                this.lazyOverflowIterator = SmallSortedMap.this.overflowEntries.entrySet().iterator();
            }
            return this.lazyOverflowIterator;
        }
    }
}

