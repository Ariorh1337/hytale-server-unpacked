/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import it.unimi.dsi.fastutil.doubles.DoubleStack;
import it.unimi.dsi.fastutil.doubles.DoubleUnaryOperator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.DoubleConsumer;

public abstract class AbstractDoubleList
extends AbstractDoubleCollection
implements DoubleList,
DoubleStack {
    protected AbstractDoubleList() {
    }

    protected void ensureIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
        }
    }

    protected void ensureRestrictedIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index >= this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size() + ")");
        }
    }

    @Override
    public void add(int index, double k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(double k) {
        this.add(this.size(), k);
        return true;
    }

    @Override
    public double removeDouble(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double set(int index, double k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Double> c) {
        if (c instanceof DoubleCollection) {
            return this.addAll(index, (DoubleCollection)c);
        }
        this.ensureIndex(index);
        Iterator<? extends Double> i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, (double)i.next());
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Double> c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public DoubleListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public DoubleListIterator listIterator() {
        return this.listIterator(0);
    }

    @Override
    public DoubleListIterator listIterator(int index) {
        this.ensureIndex(index);
        return new DoubleIterators.AbstractIndexBasedListIterator(0, index){

            @Override
            protected final double get(int i) {
                return AbstractDoubleList.this.getDouble(i);
            }

            @Override
            protected final void add(int i, double k) {
                AbstractDoubleList.this.add(i, k);
            }

            @Override
            protected final void set(int i, double k) {
                AbstractDoubleList.this.set(i, k);
            }

            @Override
            protected final void remove(int i) {
                AbstractDoubleList.this.removeDouble(i);
            }

            @Override
            protected final int getMaxPos() {
                return AbstractDoubleList.this.size();
            }
        };
    }

    @Override
    public boolean contains(double k) {
        return this.indexOf(k) >= 0;
    }

    @Override
    public int indexOf(double k) {
        DoubleListIterator i = this.listIterator();
        while (i.hasNext()) {
            double e = i.nextDouble();
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(e)) continue;
            return i.previousIndex();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(double k) {
        DoubleListIterator i = this.listIterator(this.size());
        while (i.hasPrevious()) {
            double e = i.previousDouble();
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(e)) continue;
            return i.nextIndex();
        }
        return -1;
    }

    @Override
    public void size(int size) {
        int i = this.size();
        if (size > i) {
            while (i++ < size) {
                this.add(0.0);
            }
        } else {
            while (i-- != size) {
                this.removeDouble(i);
            }
        }
    }

    @Override
    public DoubleList subList(int from, int to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return this instanceof RandomAccess ? new DoubleRandomAccessSubList(this, from, to) : new DoubleSubList(this, from, to);
    }

    @Override
    public void forEach(DoubleConsumer action) {
        if (this instanceof RandomAccess) {
            int max = this.size();
            for (int i = 0; i < max; ++i) {
                action.accept(this.getDouble(i));
            }
        } else {
            DoubleList.super.forEach(action);
        }
    }

    @Override
    public void removeElements(int from, int to) {
        this.ensureIndex(to);
        DoubleListIterator i = this.listIterator(from);
        int n = to - from;
        if (n < 0) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0) {
            i.nextDouble();
            i.remove();
        }
    }

    @Override
    public void addElements(int index, double[] a, int offset, int length) {
        this.ensureIndex(index);
        DoubleArrays.ensureOffsetLength(a, offset, length);
        if (this instanceof RandomAccess) {
            while (length-- != 0) {
                this.add(index++, a[offset++]);
            }
        } else {
            DoubleListIterator iter = this.listIterator(index);
            while (length-- != 0) {
                iter.add(a[offset++]);
            }
        }
    }

    @Override
    public void addElements(int index, double[] a) {
        this.addElements(index, a, 0, a.length);
    }

    @Override
    public void getElements(int from, double[] a, int offset, int length) {
        this.ensureIndex(from);
        DoubleArrays.ensureOffsetLength(a, offset, length);
        if (from + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
        }
        if (this instanceof RandomAccess) {
            int current = from;
            while (length-- != 0) {
                a[offset++] = this.getDouble(current++);
            }
        } else {
            DoubleListIterator i = this.listIterator(from);
            while (length-- != 0) {
                a[offset++] = i.nextDouble();
            }
        }
    }

    @Override
    public void setElements(int index, double[] a, int offset, int length) {
        this.ensureIndex(index);
        DoubleArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
        }
        if (this instanceof RandomAccess) {
            for (int i = 0; i < length; ++i) {
                this.set(i + index, a[i + offset]);
            }
        } else {
            DoubleListIterator iter = this.listIterator(index);
            int i = 0;
            while (i < length) {
                iter.nextDouble();
                iter.set(a[offset + i++]);
            }
        }
    }

    @Override
    public void clear() {
        this.removeElements(0, this.size());
    }

    @Override
    public int hashCode() {
        DoubleListIterator i = this.iterator();
        int h = 1;
        int s = this.size();
        while (s-- != 0) {
            double k = i.nextDouble();
            h = 31 * h + HashCommon.double2int(k);
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        List l = (List)o;
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        if (l instanceof DoubleList) {
            DoubleListIterator i1 = this.listIterator();
            DoubleListIterator i2 = ((DoubleList)l).listIterator();
            while (s-- != 0) {
                if (i1.nextDouble() == i2.nextDouble()) continue;
                return false;
            }
            return true;
        }
        DoubleListIterator i1 = this.listIterator();
        ListIterator i2 = l.listIterator();
        while (s-- != 0) {
            if (Objects.equals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(List<? extends Double> l) {
        if (l == this) {
            return 0;
        }
        if (l instanceof DoubleList) {
            DoubleListIterator i1 = this.listIterator();
            DoubleListIterator i2 = ((DoubleList)l).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                double e2;
                double e1 = i1.nextDouble();
                int r = Double.compare(e1, e2 = i2.nextDouble());
                if (r == 0) continue;
                return r;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        DoubleListIterator i1 = this.listIterator();
        ListIterator<? extends Double> i2 = l.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r = ((Comparable)i1.next()).compareTo(i2.next());
            if (r == 0) continue;
            return r;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public void push(double o) {
        this.add(o);
    }

    @Override
    public double popDouble() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeDouble(this.size() - 1);
    }

    @Override
    public double topDouble() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getDouble(this.size() - 1);
    }

    @Override
    public double peekDouble(int i) {
        return this.getDouble(this.size() - 1 - i);
    }

    @Override
    public boolean rem(double k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeDouble(index);
        return true;
    }

    @Override
    public double[] toDoubleArray() {
        int size = this.size();
        if (size == 0) {
            return DoubleArrays.EMPTY_ARRAY;
        }
        double[] ret = new double[size];
        this.getElements(0, ret, 0, size);
        return ret;
    }

    @Override
    public double[] toArray(double[] a) {
        int size = this.size();
        if (a.length < size) {
            a = Arrays.copyOf(a, size);
        }
        this.getElements(0, a, 0, size);
        return a;
    }

    @Override
    public boolean addAll(int index, DoubleCollection c) {
        this.ensureIndex(index);
        DoubleIterator i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.nextDouble());
        }
        return retVal;
    }

    @Override
    public boolean addAll(DoubleCollection c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public final void replaceAll(DoubleUnaryOperator operator) {
        this.replaceAll((java.util.function.DoubleUnaryOperator)operator);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        DoubleListIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("[");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            double k = i.nextDouble();
            s.append(String.valueOf(k));
        }
        s.append("]");
        return s.toString();
    }

    public static class DoubleRandomAccessSubList
    extends DoubleSubList
    implements RandomAccess {
        private static final long serialVersionUID = -107070782945191929L;

        public DoubleRandomAccessSubList(DoubleList l, int from, int to) {
            super(l, from, to);
        }

        @Override
        public DoubleList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new DoubleRandomAccessSubList(this, from, to);
        }
    }

    public static class DoubleSubList
    extends AbstractDoubleList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleList l;
        protected final int from;
        protected int to;

        public DoubleSubList(DoubleList l, int from, int to) {
            this.l = l;
            this.from = from;
            this.to = to;
        }

        private boolean assertRange() {
            assert (this.from <= this.l.size());
            assert (this.to <= this.l.size());
            assert (this.to >= this.from);
            return true;
        }

        @Override
        public boolean add(double k) {
            this.l.add(this.to, k);
            ++this.to;
            assert (this.assertRange());
            return true;
        }

        @Override
        public void add(int index, double k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(int index, Collection<? extends Double> c) {
            this.ensureIndex(index);
            this.to += c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public double getDouble(int index) {
            this.ensureRestrictedIndex(index);
            return this.l.getDouble(this.from + index);
        }

        @Override
        public double removeDouble(int index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeDouble(this.from + index);
        }

        @Override
        public double set(int index, double k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public int size() {
            return this.to - this.from;
        }

        @Override
        public void getElements(int from, double[] a, int offset, int length) {
            this.ensureIndex(from);
            if (from + length > this.size()) {
                throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size() + ")");
            }
            this.l.getElements(this.from + from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            this.l.removeElements(this.from + from, this.from + to);
            this.to -= to - from;
            assert (this.assertRange());
        }

        @Override
        public void addElements(int index, double[] a, int offset, int length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public void setElements(int index, double[] a, int offset, int length) {
            this.ensureIndex(index);
            this.l.setElements(this.from + index, a, offset, length);
            assert (this.assertRange());
        }

        @Override
        public DoubleListIterator listIterator(int index) {
            this.ensureIndex(index);
            return this.l instanceof RandomAccess ? new RandomAccessIter(index) : new ParentWrappingIter(this.l.listIterator(index + this.from));
        }

        @Override
        public DoubleSpliterator spliterator() {
            return this.l instanceof RandomAccess ? new IndexBasedSpliterator(this.l, this.from, this.to) : super.spliterator();
        }

        @Override
        public DoubleList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new DoubleSubList(this, from, to);
        }

        @Override
        public boolean rem(double k) {
            int index = this.indexOf(k);
            if (index == -1) {
                return false;
            }
            --this.to;
            this.l.removeDouble(this.from + index);
            assert (this.assertRange());
            return true;
        }

        @Override
        public boolean addAll(int index, DoubleCollection c) {
            this.ensureIndex(index);
            return super.addAll(index, c);
        }

        @Override
        public boolean addAll(int index, DoubleList l) {
            this.ensureIndex(index);
            return super.addAll(index, l);
        }

        private final class RandomAccessIter
        extends DoubleIterators.AbstractIndexBasedListIterator {
            RandomAccessIter(int pos) {
                super(0, pos);
            }

            @Override
            protected final double get(int i) {
                return DoubleSubList.this.l.getDouble(DoubleSubList.this.from + i);
            }

            @Override
            protected final void add(int i, double k) {
                DoubleSubList.this.add(i, k);
            }

            @Override
            protected final void set(int i, double k) {
                DoubleSubList.this.set(i, k);
            }

            @Override
            protected final void remove(int i) {
                DoubleSubList.this.removeDouble(i);
            }

            @Override
            protected final int getMaxPos() {
                return DoubleSubList.this.to - DoubleSubList.this.from;
            }

            @Override
            public void add(double k) {
                super.add(k);
                assert (DoubleSubList.this.assertRange());
            }

            @Override
            public void remove() {
                super.remove();
                assert (DoubleSubList.this.assertRange());
            }
        }

        private class ParentWrappingIter
        implements DoubleListIterator {
            private DoubleListIterator parent;

            ParentWrappingIter(DoubleListIterator parent) {
                this.parent = parent;
            }

            @Override
            public int nextIndex() {
                return this.parent.nextIndex() - DoubleSubList.this.from;
            }

            @Override
            public int previousIndex() {
                return this.parent.previousIndex() - DoubleSubList.this.from;
            }

            @Override
            public boolean hasNext() {
                return this.parent.nextIndex() < DoubleSubList.this.to;
            }

            @Override
            public boolean hasPrevious() {
                return this.parent.previousIndex() >= DoubleSubList.this.from;
            }

            @Override
            public double nextDouble() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return this.parent.nextDouble();
            }

            @Override
            public double previousDouble() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return this.parent.previousDouble();
            }

            @Override
            public void add(double k) {
                this.parent.add(k);
            }

            @Override
            public void set(double k) {
                this.parent.set(k);
            }

            @Override
            public void remove() {
                this.parent.remove();
            }

            @Override
            public int back(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int currentPos = this.parent.previousIndex();
                int parentNewPos = currentPos - n;
                if (parentNewPos < DoubleSubList.this.from - 1) {
                    parentNewPos = DoubleSubList.this.from - 1;
                }
                int toSkip = parentNewPos - currentPos;
                return this.parent.back(toSkip);
            }

            @Override
            public int skip(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int currentPos = this.parent.nextIndex();
                int parentNewPos = currentPos + n;
                if (parentNewPos > DoubleSubList.this.to) {
                    parentNewPos = DoubleSubList.this.to;
                }
                int toSkip = parentNewPos - currentPos;
                return this.parent.skip(toSkip);
            }
        }
    }

    static final class IndexBasedSpliterator
    extends DoubleSpliterators.LateBindingSizeIndexBasedSpliterator {
        final DoubleList l;

        IndexBasedSpliterator(DoubleList l, int pos) {
            super(pos);
            this.l = l;
        }

        IndexBasedSpliterator(DoubleList l, int pos, int maxPos) {
            super(pos, maxPos);
            this.l = l;
        }

        @Override
        protected final int getMaxPosFromBackingStore() {
            return this.l.size();
        }

        @Override
        protected final double get(int i) {
            return this.l.getDouble(i);
        }

        @Override
        protected final IndexBasedSpliterator makeForSplit(int pos, int maxPos) {
            return new IndexBasedSpliterator(this.l, pos, maxPos);
        }
    }
}

