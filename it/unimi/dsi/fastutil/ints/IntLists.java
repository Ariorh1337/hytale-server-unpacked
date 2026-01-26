/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractIntList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class IntLists {
    public static final EmptyList EMPTY_LIST = new EmptyList();

    private IntLists() {
    }

    public static IntList shuffle(IntList l, Random random) {
        int i = l.size();
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            int t = l.getInt(i);
            l.set(i, l.getInt(p));
            l.set(p, t);
        }
        return l;
    }

    public static IntList emptyList() {
        return EMPTY_LIST;
    }

    public static IntList singleton(int element) {
        return new Singleton(element);
    }

    public static IntList singleton(Object element) {
        return new Singleton((Integer)element);
    }

    public static IntList synchronize(IntList l) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l) : new SynchronizedList(l);
    }

    public static IntList synchronize(IntList l, Object sync) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l, sync) : new SynchronizedList(l, sync);
    }

    public static IntList unmodifiable(IntList l) {
        return l instanceof RandomAccess ? new UnmodifiableRandomAccessList(l) : new UnmodifiableList(l);
    }

    public static class EmptyList
    extends IntCollections.EmptyCollection
    implements IntList,
    RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyList() {
        }

        @Override
        public int getInt(int i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int removeInt(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int set(int index, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(int k) {
            return -1;
        }

        @Override
        public int lastIndexOf(int k) {
            return -1;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Integer> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(IntUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(IntList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, IntList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void add(int index, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Integer get(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean add(Integer k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Integer set(int index, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Integer remove(int k) {
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
        public void sort(IntComparator comparator) {
        }

        @Override
        public void unstableSort(IntComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Integer> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Integer> comparator) {
        }

        @Override
        public IntListIterator listIterator() {
            return IntIterators.EMPTY_ITERATOR;
        }

        @Override
        public IntListIterator iterator() {
            return IntIterators.EMPTY_ITERATOR;
        }

        @Override
        public IntListIterator listIterator(int i) {
            if (i == 0) {
                return IntIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public IntList subList(int from, int to) {
            if (from == 0 && to == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(int from, int[] a, int offset, int length) {
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
        public void addElements(int index, int[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, int[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, int[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, int[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(List<? extends Integer> o) {
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
    extends AbstractIntList
    implements RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final int element;

        protected Singleton(int element) {
            this.element = element;
        }

        @Override
        public int getInt(int i) {
            if (i == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int removeInt(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(int k) {
            return k == this.element;
        }

        @Override
        public int indexOf(int k) {
            return k == this.element ? 0 : -1;
        }

        @Override
        public int[] toIntArray() {
            return new int[]{this.element};
        }

        @Override
        public IntListIterator listIterator() {
            return IntIterators.singleton(this.element);
        }

        @Override
        public IntListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public IntSpliterator spliterator() {
            return IntSpliterators.singleton(this.element);
        }

        @Override
        public IntListIterator listIterator(int i) {
            if (i > 1 || i < 0) {
                throw new IndexOutOfBoundsException();
            }
            IntListIterator l = this.listIterator();
            if (i == 1) {
                l.nextInt();
            }
            return l;
        }

        @Override
        public IntList subList(int from, int to) {
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
        public void forEach(Consumer<? super Integer> action) {
            action.accept((Integer)this.element);
        }

        @Override
        public boolean addAll(int i, Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Integer> c) {
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
        public boolean removeIf(Predicate<? super Integer> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Integer> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(IntUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEach(IntConsumer action) {
            action.accept(this.element);
        }

        @Override
        public boolean addAll(IntList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, IntList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(IntPredicate filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Object[] toArray() {
            return new Object[]{this.element};
        }

        @Override
        public void sort(IntComparator comparator) {
        }

        @Override
        public void unstableSort(IntComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Integer> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Integer> comparator) {
        }

        @Override
        public void getElements(int from, int[] a, int offset, int length) {
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
        public void addElements(int index, int[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, int[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, int[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, int[] a, int offset, int length) {
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

        protected SynchronizedRandomAccessList(IntList l, Object sync) {
            super(l, sync);
        }

        protected SynchronizedRandomAccessList(IntList l) {
            super(l);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IntList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
            }
        }
    }

    public static class SynchronizedList
    extends IntCollections.SynchronizedCollection
    implements IntList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntList list;

        protected SynchronizedList(IntList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedList(IntList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getInt(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getInt(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int set(int i, int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i, int k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int removeInt(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeInt(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeIf(IntPredicate filter) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeIf(filter);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(IntUnaryOperator operator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.replaceAll(operator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends Integer> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(int from, int[] a, int offset, int length) {
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
        public void addElements(int index, int[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, int[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, int[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, int[] a, int offset, int length) {
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
        public IntListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public IntListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public IntListIterator listIterator(int i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IntList subList(int from, int to) {
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
        public int compareTo(List<? extends Integer> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, IntCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, IntList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(IntList l) {
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
        public Integer get(int i) {
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
        public void add(int i, Integer k) {
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
        public Integer set(int index, Integer k) {
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
        public Integer remove(int i) {
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
        public void sort(IntComparator comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.sort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unstableSort(IntComparator comparator) {
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
        public void sort(Comparator<? super Integer> comparator) {
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
        public void unstableSort(Comparator<? super Integer> comparator) {
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

        protected UnmodifiableRandomAccessList(IntList l) {
            super(l);
        }

        @Override
        public IntList subList(int from, int to) {
            return new UnmodifiableRandomAccessList(this.list.subList(from, to));
        }
    }

    public static class UnmodifiableList
    extends IntCollections.UnmodifiableCollection
    implements IntList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntList list;

        protected UnmodifiableList(IntList l) {
            super(l);
            this.list = l;
        }

        @Override
        public int getInt(int i) {
            return this.list.getInt(i);
        }

        @Override
        public int set(int i, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int i, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int removeInt(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(int k) {
            return this.list.indexOf(k);
        }

        @Override
        public int lastIndexOf(int k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Integer> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(int from, int[] a, int offset, int length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, int[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, int[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, int[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, int[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int size) {
            this.list.size(size);
        }

        @Override
        public IntListIterator listIterator() {
            return IntIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public IntListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public IntListIterator listIterator(int i) {
            return IntIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public IntList subList(int from, int to) {
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
        public int compareTo(List<? extends Integer> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(int index, IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(IntList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, IntList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(IntUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Integer get(int i) {
            return this.list.get(i);
        }

        @Override
        @Deprecated
        public void add(int i, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Integer set(int index, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Integer remove(int i) {
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
        public void sort(IntComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unstableSort(IntComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Integer> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Integer> comparator) {
            throw new UnsupportedOperationException();
        }
    }

    static abstract class ImmutableListBase
    extends AbstractIntList
    implements IntList {
        ImmutableListBase() {
        }

        @Override
        @Deprecated
        public final void add(int index, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final int removeInt(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean rem(int k) {
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
        public final boolean removeIf(Predicate<? super Integer> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeIf(IntPredicate c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void replaceAll(UnaryOperator<Integer> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void replaceAll(IntUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void add(int index, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(Integer k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Integer remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean remove(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Integer set(int index, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(IntList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, IntList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean retainAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final int set(int index, int k) {
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
        public final void addElements(int index, int[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void setElements(int index, int[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(IntComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(IntComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(Comparator<? super Integer> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(Comparator<? super Integer> comparator) {
            throw new UnsupportedOperationException();
        }
    }
}

