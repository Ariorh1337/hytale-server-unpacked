/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollections;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterators;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import it.unimi.dsi.fastutil.shorts.ShortPredicate;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ShortSets {
    static final int ARRAY_SET_CUTOFF = 4;
    public static final EmptySet EMPTY_SET = new EmptySet();
    static final ShortSet UNMODIFIABLE_EMPTY_SET = ShortSets.unmodifiable(new ShortArraySet(ShortArrays.EMPTY_ARRAY));

    private ShortSets() {
    }

    public static ShortSet emptySet() {
        return EMPTY_SET;
    }

    public static ShortSet singleton(short element) {
        return new Singleton(element);
    }

    public static ShortSet singleton(Short element) {
        return new Singleton(element);
    }

    public static ShortSet synchronize(ShortSet s) {
        return new SynchronizedSet(s);
    }

    public static ShortSet synchronize(ShortSet s, Object sync) {
        return new SynchronizedSet(s, sync);
    }

    public static ShortSet unmodifiable(ShortSet s) {
        return new UnmodifiableSet(s);
    }

    public static ShortSet fromTo(final short from, final short to) {
        return new AbstractShortSet(){

            @Override
            public boolean contains(short x) {
                return x >= from && x < to;
            }

            @Override
            public ShortIterator iterator() {
                return ShortIterators.fromTo(from, to);
            }

            @Override
            public int size() {
                long size = (long)to - (long)from;
                return size >= 0L && size <= Integer.MAX_VALUE ? (int)size : Integer.MAX_VALUE;
            }
        };
    }

    public static ShortSet from(final short from) {
        return new AbstractShortSet(){

            @Override
            public boolean contains(short x) {
                return x >= from;
            }

            @Override
            public ShortIterator iterator() {
                return ShortIterators.concat(ShortIterators.fromTo(from, (short)Short.MAX_VALUE), ShortSets.singleton((short)Short.MAX_VALUE).iterator());
            }

            @Override
            public int size() {
                long size = 32767L - (long)from + 1L;
                return size >= 0L && size <= Integer.MAX_VALUE ? (int)size : Integer.MAX_VALUE;
            }
        };
    }

    public static ShortSet to(final short to) {
        return new AbstractShortSet(){

            @Override
            public boolean contains(short x) {
                return x < to;
            }

            @Override
            public ShortIterator iterator() {
                return ShortIterators.fromTo((short)Short.MIN_VALUE, to);
            }

            @Override
            public int size() {
                long size = (long)to - -32768L;
                return size >= 0L && size <= Integer.MAX_VALUE ? (int)size : Integer.MAX_VALUE;
            }
        };
    }

    public static class EmptySet
    extends ShortCollections.EmptyCollection
    implements ShortSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(short ok) {
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
        public boolean rem(short k) {
            return super.rem(k);
        }

        private Object readResolve() {
            return EMPTY_SET;
        }
    }

    public static class Singleton
    extends AbstractShortSet
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final short element;

        protected Singleton(short element) {
            this.element = element;
        }

        @Override
        public boolean contains(short k) {
            return k == this.element;
        }

        @Override
        public boolean remove(short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ShortListIterator iterator() {
            return ShortIterators.singleton(this.element);
        }

        @Override
        public ShortSpliterator spliterator() {
            return ShortSpliterators.singleton(this.element);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public short[] toShortArray() {
            return new short[]{this.element};
        }

        @Override
        @Deprecated
        public void forEach(Consumer<? super Short> action) {
            action.accept((Short)this.element);
        }

        @Override
        public boolean addAll(Collection<? extends Short> c) {
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
        public boolean removeIf(Predicate<? super Short> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEach(ShortConsumer action) {
            action.accept(this.element);
        }

        @Override
        public boolean addAll(ShortCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(ShortCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(ShortCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(ShortPredicate filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IntIterator intIterator() {
            return IntIterators.singleton(this.element);
        }

        @Override
        public IntSpliterator intSpliterator() {
            return IntSpliterators.singleton(this.element);
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
    extends ShortCollections.SynchronizedCollection
    implements ShortSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(ShortSet s, Object sync) {
            super(s, sync);
        }

        protected SynchronizedSet(ShortSet s) {
            super(s);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(short k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        @Override
        @Deprecated
        public boolean rem(short k) {
            return super.rem(k);
        }
    }

    public static class UnmodifiableSet
    extends ShortCollections.UnmodifiableCollection
    implements ShortSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(ShortSet s) {
            super(s);
        }

        @Override
        public boolean remove(short k) {
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
        public boolean rem(short k) {
            return super.rem(k);
        }
    }
}

