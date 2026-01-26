/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class IntCollections {
    private IntCollections() {
    }

    public static IntCollection synchronize(IntCollection c) {
        return new SynchronizedCollection(c);
    }

    public static IntCollection synchronize(IntCollection c, Object sync) {
        return new SynchronizedCollection(c, sync);
    }

    public static IntCollection unmodifiable(IntCollection c) {
        return new UnmodifiableCollection(c);
    }

    public static IntCollection asCollection(IntIterable iterable) {
        if (iterable instanceof IntCollection) {
            return (IntCollection)iterable;
        }
        return new IterableCollection(iterable);
    }

    static class SynchronizedCollection
    implements IntCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntCollection collection;
        protected final Object sync;

        protected SynchronizedCollection(IntCollection c, Object sync) {
            this.collection = Objects.requireNonNull(c);
            this.sync = sync;
        }

        protected SynchronizedCollection(IntCollection c) {
            this.collection = Objects.requireNonNull(c);
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean add(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean rem(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int[] toIntArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toIntArray();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object[] toArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray();
            }
        }

        @Override
        @Deprecated
        public int[] toIntArray(int[] a) {
            return this.toArray(a);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int[] toArray(int[] a) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(IntCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(IntCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(IntCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(IntCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.retainAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public boolean add(Integer k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public boolean contains(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public boolean remove(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.remove(k);
            }
        }

        @Override
        public IntIterator intIterator() {
            return this.collection.intIterator();
        }

        @Override
        public IntSpliterator intSpliterator() {
            return this.collection.intSpliterator();
        }

        @Override
        public IntStream intStream() {
            return this.collection.intStream();
        }

        @Override
        public IntStream intParallelStream() {
            return this.collection.intParallelStream();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <T> T[] toArray(T[] a) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray(a);
            }
        }

        @Override
        public IntIterator iterator() {
            return this.collection.iterator();
        }

        @Override
        public IntSpliterator spliterator() {
            return this.collection.spliterator();
        }

        @Override
        @Deprecated
        public Stream<Integer> stream() {
            return this.collection.stream();
        }

        @Override
        @Deprecated
        public Stream<Integer> parallelStream() {
            return this.collection.parallelStream();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(IntConsumer action) {
            Object object = this.sync;
            synchronized (object) {
                this.collection.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(Collection<? extends Integer> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.retainAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeIf(IntPredicate filter) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeIf(filter);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.sync;
            synchronized (object) {
                this.collection.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public String toString() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toString();
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
        private void writeObject(ObjectOutputStream s) throws IOException {
            Object object = this.sync;
            synchronized (object) {
                s.defaultWriteObject();
            }
        }
    }

    static class UnmodifiableCollection
    implements IntCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntCollection collection;

        protected UnmodifiableCollection(IntCollection c) {
            this.collection = Objects.requireNonNull(c);
        }

        @Override
        public boolean add(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean rem(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return this.collection.size();
        }

        @Override
        public boolean isEmpty() {
            return this.collection.isEmpty();
        }

        @Override
        public boolean contains(int o) {
            return this.collection.contains(o);
        }

        @Override
        public IntIterator iterator() {
            return IntIterators.unmodifiable(this.collection.iterator());
        }

        @Override
        public IntSpliterator spliterator() {
            return this.collection.spliterator();
        }

        @Override
        @Deprecated
        public Stream<Integer> stream() {
            return this.collection.stream();
        }

        @Override
        @Deprecated
        public Stream<Integer> parallelStream() {
            return this.collection.parallelStream();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return this.collection.toArray(a);
        }

        @Override
        public Object[] toArray() {
            return this.collection.toArray();
        }

        @Override
        public void forEach(IntConsumer action) {
            this.collection.forEach(action);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.collection.containsAll(c);
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
        public boolean removeIf(IntPredicate filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean add(Integer k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean contains(Object k) {
            return this.collection.contains(k);
        }

        @Override
        @Deprecated
        public boolean remove(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int[] toIntArray() {
            return this.collection.toIntArray();
        }

        @Override
        @Deprecated
        public int[] toIntArray(int[] a) {
            return this.toArray(a);
        }

        @Override
        public int[] toArray(int[] a) {
            return this.collection.toArray(a);
        }

        @Override
        public boolean containsAll(IntCollection c) {
            return this.collection.containsAll(c);
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
        public IntIterator intIterator() {
            return this.collection.intIterator();
        }

        @Override
        public IntSpliterator intSpliterator() {
            return this.collection.intSpliterator();
        }

        @Override
        public IntStream intStream() {
            return this.collection.intStream();
        }

        @Override
        public IntStream intParallelStream() {
            return this.collection.intParallelStream();
        }

        public String toString() {
            return this.collection.toString();
        }

        @Override
        public int hashCode() {
            return this.collection.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return this.collection.equals(o);
        }
    }

    public static class IterableCollection
    extends AbstractIntCollection
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntIterable iterable;

        protected IterableCollection(IntIterable iterable) {
            this.iterable = Objects.requireNonNull(iterable);
        }

        @Override
        public int size() {
            long size = this.iterable.spliterator().getExactSizeIfKnown();
            if (size >= 0L) {
                return (int)Math.min(Integer.MAX_VALUE, size);
            }
            int c = 0;
            IntIterator iterator = this.iterator();
            while (iterator.hasNext()) {
                iterator.nextInt();
                ++c;
            }
            return c;
        }

        @Override
        public boolean isEmpty() {
            return !this.iterable.iterator().hasNext();
        }

        @Override
        public IntIterator iterator() {
            return this.iterable.iterator();
        }

        @Override
        public IntSpliterator spliterator() {
            return this.iterable.spliterator();
        }

        @Override
        public IntIterator intIterator() {
            return this.iterable.intIterator();
        }

        @Override
        public IntSpliterator intSpliterator() {
            return this.iterable.intSpliterator();
        }
    }

    static class SizeDecreasingSupplier<C extends IntCollection>
    implements Supplier<C> {
        static final int RECOMMENDED_MIN_SIZE = 8;
        final AtomicInteger suppliedCount = new AtomicInteger(0);
        final int expectedFinalSize;
        final IntFunction<C> builder;

        SizeDecreasingSupplier(int expectedFinalSize, IntFunction<C> builder) {
            this.expectedFinalSize = expectedFinalSize;
            this.builder = builder;
        }

        @Override
        public C get() {
            int expectedNeededNextSize = 1 + (this.expectedFinalSize - 1) / this.suppliedCount.incrementAndGet();
            if (expectedNeededNextSize < 0) {
                expectedNeededNextSize = 8;
            }
            return (C)((IntCollection)this.builder.apply(expectedNeededNextSize));
        }
    }

    public static abstract class EmptyCollection
    extends AbstractIntCollection {
        protected EmptyCollection() {
        }

        @Override
        public boolean contains(int k) {
            return false;
        }

        @Override
        public Object[] toArray() {
            return ObjectArrays.EMPTY_ARRAY;
        }

        @Override
        public <T> T[] toArray(T[] array) {
            if (array.length > 0) {
                array[0] = null;
            }
            return array;
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return IntIterators.EMPTY_ITERATOR;
        }

        @Override
        public IntSpliterator spliterator() {
            return IntSpliterators.EMPTY_SPLITERATOR;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Collection)) {
                return false;
            }
            return ((Collection)o).isEmpty();
        }

        @Override
        @Deprecated
        public void forEach(Consumer<? super Integer> action) {
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
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
            Objects.requireNonNull(filter);
            return false;
        }

        @Override
        public int[] toIntArray() {
            return IntArrays.EMPTY_ARRAY;
        }

        @Override
        @Deprecated
        public int[] toIntArray(int[] a) {
            return a;
        }

        @Override
        public void forEach(IntConsumer action) {
        }

        @Override
        public boolean containsAll(IntCollection c) {
            return c.isEmpty();
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
            Objects.requireNonNull(filter);
            return false;
        }
    }
}

