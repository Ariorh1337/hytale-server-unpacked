/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public final class IntSets {
    static final int ARRAY_SET_CUTOFF = 4;
    public static final EmptySet EMPTY_SET = new EmptySet();
    static final IntSet UNMODIFIABLE_EMPTY_SET = IntSets.unmodifiable(new IntArraySet(IntArrays.EMPTY_ARRAY));

    private IntSets() {
    }

    public static IntSet emptySet() {
        return EMPTY_SET;
    }

    public static IntSet singleton(int element) {
        return new Singleton(element);
    }

    public static IntSet singleton(Integer element) {
        return new Singleton(element);
    }

    public static IntSet synchronize(IntSet s) {
        return new SynchronizedSet(s);
    }

    public static IntSet synchronize(IntSet s, Object sync) {
        return new SynchronizedSet(s, sync);
    }

    public static IntSet unmodifiable(IntSet s) {
        return new UnmodifiableSet(s);
    }

    public static IntSet fromTo(final int from, final int to) {
        return new AbstractIntSet(){

            @Override
            public boolean contains(int x) {
                return x >= from && x < to;
            }

            @Override
            public IntIterator iterator() {
                return IntIterators.fromTo(from, to);
            }

            @Override
            public int size() {
                long size = (long)to - (long)from;
                return size >= 0L && size <= Integer.MAX_VALUE ? (int)size : Integer.MAX_VALUE;
            }
        };
    }

    public static IntSet from(final int from) {
        return new AbstractIntSet(){

            @Override
            public boolean contains(int x) {
                return x >= from;
            }

            @Override
            public IntIterator iterator() {
                return IntIterators.concat(IntIterators.fromTo(from, Integer.MAX_VALUE), IntSets.singleton(Integer.MAX_VALUE).iterator());
            }

            @Override
            public int size() {
                long size = Integer.MAX_VALUE - (long)from + 1L;
                return size >= 0L && size <= Integer.MAX_VALUE ? (int)size : Integer.MAX_VALUE;
            }
        };
    }

    public static IntSet to(final int to) {
        return new AbstractIntSet(){

            @Override
            public boolean contains(int x) {
                return x < to;
            }

            @Override
            public IntIterator iterator() {
                return IntIterators.fromTo(Integer.MIN_VALUE, to);
            }

            @Override
            public int size() {
                long size = (long)to - Integer.MIN_VALUE;
                return size >= 0L && size <= Integer.MAX_VALUE ? (int)size : Integer.MAX_VALUE;
            }
        };
    }

    public static class EmptySet
    extends IntCollections.EmptyCollection
    implements IntSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(int ok) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return EMPTY_SET;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Set && ((Set)o).isEmpty();
        }

        @Override
        @Deprecated
        public boolean rem(int k) {
            return super.rem(k);
        }

        private Object readResolve() {
            return EMPTY_SET;
        }
    }

    public static class Singleton
    extends AbstractIntSet
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final int element;

        protected Singleton(int element) {
            this.element = element;
        }

        @Override
        public boolean contains(int k) {
            return k == this.element;
        }

        @Override
        public boolean remove(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IntListIterator iterator() {
            return IntIterators.singleton(this.element);
        }

        @Override
        public IntSpliterator spliterator() {
            return IntSpliterators.singleton(this.element);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public int[] toIntArray() {
            return new int[]{this.element};
        }

        @Override
        @Deprecated
        public void forEach(Consumer<? super Integer> action) {
            action.accept((Integer)this.element);
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
        public void forEach(IntConsumer action) {
            action.accept(this.element);
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

        public Object clone() {
            return this;
        }
    }

    public static class SynchronizedSet
    extends IntCollections.SynchronizedCollection
    implements IntSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(IntSet s, Object sync) {
            super(s, sync);
        }

        protected SynchronizedSet(IntSet s) {
            super(s);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        @Override
        @Deprecated
        public boolean rem(int k) {
            return super.rem(k);
        }
    }

    public static class UnmodifiableSet
    extends IntCollections.UnmodifiableCollection
    implements IntSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(IntSet s) {
            super(s);
        }

        @Override
        public boolean remove(int k) {
            throw new UnsupportedOperationException();
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
        @Deprecated
        public boolean rem(int k) {
            return super.rem(k);
        }
    }
}

