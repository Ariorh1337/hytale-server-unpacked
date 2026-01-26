/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public final class ObjectBigListIterators {
    public static final EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new EmptyBigListIterator();

    private ObjectBigListIterators() {
    }

    public static <K> ObjectBigListIterator<K> singleton(K element) {
        return new SingletonBigListIterator<K>(element);
    }

    public static <K> ObjectBigListIterator<K> unmodifiable(ObjectBigListIterator<? extends K> i) {
        return new UnmodifiableBigListIterator<K>(i);
    }

    public static <K> ObjectBigListIterator<K> asBigListIterator(ObjectListIterator<K> i) {
        return new BigListIteratorListIterator<K>(i);
    }

    private static class SingletonBigListIterator<K>
    implements ObjectBigListIterator<K> {
        private final K element;
        private int curr;

        public SingletonBigListIterator(K element) {
            this.element = element;
        }

        @Override
        public boolean hasNext() {
            return this.curr == 0;
        }

        @Override
        public boolean hasPrevious() {
            return this.curr == 1;
        }

        @Override
        public K next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public K previous() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = 0;
            return this.element;
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
            Objects.requireNonNull(action);
            if (this.curr == 0) {
                action.accept(this.element);
                this.curr = 1;
            }
        }

        @Override
        public long nextIndex() {
            return this.curr;
        }

        @Override
        public long previousIndex() {
            return this.curr - 1;
        }

        @Override
        public long back(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (n == 0L || this.curr < 1) {
                return 0L;
            }
            this.curr = 1;
            return 1L;
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (n == 0L || this.curr > 0) {
                return 0L;
            }
            this.curr = 0;
            return 1L;
        }
    }

    public static class UnmodifiableBigListIterator<K>
    implements ObjectBigListIterator<K> {
        protected final ObjectBigListIterator<? extends K> i;

        public UnmodifiableBigListIterator(ObjectBigListIterator<? extends K> i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }

        @Override
        public K next() {
            return (K)this.i.next();
        }

        @Override
        public K previous() {
            return this.i.previous();
        }

        @Override
        public long nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public long previousIndex() {
            return this.i.previousIndex();
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
            this.i.forEachRemaining(action);
        }
    }

    public static class BigListIteratorListIterator<K>
    implements ObjectBigListIterator<K> {
        protected final ObjectListIterator<K> i;

        protected BigListIteratorListIterator(ObjectListIterator<K> i) {
            this.i = i;
        }

        private int intDisplacement(long n) {
            if (n < Integer.MIN_VALUE || n > Integer.MAX_VALUE) {
                throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
            }
            return (int)n;
        }

        @Override
        public void set(K ok) {
            this.i.set(ok);
        }

        @Override
        public void add(K ok) {
            this.i.add(ok);
        }

        @Override
        public int back(int n) {
            return this.i.back(n);
        }

        @Override
        public long back(long n) {
            return this.i.back(this.intDisplacement(n));
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public int skip(int n) {
            return this.i.skip(n);
        }

        @Override
        public long skip(long n) {
            return this.i.skip(this.intDisplacement(n));
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }

        @Override
        public K next() {
            return (K)this.i.next();
        }

        @Override
        public K previous() {
            return this.i.previous();
        }

        @Override
        public long nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public long previousIndex() {
            return this.i.previousIndex();
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
            this.i.forEachRemaining(action);
        }
    }

    public static class EmptyBigListIterator<K>
    implements ObjectBigListIterator<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyBigListIterator() {
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public K next() {
            throw new NoSuchElementException();
        }

        @Override
        public K previous() {
            throw new NoSuchElementException();
        }

        @Override
        public long nextIndex() {
            return 0L;
        }

        @Override
        public long previousIndex() {
            return -1L;
        }

        @Override
        public long skip(long n) {
            return 0L;
        }

        @Override
        public long back(long n) {
            return 0L;
        }

        public Object clone() {
            return EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
        }

        private Object readResolve() {
            return EMPTY_BIG_LIST_ITERATOR;
        }
    }

    public static abstract class AbstractIndexBasedBigListIterator<K>
    extends AbstractIndexBasedBigIterator<K>
    implements ObjectBigListIterator<K> {
        protected AbstractIndexBasedBigListIterator(long minPos, long initialPos) {
            super(minPos, initialPos);
        }

        protected abstract void add(long var1, K var3);

        protected abstract void set(long var1, K var3);

        @Override
        public boolean hasPrevious() {
            return this.pos > this.minPos;
        }

        @Override
        public K previous() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.lastReturned = --this.pos;
            return this.get(this.pos);
        }

        @Override
        public long nextIndex() {
            return this.pos;
        }

        @Override
        public long previousIndex() {
            return this.pos - 1L;
        }

        @Override
        public void add(K k) {
            this.add(this.pos++, k);
            this.lastReturned = -1L;
        }

        @Override
        public void set(K k) {
            if (this.lastReturned == -1L) {
                throw new IllegalStateException();
            }
            this.set(this.lastReturned, k);
        }

        @Override
        public long back(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            long remaining = this.pos - this.minPos;
            if (n < remaining) {
                this.pos -= n;
            } else {
                n = remaining;
                this.pos = this.minPos;
            }
            this.lastReturned = this.pos;
            return n;
        }
    }

    public static abstract class AbstractIndexBasedBigIterator<K>
    extends AbstractObjectIterator<K> {
        protected final long minPos;
        protected long pos;
        protected long lastReturned;

        protected AbstractIndexBasedBigIterator(long minPos, long initialPos) {
            this.minPos = minPos;
            this.pos = initialPos;
        }

        protected abstract K get(long var1);

        protected abstract void remove(long var1);

        protected abstract long getMaxPos();

        @Override
        public boolean hasNext() {
            return this.pos < this.getMaxPos();
        }

        @Override
        public K next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.pos++;
            return this.get(this.lastReturned);
        }

        @Override
        public void remove() {
            if (this.lastReturned == -1L) {
                throw new IllegalStateException();
            }
            this.remove(this.lastReturned);
            if (this.lastReturned < this.pos) {
                --this.pos;
            }
            this.lastReturned = -1L;
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
            while (this.pos < this.getMaxPos()) {
                ++this.pos;
                this.lastReturned = this.lastReturned;
                action.accept(this.get(this.lastReturned));
            }
        }

        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            long max = this.getMaxPos();
            long remaining = max - this.pos;
            if (n < remaining) {
                this.pos += n;
            } else {
                n = remaining;
                this.pos = max;
            }
            this.lastReturned = this.pos - 1L;
            return n;
        }

        @Override
        public int skip(int n) {
            return SafeMath.safeLongToInt(this.skip((long)n));
        }
    }
}

