/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.DoubleConsumer;
import java.util.stream.DoubleStream;

public class DoubleArrayList
extends AbstractDoubleList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 10;
    protected transient double[] a;
    protected int size;

    private static final double[] copyArraySafe(double[] a, int length) {
        if (length == 0) {
            return DoubleArrays.EMPTY_ARRAY;
        }
        return java.util.Arrays.copyOf(a, length);
    }

    private static final double[] copyArrayFromSafe(DoubleArrayList l) {
        return DoubleArrayList.copyArraySafe(l.a, l.size);
    }

    protected DoubleArrayList(double[] a, boolean wrapped) {
        this.a = a;
    }

    private void initArrayFromCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = capacity == 0 ? DoubleArrays.EMPTY_ARRAY : new double[capacity];
    }

    public DoubleArrayList(int capacity) {
        this.initArrayFromCapacity(capacity);
    }

    public DoubleArrayList() {
        this.a = DoubleArrays.DEFAULT_EMPTY_ARRAY;
    }

    public DoubleArrayList(Collection<? extends Double> c) {
        if (c instanceof DoubleArrayList) {
            this.a = DoubleArrayList.copyArrayFromSafe((DoubleArrayList)c);
            this.size = this.a.length;
        } else {
            this.initArrayFromCapacity(c.size());
            if (c instanceof DoubleList) {
                this.size = c.size();
                ((DoubleList)c).getElements(0, this.a, 0, this.size);
            } else {
                this.size = DoubleIterators.unwrap(DoubleIterators.asDoubleIterator(c.iterator()), this.a);
            }
        }
    }

    public DoubleArrayList(DoubleCollection c) {
        if (c instanceof DoubleArrayList) {
            this.a = DoubleArrayList.copyArrayFromSafe((DoubleArrayList)c);
            this.size = this.a.length;
        } else {
            this.initArrayFromCapacity(c.size());
            if (c instanceof DoubleList) {
                this.size = c.size();
                ((DoubleList)c).getElements(0, this.a, 0, this.size);
            } else {
                this.size = DoubleIterators.unwrap(c.iterator(), this.a);
            }
        }
    }

    public DoubleArrayList(DoubleList l) {
        if (l instanceof DoubleArrayList) {
            this.a = DoubleArrayList.copyArrayFromSafe((DoubleArrayList)l);
            this.size = this.a.length;
        } else {
            this.initArrayFromCapacity(l.size());
            this.size = l.size();
            l.getElements(0, this.a, 0, this.size);
        }
    }

    public DoubleArrayList(double[] a) {
        this(a, 0, a.length);
    }

    public DoubleArrayList(double[] a, int offset, int length) {
        this(length);
        System.arraycopy(a, offset, this.a, 0, length);
        this.size = length;
    }

    public DoubleArrayList(Iterator<? extends Double> i) {
        this();
        while (i.hasNext()) {
            this.add((double)i.next());
        }
    }

    public DoubleArrayList(DoubleIterator i) {
        this();
        while (i.hasNext()) {
            this.add(i.nextDouble());
        }
    }

    public double[] elements() {
        return this.a;
    }

    public static DoubleArrayList wrap(double[] a, int length) {
        if (length > a.length) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
        }
        DoubleArrayList l = new DoubleArrayList(a, true);
        l.size = length;
        return l;
    }

    public static DoubleArrayList wrap(double[] a) {
        return DoubleArrayList.wrap(a, a.length);
    }

    public static DoubleArrayList of() {
        return new DoubleArrayList();
    }

    public static DoubleArrayList of(double ... init) {
        return DoubleArrayList.wrap(init);
    }

    public static DoubleArrayList toList(DoubleStream stream) {
        return stream.collect(DoubleArrayList::new, DoubleArrayList::add, DoubleList::addAll);
    }

    public static DoubleArrayList toListWithExpectedSize(DoubleStream stream, int expectedSize) {
        if (expectedSize <= 10) {
            return DoubleArrayList.toList(stream);
        }
        return stream.collect(new DoubleCollections.SizeDecreasingSupplier<DoubleArrayList>(expectedSize, size -> size <= 10 ? new DoubleArrayList() : new DoubleArrayList(size)), DoubleArrayList::add, DoubleList::addAll);
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this.a.length || this.a == DoubleArrays.DEFAULT_EMPTY_ARRAY && capacity <= 10) {
            return;
        }
        this.a = DoubleArrays.ensureCapacity(this.a, capacity, this.size);
        assert (this.size <= this.a.length);
    }

    private void grow(int capacity) {
        if (capacity <= this.a.length) {
            return;
        }
        if (this.a != DoubleArrays.DEFAULT_EMPTY_ARRAY) {
            capacity = (int)Math.max(Math.min((long)this.a.length + (long)(this.a.length >> 1), 0x7FFFFFF7L), (long)capacity);
        } else if (capacity < 10) {
            capacity = 10;
        }
        this.a = DoubleArrays.forceCapacity(this.a, capacity, this.size);
        assert (this.size <= this.a.length);
    }

    @Override
    public void add(int index, double k) {
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
    public boolean add(double k) {
        this.grow(this.size + 1);
        this.a[this.size++] = k;
        assert (this.size <= this.a.length);
        return true;
    }

    @Override
    public double getDouble(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(double k) {
        for (int i = 0; i < this.size; ++i) {
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(this.a[i])) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(double k) {
        int i = this.size;
        while (i-- != 0) {
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(this.a[i])) continue;
            return i;
        }
        return -1;
    }

    @Override
    public double removeDouble(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        double old = this.a[index];
        --this.size;
        if (index != this.size) {
            System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
        }
        assert (this.size <= this.a.length);
        return old;
    }

    @Override
    public boolean rem(double k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeDouble(index);
        assert (this.size <= this.a.length);
        return true;
    }

    @Override
    public double set(int index, double k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        double old = this.a[index];
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
            this.a = DoubleArrays.forceCapacity(this.a, size, this.size);
        }
        if (size > this.size) {
            java.util.Arrays.fill(this.a, this.size, size, 0.0);
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
        double[] t = new double[Math.max(n, this.size)];
        System.arraycopy(this.a, 0, t, 0, this.size);
        this.a = t;
        assert (this.size <= this.a.length);
    }

    @Override
    public DoubleList subList(int from, int to) {
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
    public void getElements(int from, double[] a, int offset, int length) {
        DoubleArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(int from, int to) {
        Arrays.ensureFromTo(this.size, from, to);
        System.arraycopy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
    }

    @Override
    public void addElements(int index, double[] a, int offset, int length) {
        this.ensureIndex(index);
        DoubleArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        System.arraycopy(this.a, index, this.a, index + length, this.size - index);
        System.arraycopy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public void setElements(int index, double[] a, int offset, int length) {
        this.ensureIndex(index);
        DoubleArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size + ")");
        }
        System.arraycopy(a, offset, this.a, index, length);
    }

    @Override
    public void forEach(DoubleConsumer action) {
        for (int i = 0; i < this.size; ++i) {
            action.accept(this.a[i]);
        }
    }

    @Override
    public boolean addAll(int index, DoubleCollection c) {
        if (c instanceof DoubleList) {
            return this.addAll(index, (DoubleList)c);
        }
        this.ensureIndex(index);
        int n = c.size();
        if (n == 0) {
            return false;
        }
        this.grow(this.size + n);
        System.arraycopy(this.a, index, this.a, index + n, this.size - index);
        DoubleIterator i = c.iterator();
        this.size += n;
        while (n-- != 0) {
            this.a[index++] = i.nextDouble();
        }
        assert (this.size <= this.a.length);
        return true;
    }

    @Override
    public boolean addAll(int index, DoubleList l) {
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
    public boolean removeAll(DoubleCollection c) {
        double[] a = this.a;
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
    public double[] toArray(double[] a) {
        if (a == null || a.length < this.size) {
            a = java.util.Arrays.copyOf(a, this.size);
        }
        System.arraycopy(this.a, 0, a, 0, this.size);
        return a;
    }

    @Override
    public DoubleListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new DoubleListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < DoubleArrayList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public double nextDouble() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return DoubleArrayList.this.a[this.last];
            }

            @Override
            public double previousDouble() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return DoubleArrayList.this.a[this.pos];
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
            public void add(double k) {
                DoubleArrayList.this.add(this.pos++, k);
                this.last = -1;
            }

            @Override
            public void set(double k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                DoubleArrayList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                DoubleArrayList.this.removeDouble(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
                while (this.pos < DoubleArrayList.this.size) {
                    ++this.pos;
                    this.last = this.last;
                    action.accept(DoubleArrayList.this.a[this.last]);
                }
            }

            @Override
            public int back(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = DoubleArrayList.this.size - this.pos;
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
                int remaining = DoubleArrayList.this.size - this.pos;
                if (n < remaining) {
                    this.pos += n;
                } else {
                    n = remaining;
                    this.pos = DoubleArrayList.this.size;
                }
                this.last = this.pos - 1;
                return n;
            }
        };
    }

    @Override
    public DoubleSpliterator spliterator() {
        return new Spliterator();
    }

    @Override
    public void sort(DoubleComparator comp) {
        if (comp == null) {
            DoubleArrays.stableSort(this.a, 0, this.size);
        } else {
            DoubleArrays.stableSort(this.a, 0, this.size, comp);
        }
    }

    @Override
    public void unstableSort(DoubleComparator comp) {
        if (comp == null) {
            DoubleArrays.unstableSort(this.a, 0, this.size);
        } else {
            DoubleArrays.unstableSort(this.a, 0, this.size, comp);
        }
    }

    public DoubleArrayList clone() {
        DoubleArrayList cloned = null;
        if (this.getClass() == DoubleArrayList.class) {
            cloned = new DoubleArrayList(DoubleArrayList.copyArraySafe(this.a, this.size), false);
            cloned.size = this.size;
        } else {
            try {
                cloned = (DoubleArrayList)super.clone();
            }
            catch (CloneNotSupportedException err) {
                throw new InternalError(err);
            }
            cloned.a = DoubleArrayList.copyArraySafe(this.a, this.size);
        }
        return cloned;
    }

    public boolean equals(DoubleArrayList l) {
        if (l == this) {
            return true;
        }
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        double[] a1 = this.a;
        double[] a2 = l.a;
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
        if (o instanceof DoubleArrayList) {
            return this.equals((DoubleArrayList)o);
        }
        if (o instanceof SubList) {
            return ((SubList)o).equals(this);
        }
        return super.equals(o);
    }

    @Override
    public int compareTo(DoubleArrayList l) {
        int i;
        int s1 = this.size();
        int s2 = l.size();
        double[] a1 = this.a;
        double[] a2 = l.a;
        if (a1 == a2 && s1 == s2) {
            return 0;
        }
        for (i = 0; i < s1 && i < s2; ++i) {
            double e1 = a1[i];
            double e2 = a2[i];
            int r = Double.compare(e1, e2);
            if (r == 0) continue;
            return r;
        }
        return i < s2 ? -1 : (i < s1 ? 1 : 0);
    }

    @Override
    public int compareTo(List<? extends Double> l) {
        if (l instanceof DoubleArrayList) {
            return this.compareTo((DoubleArrayList)l);
        }
        if (l instanceof SubList) {
            return -((SubList)l).compareTo(this);
        }
        return super.compareTo(l);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeDouble(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new double[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readDouble();
        }
    }

    private class SubList
    extends AbstractDoubleList.DoubleRandomAccessSubList {
        private static final long serialVersionUID = -3185226345314976296L;

        protected SubList(int from, int to) {
            super(DoubleArrayList.this, from, to);
        }

        private double[] getParentArray() {
            return DoubleArrayList.this.a;
        }

        @Override
        public double getDouble(int i) {
            this.ensureRestrictedIndex(i);
            return DoubleArrayList.this.a[i + this.from];
        }

        @Override
        public DoubleListIterator listIterator(int index) {
            return new SubListIterator(index);
        }

        @Override
        public DoubleSpliterator spliterator() {
            return new SubListSpliterator();
        }

        boolean contentsEquals(double[] otherA, int otherAFrom, int otherATo) {
            if (DoubleArrayList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return true;
            }
            if (otherATo - otherAFrom != this.size()) {
                return false;
            }
            int pos = this.from;
            int otherPos = otherAFrom;
            while (pos < this.to) {
                if (DoubleArrayList.this.a[pos++] == otherA[otherPos++]) continue;
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
            if (o instanceof DoubleArrayList) {
                DoubleArrayList other = (DoubleArrayList)o;
                return this.contentsEquals(other.a, 0, other.size());
            }
            if (o instanceof SubList) {
                SubList other = (SubList)o;
                return this.contentsEquals(other.getParentArray(), other.from, other.to);
            }
            return super.equals(o);
        }

        int contentsCompareTo(double[] otherA, int otherAFrom, int otherATo) {
            if (DoubleArrayList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return 0;
            }
            int i = this.from;
            int j = otherAFrom;
            while (i < this.to && i < otherATo) {
                double e1 = DoubleArrayList.this.a[i];
                double e2 = otherA[j];
                int r = Double.compare(e1, e2);
                if (r != 0) {
                    return r;
                }
                ++i;
                ++j;
            }
            return i < otherATo ? -1 : (i < this.to ? 1 : 0);
        }

        @Override
        public int compareTo(List<? extends Double> l) {
            if (l instanceof DoubleArrayList) {
                DoubleArrayList other = (DoubleArrayList)l;
                return this.contentsCompareTo(other.a, 0, other.size());
            }
            if (l instanceof SubList) {
                SubList other = (SubList)l;
                return this.contentsCompareTo(other.getParentArray(), other.from, other.to);
            }
            return super.compareTo(l);
        }

        private final class SubListIterator
        extends DoubleIterators.AbstractIndexBasedListIterator {
            SubListIterator(int index) {
                super(0, index);
            }

            @Override
            protected final double get(int i) {
                return DoubleArrayList.this.a[SubList.this.from + i];
            }

            @Override
            protected final void add(int i, double k) {
                SubList.this.add(i, k);
            }

            @Override
            protected final void set(int i, double k) {
                SubList.this.set(i, k);
            }

            @Override
            protected final void remove(int i) {
                SubList.this.removeDouble(i);
            }

            @Override
            protected final int getMaxPos() {
                return SubList.this.to - SubList.this.from;
            }

            @Override
            public double nextDouble() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = this.pos++;
                return DoubleArrayList.this.a[SubList.this.from + this.lastReturned];
            }

            @Override
            public double previousDouble() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = --this.pos;
                return DoubleArrayList.this.a[SubList.this.from + this.pos];
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
                int max = SubList.this.to - SubList.this.from;
                while (this.pos < max) {
                    ++this.pos;
                    this.lastReturned = this.lastReturned;
                    action.accept(DoubleArrayList.this.a[SubList.this.from + this.lastReturned]);
                }
            }
        }

        private final class SubListSpliterator
        extends DoubleSpliterators.LateBindingSizeIndexBasedSpliterator {
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
            protected final double get(int i) {
                return DoubleArrayList.this.a[i];
            }

            @Override
            protected final SubListSpliterator makeForSplit(int pos, int maxPos) {
                return new SubListSpliterator(pos, maxPos);
            }

            @Override
            public boolean tryAdvance(DoubleConsumer action) {
                if (this.pos >= this.getMaxPos()) {
                    return false;
                }
                action.accept(DoubleArrayList.this.a[this.pos++]);
                return true;
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
                int max = this.getMaxPos();
                while (this.pos < max) {
                    action.accept(DoubleArrayList.this.a[this.pos++]);
                }
            }
        }
    }

    private final class Spliterator
    implements DoubleSpliterator {
        boolean hasSplit = false;
        int pos;
        int max;

        public Spliterator() {
            this(0, doubleArrayList.size, false);
        }

        private Spliterator(int pos, int max, boolean hasSplit) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
            this.hasSplit = hasSplit;
        }

        private int getWorkingMax() {
            return this.hasSplit ? this.max : DoubleArrayList.this.size;
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
        public boolean tryAdvance(DoubleConsumer action) {
            if (this.pos >= this.getWorkingMax()) {
                return false;
            }
            action.accept(DoubleArrayList.this.a[this.pos++]);
            return true;
        }

        @Override
        public void forEachRemaining(DoubleConsumer action) {
            int max = this.getWorkingMax();
            while (this.pos < max) {
                action.accept(DoubleArrayList.this.a[this.pos]);
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
        public DoubleSpliterator trySplit() {
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

