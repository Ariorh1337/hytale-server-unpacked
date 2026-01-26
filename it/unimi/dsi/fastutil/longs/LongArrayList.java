/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.longs.AbstractLongList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;

public class LongArrayList
extends AbstractLongList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 10;
    protected transient long[] a;
    protected int size;

    private static final long[] copyArraySafe(long[] a, int length) {
        if (length == 0) {
            return LongArrays.EMPTY_ARRAY;
        }
        return java.util.Arrays.copyOf(a, length);
    }

    private static final long[] copyArrayFromSafe(LongArrayList l) {
        return LongArrayList.copyArraySafe(l.a, l.size);
    }

    protected LongArrayList(long[] a, boolean wrapped) {
        this.a = a;
    }

    private void initArrayFromCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = capacity == 0 ? LongArrays.EMPTY_ARRAY : new long[capacity];
    }

    public LongArrayList(int capacity) {
        this.initArrayFromCapacity(capacity);
    }

    public LongArrayList() {
        this.a = LongArrays.DEFAULT_EMPTY_ARRAY;
    }

    public LongArrayList(Collection<? extends Long> c) {
        if (c instanceof LongArrayList) {
            this.a = LongArrayList.copyArrayFromSafe((LongArrayList)c);
            this.size = this.a.length;
        } else {
            this.initArrayFromCapacity(c.size());
            if (c instanceof LongList) {
                this.size = c.size();
                ((LongList)c).getElements(0, this.a, 0, this.size);
            } else {
                this.size = LongIterators.unwrap(LongIterators.asLongIterator(c.iterator()), this.a);
            }
        }
    }

    public LongArrayList(LongCollection c) {
        if (c instanceof LongArrayList) {
            this.a = LongArrayList.copyArrayFromSafe((LongArrayList)c);
            this.size = this.a.length;
        } else {
            this.initArrayFromCapacity(c.size());
            if (c instanceof LongList) {
                this.size = c.size();
                ((LongList)c).getElements(0, this.a, 0, this.size);
            } else {
                this.size = LongIterators.unwrap(c.iterator(), this.a);
            }
        }
    }

    public LongArrayList(LongList l) {
        if (l instanceof LongArrayList) {
            this.a = LongArrayList.copyArrayFromSafe((LongArrayList)l);
            this.size = this.a.length;
        } else {
            this.initArrayFromCapacity(l.size());
            this.size = l.size();
            l.getElements(0, this.a, 0, this.size);
        }
    }

    public LongArrayList(long[] a) {
        this(a, 0, a.length);
    }

    public LongArrayList(long[] a, int offset, int length) {
        this(length);
        System.arraycopy(a, offset, this.a, 0, length);
        this.size = length;
    }

    public LongArrayList(Iterator<? extends Long> i) {
        this();
        while (i.hasNext()) {
            this.add((long)i.next());
        }
    }

    public LongArrayList(LongIterator i) {
        this();
        while (i.hasNext()) {
            this.add(i.nextLong());
        }
    }

    public long[] elements() {
        return this.a;
    }

    public static LongArrayList wrap(long[] a, int length) {
        if (length > a.length) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
        }
        LongArrayList l = new LongArrayList(a, true);
        l.size = length;
        return l;
    }

    public static LongArrayList wrap(long[] a) {
        return LongArrayList.wrap(a, a.length);
    }

    public static LongArrayList of() {
        return new LongArrayList();
    }

    public static LongArrayList of(long ... init) {
        return LongArrayList.wrap(init);
    }

    public static LongArrayList toList(LongStream stream) {
        return stream.collect(LongArrayList::new, LongArrayList::add, LongList::addAll);
    }

    public static LongArrayList toListWithExpectedSize(LongStream stream, int expectedSize) {
        if (expectedSize <= 10) {
            return LongArrayList.toList(stream);
        }
        return stream.collect(new LongCollections.SizeDecreasingSupplier<LongArrayList>(expectedSize, size -> size <= 10 ? new LongArrayList() : new LongArrayList(size)), LongArrayList::add, LongList::addAll);
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this.a.length || this.a == LongArrays.DEFAULT_EMPTY_ARRAY && capacity <= 10) {
            return;
        }
        this.a = LongArrays.ensureCapacity(this.a, capacity, this.size);
        assert (this.size <= this.a.length);
    }

    private void grow(int capacity) {
        if (capacity <= this.a.length) {
            return;
        }
        if (this.a != LongArrays.DEFAULT_EMPTY_ARRAY) {
            capacity = (int)Math.max(Math.min((long)this.a.length + (long)(this.a.length >> 1), 0x7FFFFFF7L), (long)capacity);
        } else if (capacity < 10) {
            capacity = 10;
        }
        this.a = LongArrays.forceCapacity(this.a, capacity, this.size);
        assert (this.size <= this.a.length);
    }

    @Override
    public void add(int index, long k) {
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
    public boolean add(long k) {
        this.grow(this.size + 1);
        this.a[this.size++] = k;
        assert (this.size <= this.a.length);
        return true;
    }

    @Override
    public long getLong(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(long k) {
        for (int i = 0; i < this.size; ++i) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(long k) {
        int i = this.size;
        while (i-- != 0) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public long removeLong(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        long old = this.a[index];
        --this.size;
        if (index != this.size) {
            System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
        }
        assert (this.size <= this.a.length);
        return old;
    }

    @Override
    public boolean rem(long k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeLong(index);
        assert (this.size <= this.a.length);
        return true;
    }

    @Override
    public long set(int index, long k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        long old = this.a[index];
        this.a[index] = k;
        return old;
    }

    @Override
    public void clear() {
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
            this.a = LongArrays.forceCapacity(this.a, size, this.size);
        }
        if (size > this.size) {
            java.util.Arrays.fill(this.a, this.size, size, 0L);
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
        long[] t = new long[Math.max(n, this.size)];
        System.arraycopy(this.a, 0, t, 0, this.size);
        this.a = t;
        assert (this.size <= this.a.length);
    }

    @Override
    public LongList subList(int from, int to) {
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
    public void getElements(int from, long[] a, int offset, int length) {
        LongArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(int from, int to) {
        Arrays.ensureFromTo(this.size, from, to);
        System.arraycopy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
    }

    @Override
    public void addElements(int index, long[] a, int offset, int length) {
        this.ensureIndex(index);
        LongArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        System.arraycopy(this.a, index, this.a, index + length, this.size - index);
        System.arraycopy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public void setElements(int index, long[] a, int offset, int length) {
        this.ensureIndex(index);
        LongArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size + ")");
        }
        System.arraycopy(a, offset, this.a, index, length);
    }

    @Override
    public void forEach(LongConsumer action) {
        for (int i = 0; i < this.size; ++i) {
            action.accept(this.a[i]);
        }
    }

    @Override
    public boolean addAll(int index, LongCollection c) {
        if (c instanceof LongList) {
            return this.addAll(index, (LongList)c);
        }
        this.ensureIndex(index);
        int n = c.size();
        if (n == 0) {
            return false;
        }
        this.grow(this.size + n);
        System.arraycopy(this.a, index, this.a, index + n, this.size - index);
        LongIterator i = c.iterator();
        this.size += n;
        while (n-- != 0) {
            this.a[index++] = i.nextLong();
        }
        assert (this.size <= this.a.length);
        return true;
    }

    @Override
    public boolean addAll(int index, LongList l) {
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
    public boolean removeAll(LongCollection c) {
        long[] a = this.a;
        int j = 0;
        for (int i = 0; i < this.size; ++i) {
            if (c.contains(a[i])) continue;
            a[j++] = a[i];
        }
        boolean modified = this.size != j;
        this.size = j;
        return modified;
    }

    @Override
    public long[] toArray(long[] a) {
        if (a == null || a.length < this.size) {
            a = java.util.Arrays.copyOf(a, this.size);
        }
        System.arraycopy(this.a, 0, a, 0, this.size);
        return a;
    }

    @Override
    public LongListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new LongListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < LongArrayList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return LongArrayList.this.a[this.last];
            }

            @Override
            public long previousLong() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return LongArrayList.this.a[this.pos];
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
            public void add(long k) {
                LongArrayList.this.add(this.pos++, k);
                this.last = -1;
            }

            @Override
            public void set(long k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                LongArrayList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                LongArrayList.this.removeLong(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
                while (this.pos < LongArrayList.this.size) {
                    ++this.pos;
                    this.last = this.last;
                    action.accept(LongArrayList.this.a[this.last]);
                }
            }

            @Override
            public int back(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = LongArrayList.this.size - this.pos;
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
                int remaining = LongArrayList.this.size - this.pos;
                if (n < remaining) {
                    this.pos += n;
                } else {
                    n = remaining;
                    this.pos = LongArrayList.this.size;
                }
                this.last = this.pos - 1;
                return n;
            }
        };
    }

    @Override
    public LongSpliterator spliterator() {
        return new Spliterator();
    }

    @Override
    public void sort(LongComparator comp) {
        if (comp == null) {
            LongArrays.stableSort(this.a, 0, this.size);
        } else {
            LongArrays.stableSort(this.a, 0, this.size, comp);
        }
    }

    @Override
    public void unstableSort(LongComparator comp) {
        if (comp == null) {
            LongArrays.unstableSort(this.a, 0, this.size);
        } else {
            LongArrays.unstableSort(this.a, 0, this.size, comp);
        }
    }

    public LongArrayList clone() {
        LongArrayList cloned = null;
        if (this.getClass() == LongArrayList.class) {
            cloned = new LongArrayList(LongArrayList.copyArraySafe(this.a, this.size), false);
            cloned.size = this.size;
        } else {
            try {
                cloned = (LongArrayList)super.clone();
            }
            catch (CloneNotSupportedException err) {
                throw new InternalError(err);
            }
            cloned.a = LongArrayList.copyArraySafe(this.a, this.size);
        }
        return cloned;
    }

    public boolean equals(LongArrayList l) {
        if (l == this) {
            return true;
        }
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        long[] a1 = this.a;
        long[] a2 = l.a;
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
        if (o instanceof LongArrayList) {
            return this.equals((LongArrayList)o);
        }
        if (o instanceof SubList) {
            return ((SubList)o).equals(this);
        }
        return super.equals(o);
    }

    @Override
    public int compareTo(LongArrayList l) {
        int i;
        int s1 = this.size();
        int s2 = l.size();
        long[] a1 = this.a;
        long[] a2 = l.a;
        if (a1 == a2 && s1 == s2) {
            return 0;
        }
        for (i = 0; i < s1 && i < s2; ++i) {
            long e1 = a1[i];
            long e2 = a2[i];
            int r = Long.compare(e1, e2);
            if (r == 0) continue;
            return r;
        }
        return i < s2 ? -1 : (i < s1 ? 1 : 0);
    }

    @Override
    public int compareTo(List<? extends Long> l) {
        if (l instanceof LongArrayList) {
            return this.compareTo((LongArrayList)l);
        }
        if (l instanceof SubList) {
            return -((SubList)l).compareTo(this);
        }
        return super.compareTo(l);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeLong(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new long[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readLong();
        }
    }

    private class SubList
    extends AbstractLongList.LongRandomAccessSubList {
        private static final long serialVersionUID = -3185226345314976296L;

        protected SubList(int from, int to) {
            super(LongArrayList.this, from, to);
        }

        private long[] getParentArray() {
            return LongArrayList.this.a;
        }

        @Override
        public long getLong(int i) {
            this.ensureRestrictedIndex(i);
            return LongArrayList.this.a[i + this.from];
        }

        @Override
        public LongListIterator listIterator(int index) {
            return new SubListIterator(index);
        }

        @Override
        public LongSpliterator spliterator() {
            return new SubListSpliterator();
        }

        boolean contentsEquals(long[] otherA, int otherAFrom, int otherATo) {
            if (LongArrayList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return true;
            }
            if (otherATo - otherAFrom != this.size()) {
                return false;
            }
            int pos = this.from;
            int otherPos = otherAFrom;
            while (pos < this.to) {
                if (LongArrayList.this.a[pos++] == otherA[otherPos++]) continue;
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
            if (o instanceof LongArrayList) {
                LongArrayList other = (LongArrayList)o;
                return this.contentsEquals(other.a, 0, other.size());
            }
            if (o instanceof SubList) {
                SubList other = (SubList)o;
                return this.contentsEquals(other.getParentArray(), other.from, other.to);
            }
            return super.equals(o);
        }

        int contentsCompareTo(long[] otherA, int otherAFrom, int otherATo) {
            if (LongArrayList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return 0;
            }
            int i = this.from;
            int j = otherAFrom;
            while (i < this.to && i < otherATo) {
                long e1 = LongArrayList.this.a[i];
                long e2 = otherA[j];
                int r = Long.compare(e1, e2);
                if (r != 0) {
                    return r;
                }
                ++i;
                ++j;
            }
            return i < otherATo ? -1 : (i < this.to ? 1 : 0);
        }

        @Override
        public int compareTo(List<? extends Long> l) {
            if (l instanceof LongArrayList) {
                LongArrayList other = (LongArrayList)l;
                return this.contentsCompareTo(other.a, 0, other.size());
            }
            if (l instanceof SubList) {
                SubList other = (SubList)l;
                return this.contentsCompareTo(other.getParentArray(), other.from, other.to);
            }
            return super.compareTo(l);
        }

        private final class SubListIterator
        extends LongIterators.AbstractIndexBasedListIterator {
            SubListIterator(int index) {
                super(0, index);
            }

            @Override
            protected final long get(int i) {
                return LongArrayList.this.a[SubList.this.from + i];
            }

            @Override
            protected final void add(int i, long k) {
                SubList.this.add(i, k);
            }

            @Override
            protected final void set(int i, long k) {
                SubList.this.set(i, k);
            }

            @Override
            protected final void remove(int i) {
                SubList.this.removeLong(i);
            }

            @Override
            protected final int getMaxPos() {
                return SubList.this.to - SubList.this.from;
            }

            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = this.pos++;
                return LongArrayList.this.a[SubList.this.from + this.lastReturned];
            }

            @Override
            public long previousLong() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = --this.pos;
                return LongArrayList.this.a[SubList.this.from + this.pos];
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
                int max = SubList.this.to - SubList.this.from;
                while (this.pos < max) {
                    ++this.pos;
                    this.lastReturned = this.lastReturned;
                    action.accept(LongArrayList.this.a[SubList.this.from + this.lastReturned]);
                }
            }
        }

        private final class SubListSpliterator
        extends LongSpliterators.LateBindingSizeIndexBasedSpliterator {
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
            protected final long get(int i) {
                return LongArrayList.this.a[i];
            }

            @Override
            protected final SubListSpliterator makeForSplit(int pos, int maxPos) {
                return new SubListSpliterator(pos, maxPos);
            }

            @Override
            public boolean tryAdvance(LongConsumer action) {
                if (this.pos >= this.getMaxPos()) {
                    return false;
                }
                action.accept(LongArrayList.this.a[this.pos++]);
                return true;
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
                int max = this.getMaxPos();
                while (this.pos < max) {
                    action.accept(LongArrayList.this.a[this.pos++]);
                }
            }
        }
    }

    private final class Spliterator
    implements LongSpliterator {
        boolean hasSplit = false;
        int pos;
        int max;

        public Spliterator() {
            this(0, longArrayList.size, false);
        }

        private Spliterator(int pos, int max, boolean hasSplit) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
            this.hasSplit = hasSplit;
        }

        private int getWorkingMax() {
            return this.hasSplit ? this.max : LongArrayList.this.size;
        }

        @Override
        public int characteristics() {
            return 16720;
        }

        @Override
        public long estimateSize() {
            return this.getWorkingMax() - this.pos;
        }

        @Override
        public boolean tryAdvance(LongConsumer action) {
            if (this.pos >= this.getWorkingMax()) {
                return false;
            }
            action.accept(LongArrayList.this.a[this.pos++]);
            return true;
        }

        @Override
        public void forEachRemaining(LongConsumer action) {
            int max = this.getWorkingMax();
            while (this.pos < max) {
                action.accept(LongArrayList.this.a[this.pos]);
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
        public LongSpliterator trySplit() {
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

