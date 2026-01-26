/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLongList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import java.util.function.LongUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class LongLists {
    public static final EmptyList EMPTY_LIST = new EmptyList();

    private LongLists() {
    }

    public static LongList shuffle(LongList l, Random random) {
        int i = l.size();
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            long t = l.getLong(i);
            l.set(i, l.getLong(p));
            l.set(p, t);
        }
        return l;
    }

    public static LongList emptyList() {
        return EMPTY_LIST;
    }

    public static LongList singleton(long element) {
        return new Singleton(element);
    }

    public static LongList singleton(Object element) {
        return new Singleton((Long)element);
    }

    public static LongList synchronize(LongList l) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l) : new SynchronizedList(l);
    }

    public static LongList synchronize(LongList l, Object sync) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l, sync) : new SynchronizedList(l, sync);
    }

    public static LongList unmodifiable(LongList l) {
        return l instanceof RandomAccess ? new UnmodifiableRandomAccessList(l) : new UnmodifiableList(l);
    }

    public static class EmptyList
    extends LongCollections.EmptyCollection
    implements LongList,
    RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyList() {
        }

        @Override
        public long getLong(int i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long removeLong(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long set(int index, long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(long k) {
            return -1;
        }

        @Override
        public int lastIndexOf(long k) {
            return -1;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Long> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Long> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(LongUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(LongList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, LongList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void add(int index, Long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Long get(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean add(Long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Long set(int index, Long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Long remove(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public int indexOf(Object k) {
            return -1;
        }

        @Override
        @Deprecated
        public int lastIndexOf(Object k) {
            return -1;
        }

        @Override
        public void sort(LongComparator comparator) {
        }

        @Override
        public void unstableSort(LongComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Long> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Long> comparator) {
        }

        @Override
        public LongListIterator listIterator() {
            return LongIterators.EMPTY_ITERATOR;
        }

        @Override
        public LongListIterator iterator() {
            return LongIterators.EMPTY_ITERATOR;
        }

        @Override
        public LongListIterator listIterator(int i) {
            if (i == 0) {
                return LongIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public LongList subList(int from, int to) {
            if (from == 0 && to == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(int from, long[] a, int offset, int length) {
            if (from == 0 && length == 0 && offset >= 0 && offset <= a.length) {
                return;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, long[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, long[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(long[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, long[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, long[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(List<? extends Long> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return EMPTY_LIST;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof List && ((List)o).isEmpty();
        }

        @Override
        public String toString() {
            return "[]";
        }

        private Object readResolve() {
            return EMPTY_LIST;
        }
    }

    public static class Singleton
    extends AbstractLongList
    implements RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final long element;

        protected Singleton(long element) {
            this.element = element;
        }

        @Override
        public long getLong(int i) {
            if (i == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long removeLong(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(long k) {
            return k == this.element;
        }

        @Override
        public int indexOf(long k) {
            return k == this.element ? 0 : -1;
        }

        @Override
        public long[] toLongArray() {
            return new long[]{this.element};
        }

        @Override
        public LongListIterator listIterator() {
            return LongIterators.singleton(this.element);
        }

        @Override
        public LongListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public LongSpliterator spliterator() {
            return LongSpliterators.singleton(this.element);
        }

        @Override
        public LongListIterator listIterator(int i) {
            if (i > 1 || i < 0) {
                throw new IndexOutOfBoundsException();
            }
            LongListIterator l = this.listIterator();
            if (i == 1) {
                l.nextLong();
            }
            return l;
        }

        @Override
        public LongList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0 || to != 1) {
                return EMPTY_LIST;
            }
            return this;
        }

        @Override
        @Deprecated
        public void forEach(Consumer<? super Long> action) {
            action.accept((Long)this.element);
        }

        @Override
        public boolean addAll(int i, Collection<? extends Long> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Long> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean removeIf(Predicate<? super Long> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Long> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(LongUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEach(LongConsumer action) {
            action.accept(this.element);
        }

        @Override
        public boolean addAll(LongList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, LongList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(LongPredicate filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Object[] toArray() {
            return new Object[]{this.element};
        }

        @Override
        public void sort(LongComparator comparator) {
        }

        @Override
        public void unstableSort(LongComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Long> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Long> comparator) {
        }

        @Override
        public void getElements(int from, long[] a, int offset, int length) {
            if (offset < 0) {
                throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
            }
            if (offset + length > a.length) {
                throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")");
            }
            if (from + length > this.size()) {
                throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
            }
            if (length <= 0) {
                return;
            }
            a[offset] = this.element;
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, long[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, long[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(long[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, long[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, long[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public void size(int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return this;
        }
    }

    public static class SynchronizedRandomAccessList
    extends SynchronizedList
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 0L;

        protected SynchronizedRandomAccessList(LongList l, Object sync) {
            super(l, sync);
        }

        protected SynchronizedRandomAccessList(LongList l) {
            super(l);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public LongList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
            }
        }
    }

    public static class SynchronizedList
    extends LongCollections.SynchronizedCollection
    implements LongList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongList list;

        protected SynchronizedList(LongList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedList(LongList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long getLong(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getLong(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long set(int i, long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i, long k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long removeLong(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeLong(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeIf(LongPredicate filter) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeIf(filter);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(LongUnaryOperator operator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.replaceAll(operator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends Long> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(int from, long[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.getElements(from, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeElements(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                this.list.removeElements(from, to);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, long[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, long[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(long[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, long[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, long[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void size(int size) {
            Object object = this.sync;
            synchronized (object) {
                this.list.size(size);
            }
        }

        @Override
        public LongListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public LongListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public LongListIterator listIterator(int i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public LongList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedList(this.list.subList(from, to), this.sync);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.sync;
            synchronized (object) {
                return this.collection.equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int compareTo(List<? extends Long> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, LongCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, LongList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(LongList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Long get(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.get(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void add(int i, Long k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Long set(int index, Long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(index, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Long remove(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.remove(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public int indexOf(Object o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public int lastIndexOf(Object o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void sort(LongComparator comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.sort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unstableSort(LongComparator comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.unstableSort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void sort(Comparator<? super Long> comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.sort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Long> comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.unstableSort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void writeObject(ObjectOutputStream s) throws IOException {
            Object object = this.sync;
            synchronized (object) {
                s.defaultWriteObject();
            }
        }
    }

    public static class UnmodifiableRandomAccessList
    extends UnmodifiableList
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 0L;

        protected UnmodifiableRandomAccessList(LongList l) {
            super(l);
        }

        @Override
        public LongList subList(int from, int to) {
            return new UnmodifiableRandomAccessList(this.list.subList(from, to));
        }
    }

    public static class UnmodifiableList
    extends LongCollections.UnmodifiableCollection
    implements LongList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongList list;

        protected UnmodifiableList(LongList l) {
            super(l);
            this.list = l;
        }

        @Override
        public long getLong(int i) {
            return this.list.getLong(i);
        }

        @Override
        public long set(int i, long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int i, long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long removeLong(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(long k) {
            return this.list.indexOf(k);
        }

        @Override
        public int lastIndexOf(long k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Long> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Long> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(int from, long[] a, int offset, int length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, long[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, long[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(long[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, long[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, long[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int size) {
            this.list.size(size);
        }

        @Override
        public LongListIterator listIterator() {
            return LongIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public LongListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public LongListIterator listIterator(int i) {
            return LongIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public LongList subList(int from, int to) {
            return new UnmodifiableList(this.list.subList(from, to));
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return this.collection.equals(o);
        }

        @Override
        public int hashCode() {
            return this.collection.hashCode();
        }

        @Override
        public int compareTo(List<? extends Long> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(int index, LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(LongList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, LongList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(LongUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Long get(int i) {
            return this.list.get(i);
        }

        @Override
        @Deprecated
        public void add(int i, Long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Long set(int index, Long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Long remove(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public int indexOf(Object o) {
            return this.list.indexOf(o);
        }

        @Override
        @Deprecated
        public int lastIndexOf(Object o) {
            return this.list.lastIndexOf(o);
        }

        @Override
        public void sort(LongComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unstableSort(LongComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Long> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Long> comparator) {
            throw new UnsupportedOperationException();
        }
    }

    static abstract class ImmutableListBase
    extends AbstractLongList
    implements LongList {
        ImmutableListBase() {
        }

        @Override
        @Deprecated
        public final void add(int index, long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(Collection<? extends Long> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, Collection<? extends Long> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final long removeLong(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean rem(long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeIf(Predicate<? super Long> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeIf(LongPredicate c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void replaceAll(UnaryOperator<Long> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void replaceAll(LongUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void add(int index, Long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(Long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Long remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean remove(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Long set(int index, Long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(LongList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, LongList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeAll(LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean retainAll(LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final long set(int index, long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void size(int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void addElements(int index, long[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void setElements(int index, long[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(LongComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(LongComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(Comparator<? super Long> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(Comparator<? super Long> comparator) {
            throw new UnsupportedOperationException();
        }
    }
}

