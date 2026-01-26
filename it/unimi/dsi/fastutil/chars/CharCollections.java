/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterable;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharIterators;
import it.unimi.dsi.fastutil.chars.CharPredicate;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class CharCollections {
    private CharCollections() {
    }

    public static CharCollection synchronize(CharCollection c) {
        return new SynchronizedCollection(c);
    }

    public static CharCollection synchronize(CharCollection c, Object sync) {
        return new SynchronizedCollection(c, sync);
    }

    public static CharCollection unmodifiable(CharCollection c) {
        return new UnmodifiableCollection(c);
    }

    public static CharCollection asCollection(CharIterable iterable) {
        if (iterable instanceof CharCollection) {
            return (CharCollection)iterable;
        }
        return new IterableCollection(iterable);
    }

    static class SynchronizedCollection
    implements CharCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharCollection collection;
        protected final Object sync;

        protected SynchronizedCollection(CharCollection c, Object sync) {
            this.collection = Objects.requireNonNull(c);
            this.sync = sync;
        }

        protected SynchronizedCollection(CharCollection c) {
            this.collection = Objects.requireNonNull(c);
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean add(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean rem(char k) {
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
        public char[] toCharArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toCharArray();
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
        public char[] toCharArray(char[] a) {
            return this.toArray(a);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char[] toArray(char[] a) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(CharCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(CharCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(CharCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(CharCollection c) {
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
        public boolean add(Character k) {
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
        public CharIterator iterator() {
            return this.collection.iterator();
        }

        @Override
        public CharSpliterator spliterator() {
            return this.collection.spliterator();
        }

        @Override
        @Deprecated
        public Stream<Character> stream() {
            return this.collection.stream();
        }

        @Override
        @Deprecated
        public Stream<Character> parallelStream() {
            return this.collection.parallelStream();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(CharConsumer action) {
            Object object = this.sync;
            synchronized (object) {
                this.collection.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(Collection<? extends Character> c) {
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
        public boolean removeIf(CharPredicate filter) {
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
    implements CharCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharCollection collection;

        protected UnmodifiableCollection(CharCollection c) {
            this.collection = Objects.requireNonNull(c);
        }

        @Override
        public boolean add(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean rem(char k) {
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
        public boolean contains(char o) {
            return this.collection.contains(o);
        }

        @Override
        public CharIterator iterator() {
            return CharIterators.unmodifiable(this.collection.iterator());
        }

        @Override
        public CharSpliterator spliterator() {
            return this.collection.spliterator();
        }

        @Override
        @Deprecated
        public Stream<Character> stream() {
            return this.collection.stream();
        }

        @Override
        @Deprecated
        public Stream<Character> parallelStream() {
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
        public void forEach(CharConsumer action) {
            this.collection.forEach(action);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.collection.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends Character> c) {
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
        public boolean removeIf(CharPredicate filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean add(Character k) {
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
        public char[] toCharArray() {
            return this.collection.toCharArray();
        }

        @Override
        @Deprecated
        public char[] toCharArray(char[] a) {
            return this.toArray(a);
        }

        @Override
        public char[] toArray(char[] a) {
            return this.collection.toArray(a);
        }

        @Override
        public boolean containsAll(CharCollection c) {
            return this.collection.containsAll(c);
        }

        @Override
        public boolean addAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(CharCollection c) {
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
    extends AbstractCharCollection
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharIterable iterable;

        protected IterableCollection(CharIterable iterable) {
            this.iterable = Objects.requireNonNull(iterable);
        }

        @Override
        public int size() {
            long size = this.iterable.spliterator().getExactSizeIfKnown();
            if (size >= 0L) {
                return (int)Math.min(Integer.MAX_VALUE, size);
            }
            int c = 0;
            CharIterator iterator = this.iterator();
            while (iterator.hasNext()) {
                iterator.nextChar();
                ++c;
            }
            return c;
        }

        @Override
        public boolean isEmpty() {
            return !this.iterable.iterator().hasNext();
        }

        @Override
        public CharIterator iterator() {
            return this.iterable.iterator();
        }

        @Override
        public CharSpliterator spliterator() {
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

    public static abstract class EmptyCollection
    extends AbstractCharCollection {
        protected EmptyCollection() {
        }

        @Override
        public boolean contains(char k) {
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
        public CharBidirectionalIterator iterator() {
            return CharIterators.EMPTY_ITERATOR;
        }

        @Override
        public CharSpliterator spliterator() {
            return CharSpliterators.EMPTY_SPLITERATOR;
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
        public void forEach(Consumer<? super Character> action) {
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
        }

        @Override
        public boolean addAll(Collection<? extends Character> c) {
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
        public boolean removeIf(Predicate<? super Character> filter) {
            Objects.requireNonNull(filter);
            return false;
        }

        @Override
        public char[] toCharArray() {
            return CharArrays.EMPTY_ARRAY;
        }

        @Override
        @Deprecated
        public char[] toCharArray(char[] a) {
            return a;
        }

        @Override
        public void forEach(CharConsumer action) {
        }

        @Override
        public boolean containsAll(CharCollection c) {
            return c.isEmpty();
        }

        @Override
        public boolean addAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(CharPredicate filter) {
            Objects.requireNonNull(filter);
            return false;
        }

        @Override
        public IntIterator intIterator() {
            return IntIterators.EMPTY_ITERATOR;
        }

        @Override
        public IntSpliterator intSpliterator() {
            return IntSpliterators.EMPTY_SPLITERATOR;
        }
    }
}

