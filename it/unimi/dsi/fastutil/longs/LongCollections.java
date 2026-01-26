/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final class LongCollections {
    private LongCollections() {
    }

    public static LongCollection synchronize(LongCollection c) {
        return new SynchronizedCollection(c);
    }

    public static LongCollection synchronize(LongCollection c, Object sync) {
        return new SynchronizedCollection(c, sync);
    }

    public static LongCollection unmodifiable(LongCollection c) {
        return new UnmodifiableCollection(c);
    }

    public static LongCollection asCollection(LongIterable iterable) {
        if (iterable instanceof LongCollection) {
            return (LongCollection)iterable;
        }
        return new IterableCollection(iterable);
    }

    static class SynchronizedCollection
    implements LongCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongCollection collection;
        protected final Object sync;

        protected SynchronizedCollection(LongCollection c, Object sync) {
            this.collection = Objects.requireNonNull(c);
            this.sync = sync;
        }

        protected SynchronizedCollection(LongCollection c) {
            this.collection = Objects.requireNonNull(c);
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean add(long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean rem(long k) {
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
        public long[] toLongArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toLongArray();
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
        public long[] toLongArray(long[] a) {
            return this.toArray(a);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long[] toArray(long[] a) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(LongCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(LongCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(LongCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(LongCollection c) {
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
        public boolean add(Long k) {
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
        public LongIterator longIterator() {
            return this.collection.longIterator();
        }

        @Override
        public LongSpliterator longSpliterator() {
            return this.collection.longSpliterator();
        }

        @Override
        public LongStream longStream() {
            return this.collection.longStream();
        }

        @Override
        public LongStream longParallelStream() {
            return this.collection.longParallelStream();
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
        public LongIterator iterator() {
            return this.collection.iterator();
        }

        @Override
        public LongSpliterator spliterator() {
            return this.collection.spliterator();
        }

        @Override
        @Deprecated
        public Stream<Long> stream() {
            return this.collection.stream();
        }

        @Override
        @Deprecated
        public Stream<Long> parallelStream() {
            return this.collection.parallelStream();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(LongConsumer action) {
            Object object = this.sync;
            synchronized (object) {
                this.collection.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(Collection<? extends Long> c) {
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
        public boolean removeIf(LongPredicate filter) {
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
    implements LongCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongCollection collection;

        protected UnmodifiableCollection(LongCollection c) {
            this.collection = Objects.requireNonNull(c);
        }

        @Override
        public boolean add(long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean rem(long k) {
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
        public boolean contains(long o) {
            return this.collection.contains(o);
        }

        @Override
        public LongIterator iterator() {
            return LongIterators.unmodifiable(this.collection.iterator());
        }

        @Override
        public LongSpliterator spliterator() {
            return this.collection.spliterator();
        }

        @Override
        @Deprecated
        public Stream<Long> stream() {
            return this.collection.stream();
        }

        @Override
        @Deprecated
        public Stream<Long> parallelStream() {
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
        public void forEach(LongConsumer action) {
            this.collection.forEach(action);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.collection.containsAll(c);
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
        public boolean removeIf(LongPredicate filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean add(Long k) {
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
        public long[] toLongArray() {
            return this.collection.toLongArray();
        }

        @Override
        @Deprecated
        public long[] toLongArray(long[] a) {
            return this.toArray(a);
        }

        @Override
        public long[] toArray(long[] a) {
            return this.collection.toArray(a);
        }

        @Override
        public boolean containsAll(LongCollection c) {
            return this.collection.containsAll(c);
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
        public LongIterator longIterator() {
            return this.collection.longIterator();
        }

        @Override
        public LongSpliterator longSpliterator() {
            return this.collection.longSpliterator();
        }

        @Override
        public LongStream longStream() {
            return this.collection.longStream();
        }

        @Override
        public LongStream longParallelStream() {
            return this.collection.longParallelStream();
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
    extends AbstractLongCollection
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongIterable iterable;

        protected IterableCollection(LongIterable iterable) {
            this.iterable = Objects.requireNonNull(iterable);
        }

        @Override
        public int size() {
            long size = this.iterable.spliterator().getExactSizeIfKnown();
            if (size >= 0L) {
                return (int)Math.min(Integer.MAX_VALUE, size);
            }
            int c = 0;
            LongIterator iterator = this.iterator();
            while (iterator.hasNext()) {
                iterator.nextLong();
                ++c;
            }
            return c;
        }

        @Override
        public boolean isEmpty() {
            return !this.iterable.iterator().hasNext();
        }

        @Override
        public LongIterator iterator() {
            return this.iterable.iterator();
        }

        @Override
        public LongSpliterator spliterator() {
            return this.iterable.spliterator();
        }

        @Override
        public LongIterator longIterator() {
            return this.iterable.longIterator();
        }

        @Override
        public LongSpliterator longSpliterator() {
            return this.iterable.longSpliterator();
        }
    }

    static class SizeDecreasingSupplier<C extends LongCollection>
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
            return (C)((LongCollection)this.builder.apply(expectedNeededNextSize));
        }
    }

    public static abstract class EmptyCollection
    extends AbstractLongCollection {
        protected EmptyCollection() {
        }

        @Override
        public boolean contains(long k) {
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
        public LongBidirectionalIterator iterator() {
            return LongIterators.EMPTY_ITERATOR;
        }

        @Override
        public LongSpliterator spliterator() {
            return LongSpliterators.EMPTY_SPLITERATOR;
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
        public void forEach(Consumer<? super Long> action) {
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
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
            Objects.requireNonNull(filter);
            return false;
        }

        @Override
        public long[] toLongArray() {
            return LongArrays.EMPTY_ARRAY;
        }

        @Override
        @Deprecated
        public long[] toLongArray(long[] a) {
            return a;
        }

        @Override
        public void forEach(LongConsumer action) {
        }

        @Override
        public boolean containsAll(LongCollection c) {
            return c.isEmpty();
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
            Objects.requireNonNull(filter);
            return false;
        }
    }
}

