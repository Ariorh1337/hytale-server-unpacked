/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanIterators;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanLists;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class BooleanImmutableList
extends BooleanLists.ImmutableListBase
implements BooleanList,
RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = 0L;
    static final BooleanImmutableList EMPTY = new BooleanImmutableList(BooleanArrays.EMPTY_ARRAY);
    private final boolean[] a;

    public BooleanImmutableList(boolean[] a) {
        this.a = a;
    }

    public BooleanImmutableList(Collection<? extends Boolean> c) {
        this(c.isEmpty() ? BooleanArrays.EMPTY_ARRAY : BooleanIterators.unwrap(BooleanIterators.asBooleanIterator(c.iterator())));
    }

    public BooleanImmutableList(BooleanCollection c) {
        this(c.isEmpty() ? BooleanArrays.EMPTY_ARRAY : BooleanIterators.unwrap(c.iterator()));
    }

    public BooleanImmutableList(BooleanList l) {
        this(l.isEmpty() ? BooleanArrays.EMPTY_ARRAY : new boolean[l.size()]);
        l.getElements(0, this.a, 0, l.size());
    }

    public BooleanImmutableList(boolean[] a, int offset, int length) {
        this(length == 0 ? BooleanArrays.EMPTY_ARRAY : new boolean[length]);
        System.arraycopy(a, offset, this.a, 0, length);
    }

    public BooleanImmutableList(BooleanIterator i) {
        this(i.hasNext() ? BooleanIterators.unwrap(i) : BooleanArrays.EMPTY_ARRAY);
    }

    public static BooleanImmutableList of() {
        return EMPTY;
    }

    public static BooleanImmutableList of(boolean ... init) {
        return init.length == 0 ? BooleanImmutableList.of() : new BooleanImmutableList(init);
    }

    @Override
    public boolean getBoolean(int index) {
        if (index >= this.a.length) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.a.length + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(boolean k) {
        int size = this.a.length;
        for (int i = 0; i < size; ++i) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(boolean k) {
        int i = this.a.length;
        while (i-- != 0) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int size() {
        return this.a.length;
    }

    @Override
    public boolean isEmpty() {
        return this.a.length == 0;
    }

    @Override
    public void getElements(int from, boolean[] a, int offset, int length) {
        BooleanArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    @Override
    public void forEach(BooleanConsumer action) {
        for (int i = 0; i < this.a.length; ++i) {
            action.accept(this.a[i]);
        }
    }

    @Override
    public boolean[] toBooleanArray() {
        if (this.a.length == 0) {
            return BooleanArrays.EMPTY_ARRAY;
        }
        return (boolean[])this.a.clone();
    }

    @Override
    public boolean[] toArray(boolean[] a) {
        if (a == null || a.length < this.size()) {
            a = new boolean[this.a.length];
        }
        System.arraycopy(this.a, 0, a, 0, a.length);
        return a;
    }

    @Override
    public BooleanListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new BooleanListIterator(){
            int pos;
            {
                this.pos = index;
            }

            @Override
            public boolean hasNext() {
                return this.pos < BooleanImmutableList.this.a.length;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public boolean nextBoolean() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return BooleanImmutableList.this.a[this.pos++];
            }

            @Override
            public boolean previousBoolean() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return BooleanImmutableList.this.a[--this.pos];
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
            public void forEachRemaining(BooleanConsumer action) {
                while (this.pos < BooleanImmutableList.this.a.length) {
                    action.accept(BooleanImmutableList.this.a[this.pos++]);
                }
            }

            @Override
            public void add(boolean k) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(boolean k) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int back(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = BooleanImmutableList.this.a.length - this.pos;
                if (n < remaining) {
                    this.pos -= n;
                } else {
                    n = remaining;
                    this.pos = 0;
                }
                return n;
            }

            @Override
            public int skip(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = BooleanImmutableList.this.a.length - this.pos;
                if (n < remaining) {
                    this.pos += n;
                } else {
                    n = remaining;
                    this.pos = BooleanImmutableList.this.a.length;
                }
                return n;
            }
        };
    }

    @Override
    public BooleanSpliterator spliterator() {
        return new Spliterator();
    }

    @Override
    public BooleanList subList(int from, int to) {
        if (from == 0 && to == this.size()) {
            return this;
        }
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from == to) {
            return EMPTY;
        }
        if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return new ImmutableSubList(this, from, to);
    }

    public BooleanImmutableList clone() {
        return this;
    }

    public boolean equals(BooleanImmutableList l) {
        if (l == this) {
            return true;
        }
        if (this.a == l.a) {
            return true;
        }
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        boolean[] a1 = this.a;
        boolean[] a2 = l.a;
        return Arrays.equals(a1, a2);
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
        if (o instanceof BooleanImmutableList) {
            return this.equals((BooleanImmutableList)o);
        }
        if (o instanceof ImmutableSubList) {
            return ((ImmutableSubList)o).equals(this);
        }
        return super.equals(o);
    }

    @Override
    public int compareTo(BooleanImmutableList l) {
        int i;
        if (this.a == l.a) {
            return 0;
        }
        int s1 = this.size();
        int s2 = l.size();
        boolean[] a1 = this.a;
        boolean[] a2 = l.a;
        for (i = 0; i < s1 && i < s2; ++i) {
            boolean e1 = a1[i];
            boolean e2 = a2[i];
            int r = Boolean.compare(e1, e2);
            if (r == 0) continue;
            return r;
        }
        return i < s2 ? -1 : (i < s1 ? 1 : 0);
    }

    @Override
    public int compareTo(List<? extends Boolean> l) {
        if (l instanceof BooleanImmutableList) {
            return this.compareTo((BooleanImmutableList)l);
        }
        if (l instanceof ImmutableSubList) {
            ImmutableSubList other = (ImmutableSubList)l;
            return -other.compareTo(this);
        }
        return super.compareTo(l);
    }

    private final class Spliterator
    implements BooleanSpliterator {
        int pos;
        int max;

        public Spliterator() {
            this(0, booleanImmutableList.a.length);
        }

        private Spliterator(int pos, int max) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
        }

        @Override
        public int characteristics() {
            return 17744;
        }

        @Override
        public long estimateSize() {
            return this.max - this.pos;
        }

        @Override
        public boolean tryAdvance(BooleanConsumer action) {
            if (this.pos >= this.max) {
                return false;
            }
            action.accept(BooleanImmutableList.this.a[this.pos++]);
            return true;
        }

        @Override
        public void forEachRemaining(BooleanConsumer action) {
            while (this.pos < this.max) {
                action.accept(BooleanImmutableList.this.a[this.pos]);
                ++this.pos;
            }
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (this.pos >= this.max) {
                return 0L;
            }
            int remaining = this.max - this.pos;
            if (n < (long)remaining) {
                this.pos = SafeMath.safeLongToInt((long)this.pos + n);
                return n;
            }
            n = remaining;
            this.pos = this.max;
            return n;
        }

        @Override
        public BooleanSpliterator trySplit() {
            int myNewPos;
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
                return null;
            }
            int retMax = myNewPos = this.pos + retLen;
            int oldPos = this.pos;
            this.pos = myNewPos;
            return new Spliterator(oldPos, retMax);
        }
    }

    private static final class ImmutableSubList
    extends BooleanLists.ImmutableListBase
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 7054639518438982401L;
        final BooleanImmutableList innerList;
        final int from;
        final int to;
        final transient boolean[] a;

        ImmutableSubList(BooleanImmutableList innerList, int from, int to) {
            this.innerList = innerList;
            this.from = from;
            this.to = to;
            this.a = innerList.a;
        }

        @Override
        public boolean getBoolean(int index) {
            this.ensureRestrictedIndex(index);
            return this.a[index + this.from];
        }

        @Override
        public int indexOf(boolean k) {
            for (int i = this.from; i < this.to; ++i) {
                if (k != this.a[i]) continue;
                return i - this.from;
            }
            return -1;
        }

        @Override
        public int lastIndexOf(boolean k) {
            int i = this.to;
            while (i-- != this.from) {
                if (k != this.a[i]) continue;
                return i - this.from;
            }
            return -1;
        }

        @Override
        public int size() {
            return this.to - this.from;
        }

        @Override
        public boolean isEmpty() {
            return this.to <= this.from;
        }

        @Override
        public void getElements(int fromSublistIndex, boolean[] a, int offset, int length) {
            BooleanArrays.ensureOffsetLength(a, offset, length);
            this.ensureRestrictedIndex(fromSublistIndex);
            if (this.from + length > this.to) {
                throw new IndexOutOfBoundsException("Final index " + (this.from + length) + " (startingIndex: " + this.from + " + length: " + length + ") is greater then list length " + this.size());
            }
            System.arraycopy(this.a, fromSublistIndex + this.from, a, offset, length);
        }

        @Override
        public void forEach(BooleanConsumer action) {
            for (int i = this.from; i < this.to; ++i) {
                action.accept(this.a[i]);
            }
        }

        @Override
        public boolean[] toBooleanArray() {
            return Arrays.copyOfRange(this.a, this.from, this.to);
        }

        @Override
        public boolean[] toArray(boolean[] a) {
            if (a == null || a.length < this.size()) {
                a = new boolean[this.size()];
            }
            System.arraycopy(this.a, this.from, a, 0, this.size());
            return a;
        }

        @Override
        public BooleanListIterator listIterator(final int index) {
            this.ensureIndex(index);
            return new BooleanListIterator(){
                int pos;
                {
                    this.pos = index;
                }

                @Override
                public boolean hasNext() {
                    return this.pos < to;
                }

                @Override
                public boolean hasPrevious() {
                    return this.pos > from;
                }

                @Override
                public boolean nextBoolean() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return a[this.pos++ + from];
                }

                @Override
                public boolean previousBoolean() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    return a[--this.pos + from];
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
                public void forEachRemaining(BooleanConsumer action) {
                    while (this.pos < to) {
                        action.accept(a[this.pos++ + from]);
                    }
                }

                @Override
                public void add(boolean k) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void set(boolean k) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int back(int n) {
                    if (n < 0) {
                        throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                    }
                    int remaining = to - this.pos;
                    if (n < remaining) {
                        this.pos -= n;
                    } else {
                        n = remaining;
                        this.pos = 0;
                    }
                    return n;
                }

                @Override
                public int skip(int n) {
                    if (n < 0) {
                        throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                    }
                    int remaining = to - this.pos;
                    if (n < remaining) {
                        this.pos += n;
                    } else {
                        n = remaining;
                        this.pos = to;
                    }
                    return n;
                }
            };
        }

        @Override
        public BooleanSpliterator spliterator() {
            return new SubListSpliterator();
        }

        boolean contentsEquals(boolean[] otherA, int otherAFrom, int otherATo) {
            if (this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return true;
            }
            if (otherATo - otherAFrom != this.size()) {
                return false;
            }
            int pos = this.from;
            int otherPos = otherAFrom;
            while (pos < this.to) {
                if (this.a[pos++] == otherA[otherPos++]) continue;
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
            if (o instanceof BooleanImmutableList) {
                BooleanImmutableList other = (BooleanImmutableList)o;
                return this.contentsEquals(other.a, 0, other.size());
            }
            if (o instanceof ImmutableSubList) {
                ImmutableSubList other = (ImmutableSubList)o;
                return this.contentsEquals(other.a, other.from, other.to);
            }
            return super.equals(o);
        }

        int contentsCompareTo(boolean[] otherA, int otherAFrom, int otherATo) {
            if (this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return 0;
            }
            int i = this.from;
            int j = otherAFrom;
            while (i < this.to && i < otherATo) {
                boolean e1 = this.a[i];
                boolean e2 = otherA[j];
                int r = Boolean.compare(e1, e2);
                if (r != 0) {
                    return r;
                }
                ++i;
                ++j;
            }
            return i < otherATo ? -1 : (i < this.to ? 1 : 0);
        }

        @Override
        public int compareTo(List<? extends Boolean> l) {
            if (l instanceof BooleanImmutableList) {
                BooleanImmutableList other = (BooleanImmutableList)l;
                return this.contentsCompareTo(other.a, 0, other.size());
            }
            if (l instanceof ImmutableSubList) {
                ImmutableSubList other = (ImmutableSubList)l;
                return this.contentsCompareTo(other.a, other.from, other.to);
            }
            return super.compareTo(l);
        }

        private Object readResolve() throws ObjectStreamException {
            try {
                return this.innerList.subList(this.from, this.to);
            }
            catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                throw (InvalidObjectException)new InvalidObjectException(ex.getMessage()).initCause(ex);
            }
        }

        @Override
        public BooleanList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from == to) {
                return EMPTY;
            }
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new ImmutableSubList(this.innerList, from + this.from, to + this.from);
        }

        private final class SubListSpliterator
        extends BooleanSpliterators.EarlyBindingSizeIndexBasedSpliterator {
            SubListSpliterator() {
                super(ImmutableSubList.this.from, ImmutableSubList.this.to);
            }

            private SubListSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            protected final boolean get(int i) {
                return ImmutableSubList.this.a[i];
            }

            @Override
            protected final SubListSpliterator makeForSplit(int pos, int maxPos) {
                return new SubListSpliterator(pos, maxPos);
            }

            @Override
            public boolean tryAdvance(BooleanConsumer action) {
                if (this.pos >= this.maxPos) {
                    return false;
                }
                action.accept(ImmutableSubList.this.a[this.pos++]);
                return true;
            }

            @Override
            public void forEachRemaining(BooleanConsumer action) {
                int max = this.maxPos;
                while (this.pos < max) {
                    action.accept(ImmutableSubList.this.a[this.pos++]);
                }
            }

            @Override
            public int characteristics() {
                return 17744;
            }
        }
    }
}

