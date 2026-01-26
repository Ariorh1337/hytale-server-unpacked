/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ReferenceBigArrayBigList$SubList.SubListSpliterator
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractReferenceBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrays;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterator;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterators;
import it.unimi.dsi.fastutil.objects.ObjectBigSpliterators;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ReferenceBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ReferenceBigList;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.stream.Collector;

public class ReferenceBigArrayBigList<K>
extends AbstractReferenceBigList<K>
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353131L;
    public static final int DEFAULT_INITIAL_CAPACITY = 10;
    protected final boolean wrapped;
    protected transient K[][] a;
    protected long size;
    private static final Collector<Object, ?, ReferenceBigArrayBigList<Object>> TO_LIST_COLLECTOR = Collector.of(ReferenceBigArrayBigList::new, ReferenceBigArrayBigList::add, ReferenceBigArrayBigList::combine, new Collector.Characteristics[0]);

    protected ReferenceBigArrayBigList(K[][] a, boolean dummy) {
        this.a = a;
        this.wrapped = true;
    }

    public ReferenceBigArrayBigList(long capacity) {
        if (capacity < 0L) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = capacity == 0L ? ObjectBigArrays.EMPTY_BIG_ARRAY : ObjectBigArrays.newBigArray(capacity);
        this.wrapped = false;
    }

    public ReferenceBigArrayBigList() {
        this.a = ObjectBigArrays.DEFAULT_EMPTY_BIG_ARRAY;
        this.wrapped = false;
    }

    public ReferenceBigArrayBigList(ReferenceCollection<? extends K> c) {
        this(Size64.sizeOf(c));
        if (c instanceof ReferenceBigList) {
            this.size = Size64.sizeOf(c);
            ((ReferenceBigList)c).getElements(0L, this.a, 0L, this.size);
        } else {
            Iterator i = c.iterator();
            while (i.hasNext()) {
                this.add((K)i.next());
            }
        }
    }

    public ReferenceBigArrayBigList(Collection<? extends K> c) {
        this(Size64.sizeOf(c));
        if (c instanceof ReferenceBigList) {
            this.size = Size64.sizeOf(c);
            ((ReferenceBigList)c).getElements(0L, this.a, 0L, this.size);
        } else {
            Iterator<K> i = c.iterator();
            while (i.hasNext()) {
                this.add(i.next());
            }
        }
    }

    public ReferenceBigArrayBigList(ReferenceBigList<? extends K> l) {
        this(l.size64());
        this.size = l.size64();
        l.getElements(0L, this.a, 0L, this.size);
    }

    public ReferenceBigArrayBigList(K[][] a) {
        this(a, 0L, BigArrays.length(a));
    }

    public ReferenceBigArrayBigList(K[][] a, long offset, long length) {
        this(length);
        BigArrays.copy(a, offset, this.a, 0L, length);
        this.size = length;
    }

    public ReferenceBigArrayBigList(Iterator<? extends K> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public ReferenceBigArrayBigList(ObjectIterator<? extends K> i) {
        this();
        while (i.hasNext()) {
            this.add((K)i.next());
        }
    }

    public K[][] elements() {
        return this.a;
    }

    public static <K> ReferenceBigArrayBigList<K> wrap(K[][] a, long length) {
        if (length > BigArrays.length(a)) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + BigArrays.length(a) + ")");
        }
        ReferenceBigArrayBigList<K> l = new ReferenceBigArrayBigList<K>(a, false);
        l.size = length;
        return l;
    }

    public static <K> ReferenceBigArrayBigList<K> wrap(K[][] a) {
        return ReferenceBigArrayBigList.wrap(a, BigArrays.length(a));
    }

    public static <K> ReferenceBigArrayBigList<K> of() {
        return new ReferenceBigArrayBigList<K>();
    }

    @SafeVarargs
    public static <K> ReferenceBigArrayBigList<K> of(K ... init) {
        return ReferenceBigArrayBigList.wrap(BigArrays.wrap(init));
    }

    private ReferenceBigArrayBigList<K> combine(ReferenceBigArrayBigList<? extends K> toAddFrom) {
        this.addAll(toAddFrom);
        return this;
    }

    public static <K> Collector<K, ?, ReferenceBigArrayBigList<K>> toBigList() {
        return TO_LIST_COLLECTOR;
    }

    public static <K> Collector<K, ?, ReferenceBigArrayBigList<K>> toBigListWithExpectedSize(long expectedSize) {
        return Collector.of(() -> new ReferenceBigArrayBigList(expectedSize), ReferenceBigArrayBigList::add, ReferenceBigArrayBigList::combine, new Collector.Characteristics[0]);
    }

    public void ensureCapacity(long capacity) {
        if (capacity <= BigArrays.length(this.a) || this.a == ObjectBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            return;
        }
        if (this.wrapped) {
            this.a = BigArrays.forceCapacity(this.a, capacity, this.size);
        } else if (capacity > BigArrays.length(this.a)) {
            Object[][] t = ObjectBigArrays.newBigArray(capacity);
            BigArrays.copy(this.a, 0L, t, 0L, this.size);
            this.a = t;
        }
        assert (this.size <= BigArrays.length(this.a));
    }

    private void grow(long capacity) {
        long oldLength = BigArrays.length(this.a);
        if (capacity <= oldLength) {
            return;
        }
        if (this.a != ObjectBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            capacity = Math.max(oldLength + (oldLength >> 1), capacity);
        } else if (capacity < 10L) {
            capacity = 10L;
        }
        if (this.wrapped) {
            this.a = BigArrays.forceCapacity(this.a, capacity, this.size);
        } else {
            Object[][] t = ObjectBigArrays.newBigArray(capacity);
            BigArrays.copy(this.a, 0L, t, 0L, this.size);
            this.a = t;
        }
        assert (this.size <= BigArrays.length(this.a));
    }

    @Override
    public void add(long index, K k) {
        this.ensureIndex(index);
        this.grow(this.size + 1L);
        if (index != this.size) {
            BigArrays.copy(this.a, index, this.a, index + 1L, this.size - index);
        }
        BigArrays.set(this.a, index, k);
        ++this.size;
        assert (this.size <= BigArrays.length(this.a));
    }

    @Override
    public boolean add(K k) {
        this.grow(this.size + 1L);
        BigArrays.set(this.a, this.size++, k);
        assert (this.size <= BigArrays.length(this.a));
        return true;
    }

    @Override
    public K get(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return BigArrays.get(this.a, index);
    }

    @Override
    public long indexOf(Object k) {
        for (long i = 0L; i < this.size; ++i) {
            if (k != BigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(Object k) {
        long i = this.size;
        while (i-- != 0L) {
            if (k != BigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public K remove(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        K old = BigArrays.get(this.a, index);
        --this.size;
        if (index != this.size) {
            BigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
        }
        BigArrays.set(this.a, this.size, null);
        assert (this.size <= BigArrays.length(this.a));
        return old;
    }

    @Override
    public boolean remove(Object k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.remove(index);
        assert (this.size <= BigArrays.length(this.a));
        return true;
    }

    @Override
    public K set(long index, K k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        K old = BigArrays.get(this.a, index);
        BigArrays.set(this.a, index, k);
        return old;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        K[] s = null;
        K[] d = null;
        int ss = -1;
        int sd = 0x8000000;
        int ds = -1;
        int dd = 0x8000000;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 0x8000000) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains(s[sd])) {
                if (dd == 0x8000000) {
                    d = this.a[++ds];
                    dd = 0;
                }
                d[dd++] = s[sd];
            }
            ++sd;
        }
        long j = BigArrays.index(ds, dd);
        BigArrays.fill(this.a, j, this.size, null);
        boolean modified = this.size != j;
        this.size = j;
        return modified;
    }

    @Override
    public boolean addAll(long index, Collection<? extends K> c) {
        if (c instanceof ReferenceList) {
            return this.addAll(index, (ReferenceList)c);
        }
        if (c instanceof ReferenceBigList) {
            return this.addAll(index, (ReferenceBigList)c);
        }
        this.ensureIndex(index);
        int n = c.size();
        if (n == 0) {
            return false;
        }
        this.grow(this.size + (long)n);
        BigArrays.copy(this.a, index, this.a, index + (long)n, this.size - index);
        Iterator<K> i = c.iterator();
        this.size += (long)n;
        assert (this.size <= BigArrays.length(this.a));
        while (n-- != 0) {
            BigArrays.set(this.a, index++, i.next());
        }
        return true;
    }

    @Override
    public boolean addAll(long index, ReferenceBigList<? extends K> list) {
        this.ensureIndex(index);
        long n = list.size64();
        if (n == 0L) {
            return false;
        }
        this.grow(this.size + n);
        BigArrays.copy(this.a, index, this.a, index + n, this.size - index);
        list.getElements(0L, this.a, index, n);
        this.size += n;
        assert (this.size <= BigArrays.length(this.a));
        return true;
    }

    @Override
    public boolean addAll(long index, ReferenceList<? extends K> list) {
        int l;
        int n;
        this.ensureIndex(index);
        if (n == 0) {
            return false;
        }
        this.grow(this.size + (long)n);
        BigArrays.copy(this.a, index, this.a, index + (long)n, this.size - index);
        this.size += (long)n;
        assert (this.size <= BigArrays.length(this.a));
        int segment = BigArrays.segment(index);
        int displ = BigArrays.displacement(index);
        int pos = 0;
        for (n = list.size(); n > 0; n -= l) {
            l = Math.min(this.a[segment].length - displ, n);
            list.getElements(pos, this.a[segment], displ, l);
            if ((displ += l) == 0x8000000) {
                displ = 0;
                ++segment;
            }
            pos += l;
        }
        return true;
    }

    @Override
    public void clear() {
        BigArrays.fill(this.a, 0L, this.size, null);
        this.size = 0L;
        assert (this.size <= BigArrays.length(this.a));
    }

    @Override
    public long size64() {
        return this.size;
    }

    @Override
    public void size(long size) {
        if (size > BigArrays.length(this.a)) {
            this.a = BigArrays.forceCapacity(this.a, size, this.size);
        }
        if (size > this.size) {
            BigArrays.fill(this.a, this.size, size, null);
        } else {
            BigArrays.fill(this.a, size, this.size, null);
        }
        this.size = size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0L;
    }

    public void trim() {
        this.trim(0L);
    }

    public void trim(long n) {
        long arrayLength = BigArrays.length(this.a);
        if (n >= arrayLength || this.size == arrayLength) {
            return;
        }
        this.a = BigArrays.trim(this.a, Math.max(n, this.size));
        assert (this.size <= BigArrays.length(this.a));
    }

    @Override
    public ReferenceBigList<K> subList(long from, long to) {
        if (from == 0L && to == this.size64()) {
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
    public void getElements(long from, Object[][] a, long offset, long length) {
        BigArrays.copy(this.a, from, a, offset, length);
    }

    @Override
    public void getElements(long from, Object[] a, int offset, int length) {
        BigArrays.copyFromBig(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(long from, long to) {
        BigArrays.ensureFromTo(this.size, from, to);
        BigArrays.copy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
        BigArrays.fill(this.a, this.size, this.size + to - from, null);
    }

    @Override
    public void addElements(long index, K[][] a, long offset, long length) {
        this.ensureIndex(index);
        BigArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        BigArrays.copy(this.a, index, this.a, index + length, this.size - index);
        BigArrays.copy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public void setElements(long index, Object[][] a, long offset, long length) {
        BigArrays.copy(a, offset, this.a, index, length);
    }

    @Override
    public void forEach(Consumer<? super K> action) {
        for (long i = 0L; i < this.size; ++i) {
            action.accept(BigArrays.get(this.a, i));
        }
    }

    @Override
    public ObjectBigListIterator<K> listIterator(final long index) {
        this.ensureIndex(index);
        return new ObjectBigListIterator<K>(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < ReferenceBigArrayBigList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public K next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return BigArrays.get(ReferenceBigArrayBigList.this.a, this.last);
            }

            @Override
            public K previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return BigArrays.get(ReferenceBigArrayBigList.this.a, this.pos);
            }

            @Override
            public long nextIndex() {
                return this.pos;
            }

            @Override
            public long previousIndex() {
                return this.pos - 1L;
            }

            @Override
            public void add(K k) {
                ReferenceBigArrayBigList.this.add(this.pos++, k);
                this.last = -1L;
            }

            @Override
            public void set(K k) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                ReferenceBigArrayBigList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                ReferenceBigArrayBigList.this.remove(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
                while (this.pos < ReferenceBigArrayBigList.this.size) {
                    ++this.pos;
                    this.last = this.last;
                    action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, this.last));
                }
            }

            @Override
            public long back(long n) {
                if (n < 0L) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                long remaining = ReferenceBigArrayBigList.this.size - this.pos;
                if (n < remaining) {
                    this.pos -= n;
                } else {
                    n = remaining;
                    this.pos = 0L;
                }
                this.last = this.pos;
                return n;
            }

            @Override
            public long skip(long n) {
                if (n < 0L) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                long remaining = ReferenceBigArrayBigList.this.size - this.pos;
                if (n < remaining) {
                    this.pos += n;
                } else {
                    n = remaining;
                    this.pos = ReferenceBigArrayBigList.this.size;
                }
                this.last = this.pos - 1L;
                return n;
            }
        };
    }

    @Override
    public ObjectSpliterator<K> spliterator() {
        return new Spliterator();
    }

    public ReferenceBigArrayBigList<K> clone() {
        ReferenceBigArrayBigList<K> c;
        if (this.getClass() == ReferenceBigArrayBigList.class) {
            c = new ReferenceBigArrayBigList<K>(this.size);
            c.size = this.size;
        } else {
            try {
                c = (ReferenceBigArrayBigList<K>)super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new InternalError(e);
            }
            c.a = ObjectBigArrays.newBigArray(this.size);
        }
        BigArrays.copy(this.a, 0L, c.a, 0L, this.size);
        return c;
    }

    public boolean equals(ReferenceBigArrayBigList<K> l) {
        if (l == this) {
            return true;
        }
        long s = this.size64();
        if (s != l.size64()) {
            return false;
        }
        K[][] a1 = this.a;
        K[][] a2 = l.a;
        if (a1 == a2) {
            return true;
        }
        while (s-- != 0L) {
            if (BigArrays.get(a1, s) == BigArrays.get(a2, s)) continue;
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
        if (!(o instanceof BigList)) {
            return false;
        }
        if (o instanceof ReferenceBigArrayBigList) {
            return this.equals((ReferenceBigArrayBigList)o);
        }
        if (o instanceof SubList) {
            return ((SubList)o).equals(this);
        }
        return super.equals(o);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        int i = 0;
        while ((long)i < this.size) {
            s.writeObject(BigArrays.get(this.a, (long)i));
            ++i;
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = ObjectBigArrays.newBigArray(this.size);
        int i = 0;
        while ((long)i < this.size) {
            BigArrays.set(this.a, (long)i, s.readObject());
            ++i;
        }
    }

    private class SubList
    extends AbstractReferenceBigList.ReferenceRandomAccessSubList<K> {
        private static final long serialVersionUID = -3185226345314976296L;

        protected SubList(long from, long to) {
            super(ReferenceBigArrayBigList.this, from, to);
        }

        private K[][] getParentArray() {
            return ReferenceBigArrayBigList.this.a;
        }

        @Override
        public K get(long i) {
            this.ensureRestrictedIndex(i);
            return BigArrays.get(ReferenceBigArrayBigList.this.a, i + this.from);
        }

        @Override
        public ObjectBigListIterator<K> listIterator(long index) {
            return new SubListIterator(index);
        }

        @Override
        public ObjectSpliterator<K> spliterator() {
            return new SubListSpliterator();
        }

        boolean contentsEquals(K[][] otherA, long otherAFrom, long otherATo) {
            if (ReferenceBigArrayBigList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return true;
            }
            if (otherATo - otherAFrom != this.size64()) {
                return false;
            }
            long pos = this.to;
            long otherPos = otherATo;
            while (--pos >= this.from) {
                if (BigArrays.get(ReferenceBigArrayBigList.this.a, pos) == BigArrays.get(otherA, --otherPos)) continue;
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
            if (!(o instanceof BigList)) {
                return false;
            }
            if (o instanceof ReferenceBigArrayBigList) {
                ReferenceBigArrayBigList other = (ReferenceBigArrayBigList)o;
                return this.contentsEquals(other.a, 0L, other.size64());
            }
            if (o instanceof SubList) {
                SubList other = (SubList)o;
                return this.contentsEquals(other.getParentArray(), other.from, other.to);
            }
            return super.equals(o);
        }

        private final class SubListIterator
        extends ObjectBigListIterators.AbstractIndexBasedBigListIterator<K> {
            SubListIterator(long index) {
                super(0L, index);
            }

            @Override
            protected final K get(long i) {
                return BigArrays.get(ReferenceBigArrayBigList.this.a, SubList.this.from + i);
            }

            @Override
            protected final void add(long i, K k) {
                SubList.this.add(i, k);
            }

            @Override
            protected final void set(long i, K k) {
                SubList.this.set(i, k);
            }

            @Override
            protected final void remove(long i) {
                SubList.this.remove(i);
            }

            @Override
            protected final long getMaxPos() {
                return SubList.this.to - SubList.this.from;
            }

            @Override
            public K next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = this.pos++;
                return BigArrays.get(ReferenceBigArrayBigList.this.a, SubList.this.from + this.lastReturned);
            }

            @Override
            public K previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = --this.pos;
                return BigArrays.get(ReferenceBigArrayBigList.this.a, SubList.this.from + this.pos);
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
                long max = SubList.this.to - SubList.this.from;
                while (this.pos < max) {
                    ++this.pos;
                    this.lastReturned = this.lastReturned;
                    action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, SubList.this.from + this.lastReturned));
                }
            }
        }

        private final class SubListSpliterator
        extends ObjectBigSpliterators.LateBindingSizeIndexBasedSpliterator<K> {
            SubListSpliterator() {
                super(SubList.this.from);
            }

            private SubListSpliterator(long pos, long maxPos) {
                super(pos, maxPos);
            }

            @Override
            protected final long getMaxPosFromBackingStore() {
                return SubList.this.to;
            }

            @Override
            protected final K get(long i) {
                return BigArrays.get(ReferenceBigArrayBigList.this.a, i);
            }

            protected final it.unimi.dsi.fastutil.objects.ReferenceBigArrayBigList$SubList.SubListSpliterator makeForSplit(long pos, long maxPos) {
                return new SubListSpliterator(pos, maxPos);
            }

            @Override
            protected final long computeSplitPoint() {
                long defaultSplit = super.computeSplitPoint();
                return BigArrays.nearestSegmentStart(defaultSplit, this.pos + 1L, this.getMaxPos() - 1L);
            }

            @Override
            public boolean tryAdvance(Consumer<? super K> action) {
                if (this.pos >= this.getMaxPos()) {
                    return false;
                }
                action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, this.pos++));
                return true;
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
                long max = this.getMaxPos();
                while (this.pos < max) {
                    action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, this.pos++));
                }
            }
        }
    }

    private final class Spliterator
    implements ObjectSpliterator<K> {
        boolean hasSplit = false;
        long pos;
        long max;

        public Spliterator() {
            this(0L, referenceBigArrayBigList.size, false);
        }

        private Spliterator(long pos, long max, boolean hasSplit) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
            this.hasSplit = hasSplit;
        }

        private long getWorkingMax() {
            return this.hasSplit ? this.max : ReferenceBigArrayBigList.this.size;
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
            action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, this.pos++));
            return true;
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
            long max = this.getWorkingMax();
            while (this.pos < max) {
                action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, this.pos));
                ++this.pos;
            }
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            long max = this.getWorkingMax();
            if (this.pos >= max) {
                return 0L;
            }
            long remaining = max - this.pos;
            if (n < remaining) {
                this.pos += n;
                return n;
            }
            n = remaining;
            this.pos = max;
            return n;
        }

        @Override
        public ObjectSpliterator<K> trySplit() {
            long max = this.getWorkingMax();
            long retLen = max - this.pos >> 1;
            if (retLen <= 1L) {
                return null;
            }
            this.max = max;
            long myNewPos = this.pos + retLen;
            long retMax = myNewPos = BigArrays.nearestSegmentStart(myNewPos, this.pos + 1L, max - 1L);
            long oldPos = this.pos;
            this.pos = myNewPos;
            this.hasSplit = true;
            return new Spliterator(oldPos, retMax, true);
        }
    }
}

