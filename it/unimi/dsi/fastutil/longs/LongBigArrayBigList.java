/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.longs.AbstractLongBigList;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import it.unimi.dsi.fastutil.longs.LongBigList;
import it.unimi.dsi.fastutil.longs.LongBigListIterator;
import it.unimi.dsi.fastutil.longs.LongBigListIterators;
import it.unimi.dsi.fastutil.longs.LongBigSpliterators;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;

public class LongBigArrayBigList
extends AbstractLongBigList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 10;
    protected transient long[][] a;
    protected long size;

    protected LongBigArrayBigList(long[][] a, boolean dummy) {
        this.a = a;
    }

    public LongBigArrayBigList(long capacity) {
        if (capacity < 0L) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = capacity == 0L ? LongBigArrays.EMPTY_BIG_ARRAY : LongBigArrays.newBigArray(capacity);
    }

    public LongBigArrayBigList() {
        this.a = LongBigArrays.DEFAULT_EMPTY_BIG_ARRAY;
    }

    public LongBigArrayBigList(LongCollection c) {
        this(Size64.sizeOf(c));
        if (c instanceof LongBigList) {
            this.size = Size64.sizeOf(c);
            ((LongBigList)c).getElements(0L, this.a, 0L, this.size);
        } else {
            LongIterator i = c.iterator();
            while (i.hasNext()) {
                this.add(i.nextLong());
            }
        }
    }

    public LongBigArrayBigList(LongBigList l) {
        this(l.size64());
        this.size = l.size64();
        l.getElements(0L, this.a, 0L, this.size);
    }

    public LongBigArrayBigList(long[][] a) {
        this(a, 0L, BigArrays.length(a));
    }

    public LongBigArrayBigList(long[][] a, long offset, long length) {
        this(length);
        BigArrays.copy(a, offset, this.a, 0L, length);
        this.size = length;
    }

    public LongBigArrayBigList(Iterator<? extends Long> i) {
        this();
        while (i.hasNext()) {
            this.add((long)i.next());
        }
    }

    public LongBigArrayBigList(LongIterator i) {
        this();
        while (i.hasNext()) {
            this.add(i.nextLong());
        }
    }

    public long[][] elements() {
        return this.a;
    }

    public static LongBigArrayBigList wrap(long[][] a, long length) {
        if (length > BigArrays.length(a)) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + BigArrays.length(a) + ")");
        }
        LongBigArrayBigList l = new LongBigArrayBigList(a, false);
        l.size = length;
        return l;
    }

    public static LongBigArrayBigList wrap(long[][] a) {
        return LongBigArrayBigList.wrap(a, BigArrays.length(a));
    }

    public static LongBigArrayBigList of() {
        return new LongBigArrayBigList();
    }

    public static LongBigArrayBigList of(long ... init) {
        return LongBigArrayBigList.wrap(BigArrays.wrap(init));
    }

    public static LongBigArrayBigList toBigList(LongStream stream) {
        return stream.collect(LongBigArrayBigList::new, LongBigArrayBigList::add, LongBigList::addAll);
    }

    public static LongBigArrayBigList toBigListWithExpectedSize(LongStream stream, long expectedSize) {
        return stream.collect(() -> new LongBigArrayBigList(expectedSize), LongBigArrayBigList::add, LongBigList::addAll);
    }

    public void ensureCapacity(long capacity) {
        if (capacity <= BigArrays.length(this.a) || this.a == LongBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            return;
        }
        this.a = BigArrays.forceCapacity(this.a, capacity, this.size);
        assert (this.size <= BigArrays.length(this.a));
    }

    private void grow(long capacity) {
        long oldLength = BigArrays.length(this.a);
        if (capacity <= oldLength) {
            return;
        }
        if (this.a != LongBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            capacity = Math.max(oldLength + (oldLength >> 1), capacity);
        } else if (capacity < 10L) {
            capacity = 10L;
        }
        this.a = BigArrays.forceCapacity(this.a, capacity, this.size);
        assert (this.size <= BigArrays.length(this.a));
    }

    @Override
    public void add(long index, long k) {
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
    public boolean add(long k) {
        this.grow(this.size + 1L);
        BigArrays.set(this.a, this.size++, k);
        assert (this.size <= BigArrays.length(this.a));
        return true;
    }

    @Override
    public long getLong(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return BigArrays.get(this.a, index);
    }

    @Override
    public long indexOf(long k) {
        for (long i = 0L; i < this.size; ++i) {
            if (k != BigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(long k) {
        long i = this.size;
        while (i-- != 0L) {
            if (k != BigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public long removeLong(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        long old = BigArrays.get(this.a, index);
        --this.size;
        if (index != this.size) {
            BigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
        }
        assert (this.size <= BigArrays.length(this.a));
        return old;
    }

    @Override
    public boolean rem(long k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.removeLong(index);
        assert (this.size <= BigArrays.length(this.a));
        return true;
    }

    @Override
    public long set(long index, long k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        long old = BigArrays.get(this.a, index);
        BigArrays.set(this.a, index, k);
        return old;
    }

    @Override
    public boolean removeAll(LongCollection c) {
        long[] s = null;
        long[] d = null;
        int ss = -1;
        int sd = 0x8000000;
        int ds = -1;
        int dd = 0x8000000;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 0x8000000) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains((long)s[sd])) {
                if (dd == 0x8000000) {
                    d = this.a[++ds];
                    dd = 0;
                }
                d[dd++] = s[sd];
            }
            ++sd;
        }
        long j = BigArrays.index(ds, dd);
        boolean modified = this.size != j;
        this.size = j;
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        long[] s = null;
        long[] d = null;
        int ss = -1;
        int sd = 0x8000000;
        int ds = -1;
        int dd = 0x8000000;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 0x8000000) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains((long)s[sd])) {
                if (dd == 0x8000000) {
                    d = this.a[++ds];
                    dd = 0;
                }
                d[dd++] = s[sd];
            }
            ++sd;
        }
        long j = BigArrays.index(ds, dd);
        boolean modified = this.size != j;
        this.size = j;
        return modified;
    }

    @Override
    public boolean addAll(long index, LongCollection c) {
        if (c instanceof LongList) {
            return this.addAll(index, (LongList)c);
        }
        if (c instanceof LongBigList) {
            return this.addAll(index, (LongBigList)c);
        }
        this.ensureIndex(index);
        int n = c.size();
        if (n == 0) {
            return false;
        }
        this.grow(this.size + (long)n);
        BigArrays.copy(this.a, index, this.a, index + (long)n, this.size - index);
        LongIterator i = c.iterator();
        this.size += (long)n;
        assert (this.size <= BigArrays.length(this.a));
        while (n-- != 0) {
            BigArrays.set(this.a, index++, i.nextLong());
        }
        return true;
    }

    @Override
    public boolean addAll(long index, LongBigList list) {
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
    public boolean addAll(long index, LongList list) {
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
            BigArrays.fill(this.a, this.size, size, 0L);
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
    public LongBigList subList(long from, long to) {
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
    public void getElements(long from, long[][] a, long offset, long length) {
        BigArrays.copy(this.a, from, a, offset, length);
    }

    @Override
    public void getElements(long from, long[] a, int offset, int length) {
        BigArrays.copyFromBig(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(long from, long to) {
        BigArrays.ensureFromTo(this.size, from, to);
        BigArrays.copy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
    }

    @Override
    public void addElements(long index, long[][] a, long offset, long length) {
        this.ensureIndex(index);
        BigArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        BigArrays.copy(this.a, index, this.a, index + length, this.size - index);
        BigArrays.copy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public void setElements(long index, long[][] a, long offset, long length) {
        BigArrays.copy(a, offset, this.a, index, length);
    }

    @Override
    public void forEach(LongConsumer action) {
        for (long i = 0L; i < this.size; ++i) {
            action.accept(BigArrays.get(this.a, i));
        }
    }

    @Override
    public LongBigListIterator listIterator(final long index) {
        this.ensureIndex(index);
        return new LongBigListIterator(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < LongBigArrayBigList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return BigArrays.get(LongBigArrayBigList.this.a, this.last);
            }

            @Override
            public long previousLong() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return BigArrays.get(LongBigArrayBigList.this.a, this.pos);
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
            public void add(long k) {
                LongBigArrayBigList.this.add(this.pos++, k);
                this.last = -1L;
            }

            @Override
            public void set(long k) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                LongBigArrayBigList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                LongBigArrayBigList.this.removeLong(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
                while (this.pos < LongBigArrayBigList.this.size) {
                    ++this.pos;
                    this.last = this.last;
                    action.accept(BigArrays.get(LongBigArrayBigList.this.a, this.last));
                }
            }

            @Override
            public long back(long n) {
                if (n < 0L) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                long remaining = LongBigArrayBigList.this.size - this.pos;
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
                long remaining = LongBigArrayBigList.this.size - this.pos;
                if (n < remaining) {
                    this.pos += n;
                } else {
                    n = remaining;
                    this.pos = LongBigArrayBigList.this.size;
                }
                this.last = this.pos - 1L;
                return n;
            }
        };
    }

    @Override
    public LongSpliterator spliterator() {
        return new Spliterator();
    }

    public LongBigArrayBigList clone() {
        LongBigArrayBigList c;
        if (this.getClass() == LongBigArrayBigList.class) {
            c = new LongBigArrayBigList(this.size);
            c.size = this.size;
        } else {
            try {
                c = (LongBigArrayBigList)super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new InternalError(e);
            }
            c.a = LongBigArrays.newBigArray(this.size);
        }
        BigArrays.copy(this.a, 0L, c.a, 0L, this.size);
        return c;
    }

    public boolean equals(LongBigArrayBigList l) {
        if (l == this) {
            return true;
        }
        long s = this.size64();
        if (s != l.size64()) {
            return false;
        }
        long[][] a1 = this.a;
        long[][] a2 = l.a;
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
        if (o instanceof LongBigArrayBigList) {
            return this.equals((LongBigArrayBigList)o);
        }
        if (o instanceof SubList) {
            return ((SubList)o).equals(this);
        }
        return super.equals(o);
    }

    @Override
    public int compareTo(LongBigArrayBigList l) {
        long s1 = this.size64();
        long s2 = l.size64();
        long[][] a1 = this.a;
        long[][] a2 = l.a;
        if (a1 == a2 && s1 == s2) {
            return 0;
        }
        int i = 0;
        while ((long)i < s1 && (long)i < s2) {
            long e2;
            long e1 = BigArrays.get(a1, (long)i);
            int r = Long.compare(e1, e2 = BigArrays.get(a2, (long)i));
            if (r != 0) {
                return r;
            }
            ++i;
        }
        return (long)i < s2 ? -1 : ((long)i < s1 ? 1 : 0);
    }

    @Override
    public int compareTo(BigList<? extends Long> l) {
        if (l instanceof LongBigArrayBigList) {
            return this.compareTo((LongBigArrayBigList)l);
        }
        if (l instanceof SubList) {
            return -((SubList)l).compareTo(this);
        }
        return super.compareTo(l);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        int i = 0;
        while ((long)i < this.size) {
            s.writeLong(BigArrays.get(this.a, (long)i));
            ++i;
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = LongBigArrays.newBigArray(this.size);
        int i = 0;
        while ((long)i < this.size) {
            BigArrays.set(this.a, (long)i, s.readLong());
            ++i;
        }
    }

    private class SubList
    extends AbstractLongBigList.LongRandomAccessSubList {
        private static final long serialVersionUID = -3185226345314976296L;

        protected SubList(long from, long to) {
            super(LongBigArrayBigList.this, from, to);
        }

        private long[][] getParentArray() {
            return LongBigArrayBigList.this.a;
        }

        @Override
        public long getLong(long i) {
            this.ensureRestrictedIndex(i);
            return BigArrays.get(LongBigArrayBigList.this.a, i + this.from);
        }

        @Override
        public LongBigListIterator listIterator(long index) {
            return new SubListIterator(index);
        }

        @Override
        public LongSpliterator spliterator() {
            return new SubListSpliterator();
        }

        boolean contentsEquals(long[][] otherA, long otherAFrom, long otherATo) {
            if (LongBigArrayBigList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return true;
            }
            if (otherATo - otherAFrom != this.size64()) {
                return false;
            }
            long pos = this.to;
            long otherPos = otherATo;
            while (--pos >= this.from) {
                if (BigArrays.get(LongBigArrayBigList.this.a, pos) == BigArrays.get(otherA, --otherPos)) continue;
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
            if (o instanceof LongBigArrayBigList) {
                LongBigArrayBigList other = (LongBigArrayBigList)o;
                return this.contentsEquals(other.a, 0L, other.size64());
            }
            if (o instanceof SubList) {
                SubList other = (SubList)o;
                return this.contentsEquals(other.getParentArray(), other.from, other.to);
            }
            return super.equals(o);
        }

        int contentsCompareTo(long[][] otherA, long otherAFrom, long otherATo) {
            if (LongBigArrayBigList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return 0;
            }
            long i = this.from;
            long j = otherAFrom;
            while (i < this.to && i < otherATo) {
                long e2;
                long e1 = BigArrays.get(LongBigArrayBigList.this.a, i);
                int r = Long.compare(e1, e2 = BigArrays.get(otherA, j));
                if (r != 0) {
                    return r;
                }
                ++i;
                ++j;
            }
            return i < otherATo ? -1 : (i < this.to ? 1 : 0);
        }

        @Override
        public int compareTo(BigList<? extends Long> l) {
            if (l instanceof LongBigArrayBigList) {
                LongBigArrayBigList other = (LongBigArrayBigList)l;
                return this.contentsCompareTo(other.a, 0L, other.size64());
            }
            if (l instanceof SubList) {
                SubList other = (SubList)l;
                return this.contentsCompareTo(other.getParentArray(), other.from, other.to);
            }
            return super.compareTo(l);
        }

        private final class SubListIterator
        extends LongBigListIterators.AbstractIndexBasedBigListIterator {
            SubListIterator(long index) {
                super(0L, index);
            }

            @Override
            protected final long get(long i) {
                return BigArrays.get(LongBigArrayBigList.this.a, SubList.this.from + i);
            }

            @Override
            protected final void add(long i, long k) {
                SubList.this.add(i, k);
            }

            @Override
            protected final void set(long i, long k) {
                SubList.this.set(i, k);
            }

            @Override
            protected final void remove(long i) {
                SubList.this.removeLong(i);
            }

            @Override
            protected final long getMaxPos() {
                return SubList.this.to - SubList.this.from;
            }

            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = this.pos++;
                return BigArrays.get(LongBigArrayBigList.this.a, SubList.this.from + this.lastReturned);
            }

            @Override
            public long previousLong() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = --this.pos;
                return BigArrays.get(LongBigArrayBigList.this.a, SubList.this.from + this.pos);
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
                long max = SubList.this.to - SubList.this.from;
                while (this.pos < max) {
                    ++this.pos;
                    this.lastReturned = this.lastReturned;
                    action.accept(BigArrays.get(LongBigArrayBigList.this.a, SubList.this.from + this.lastReturned));
                }
            }
        }

        private final class SubListSpliterator
        extends LongBigSpliterators.LateBindingSizeIndexBasedSpliterator {
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
            protected final long get(long i) {
                return BigArrays.get(LongBigArrayBigList.this.a, i);
            }

            @Override
            protected final SubListSpliterator makeForSplit(long pos, long maxPos) {
                return new SubListSpliterator(pos, maxPos);
            }

            @Override
            protected final long computeSplitPoint() {
                long defaultSplit = super.computeSplitPoint();
                return BigArrays.nearestSegmentStart(defaultSplit, this.pos + 1L, this.getMaxPos() - 1L);
            }

            @Override
            public boolean tryAdvance(LongConsumer action) {
                if (this.pos >= this.getMaxPos()) {
                    return false;
                }
                action.accept(BigArrays.get(LongBigArrayBigList.this.a, this.pos++));
                return true;
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
                long max = this.getMaxPos();
                while (this.pos < max) {
                    action.accept(BigArrays.get(LongBigArrayBigList.this.a, this.pos++));
                }
            }
        }
    }

    private final class Spliterator
    implements LongSpliterator {
        boolean hasSplit = false;
        long pos;
        long max;

        public Spliterator() {
            this(0L, longBigArrayBigList.size, false);
        }

        private Spliterator(long pos, long max, boolean hasSplit) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
            this.hasSplit = hasSplit;
        }

        private long getWorkingMax() {
            return this.hasSplit ? this.max : LongBigArrayBigList.this.size;
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
            action.accept(BigArrays.get(LongBigArrayBigList.this.a, this.pos++));
            return true;
        }

        @Override
        public void forEachRemaining(LongConsumer action) {
            long max = this.getWorkingMax();
            while (this.pos < max) {
                action.accept(BigArrays.get(LongBigArrayBigList.this.a, this.pos));
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
        public LongSpliterator trySplit() {
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

