/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import it.unimi.dsi.fastutil.longs.LongStack;
import it.unimi.dsi.fastutil.longs.LongUnaryOperator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.LongConsumer;

public abstract class AbstractLongList
extends AbstractLongCollection
implements LongList,
LongStack {
    protected AbstractLongList() {
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
    public void add(int index, long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(long k) {
        this.add(this.size(), k);
        return true;
    }

    @Override
    public long removeLong(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long set(int index, long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Long> c) {
        if (c instanceof LongCollection) {
            return this.addAll(index, (LongCollection)c);
        }
        this.ensureIndex(index);
        Iterator<? extends Long> i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, (long)i.next());
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Long> c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public LongListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public LongListIterator listIterator() {
        return this.listIterator(0);
    }

    @Override
    public LongListIterator listIterator(int index) {
        this.ensureIndex(index);
        return new LongIterators.AbstractIndexBasedListIterator(0, index){

            @Override
            protected final long get(int i) {
                return AbstractLongList.this.getLong(i);
            }

            @Override
            protected final void add(int i, long k) {
                AbstractLongList.this.add(i, k);
            }

            @Override
            protected final void set(int i, long k) {
                AbstractLongList.this.set(i, k);
            }

            @Override
            protected final void remove(int i) {
                AbstractLongList.this.removeLong(i);
            }

            @Override
            protected final int getMaxPos() {
                return AbstractLongList.this.size();
            }
        };
    }

    @Override
    public boolean contains(long k) {
        return this.indexOf(k) >= 0;
    }

    @Override
    public int indexOf(long k) {
        LongListIterator i = this.listIterator();
        while (i.hasNext()) {
            long e = i.nextLong();
            if (k != e) continue;
            return i.previousIndex();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(long k) {
        LongListIterator i = this.listIterator(this.size());
        while (i.hasPrevious()) {
            long e = i.previousLong();
            if (k != e) continue;
            return i.nextIndex();
        }
        return -1;
    }

    @Override
    public void size(int size) {
        int i = this.size();
        if (size > i) {
            while (i++ < size) {
                this.add(0L);
            }
        } else {
            while (i-- != size) {
                this.removeLong(i);
            }
        }
    }

    @Override
    public LongList subList(int from, int to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return this instanceof RandomAccess ? new LongRandomAccessSubList(this, from, to) : new LongSubList(this, from, to);
    }

    @Override
    public void forEach(LongConsumer action) {
        if (this instanceof RandomAccess) {
            int max = this.size();
            for (int i = 0; i < max; ++i) {
                action.accept(this.getLong(i));
            }
        } else {
            LongList.super.forEach(action);
        }
    }

    @Override
    public void removeElements(int from, int to) {
        this.ensureIndex(to);
        LongListIterator i = this.listIterator(from);
        int n = to - from;
        if (n < 0) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0) {
            i.nextLong();
            i.remove();
        }
    }

    @Override
    public void addElements(int index, long[] a, int offset, int length) {
        this.ensureIndex(index);
        LongArrays.ensureOffsetLength(a, offset, length);
        if (this instanceof RandomAccess) {
            while (length-- != 0) {
                this.add(index++, a[offset++]);
            }
        } else {
            LongListIterator iter = this.listIterator(index);
            while (length-- != 0) {
                iter.add(a[offset++]);
            }
        }
    }

    @Override
    public void addElements(int index, long[] a) {
        this.addElements(index, a, 0, a.length);
    }

    @Override
    public void getElements(int from, long[] a, int offset, int length) {
        this.ensureIndex(from);
        LongArrays.ensureOffsetLength(a, offset, length);
        if (from + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
        }
        if (this instanceof RandomAccess) {
            int current = from;
            while (length-- != 0) {
                a[offset++] = this.getLong(current++);
            }
        } else {
            LongListIterator i = this.listIterator(from);
            while (length-- != 0) {
                a[offset++] = i.nextLong();
            }
        }
    }

    @Override
    public void setElements(int index, long[] a, int offset, int length) {
        this.ensureIndex(index);
        LongArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
        }
        if (this instanceof RandomAccess) {
            for (int i = 0; i < length; ++i) {
                this.set(i + index, a[i + offset]);
            }
        } else {
            LongListIterator iter = this.listIterator(index);
            int i = 0;
            while (i < length) {
                iter.nextLong();
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
        LongListIterator i = this.iterator();
        int h = 1;
        int s = this.size();
        while (s-- != 0) {
            long k = i.nextLong();
            h = 31 * h + HashCommon.long2int(k);
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
        if (l instanceof LongList) {
            LongListIterator i1 = this.listIterator();
            LongListIterator i2 = ((LongList)l).listIterator();
            while (s-- != 0) {
                if (i1.nextLong() == i2.nextLong()) continue;
                return false;
            }
            return true;
        }
        LongListIterator i1 = this.listIterator();
        ListIterator i2 = l.listIterator();
        while (s-- != 0) {
            if (Objects.equals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(List<? extends Long> l) {
        if (l == this) {
            return 0;
        }
        if (l instanceof LongList) {
            LongListIterator i1 = this.listIterator();
            LongListIterator i2 = ((LongList)l).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                long e2;
                long e1 = i1.nextLong();
                int r = Long.compare(e1, e2 = i2.nextLong());
                if (r == 0) continue;
                return r;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        LongListIterator i1 = this.listIterator();
        ListIterator<? extends Long> i2 = l.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r = ((Comparable)i1.next()).compareTo(i2.next());
            if (r == 0) continue;
            return r;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public void push(long o) {
        this.add(o);
    }

    @Override
    public long popLong() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeLong(this.size() - 1);
    }

    @Override
    public long topLong() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getLong(this.size() - 1);
    }

    @Override
    public long peekLong(int i) {
        return this.getLong(this.size() - 1 - i);
    }

    @Override
    public boolean rem(long k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeLong(index);
        return true;
    }

    @Override
    public long[] toLongArray() {
        int size = this.size();
        if (size == 0) {
            return LongArrays.EMPTY_ARRAY;
        }
        long[] ret = new long[size];
        this.getElements(0, ret, 0, size);
        return ret;
    }

    @Override
    public long[] toArray(long[] a) {
        int size = this.size();
        if (a.length < size) {
            a = Arrays.copyOf(a, size);
        }
        this.getElements(0, a, 0, size);
        return a;
    }

    @Override
    public boolean addAll(int index, LongCollection c) {
        this.ensureIndex(index);
        LongIterator i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.nextLong());
        }
        return retVal;
    }

    @Override
    public boolean addAll(LongCollection c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public final void replaceAll(LongUnaryOperator operator) {
        this.replaceAll((java.util.function.LongUnaryOperator)operator);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        LongListIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("[");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            long k = i.nextLong();
            s.append(String.valueOf(k));
        }
        s.append("]");
        return s.toString();
    }

    public static class LongRandomAccessSubList
    extends LongSubList
    implements RandomAccess {
        private static final long serialVersionUID = -107070782945191929L;

        public LongRandomAccessSubList(LongList l, int from, int to) {
            super(l, from, to);
        }

        @Override
        public LongList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new LongRandomAccessSubList(this, from, to);
        }
    }

    public static class LongSubList
    extends AbstractLongList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongList l;
        protected final int from;
        protected int to;

        public LongSubList(LongList l, int from, int to) {
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
        public boolean add(long k) {
            this.l.add(this.to, k);
            ++this.to;
            assert (this.assertRange());
            return true;
        }

        @Override
        public void add(int index, long k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(int index, Collection<? extends Long> c) {
            this.ensureIndex(index);
            this.to += c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public long getLong(int index) {
            this.ensureRestrictedIndex(index);
            return this.l.getLong(this.from + index);
        }

        @Override
        public long removeLong(int index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeLong(this.from + index);
        }

        @Override
        public long set(int index, long k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public int size() {
            return this.to - this.from;
        }

        @Override
        public void getElements(int from, long[] a, int offset, int length) {
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
        public void addElements(int index, long[] a, int offset, int length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public void setElements(int index, long[] a, int offset, int length) {
            this.ensureIndex(index);
            this.l.setElements(this.from + index, a, offset, length);
            assert (this.assertRange());
        }

        @Override
        public LongListIterator listIterator(int index) {
            this.ensureIndex(index);
            return this.l instanceof RandomAccess ? new RandomAccessIter(index) : new ParentWrappingIter(this.l.listIterator(index + this.from));
        }

        @Override
        public LongSpliterator spliterator() {
            return this.l instanceof RandomAccess ? new IndexBasedSpliterator(this.l, this.from, this.to) : super.spliterator();
        }

        @Override
        public LongList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new LongSubList(this, from, to);
        }

        @Override
        public boolean rem(long k) {
            int index = this.indexOf(k);
            if (index == -1) {
                return false;
            }
            --this.to;
            this.l.removeLong(this.from + index);
            assert (this.assertRange());
            return true;
        }

        @Override
        public boolean addAll(int index, LongCollection c) {
            this.ensureIndex(index);
            return super.addAll(index, c);
        }

        @Override
        public boolean addAll(int index, LongList l) {
            this.ensureIndex(index);
            return super.addAll(index, l);
        }

        private final class RandomAccessIter
        extends LongIterators.AbstractIndexBasedListIterator {
            RandomAccessIter(int pos) {
                super(0, pos);
            }

            @Override
            protected final long get(int i) {
                return LongSubList.this.l.getLong(LongSubList.this.from + i);
            }

            @Override
            protected final void add(int i, long k) {
                LongSubList.this.add(i, k);
            }

            @Override
            protected final void set(int i, long k) {
                LongSubList.this.set(i, k);
            }

            @Override
            protected final void remove(int i) {
                LongSubList.this.removeLong(i);
            }

            @Override
            protected final int getMaxPos() {
                return LongSubList.this.to - LongSubList.this.from;
            }

            @Override
            public void add(long k) {
                super.add(k);
                assert (LongSubList.this.assertRange());
            }

            @Override
            public void remove() {
                super.remove();
                assert (LongSubList.this.assertRange());
            }
        }

        private class ParentWrappingIter
        implements LongListIterator {
            private LongListIterator parent;

            ParentWrappingIter(LongListIterator parent) {
                this.parent = parent;
            }

            @Override
            public int nextIndex() {
                return this.parent.nextIndex() - LongSubList.this.from;
            }

            @Override
            public int previousIndex() {
                return this.parent.previousIndex() - LongSubList.this.from;
            }

            @Override
            public boolean hasNext() {
                return this.parent.nextIndex() < LongSubList.this.to;
            }

            @Override
            public boolean hasPrevious() {
                return this.parent.previousIndex() >= LongSubList.this.from;
            }

            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return this.parent.nextLong();
            }

            @Override
            public long previousLong() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return this.parent.previousLong();
            }

            @Override
            public void add(long k) {
                this.parent.add(k);
            }

            @Override
            public void set(long k) {
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
                if (parentNewPos < LongSubList.this.from - 1) {
                    parentNewPos = LongSubList.this.from - 1;
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
                if (parentNewPos > LongSubList.this.to) {
                    parentNewPos = LongSubList.this.to;
                }
                int toSkip = parentNewPos - currentPos;
                return this.parent.skip(toSkip);
            }
        }
    }

    static final class IndexBasedSpliterator
    extends LongSpliterators.LateBindingSizeIndexBasedSpliterator {
        final LongList l;

        IndexBasedSpliterator(LongList l, int pos) {
            super(pos);
            this.l = l;
        }

        IndexBasedSpliterator(LongList l, int pos, int maxPos) {
            super(pos, maxPos);
            this.l = l;
        }

        @Override
        protected final int getMaxPosFromBackingStore() {
            return this.l.size();
        }

        @Override
        protected final long get(int i) {
            return this.l.getLong(i);
        }

        @Override
        protected final IndexBasedSpliterator makeForSplit(int pos, int maxPos) {
            return new IndexBasedSpliterator(this.l, pos, maxPos);
        }
    }
}

