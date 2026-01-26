/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ReferenceArrayList$SubList.SubListSpliterator
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.AbstractReferenceList;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceCollections;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.stream.Collector;

public class ReferenceArrayList<K>
extends AbstractReferenceList<K>
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353131L;
    public static final int DEFAULT_INITIAL_CAPACITY = 10;
    protected final boolean wrapped;
    protected transient K[] a;
    protected int size;
    private static final Collector<Object, ?, ReferenceArrayList<Object>> TO_LIST_COLLECTOR = Collector.of(ReferenceArrayList::new, ReferenceArrayList::add, ReferenceArrayList::combine, new Collector.Characteristics[0]);

    private static final <K> K[] copyArraySafe(K[] a, int length) {
        if (length == 0) {
            return ObjectArrays.EMPTY_ARRAY;
        }
        return java.util.Arrays.copyOf(a, length, Object[].class);
    }

    private static final <K> K[] copyArrayFromSafe(ReferenceArrayList<K> l) {
        return ReferenceArrayList.copyArraySafe(l.a, l.size);
    }

    protected ReferenceArrayList(K[] a, boolean wrapped) {
        this.a = a;
        this.wrapped = wrapped;
    }

    private void initArrayFromCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = capacity == 0 ? ObjectArrays.EMPTY_ARRAY : new Object[capacity];
    }

    public ReferenceArrayList(int capacity) {
        this.initArrayFromCapacity(capacity);
        this.wrapped = false;
    }

    public ReferenceArrayList() {
        this.a = ObjectArrays.DEFAULT_EMPTY_ARRAY;
        this.wrapped = false;
    }

    public ReferenceArrayList(Collection<? extends K> c) {
        if (c instanceof ReferenceArrayList) {
            this.a = ReferenceArrayList.copyArrayFromSafe((ReferenceArrayList)c);
            this.size = this.a.length;
        } else {
            this.initArrayFromCapacity(c.size());
            if (c instanceof ReferenceList) {
                this.size = c.size();
                ((ReferenceList)c).getElements(0, this.a, 0, this.size);
            } else {
                this.size = ObjectIterators.unwrap(c.iterator(), this.a);
            }
        }
        this.wrapped = false;
    }

    public ReferenceArrayList(ReferenceCollection<? extends K> c) {
        if (c instanceof ReferenceArrayList) {
            this.a = ReferenceArrayList.copyArrayFromSafe((ReferenceArrayList)c);
            this.size = this.a.length;
        } else {
            this.initArrayFromCapacity(c.size());
            if (c instanceof ReferenceList) {
                this.size = c.size();
                ((ReferenceList)c).getElements(0, this.a, 0, this.size);
            } else {
                this.size = ObjectIterators.unwrap(c.iterator(), this.a);
            }
        }
        this.wrapped = false;
    }

    public ReferenceArrayList(ReferenceList<? extends K> l) {
        if (l instanceof ReferenceArrayList) {
            this.a = ReferenceArrayList.copyArrayFromSafe((ReferenceArrayList)l);
            this.size = this.a.length;
        } else {
            this.initArrayFromCapacity(l.size());
            this.size = l.size();
            l.getElements(0, this.a, 0, this.size);
        }
        this.wrapped = false;
    }

    public ReferenceArrayList(K[] a) {
        this(a, 0, a.length);
    }

    public ReferenceArrayList(K[] a, int offset, int length) {
        this(length);
        System.arraycopy(a, offset, this.a, 0, length);
        this.size = length;
    }

    public ReferenceArrayList(Iterator<? extends K> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public ReferenceArrayList(ObjectIterator<? extends K> i) {
        this();
        while (i.hasNext()) {
            this.add((K)i.next());
        }
    }

    public K[] elements() {
        return this.a;
    }

    public static <K> ReferenceArrayList<K> wrap(K[] a, int length) {
        if (length > a.length) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
        }
        ReferenceArrayList<K> l = new ReferenceArrayList<K>(a, true);
        l.size = length;
        return l;
    }

    public static <K> ReferenceArrayList<K> wrap(K[] a) {
        return ReferenceArrayList.wrap(a, a.length);
    }

    public static <K> ReferenceArrayList<K> of() {
        return new ReferenceArrayList<K>();
    }

    @SafeVarargs
    public static <K> ReferenceArrayList<K> of(K ... init) {
        return ReferenceArrayList.wrap(init);
    }

    ReferenceArrayList<K> combine(ReferenceArrayList<? extends K> toAddFrom) {
        this.addAll(toAddFrom);
        return this;
    }

    public static <K> Collector<K, ?, ReferenceArrayList<K>> toList() {
        return TO_LIST_COLLECTOR;
    }

    public static <K> Collector<K, ?, ReferenceArrayList<K>> toListWithExpectedSize(int expectedSize) {
        if (expectedSize <= 10) {
            return ReferenceArrayList.toList();
        }
        return Collector.of(new ReferenceCollections.SizeDecreasingSupplier(expectedSize, size -> size <= 10 ? new ReferenceArrayList() : new ReferenceArrayList(size)), ReferenceArrayList::add, ReferenceArrayList::combine, new Collector.Characteristics[0]);
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this.a.length || this.a == ObjectArrays.DEFAULT_EMPTY_ARRAY && capacity <= 10) {
            return;
        }
        if (this.wrapped) {
            this.a = ObjectArrays.ensureCapacity(this.a, capacity, this.size);
        } else if (capacity > this.a.length) {
            Object[] t = new Object[capacity];
            System.arraycopy(this.a, 0, t, 0, this.size);
            this.a = t;
        }
        assert (this.size <= this.a.length);
    }

    private void grow(int capacity) {
        if (capacity <= this.a.length) {
            return;
        }
        if (this.a != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
            capacity = (int)Math.max(Math.min((long)this.a.length + (long)(this.a.length >> 1), 0x7FFFFFF7L), (long)capacity);
        } else if (capacity < 10) {
            capacity = 10;
        }
        if (this.wrapped) {
            this.a = ObjectArrays.forceCapacity(this.a, capacity, this.size);
        } else {
            Object[] t = new Object[capacity];
            System.arraycopy(this.a, 0, t, 0, this.size);
            this.a = t;
        }
        assert (this.size <= this.a.length);
    }

    @Override
    public void add(int index, K k) {
        this.ensureIndex(index);
        this.grow(this.size + 1);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + 1, this.size - index);
        }
        this.a[index] = k;
        ++this.size;
        assert (this.size <= this.a.length);
    }

    @Override
    public boolean add(K k) {
        this.grow(this.size + 1);
        this.a[this.size++] = k;
        assert (this.size <= this.a.length);
        return true;
    }

    @Override
    public K get(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(Object k) {
        for (int i = 0; i < this.size; ++i) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object k) {
        int i = this.size;
        while (i-- != 0) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public K remove(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        K old = this.a[index];
        --this.size;
        if (index != this.size) {
            System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
        }
        this.a[this.size] = null;
        assert (this.size <= this.a.length);
        return old;
    }

    @Override
    public boolean remove(Object k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.remove(index);
        assert (this.size <= this.a.length);
        return true;
    }

    @Override
    public K set(int index, K k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        K old = this.a[index];
        this.a[index] = k;
        return old;
    }

    @Override
    public void clear() {
        java.util.Arrays.fill(this.a, 0, this.size, null);
        this.size = 0;
        assert (this.size <= this.a.length);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void size(int size) {
        if (size > this.a.length) {
            this.a = ObjectArrays.forceCapacity(this.a, size, this.size);
        }
        if (size > this.size) {
            java.util.Arrays.fill(this.a, this.size, size, null);
        } else {
            java.util.Arrays.fill(this.a, size, this.size, null);
        }
        this.size = size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public void trim() {
        this.trim(0);
    }

    public void trim(int n) {
        if (n >= this.a.length || this.size == this.a.length) {
            return;
        }
        Object[] t = new Object[Math.max(n, this.size)];
        System.arraycopy(this.a, 0, t, 0, this.size);
        this.a = t;
        assert (this.size <= this.a.length);
    }

    @Override
    public ReferenceList<K> subList(int from, int to) {
        if (from == 0 && to == this.size()) {
            return this;
        }
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return new SubList(from, to);
    }

    @Override
    public void getElements(int from, Object[] a, int offset, int length) {
        ObjectArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(int from, int to) {
        Arrays.ensureFromTo(this.size, from, to);
        System.arraycopy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
        int i = to - from;
        while (i-- != 0) {
            this.a[this.size + i] = null;
        }
    }

    @Override
    public void addElements(int index, K[] a, int offset, int length) {
        this.ensureIndex(index);
        ObjectArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        System.arraycopy(this.a, index, this.a, index + length, this.size - index);
        System.arraycopy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public void setElements(int index, K[] a, int offset, int length) {
        this.ensureIndex(index);
        ObjectArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size + ")");
        }
        System.arraycopy(a, offset, this.a, index, length);
    }

    @Override
    public void forEach(Consumer<? super K> action) {
        for (int i = 0; i < this.size; ++i) {
            action.accept(this.a[i]);
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends K> c) {
        if (c instanceof ReferenceList) {
            return this.addAll(index, (ReferenceList)c);
        }
        this.ensureIndex(index);
        int n = c.size();
        if (n == 0) {
            return false;
        }
        this.grow(this.size + n);
        System.arraycopy(this.a, index, this.a, index + n, this.size - index);
        Iterator<K> i = c.iterator();
        this.size += n;
        while (n-- != 0) {
            this.a[index++] = i.next();
        }
        assert (this.size <= this.a.length);
        return true;
    }

    @Override
    public boolean addAll(int index, ReferenceList<? extends K> l) {
        this.ensureIndex(index);
        int n = l.size();
        if (n == 0) {
            return false;
        }
        this.grow(this.size + n);
        System.arraycopy(this.a, index, this.a, index + n, this.size - index);
        l.getElements(0, this.a, index, n);
        this.size += n;
        assert (this.size <= this.a.length);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Object[] a = this.a;
        int j = 0;
        for (int i = 0; i < this.size; ++i) {
            if (c.contains(a[i])) continue;
            a[j++] = a[i];
        }
        java.util.Arrays.fill(a, j, this.size, null);
        boolean modified = this.size != j;
        this.size = j;
        return modified;
    }

    @Override
    public Object[] toArray() {
        int size = this.size();
        if (size == 0) {
            return ObjectArrays.EMPTY_ARRAY;
        }
        return java.util.Arrays.copyOf(this.a, size, Object[].class);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a == null) {
            a = new Object[this.size()];
        } else if (a.length < this.size()) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), this.size());
        }
        System.arraycopy(this.a, 0, a, 0, this.size());
        if (a.length > this.size()) {
            a[this.size()] = null;
        }
        return a;
    }

    @Override
    public ObjectListIterator<K> listIterator(final int index) {
        this.ensureIndex(index);
        return new ObjectListIterator<K>(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < ReferenceArrayList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public K next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return ReferenceArrayList.this.a[this.last];
            }

            @Override
            public K previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return ReferenceArrayList.this.a[this.pos];
            }

            @Override
            public int nextIndex() {
                return this.pos;
            }

            @Override
            public int previousIndex() {
                return this.pos - 1;
            }

            @Override
            public void add(K k) {
                ReferenceArrayList.this.add(this.pos++, k);
                this.last = -1;
            }

            @Override
            public void set(K k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                ReferenceArrayList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                ReferenceArrayList.this.remove(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
                while (this.pos < ReferenceArrayList.this.size) {
                    ++this.pos;
                    this.last = this.last;
                    action.accept(ReferenceArrayList.this.a[this.last]);
                }
            }

            @Override
            public int back(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = ReferenceArrayList.this.size - this.pos;
                if (n < remaining) {
                    this.pos -= n;
                } else {
                    n = remaining;
                    this.pos = 0;
                }
                this.last = this.pos;
                return n;
            }

            @Override
            public int skip(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = ReferenceArrayList.this.size - this.pos;
                if (n < remaining) {
                    this.pos += n;
                } else {
                    n = remaining;
                    this.pos = ReferenceArrayList.this.size;
                }
                this.last = this.pos - 1;
                return n;
            }
        };
    }

    @Override
    public ObjectSpliterator<K> spliterator() {
        return new Spliterator();
    }

    @Override
    public void sort(Comparator<? super K> comp) {
        if (comp == null) {
            ObjectArrays.stableSort(this.a, 0, this.size);
        } else {
            ObjectArrays.stableSort(this.a, 0, this.size, comp);
        }
    }

    @Override
    public void unstableSort(Comparator<? super K> comp) {
        if (comp == null) {
            ObjectArrays.unstableSort(this.a, 0, this.size);
        } else {
            ObjectArrays.unstableSort(this.a, 0, this.size, comp);
        }
    }

    public ReferenceArrayList<K> clone() {
        ReferenceArrayList<K> cloned = null;
        if (this.getClass() == ReferenceArrayList.class) {
            cloned = new ReferenceArrayList<K>(ReferenceArrayList.copyArraySafe(this.a, this.size), false);
            cloned.size = this.size;
        } else {
            try {
                cloned = (ReferenceArrayList<K>)super.clone();
            }
            catch (CloneNotSupportedException err) {
                throw new InternalError(err);
            }
            cloned.a = ReferenceArrayList.copyArraySafe(this.a, this.size);
        }
        return cloned;
    }

    public boolean equals(ReferenceArrayList<K> l) {
        if (l == this) {
            return true;
        }
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        K[] a1 = this.a;
        K[] a2 = l.a;
        if (a1 == a2 && s == l.size()) {
            return true;
        }
        while (s-- != 0) {
            if (a1[s] == a2[s]) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof List)) {
            return false;
        }
        if (o instanceof ReferenceArrayList) {
            return this.equals((ReferenceArrayList)o);
        }
        if (o instanceof SubList) {
            return ((SubList)o).equals(this);
        }
        return super.equals(o);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeObject(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new Object[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readObject();
        }
    }

    private class SubList
    extends AbstractReferenceList.ReferenceRandomAccessSubList<K> {
        private static final long serialVersionUID = -3185226345314976296L;

        protected SubList(int from, int to) {
            super(ReferenceArrayList.this, from, to);
        }

        private K[] getParentArray() {
            return ReferenceArrayList.this.a;
        }

        @Override
        public K get(int i) {
            this.ensureRestrictedIndex(i);
            return ReferenceArrayList.this.a[i + this.from];
        }

        @Override
        public ObjectListIterator<K> listIterator(int index) {
            return new SubListIterator(index);
        }

        @Override
        public ObjectSpliterator<K> spliterator() {
            return new SubListSpliterator();
        }

        boolean contentsEquals(K[] otherA, int otherAFrom, int otherATo) {
            if (ReferenceArrayList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return true;
            }
            if (otherATo - otherAFrom != this.size()) {
                return false;
            }
            int pos = this.from;
            int otherPos = otherAFrom;
            while (pos < this.to) {
                if (ReferenceArrayList.this.a[pos++] == otherA[otherPos++]) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (!(o instanceof List)) {
                return false;
            }
            if (o instanceof ReferenceArrayList) {
                ReferenceArrayList other = (ReferenceArrayList)o;
                return this.contentsEquals(other.a, 0, other.size());
            }
            if (o instanceof SubList) {
                SubList other = (SubList)o;
                return this.contentsEquals(other.getParentArray(), other.from, other.to);
            }
            return super.equals(o);
        }

        private final class SubListIterator
        extends ObjectIterators.AbstractIndexBasedListIterator<K> {
            SubListIterator(int index) {
                super(0, index);
            }

            @Override
            protected final K get(int i) {
                return ReferenceArrayList.this.a[SubList.this.from + i];
            }

            @Override
            protected final void add(int i, K k) {
                SubList.this.add(i, k);
            }

            @Override
            protected final void set(int i, K k) {
                SubList.this.set(i, k);
            }

            @Override
            protected final void remove(int i) {
                SubList.this.remove(i);
            }

            @Override
            protected final int getMaxPos() {
                return SubList.this.to - SubList.this.from;
            }

            @Override
            public K next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = this.pos++;
                return ReferenceArrayList.this.a[SubList.this.from + this.lastReturned];
            }

            @Override
            public K previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = --this.pos;
                return ReferenceArrayList.this.a[SubList.this.from + this.pos];
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
                int max = SubList.this.to - SubList.this.from;
                while (this.pos < max) {
                    ++this.pos;
                    this.lastReturned = this.lastReturned;
                    action.accept(ReferenceArrayList.this.a[SubList.this.from + this.lastReturned]);
                }
            }
        }

        private final class SubListSpliterator
        extends ObjectSpliterators.LateBindingSizeIndexBasedSpliterator<K> {
            SubListSpliterator() {
                super(SubList.this.from);
            }

            private SubListSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            protected final int getMaxPosFromBackingStore() {
                return SubList.this.to;
            }

            @Override
            protected final K get(int i) {
                return ReferenceArrayList.this.a[i];
            }

            protected final it.unimi.dsi.fastutil.objects.ReferenceArrayList$SubList.SubListSpliterator makeForSplit(int pos, int maxPos) {
                return new SubListSpliterator(pos, maxPos);
            }

            @Override
            public boolean tryAdvance(Consumer<? super K> action) {
                if (this.pos >= this.getMaxPos()) {
                    return false;
                }
                action.accept(ReferenceArrayList.this.a[this.pos++]);
                return true;
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
                int max = this.getMaxPos();
                while (this.pos < max) {
                    action.accept(ReferenceArrayList.this.a[this.pos++]);
                }
            }
        }
    }

    private final class Spliterator
    implements ObjectSpliterator<K> {
        boolean hasSplit = false;
        int pos;
        int max;

        public Spliterator() {
            this(0, referenceArrayList.size, false);
        }

        private Spliterator(int pos, int max, boolean hasSplit) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
            this.hasSplit = hasSplit;
        }

        private int getWorkingMax() {
            return this.hasSplit ? this.max : ReferenceArrayList.this.size;
        }

        @Override
        public int characteristics() {
            return 16464;
        }

        @Override
        public long estimateSize() {
            return this.getWorkingMax() - this.pos;
        }

        @Override
        public boolean tryAdvance(Consumer<? super K> action) {
            if (this.pos >= this.getWorkingMax()) {
                return false;
            }
            action.accept(ReferenceArrayList.this.a[this.pos++]);
            return true;
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
            int max = this.getWorkingMax();
            while (this.pos < max) {
                action.accept(ReferenceArrayList.this.a[this.pos]);
                ++this.pos;
            }
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            int max = this.getWorkingMax();
            if (this.pos >= max) {
                return 0L;
            }
            int remaining = max - this.pos;
            if (n < (long)remaining) {
                this.pos = SafeMath.safeLongToInt((long)this.pos + n);
                return n;
            }
            n = remaining;
            this.pos = max;
            return n;
        }

        @Override
        public ObjectSpliterator<K> trySplit() {
            int myNewPos;
            int max = this.getWorkingMax();
            int retLen = max - this.pos >> 1;
            if (retLen <= 1) {
                return null;
            }
            this.max = max;
            int retMax = myNewPos = this.pos + retLen;
            int oldPos = this.pos;
            this.pos = myNewPos;
            this.hasSplit = true;
            return new Spliterator(oldPos, retMax, true);
        }
    }
}

