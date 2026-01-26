/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.shorts.AbstractShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortBigArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import it.unimi.dsi.fastutil.shorts.ShortPredicate;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public final class ShortIterators {
    public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    private ShortIterators() {
    }

    public static ShortListIterator singleton(short element) {
        return new SingletonIterator(element);
    }

    public static ShortListIterator wrap(short[] array, int offset, int length) {
        ShortArrays.ensureOffsetLength(array, offset, length);
        return new ArrayIterator(array, offset, length);
    }

    public static ShortListIterator wrap(short[] array) {
        return new ArrayIterator(array, 0, array.length);
    }

    public static int unwrap(ShortIterator i, short[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0 || offset + max > array.length) {
            throw new IllegalArgumentException();
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextShort();
        }
        return max - j - 1;
    }

    public static int unwrap(ShortIterator i, short[] array) {
        return ShortIterators.unwrap(i, array, 0, array.length);
    }

    public static short[] unwrap(ShortIterator i, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        short[] array = new short[16];
        int j = 0;
        while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
                array = ShortArrays.grow(array, j + 1);
            }
            array[j++] = i.nextShort();
        }
        return ShortArrays.trim(array, j);
    }

    public static short[] unwrap(ShortIterator i) {
        return ShortIterators.unwrap(i, Integer.MAX_VALUE);
    }

    public static long unwrap(ShortIterator i, short[][] array, long offset, long max) {
        if (max < 0L) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0L || offset + max > BigArrays.length(array)) {
            throw new IllegalArgumentException();
        }
        long j = max;
        while (j-- != 0L && i.hasNext()) {
            BigArrays.set(array, offset++, i.nextShort());
        }
        return max - j - 1L;
    }

    public static long unwrap(ShortIterator i, short[][] array) {
        return ShortIterators.unwrap(i, array, 0L, BigArrays.length(array));
    }

    public static int unwrap(ShortIterator i, ShortCollection c, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            c.add(i.nextShort());
        }
        return max - j - 1;
    }

    public static short[][] unwrapBig(ShortIterator i, long max) {
        if (max < 0L) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        short[][] array = ShortBigArrays.newBigArray(16L);
        long j = 0L;
        while (max-- != 0L && i.hasNext()) {
            if (j == BigArrays.length(array)) {
                array = BigArrays.grow(array, j + 1L);
            }
            BigArrays.set(array, j++, i.nextShort());
        }
        return BigArrays.trim(array, j);
    }

    public static short[][] unwrapBig(ShortIterator i) {
        return ShortIterators.unwrapBig(i, Long.MAX_VALUE);
    }

    public static long unwrap(ShortIterator i, ShortCollection c) {
        long n = 0L;
        while (i.hasNext()) {
            c.add(i.nextShort());
            ++n;
        }
        return n;
    }

    public static int pour(ShortIterator i, ShortCollection s, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            s.add(i.nextShort());
        }
        return max - j - 1;
    }

    public static int pour(ShortIterator i, ShortCollection s) {
        return ShortIterators.pour(i, s, Integer.MAX_VALUE);
    }

    public static ShortList pour(ShortIterator i, int max) {
        ShortArrayList l = new ShortArrayList();
        ShortIterators.pour(i, l, max);
        l.trim();
        return l;
    }

    public static ShortList pour(ShortIterator i) {
        return ShortIterators.pour(i, Integer.MAX_VALUE);
    }

    public static ShortIterator asShortIterator(Iterator i) {
        if (i instanceof ShortIterator) {
            return (ShortIterator)i;
        }
        return new IteratorWrapper(i);
    }

    public static ShortIterator narrow(PrimitiveIterator.OfInt i) {
        return new CheckedPrimitiveIteratorWrapper(i);
    }

    public static ShortIterator uncheckedNarrow(PrimitiveIterator.OfInt i) {
        return new PrimitiveIteratorWrapper(i);
    }

    public static IntIterator widen(ShortIterator i) {
        return IntIterators.wrap(i);
    }

    public static ShortListIterator asShortIterator(ListIterator i) {
        if (i instanceof ShortListIterator) {
            return (ShortListIterator)i;
        }
        return new ListIteratorWrapper(i);
    }

    public static boolean any(ShortIterator iterator, ShortPredicate predicate) {
        return ShortIterators.indexOf(iterator, predicate) != -1;
    }

    public static boolean any(ShortIterator iterator, IntPredicate predicate) {
        return ShortIterators.any(iterator, predicate instanceof ShortPredicate ? (ShortPredicate)predicate : predicate::test);
    }

    public static boolean all(ShortIterator iterator, ShortPredicate predicate) {
        Objects.requireNonNull(predicate);
        do {
            if (iterator.hasNext()) continue;
            return true;
        } while (predicate.test(iterator.nextShort()));
        return false;
    }

    public static boolean all(ShortIterator iterator, IntPredicate predicate) {
        return ShortIterators.all(iterator, predicate instanceof ShortPredicate ? (ShortPredicate)predicate : predicate::test);
    }

    public static int indexOf(ShortIterator iterator, ShortPredicate predicate) {
        Objects.requireNonNull(predicate);
        int i = 0;
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextShort())) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOf(ShortIterator iterator, IntPredicate predicate) {
        return ShortIterators.indexOf(iterator, predicate instanceof ShortPredicate ? (ShortPredicate)predicate : predicate::test);
    }

    public static ShortListIterator fromTo(short from, short to) {
        return new IntervalIterator(from, to);
    }

    public static ShortIterator concat(ShortIterator ... a) {
        return ShortIterators.concat(a, 0, a.length);
    }

    public static ShortIterator concat(ShortIterator[] a, int offset, int length) {
        return new IteratorConcatenator(a, offset, length);
    }

    public static ShortIterator unmodifiable(ShortIterator i) {
        return new UnmodifiableIterator(i);
    }

    public static ShortBidirectionalIterator unmodifiable(ShortBidirectionalIterator i) {
        return new UnmodifiableBidirectionalIterator(i);
    }

    public static ShortListIterator unmodifiable(ShortListIterator i) {
        return new UnmodifiableListIterator(i);
    }

    public static ShortIterator wrap(ByteIterator iterator) {
        return new ByteIteratorWrapper(iterator);
    }

    private static class SingletonIterator
    implements ShortListIterator {
        private final short element;
        private byte curr;

        public SingletonIterator(short element) {
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
        public short nextShort() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public short previousShort() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = 0;
            return this.element;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
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
    implements ShortListIterator {
        private final short[] array;
        private final int offset;
        private final int length;
        private int curr;

        public ArrayIterator(short[] array, int offset, int length) {
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
        public short nextShort() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + this.curr++];
        }

        @Override
        public short previousShort() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + --this.curr];
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
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

    private static class IteratorWrapper
    implements ShortIterator {
        final Iterator<Short> i;

        public IteratorWrapper(Iterator<Short> i) {
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
        public short nextShort() {
            return this.i.next();
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Short> action) {
            this.i.forEachRemaining(action);
        }
    }

    private static class CheckedPrimitiveIteratorWrapper
    extends PrimitiveIteratorWrapper {
        public CheckedPrimitiveIteratorWrapper(PrimitiveIterator.OfInt i) {
            super(i);
        }

        @Override
        public short nextShort() {
            return SafeMath.safeIntToShort(this.i.nextInt());
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            this.i.forEachRemaining((int value) -> action.accept(SafeMath.safeIntToShort(value)));
        }
    }

    private static class PrimitiveIteratorWrapper
    implements ShortIterator {
        final PrimitiveIterator.OfInt i;

        public PrimitiveIteratorWrapper(PrimitiveIterator.OfInt i) {
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
        public short nextShort() {
            return (short)this.i.nextInt();
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            this.i.forEachRemaining(action);
        }
    }

    private static class ListIteratorWrapper
    implements ShortListIterator {
        final ListIterator<Short> i;

        public ListIteratorWrapper(ListIterator<Short> i) {
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
        public void set(short k) {
            this.i.set(k);
        }

        @Override
        public void add(short k) {
            this.i.add(k);
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public short nextShort() {
            return this.i.next();
        }

        @Override
        public short previousShort() {
            return this.i.previous();
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Short> action) {
            this.i.forEachRemaining(action);
        }
    }

    private static class IntervalIterator
    implements ShortListIterator {
        private final short from;
        private final short to;
        short curr;

        public IntervalIterator(short from, short to) {
            this.from = this.curr = from;
            this.to = to;
        }

        @Override
        public boolean hasNext() {
            return this.curr < this.to;
        }

        @Override
        public boolean hasPrevious() {
            return this.curr > this.from;
        }

        @Override
        public short nextShort() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            short s = this.curr;
            this.curr = (short)(s + 1);
            return s;
        }

        @Override
        public short previousShort() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = (short)(this.curr - 1);
            return this.curr;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            Objects.requireNonNull(action);
            while (this.curr < this.to) {
                action.accept(this.curr);
                this.curr = (short)(this.curr + 1);
            }
        }

        @Override
        public int nextIndex() {
            return this.curr - this.from;
        }

        @Override
        public int previousIndex() {
            return this.curr - this.from - 1;
        }

        @Override
        public int skip(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (this.curr + n <= this.to) {
                this.curr = (short)(this.curr + n);
                return n;
            }
            n = this.to - this.curr;
            this.curr = this.to;
            return n;
        }

        @Override
        public int back(int n) {
            if (this.curr - n >= this.from) {
                this.curr = (short)(this.curr - n);
                return n;
            }
            n = this.curr - this.from;
            this.curr = this.from;
            return n;
        }
    }

    private static class IteratorConcatenator
    implements ShortIterator {
        final ShortIterator[] a;
        int offset;
        int length;
        int lastOffset = -1;

        public IteratorConcatenator(ShortIterator[] a, int offset, int length) {
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
        public short nextShort() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastOffset = this.offset;
            short next = this.a[this.lastOffset].nextShort();
            this.advance();
            return next;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            while (this.length > 0) {
                this.lastOffset = this.offset;
                this.a[this.lastOffset].forEachRemaining(action);
                this.advance();
            }
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Short> action) {
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
    implements ShortIterator {
        protected final ShortIterator i;

        public UnmodifiableIterator(ShortIterator i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public short nextShort() {
            return this.i.nextShort();
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Short> action) {
            this.i.forEachRemaining(action);
        }
    }

    public static class UnmodifiableBidirectionalIterator
    implements ShortBidirectionalIterator {
        protected final ShortBidirectionalIterator i;

        public UnmodifiableBidirectionalIterator(ShortBidirectionalIterator i) {
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
        public short nextShort() {
            return this.i.nextShort();
        }

        @Override
        public short previousShort() {
            return this.i.previousShort();
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Short> action) {
            this.i.forEachRemaining(action);
        }
    }

    public static class UnmodifiableListIterator
    implements ShortListIterator {
        protected final ShortListIterator i;

        public UnmodifiableListIterator(ShortListIterator i) {
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
        public short nextShort() {
            return this.i.nextShort();
        }

        @Override
        public short previousShort() {
            return this.i.previousShort();
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
        public void forEachRemaining(ShortConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Short> action) {
            this.i.forEachRemaining(action);
        }
    }

    private static final class ByteIteratorWrapper
    implements ShortIterator {
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
        public Short next() {
            return this.iterator.nextByte();
        }

        @Override
        public short nextShort() {
            return this.iterator.nextByte();
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
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
    implements ShortListIterator,
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
        public short nextShort() {
            throw new NoSuchElementException();
        }

        @Override
        public short previousShort() {
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
        public void forEachRemaining(ShortConsumer action) {
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Short> action) {
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
    implements ShortListIterator {
        protected AbstractIndexBasedListIterator(int minPos, int initialPos) {
            super(minPos, initialPos);
        }

        protected abstract void add(int var1, short var2);

        protected abstract void set(int var1, short var2);

        @Override
        public boolean hasPrevious() {
            return this.pos > this.minPos;
        }

        @Override
        public short previousShort() {
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
        public void add(short k) {
            this.add(this.pos++, k);
            this.lastReturned = -1;
        }

        @Override
        public void set(short k) {
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
    extends AbstractShortIterator {
        protected final int minPos;
        protected int pos;
        protected int lastReturned;

        protected AbstractIndexBasedIterator(int minPos, int initialPos) {
            this.minPos = minPos;
            this.pos = initialPos;
        }

        protected abstract short get(int var1);

        protected abstract void remove(int var1);

        protected abstract int getMaxPos();

        @Override
        public boolean hasNext() {
            return this.pos < this.getMaxPos();
        }

        @Override
        public short nextShort() {
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
        public void forEachRemaining(ShortConsumer action) {
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

