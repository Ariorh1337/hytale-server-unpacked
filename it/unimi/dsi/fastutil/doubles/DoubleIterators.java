/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleBigArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleConsumer;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoublePredicate;

public final class DoubleIterators {
    public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    private DoubleIterators() {
    }

    public static DoubleListIterator singleton(double element) {
        return new SingletonIterator(element);
    }

    public static DoubleListIterator wrap(double[] array, int offset, int length) {
        DoubleArrays.ensureOffsetLength(array, offset, length);
        return new ArrayIterator(array, offset, length);
    }

    public static DoubleListIterator wrap(double[] array) {
        return new ArrayIterator(array, 0, array.length);
    }

    public static int unwrap(DoubleIterator i, double[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0 || offset + max > array.length) {
            throw new IllegalArgumentException();
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextDouble();
        }
        return max - j - 1;
    }

    public static int unwrap(DoubleIterator i, double[] array) {
        return DoubleIterators.unwrap(i, array, 0, array.length);
    }

    public static double[] unwrap(DoubleIterator i, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        double[] array = new double[16];
        int j = 0;
        while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
                array = DoubleArrays.grow(array, j + 1);
            }
            array[j++] = i.nextDouble();
        }
        return DoubleArrays.trim(array, j);
    }

    public static double[] unwrap(DoubleIterator i) {
        return DoubleIterators.unwrap(i, Integer.MAX_VALUE);
    }

    public static long unwrap(DoubleIterator i, double[][] array, long offset, long max) {
        if (max < 0L) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0L || offset + max > BigArrays.length(array)) {
            throw new IllegalArgumentException();
        }
        long j = max;
        while (j-- != 0L && i.hasNext()) {
            BigArrays.set(array, offset++, i.nextDouble());
        }
        return max - j - 1L;
    }

    public static long unwrap(DoubleIterator i, double[][] array) {
        return DoubleIterators.unwrap(i, array, 0L, BigArrays.length(array));
    }

    public static int unwrap(DoubleIterator i, DoubleCollection c, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            c.add(i.nextDouble());
        }
        return max - j - 1;
    }

    public static double[][] unwrapBig(DoubleIterator i, long max) {
        if (max < 0L) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        double[][] array = DoubleBigArrays.newBigArray(16L);
        long j = 0L;
        while (max-- != 0L && i.hasNext()) {
            if (j == BigArrays.length(array)) {
                array = BigArrays.grow(array, j + 1L);
            }
            BigArrays.set(array, j++, i.nextDouble());
        }
        return BigArrays.trim(array, j);
    }

    public static double[][] unwrapBig(DoubleIterator i) {
        return DoubleIterators.unwrapBig(i, Long.MAX_VALUE);
    }

    public static long unwrap(DoubleIterator i, DoubleCollection c) {
        long n = 0L;
        while (i.hasNext()) {
            c.add(i.nextDouble());
            ++n;
        }
        return n;
    }

    public static int pour(DoubleIterator i, DoubleCollection s, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            s.add(i.nextDouble());
        }
        return max - j - 1;
    }

    public static int pour(DoubleIterator i, DoubleCollection s) {
        return DoubleIterators.pour(i, s, Integer.MAX_VALUE);
    }

    public static DoubleList pour(DoubleIterator i, int max) {
        DoubleArrayList l = new DoubleArrayList();
        DoubleIterators.pour(i, l, max);
        l.trim();
        return l;
    }

    public static DoubleList pour(DoubleIterator i) {
        return DoubleIterators.pour(i, Integer.MAX_VALUE);
    }

    public static DoubleIterator asDoubleIterator(Iterator i) {
        if (i instanceof DoubleIterator) {
            return (DoubleIterator)i;
        }
        if (i instanceof PrimitiveIterator.OfDouble) {
            return new PrimitiveIteratorWrapper((PrimitiveIterator.OfDouble)i);
        }
        return new IteratorWrapper(i);
    }

    public static DoubleListIterator asDoubleIterator(ListIterator i) {
        if (i instanceof DoubleListIterator) {
            return (DoubleListIterator)i;
        }
        return new ListIteratorWrapper(i);
    }

    public static boolean any(DoubleIterator iterator, DoublePredicate predicate) {
        return DoubleIterators.indexOf(iterator, predicate) != -1;
    }

    public static boolean all(DoubleIterator iterator, DoublePredicate predicate) {
        Objects.requireNonNull(predicate);
        do {
            if (iterator.hasNext()) continue;
            return true;
        } while (predicate.test(iterator.nextDouble()));
        return false;
    }

    public static int indexOf(DoubleIterator iterator, DoublePredicate predicate) {
        Objects.requireNonNull(predicate);
        int i = 0;
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextDouble())) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static DoubleIterator concat(DoubleIterator ... a) {
        return DoubleIterators.concat(a, 0, a.length);
    }

    public static DoubleIterator concat(DoubleIterator[] a, int offset, int length) {
        return new IteratorConcatenator(a, offset, length);
    }

    public static DoubleIterator unmodifiable(DoubleIterator i) {
        return new UnmodifiableIterator(i);
    }

    public static DoubleBidirectionalIterator unmodifiable(DoubleBidirectionalIterator i) {
        return new UnmodifiableBidirectionalIterator(i);
    }

    public static DoubleListIterator unmodifiable(DoubleListIterator i) {
        return new UnmodifiableListIterator(i);
    }

    public static DoubleIterator wrap(ByteIterator iterator) {
        return new ByteIteratorWrapper(iterator);
    }

    public static DoubleIterator wrap(ShortIterator iterator) {
        return new ShortIteratorWrapper(iterator);
    }

    public static DoubleIterator wrap(CharIterator iterator) {
        return new CharIteratorWrapper(iterator);
    }

    public static DoubleIterator wrap(IntIterator iterator) {
        return new IntIteratorWrapper(iterator);
    }

    public static DoubleIterator wrap(FloatIterator iterator) {
        return new FloatIteratorWrapper(iterator);
    }

    private static class SingletonIterator
    implements DoubleListIterator {
        private final double element;
        private byte curr;

        public SingletonIterator(double element) {
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
        public double nextDouble() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public double previousDouble() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = 0;
            return this.element;
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            Objects.requireNonNull(action);
            if (this.curr == 0) {
                action.accept(this.element);
                this.curr = 1;
            }
        }

        @Override
        public int nextIndex() {
            return this.curr;
        }

        @Override
        public int previousIndex() {
            return this.curr - 1;
        }

        @Override
        public int back(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (n == 0 || this.curr < 1) {
                return 0;
            }
            this.curr = 1;
            return 1;
        }

        @Override
        public int skip(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (n == 0 || this.curr > 0) {
                return 0;
            }
            this.curr = 0;
            return 1;
        }
    }

    private static class ArrayIterator
    implements DoubleListIterator {
        private final double[] array;
        private final int offset;
        private final int length;
        private int curr;

        public ArrayIterator(double[] array, int offset, int length) {
            this.array = array;
            this.offset = offset;
            this.length = length;
        }

        @Override
        public boolean hasNext() {
            return this.curr < this.length;
        }

        @Override
        public boolean hasPrevious() {
            return this.curr > 0;
        }

        @Override
        public double nextDouble() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + this.curr++];
        }

        @Override
        public double previousDouble() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + --this.curr];
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            Objects.requireNonNull(action);
            while (this.curr < this.length) {
                action.accept(this.array[this.offset + this.curr]);
                ++this.curr;
            }
        }

        @Override
        public int skip(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (n <= this.length - this.curr) {
                this.curr += n;
                return n;
            }
            n = this.length - this.curr;
            this.curr = this.length;
            return n;
        }

        @Override
        public int back(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (n <= this.curr) {
                this.curr -= n;
                return n;
            }
            n = this.curr;
            this.curr = 0;
            return n;
        }

        @Override
        public int nextIndex() {
            return this.curr;
        }

        @Override
        public int previousIndex() {
            return this.curr - 1;
        }
    }

    private static class PrimitiveIteratorWrapper
    implements DoubleIterator {
        final PrimitiveIterator.OfDouble i;

        public PrimitiveIteratorWrapper(PrimitiveIterator.OfDouble i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public double nextDouble() {
            return this.i.nextDouble();
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            this.i.forEachRemaining(action);
        }
    }

    private static class IteratorWrapper
    implements DoubleIterator {
        final Iterator<Double> i;

        public IteratorWrapper(Iterator<Double> i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public double nextDouble() {
            return this.i.next();
        }

        @Override
        public void forEachRemaining(DoubleConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            Objects.requireNonNull(action);
            this.i.forEachRemaining(action instanceof Consumer ? (Consumer<Double>)((Object)action) : action::accept);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Double> action) {
            this.i.forEachRemaining(action);
        }
    }

    private static class ListIteratorWrapper
    implements DoubleListIterator {
        final ListIterator<Double> i;

        public ListIteratorWrapper(ListIterator<Double> i) {
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
        public int nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.i.previousIndex();
        }

        @Override
        public void set(double k) {
            this.i.set(k);
        }

        @Override
        public void add(double k) {
            this.i.add(k);
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public double nextDouble() {
            return this.i.next();
        }

        @Override
        public double previousDouble() {
            return this.i.previous();
        }

        @Override
        public void forEachRemaining(DoubleConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            Objects.requireNonNull(action);
            this.i.forEachRemaining(action instanceof Consumer ? (Consumer<Double>)((Object)action) : action::accept);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Double> action) {
            this.i.forEachRemaining(action);
        }
    }

    private static class IteratorConcatenator
    implements DoubleIterator {
        final DoubleIterator[] a;
        int offset;
        int length;
        int lastOffset = -1;

        public IteratorConcatenator(DoubleIterator[] a, int offset, int length) {
            this.a = a;
            this.offset = offset;
            this.length = length;
            this.advance();
        }

        private void advance() {
            while (this.length != 0 && !this.a[this.offset].hasNext()) {
                --this.length;
                ++this.offset;
            }
        }

        @Override
        public boolean hasNext() {
            return this.length > 0;
        }

        @Override
        public double nextDouble() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastOffset = this.offset;
            double next = this.a[this.lastOffset].nextDouble();
            this.advance();
            return next;
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            while (this.length > 0) {
                this.lastOffset = this.offset;
                this.a[this.lastOffset].forEachRemaining(action);
                this.advance();
            }
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Double> action) {
            while (this.length > 0) {
                this.lastOffset = this.offset;
                this.a[this.lastOffset].forEachRemaining(action);
                this.advance();
            }
        }

        @Override
        public void remove() {
            if (this.lastOffset == -1) {
                throw new IllegalStateException();
            }
            this.a[this.lastOffset].remove();
        }

        @Override
        public int skip(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            this.lastOffset = -1;
            int skipped = 0;
            while (skipped < n && this.length != 0) {
                skipped += this.a[this.offset].skip(n - skipped);
                if (this.a[this.offset].hasNext()) break;
                --this.length;
                ++this.offset;
            }
            return skipped;
        }
    }

    public static class UnmodifiableIterator
    implements DoubleIterator {
        protected final DoubleIterator i;

        public UnmodifiableIterator(DoubleIterator i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public double nextDouble() {
            return this.i.nextDouble();
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Double> action) {
            this.i.forEachRemaining(action);
        }
    }

    public static class UnmodifiableBidirectionalIterator
    implements DoubleBidirectionalIterator {
        protected final DoubleBidirectionalIterator i;

        public UnmodifiableBidirectionalIterator(DoubleBidirectionalIterator i) {
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
        public double nextDouble() {
            return this.i.nextDouble();
        }

        @Override
        public double previousDouble() {
            return this.i.previousDouble();
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Double> action) {
            this.i.forEachRemaining(action);
        }
    }

    public static class UnmodifiableListIterator
    implements DoubleListIterator {
        protected final DoubleListIterator i;

        public UnmodifiableListIterator(DoubleListIterator i) {
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
        public double nextDouble() {
            return this.i.nextDouble();
        }

        @Override
        public double previousDouble() {
            return this.i.previousDouble();
        }

        @Override
        public int nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.i.previousIndex();
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Double> action) {
            this.i.forEachRemaining(action);
        }
    }

    private static final class ByteIteratorWrapper
    implements DoubleIterator {
        final ByteIterator iterator;

        public ByteIteratorWrapper(ByteIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        @Deprecated
        public Double next() {
            return this.iterator.nextByte();
        }

        @Override
        public double nextDouble() {
            return this.iterator.nextByte();
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            Objects.requireNonNull(action);
            this.iterator.forEachRemaining(action::accept);
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }

        @Override
        public int skip(int n) {
            return this.iterator.skip(n);
        }
    }

    private static final class ShortIteratorWrapper
    implements DoubleIterator {
        final ShortIterator iterator;

        public ShortIteratorWrapper(ShortIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        @Deprecated
        public Double next() {
            return this.iterator.nextShort();
        }

        @Override
        public double nextDouble() {
            return this.iterator.nextShort();
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            Objects.requireNonNull(action);
            this.iterator.forEachRemaining(action::accept);
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }

        @Override
        public int skip(int n) {
            return this.iterator.skip(n);
        }
    }

    private static final class CharIteratorWrapper
    implements DoubleIterator {
        final CharIterator iterator;

        public CharIteratorWrapper(CharIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        @Deprecated
        public Double next() {
            return this.iterator.nextChar();
        }

        @Override
        public double nextDouble() {
            return this.iterator.nextChar();
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            Objects.requireNonNull(action);
            this.iterator.forEachRemaining(action::accept);
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }

        @Override
        public int skip(int n) {
            return this.iterator.skip(n);
        }
    }

    private static final class IntIteratorWrapper
    implements DoubleIterator {
        final IntIterator iterator;

        public IntIteratorWrapper(IntIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        @Deprecated
        public Double next() {
            return this.iterator.nextInt();
        }

        @Override
        public double nextDouble() {
            return this.iterator.nextInt();
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            Objects.requireNonNull(action);
            this.iterator.forEachRemaining(action::accept);
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }

        @Override
        public int skip(int n) {
            return this.iterator.skip(n);
        }
    }

    private static final class FloatIteratorWrapper
    implements DoubleIterator {
        final FloatIterator iterator;

        public FloatIteratorWrapper(FloatIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        @Deprecated
        public Double next() {
            return this.iterator.nextFloat();
        }

        @Override
        public double nextDouble() {
            return this.iterator.nextFloat();
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            Objects.requireNonNull(action);
            this.iterator.forEachRemaining(action::accept);
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }

        @Override
        public int skip(int n) {
            return this.iterator.skip(n);
        }
    }

    public static class EmptyIterator
    implements DoubleListIterator,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyIterator() {
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
        public double nextDouble() {
            throw new NoSuchElementException();
        }

        @Override
        public double previousDouble() {
            throw new NoSuchElementException();
        }

        @Override
        public int nextIndex() {
            return 0;
        }

        @Override
        public int previousIndex() {
            return -1;
        }

        @Override
        public int skip(int n) {
            return 0;
        }

        @Override
        public int back(int n) {
            return 0;
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Double> action) {
        }

        public Object clone() {
            return EMPTY_ITERATOR;
        }

        private Object readResolve() {
            return EMPTY_ITERATOR;
        }
    }

    public static abstract class AbstractIndexBasedListIterator
    extends AbstractIndexBasedIterator
    implements DoubleListIterator {
        protected AbstractIndexBasedListIterator(int minPos, int initialPos) {
            super(minPos, initialPos);
        }

        protected abstract void add(int var1, double var2);

        protected abstract void set(int var1, double var2);

        @Override
        public boolean hasPrevious() {
            return this.pos > this.minPos;
        }

        @Override
        public double previousDouble() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.lastReturned = --this.pos;
            return this.get(this.pos);
        }

        @Override
        public int nextIndex() {
            return this.pos;
        }

        @Override
        public int previousIndex() {
            return this.pos - 1;
        }

        @Override
        public void add(double k) {
            this.add(this.pos++, k);
            this.lastReturned = -1;
        }

        @Override
        public void set(double k) {
            if (this.lastReturned == -1) {
                throw new IllegalStateException();
            }
            this.set(this.lastReturned, k);
        }

        @Override
        public int back(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            int remaining = this.pos - this.minPos;
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

    public static abstract class AbstractIndexBasedIterator
    extends AbstractDoubleIterator {
        protected final int minPos;
        protected int pos;
        protected int lastReturned;

        protected AbstractIndexBasedIterator(int minPos, int initialPos) {
            this.minPos = minPos;
            this.pos = initialPos;
        }

        protected abstract double get(int var1);

        protected abstract void remove(int var1);

        protected abstract int getMaxPos();

        @Override
        public boolean hasNext() {
            return this.pos < this.getMaxPos();
        }

        @Override
        public double nextDouble() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.pos++;
            return this.get(this.lastReturned);
        }

        @Override
        public void remove() {
            if (this.lastReturned == -1) {
                throw new IllegalStateException();
            }
            this.remove(this.lastReturned);
            if (this.lastReturned < this.pos) {
                --this.pos;
            }
            this.lastReturned = -1;
        }

        @Override
        public void forEachRemaining(java.util.function.DoubleConsumer action) {
            while (this.pos < this.getMaxPos()) {
                ++this.pos;
                this.lastReturned = this.lastReturned;
                action.accept(this.get(this.lastReturned));
            }
        }

        @Override
        public int skip(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            int max = this.getMaxPos();
            int remaining = max - this.pos;
            if (n < remaining) {
                this.pos += n;
            } else {
                n = remaining;
                this.pos = max;
            }
            this.lastReturned = this.pos - 1;
            return n;
        }
    }
}

