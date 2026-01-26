/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanBigListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public final class BooleanBigListIterators {
    public static final EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new EmptyBigListIterator();

    private BooleanBigListIterators() {
    }

    public static BooleanBigListIterator singleton(boolean element) {
        return new SingletonBigListIterator(element);
    }

    public static BooleanBigListIterator unmodifiable(BooleanBigListIterator i) {
        return new UnmodifiableBigListIterator(i);
    }

    public static BooleanBigListIterator asBigListIterator(BooleanListIterator i) {
        return new BigListIteratorListIterator(i);
    }

    private static class SingletonBigListIterator
    implements BooleanBigListIterator {
        private final boolean element;
        private int curr;

        public SingletonBigListIterator(boolean element) {
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
        public boolean nextBoolean() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public boolean previousBoolean() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = 0;
            return this.element;
        }

        @Override
        public void forEachRemaining(BooleanConsumer action) {
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

    public static class UnmodifiableBigListIterator
    implements BooleanBigListIterator {
        protected final BooleanBigListIterator i;

        public UnmodifiableBigListIterator(BooleanBigListIterator i) {
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
        public boolean nextBoolean() {
            return this.i.nextBoolean();
        }

        @Override
        public boolean previousBoolean() {
            return this.i.previousBoolean();
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
        public void forEachRemaining(BooleanConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Boolean> action) {
            this.i.forEachRemaining(action);
        }
    }

    public static class BigListIteratorListIterator
    implements BooleanBigListIterator {
        protected final BooleanListIterator i;

        protected BigListIteratorListIterator(BooleanListIterator i) {
            this.i = i;
        }

        private int intDisplacement(long n) {
            if (n < Integer.MIN_VALUE || n > Integer.MAX_VALUE) {
                throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
            }
            return (int)n;
        }

        @Override
        public void set(boolean ok) {
            this.i.set(ok);
        }

        @Override
        public void add(boolean ok) {
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
        public boolean nextBoolean() {
            return this.i.nextBoolean();
        }

        @Override
        public boolean previousBoolean() {
            return this.i.previousBoolean();
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
        public void forEachRemaining(BooleanConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Boolean> action) {
            this.i.forEachRemaining(action);
        }
    }

    public static class EmptyBigListIterator
    implements BooleanBigListIterator,
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
        public boolean nextBoolean() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean previousBoolean() {
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
        public void forEachRemaining(BooleanConsumer action) {
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Boolean> action) {
        }

        private Object readResolve() {
            return EMPTY_BIG_LIST_ITERATOR;
        }
    }

    public static abstract class AbstractIndexBasedBigListIterator
    extends AbstractIndexBasedBigIterator
    implements BooleanBigListIterator {
        protected AbstractIndexBasedBigListIterator(long minPos, long initialPos) {
            super(minPos, initialPos);
        }

        protected abstract void add(long var1, boolean var3);

        protected abstract void set(long var1, boolean var3);

        @Override
        public boolean hasPrevious() {
            return this.pos > this.minPos;
        }

        @Override
        public boolean previousBoolean() {
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
        public void add(boolean k) {
            this.add(this.pos++, k);
            this.lastReturned = -1L;
        }

        @Override
        public void set(boolean k) {
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

    public static abstract class AbstractIndexBasedBigIterator
    extends AbstractBooleanIterator {
        protected final long minPos;
        protected long pos;
        protected long lastReturned;

        protected AbstractIndexBasedBigIterator(long minPos, long initialPos) {
            this.minPos = minPos;
            this.pos = initialPos;
        }

        protected abstract boolean get(long var1);

        protected abstract void remove(long var1);

        protected abstract long getMaxPos();

        @Override
        public boolean hasNext() {
            return this.pos < this.getMaxPos();
        }

        @Override
        public boolean nextBoolean() {
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
        public void forEachRemaining(BooleanConsumer action) {
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

