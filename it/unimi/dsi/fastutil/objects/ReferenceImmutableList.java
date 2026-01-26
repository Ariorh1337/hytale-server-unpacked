/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
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
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.stream.Collector;

public class ReferenceImmutableList<K>
extends ReferenceLists.ImmutableListBase<K>
implements ReferenceList<K>,
RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = 0L;
    static final ReferenceImmutableList EMPTY = new ReferenceImmutableList<Object>(ObjectArrays.EMPTY_ARRAY);
    private final K[] a;
    private static final Collector<Object, ?, ReferenceImmutableList<Object>> TO_LIST_COLLECTOR = Collector.of(ReferenceArrayList::new, ReferenceArrayList::add, ReferenceArrayList::combine, ReferenceImmutableList::convertTrustedToImmutableList, new Collector.Characteristics[0]);

    private static final <K> K[] emptyArray() {
        return ObjectArrays.EMPTY_ARRAY;
    }

    public ReferenceImmutableList(K[] a) {
        this.a = a;
    }

    public ReferenceImmutableList(Collection<? extends K> c) {
        this(c.isEmpty() ? ReferenceImmutableList.emptyArray() : ObjectIterators.unwrap(c.iterator()));
    }

    public ReferenceImmutableList(ReferenceCollection<? extends K> c) {
        this(c.isEmpty() ? ReferenceImmutableList.emptyArray() : ObjectIterators.unwrap(c.iterator()));
    }

    public ReferenceImmutableList(ReferenceList<? extends K> l) {
        this(l.isEmpty() ? ReferenceImmutableList.emptyArray() : new Object[l.size()]);
        l.getElements(0, this.a, 0, l.size());
    }

    public ReferenceImmutableList(K[] a, int offset, int length) {
        this(length == 0 ? ReferenceImmutableList.emptyArray() : new Object[length]);
        System.arraycopy(a, offset, this.a, 0, length);
    }

    public ReferenceImmutableList(ObjectIterator<? extends K> i) {
        this(i.hasNext() ? ObjectIterators.unwrap(i) : ReferenceImmutableList.emptyArray());
    }

    public static <K> ReferenceImmutableList<K> of() {
        return EMPTY;
    }

    @SafeVarargs
    public static <K> ReferenceImmutableList<K> of(K ... init) {
        return init.length == 0 ? ReferenceImmutableList.of() : new ReferenceImmutableList<K>(init);
    }

    private static <K> ReferenceImmutableList<K> convertTrustedToImmutableList(ReferenceArrayList<K> arrayList) {
        if (arrayList.isEmpty()) {
            return ReferenceImmutableList.of();
        }
        K[] backingArray = arrayList.elements();
        if (arrayList.size() != backingArray.length) {
            backingArray = Arrays.copyOf(backingArray, arrayList.size());
        }
        return new ReferenceImmutableList<K>(backingArray);
    }

    public static <K> Collector<K, ?, ReferenceImmutableList<K>> toList() {
        return TO_LIST_COLLECTOR;
    }

    public static <K> Collector<K, ?, ReferenceImmutableList<K>> toListWithExpectedSize(int expectedSize) {
        if (expectedSize <= 10) {
            return ReferenceImmutableList.toList();
        }
        return Collector.of(new ReferenceCollections.SizeDecreasingSupplier(expectedSize, size -> size <= 10 ? new ReferenceArrayList() : new ReferenceArrayList(size)), ReferenceArrayList::add, ReferenceArrayList::combine, ReferenceImmutableList::convertTrustedToImmutableList, new Collector.Characteristics[0]);
    }

    @Override
    public K get(int index) {
        if (index >= this.a.length) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.a.length + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(Object k) {
        int size = this.a.length;
        for (int i = 0; i < size; ++i) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object k) {
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
    public void getElements(int from, Object[] a, int offset, int length) {
        ObjectArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    @Override
    public void forEach(Consumer<? super K> action) {
        for (int i = 0; i < this.a.length; ++i) {
            action.accept(this.a[i]);
        }
    }

    @Override
    public Object[] toArray() {
        if (this.a.length == 0) {
            return ObjectArrays.EMPTY_ARRAY;
        }
        if (this.a.getClass() == Object[].class) {
            return (Object[])this.a.clone();
        }
        return Arrays.copyOf(this.a, this.a.length, Object[].class);
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
            {
                this.pos = index;
            }

            @Override
            public boolean hasNext() {
                return this.pos < ReferenceImmutableList.this.a.length;
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
                return ReferenceImmutableList.this.a[this.pos++];
            }

            @Override
            public K previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return ReferenceImmutableList.this.a[--this.pos];
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
            public void forEachRemaining(Consumer<? super K> action) {
                while (this.pos < ReferenceImmutableList.this.a.length) {
                    action.accept(ReferenceImmutableList.this.a[this.pos++]);
                }
            }

            @Override
            public void add(K k) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(K k) {
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
                int remaining = ReferenceImmutableList.this.a.length - this.pos;
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
                int remaining = ReferenceImmutableList.this.a.length - this.pos;
                if (n < remaining) {
                    this.pos += n;
                } else {
                    n = remaining;
                    this.pos = ReferenceImmutableList.this.a.length;
                }
                return n;
            }
        };
    }

    @Override
    public ObjectSpliterator<K> spliterator() {
        return new Spliterator();
    }

    @Override
    public ReferenceList<K> subList(int from, int to) {
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

    public ReferenceImmutableList<K> clone() {
        return this;
    }

    public boolean equals(ReferenceImmutableList<K> l) {
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
        K[] a1 = this.a;
        K[] a2 = l.a;
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
        if (o instanceof ReferenceImmutableList) {
            return this.equals((ReferenceImmutableList)o);
        }
        if (o instanceof ImmutableSubList) {
            return ((ImmutableSubList)o).equals(this);
        }
        return super.equals(o);
    }

    private final class Spliterator
    implements ObjectSpliterator<K> {
        int pos;
        int max;

        public Spliterator() {
            this(0, referenceImmutableList.a.length);
        }

        private Spliterator(int pos, int max) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
        }

        @Override
        public int characteristics() {
            return 17488;
        }

        @Override
        public long estimateSize() {
            return this.max - this.pos;
        }

        @Override
        public boolean tryAdvance(Consumer<? super K> action) {
            if (this.pos >= this.max) {
                return false;
            }
            action.accept(ReferenceImmutableList.this.a[this.pos++]);
            return true;
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
            while (this.pos < this.max) {
                action.accept(ReferenceImmutableList.this.a[this.pos]);
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
        public ObjectSpliterator<K> trySplit() {
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

    private static final class ImmutableSubList<K>
    extends ReferenceLists.ImmutableListBase<K>
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 7054639518438982401L;
        final ReferenceImmutableList<K> innerList;
        final int from;
        final int to;
        final transient K[] a;

        ImmutableSubList(ReferenceImmutableList<K> innerList, int from, int to) {
            this.innerList = innerList;
            this.from = from;
            this.to = to;
            this.a = ((ReferenceImmutableList)innerList).a;
        }

        @Override
        public K get(int index) {
            this.ensureRestrictedIndex(index);
            return this.a[index + this.from];
        }

        @Override
        public int indexOf(Object k) {
            for (int i = this.from; i < this.to; ++i) {
                if (k != this.a[i]) continue;
                return i - this.from;
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object k) {
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
        public void getElements(int fromSublistIndex, Object[] a, int offset, int length) {
            ObjectArrays.ensureOffsetLength(a, offset, length);
            this.ensureRestrictedIndex(fromSublistIndex);
            if (this.from + length > this.to) {
                throw new IndexOutOfBoundsException("Final index " + (this.from + length) + " (startingIndex: " + this.from + " + length: " + length + ") is greater then list length " + this.size());
            }
            System.arraycopy(this.a, fromSublistIndex + this.from, a, offset, length);
        }

        @Override
        public void forEach(Consumer<? super K> action) {
            for (int i = this.from; i < this.to; ++i) {
                action.accept(this.a[i]);
            }
        }

        @Override
        public Object[] toArray() {
            return Arrays.copyOfRange(this.a, this.from, this.to, Object[].class);
        }

        @Override
        public <K> K[] toArray(K[] a) {
            int size = this.size();
            if (a == null) {
                a = new Object[size];
            } else if (a.length < size) {
                a = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
            }
            System.arraycopy(this.a, this.from, a, 0, size);
            if (a.length > size) {
                a[size] = null;
            }
            return a;
        }

        @Override
        public ObjectListIterator<K> listIterator(final int index) {
            this.ensureIndex(index);
            return new ObjectListIterator<K>(){
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
                public K next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return a[this.pos++ + from];
                }

                @Override
                public K previous() {
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
                public void forEachRemaining(Consumer<? super K> action) {
                    while (this.pos < to) {
                        action.accept(a[this.pos++ + from]);
                    }
                }

                @Override
                public void add(K k) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void set(K k) {
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
        public ObjectSpliterator<K> spliterator() {
            return new SubListSpliterator();
        }

        boolean contentsEquals(K[] otherA, int otherAFrom, int otherATo) {
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
            if (o instanceof ReferenceImmutableList) {
                ReferenceImmutableList other = (ReferenceImmutableList)o;
                return this.contentsEquals(other.a, 0, other.size());
            }
            if (o instanceof ImmutableSubList) {
                ImmutableSubList other = (ImmutableSubList)o;
                return this.contentsEquals(other.a, other.from, other.to);
            }
            return super.equals(o);
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
        public ReferenceList<K> subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from == to) {
                return EMPTY;
            }
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new ImmutableSubList<K>(this.innerList, from + this.from, to + this.from);
        }

        private final class SubListSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<K> {
            SubListSpliterator() {
                super(ImmutableSubList.this.from, ImmutableSubList.this.to);
            }

            private SubListSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            protected final K get(int i) {
                return ImmutableSubList.this.a[i];
            }

            protected final SubListSpliterator makeForSplit(int pos, int maxPos) {
                return new SubListSpliterator(pos, maxPos);
            }

            @Override
            public boolean tryAdvance(Consumer<? super K> action) {
                if (this.pos >= this.maxPos) {
                    return false;
                }
                action.accept(ImmutableSubList.this.a[this.pos++]);
                return true;
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
                int max = this.maxPos;
                while (this.pos < max) {
                    action.accept(ImmutableSubList.this.a[this.pos++]);
                }
            }

            @Override
            public int characteristics() {
                return 17488;
            }
        }
    }
}

