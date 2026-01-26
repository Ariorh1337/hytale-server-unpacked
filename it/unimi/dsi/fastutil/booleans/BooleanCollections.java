/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterable;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanIterators;
import it.unimi.dsi.fastutil.booleans.BooleanPredicate;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class BooleanCollections {
    private BooleanCollections() {
    }

    public static BooleanCollection synchronize(BooleanCollection c) {
        return new SynchronizedCollection(c);
    }

    public static BooleanCollection synchronize(BooleanCollection c, Object sync) {
        return new SynchronizedCollection(c, sync);
    }

    public static BooleanCollection unmodifiable(BooleanCollection c) {
        return new UnmodifiableCollection(c);
    }

    public static BooleanCollection asCollection(BooleanIterable iterable) {
        if (iterable instanceof BooleanCollection) {
            return (BooleanCollection)iterable;
        }
        return new IterableCollection(iterable);
    }

    static class SynchronizedCollection
    implements BooleanCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanCollection collection;
        protected final Object sync;

        protected SynchronizedCollection(BooleanCollection c, Object sync) {
            this.collection = Objects.requireNonNull(c);
            this.sync = sync;
        }

        protected SynchronizedCollection(BooleanCollection c) {
            this.collection = Objects.requireNonNull(c);
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean add(boolean k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(boolean k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean rem(boolean k) {
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
        public boolean[] toBooleanArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toBooleanArray();
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
        public boolean[] toBooleanArray(boolean[] a) {
            return this.toArray(a);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean[] toArray(boolean[] a) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(BooleanCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(BooleanCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(BooleanCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(BooleanCollection c) {
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
        public boolean add(Boolean k) {
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
        public BooleanIterator iterator() {
            return this.collection.iterator();
        }

        @Override
        public BooleanSpliterator spliterator() {
            return this.collection.spliterator();
        }

        @Override
        public Stream<Boolean> stream() {
            return this.collection.stream();
        }

        @Override
        public Stream<Boolean> parallelStream() {
            return this.collection.parallelStream();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BooleanConsumer action) {
            Object object = this.sync;
            synchronized (object) {
                this.collection.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(Collection<? extends Boolean> c) {
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
        public boolean removeIf(BooleanPredicate filter) {
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
    implements BooleanCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanCollection collection;

        protected UnmodifiableCollection(BooleanCollection c) {
            this.collection = Objects.requireNonNull(c);
        }

        @Override
        public boolean add(boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean rem(boolean k) {
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
        public boolean contains(boolean o) {
            return this.collection.contains(o);
        }

        @Override
        public BooleanIterator iterator() {
            return BooleanIterators.unmodifiable(this.collection.iterator());
        }

        @Override
        public BooleanSpliterator spliterator() {
            return this.collection.spliterator();
        }

        @Override
        public Stream<Boolean> stream() {
            return this.collection.stream();
        }

        @Override
        public Stream<Boolean> parallelStream() {
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
        public void forEach(BooleanConsumer action) {
            this.collection.forEach(action);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.collection.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends Boolean> c) {
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
        public boolean removeIf(BooleanPredicate filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean add(Boolean k) {
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
        public boolean[] toBooleanArray() {
            return this.collection.toBooleanArray();
        }

        @Override
        @Deprecated
        public boolean[] toBooleanArray(boolean[] a) {
            return this.toArray(a);
        }

        @Override
        public boolean[] toArray(boolean[] a) {
            return this.collection.toArray(a);
        }

        @Override
        public boolean containsAll(BooleanCollection c) {
            return this.collection.containsAll(c);
        }

        @Override
        public boolean addAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
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
    extends AbstractBooleanCollection
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanIterable iterable;

        protected IterableCollection(BooleanIterable iterable) {
            this.iterable = Objects.requireNonNull(iterable);
        }

        @Override
        public int size() {
            long size = this.iterable.spliterator().getExactSizeIfKnown();
            if (size >= 0L) {
                return (int)Math.min(Integer.MAX_VALUE, size);
            }
            int c = 0;
            BooleanIterator iterator = this.iterator();
            while (iterator.hasNext()) {
                iterator.nextBoolean();
                ++c;
            }
            return c;
        }

        @Override
        public boolean isEmpty() {
            return !this.iterable.iterator().hasNext();
        }

        @Override
        public BooleanIterator iterator() {
            return this.iterable.iterator();
        }

        @Override
        public BooleanSpliterator spliterator() {
            return this.iterable.spliterator();
        }
    }

    public static abstract class EmptyCollection
    extends AbstractBooleanCollection {
        protected EmptyCollection() {
        }

        @Override
        public boolean contains(boolean k) {
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
        public BooleanBidirectionalIterator iterator() {
            return BooleanIterators.EMPTY_ITERATOR;
        }

        @Override
        public BooleanSpliterator spliterator() {
            return BooleanSpliterators.EMPTY_SPLITERATOR;
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
        public void forEach(Consumer<? super Boolean> action) {
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
        }

        @Override
        public boolean addAll(Collection<? extends Boolean> c) {
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
        public boolean removeIf(Predicate<? super Boolean> filter) {
            Objects.requireNonNull(filter);
            return false;
        }

        @Override
        public boolean[] toBooleanArray() {
            return BooleanArrays.EMPTY_ARRAY;
        }

        @Override
        @Deprecated
        public boolean[] toBooleanArray(boolean[] a) {
            return a;
        }

        @Override
        public void forEach(BooleanConsumer action) {
        }

        @Override
        public boolean containsAll(BooleanCollection c) {
            return c.isEmpty();
        }

        @Override
        public boolean addAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(BooleanPredicate filter) {
            Objects.requireNonNull(filter);
            return false;
        }
    }
}

