/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.AbstractByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.BytePredicate;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public final class ByteIterators {
    public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    private ByteIterators() {
    }

    public static ByteListIterator singleton(byte element) {
        return new SingletonIterator(element);
    }

    public static ByteListIterator wrap(byte[] array, int offset, int length) {
        ByteArrays.ensureOffsetLength(array, offset, length);
        return new ArrayIterator(array, offset, length);
    }

    public static ByteListIterator wrap(byte[] array) {
        return new ArrayIterator(array, 0, array.length);
    }

    public static int unwrap(ByteIterator i, byte[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0 || offset + max > array.length) {
            throw new IllegalArgumentException();
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextByte();
        }
        return max - j - 1;
    }

    public static int unwrap(ByteIterator i, byte[] array) {
        return ByteIterators.unwrap(i, array, 0, array.length);
    }

    public static byte[] unwrap(ByteIterator i, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        byte[] array = new byte[16];
        int j = 0;
        while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
                array = ByteArrays.grow(array, j + 1);
            }
            array[j++] = i.nextByte();
        }
        return ByteArrays.trim(array, j);
    }

    public static byte[] unwrap(ByteIterator i) {
        return ByteIterators.unwrap(i, Integer.MAX_VALUE);
    }

    public static long unwrap(ByteIterator i, byte[][] array, long offset, long max) {
        if (max < 0L) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0L || offset + max > BigArrays.length(array)) {
            throw new IllegalArgumentException();
        }
        long j = max;
        while (j-- != 0L && i.hasNext()) {
            BigArrays.set(array, offset++, i.nextByte());
        }
        return max - j - 1L;
    }

    public static long unwrap(ByteIterator i, byte[][] array) {
        return ByteIterators.unwrap(i, array, 0L, BigArrays.length(array));
    }

    public static int unwrap(ByteIterator i, ByteCollection c, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            c.add(i.nextByte());
        }
        return max - j - 1;
    }

    public static byte[][] unwrapBig(ByteIterator i, long max) {
        if (max < 0L) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        byte[][] array = ByteBigArrays.newBigArray(16L);
        long j = 0L;
        while (max-- != 0L && i.hasNext()) {
            if (j == BigArrays.length(array)) {
                array = BigArrays.grow(array, j + 1L);
            }
            BigArrays.set(array, j++, i.nextByte());
        }
        return BigArrays.trim(array, j);
    }

    public static byte[][] unwrapBig(ByteIterator i) {
        return ByteIterators.unwrapBig(i, Long.MAX_VALUE);
    }

    public static long unwrap(ByteIterator i, ByteCollection c) {
        long n = 0L;
        while (i.hasNext()) {
            c.add(i.nextByte());
            ++n;
        }
        return n;
    }

    public static int pour(ByteIterator i, ByteCollection s, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            s.add(i.nextByte());
        }
        return max - j - 1;
    }

    public static int pour(ByteIterator i, ByteCollection s) {
        return ByteIterators.pour(i, s, Integer.MAX_VALUE);
    }

    public static ByteList pour(ByteIterator i, int max) {
        ByteArrayList l = new ByteArrayList();
        ByteIterators.pour(i, l, max);
        l.trim();
        return l;
    }

    public static ByteList pour(ByteIterator i) {
        return ByteIterators.pour(i, Integer.MAX_VALUE);
    }

    public static ByteIterator asByteIterator(Iterator i) {
        if (i instanceof ByteIterator) {
            return (ByteIterator)i;
        }
        return new IteratorWrapper(i);
    }

    public static ByteIterator narrow(PrimitiveIterator.OfInt i) {
        return new CheckedPrimitiveIteratorWrapper(i);
    }

    public static ByteIterator uncheckedNarrow(PrimitiveIterator.OfInt i) {
        return new PrimitiveIteratorWrapper(i);
    }

    public static IntIterator widen(ByteIterator i) {
        return IntIterators.wrap(i);
    }

    public static ByteListIterator asByteIterator(ListIterator i) {
        if (i instanceof ByteListIterator) {
            return (ByteListIterator)i;
        }
        return new ListIteratorWrapper(i);
    }

    public static boolean any(ByteIterator iterator, BytePredicate predicate) {
        return ByteIterators.indexOf(iterator, predicate) != -1;
    }

    public static boolean any(ByteIterator iterator, IntPredicate predicate) {
        return ByteIterators.any(iterator, predicate instanceof BytePredicate ? (BytePredicate)predicate : predicate::test);
    }

    public static boolean all(ByteIterator iterator, BytePredicate predicate) {
        Objects.requireNonNull(predicate);
        do {
            if (iterator.hasNext()) continue;
            return true;
        } while (predicate.test(iterator.nextByte()));
        return false;
    }

    public static boolean all(ByteIterator iterator, IntPredicate predicate) {
        return ByteIterators.all(iterator, predicate instanceof BytePredicate ? (BytePredicate)predicate : predicate::test);
    }

    public static int indexOf(ByteIterator iterator, BytePredicate predicate) {
        Objects.requireNonNull(predicate);
        int i = 0;
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextByte())) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOf(ByteIterator iterator, IntPredicate predicate) {
        return ByteIterators.indexOf(iterator, predicate instanceof BytePredicate ? (BytePredicate)predicate : predicate::test);
    }

    public static ByteListIterator fromTo(byte from, byte to) {
        return new IntervalIterator(from, to);
    }

    public static ByteIterator concat(ByteIterator ... a) {
        return ByteIterators.concat(a, 0, a.length);
    }

    public static ByteIterator concat(ByteIterator[] a, int offset, int length) {
        return new IteratorConcatenator(a, offset, length);
    }

    public static ByteIterator unmodifiable(ByteIterator i) {
        return new UnmodifiableIterator(i);
    }

    public static ByteBidirectionalIterator unmodifiable(ByteBidirectionalIterator i) {
        return new UnmodifiableBidirectionalIterator(i);
    }

    public static ByteListIterator unmodifiable(ByteListIterator i) {
        return new UnmodifiableListIterator(i);
    }

    private static class SingletonIterator
    implements ByteListIterator {
        private final byte element;
        private byte curr;

        public SingletonIterator(byte element) {
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
        public byte nextByte() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public byte previousByte() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = 0;
            return this.element;
        }

        @Override
        public void forEachRemaining(ByteConsumer action) {
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
    implements ByteListIterator {
        private final byte[] array;
        private final int offset;
        private final int length;
        private int curr;

        public ArrayIterator(byte[] array, int offset, int length) {
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
        public byte nextByte() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + this.curr++];
        }

        @Override
        public byte previousByte() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + --this.curr];
        }

        @Override
        public void forEachRemaining(ByteConsumer action) {
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
    implements ByteIterator {
        final Iterator<Byte> i;

        public IteratorWrapper(Iterator<Byte> i) {
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
        public byte nextByte() {
            return this.i.next();
        }

        @Override
        public void forEachRemaining(ByteConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Byte> action) {
            this.i.forEachRemaining(action);
        }
    }

    private static class CheckedPrimitiveIteratorWrapper
    extends PrimitiveIteratorWrapper {
        public CheckedPrimitiveIteratorWrapper(PrimitiveIterator.OfInt i) {
            super(i);
        }

        @Override
        public byte nextByte() {
            return SafeMath.safeIntToByte(this.i.nextInt());
        }

        @Override
        public void forEachRemaining(ByteConsumer action) {
            this.i.forEachRemaining((int value) -> action.accept(SafeMath.safeIntToByte(value)));
        }
    }

    private static class PrimitiveIteratorWrapper
    implements ByteIterator {
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
        public byte nextByte() {
            return (byte)this.i.nextInt();
        }

        @Override
        public void forEachRemaining(ByteConsumer action) {
            this.i.forEachRemaining(action);
        }
    }

    private static class ListIteratorWrapper
    implements ByteListIterator {
        final ListIterator<Byte> i;

        public ListIteratorWrapper(ListIterator<Byte> i) {
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
        public void set(byte k) {
            this.i.set(k);
        }

        @Override
        public void add(byte k) {
            this.i.add(k);
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public byte nextByte() {
            return this.i.next();
        }

        @Override
        public byte previousByte() {
            return this.i.previous();
        }

        @Override
        public void forEachRemaining(ByteConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Byte> action) {
            this.i.forEachRemaining(action);
        }
    }

    private static class IntervalIterator
    implements ByteListIterator {
        private final byte from;
        private final byte to;
        byte curr;

        public IntervalIterator(byte from, byte to) {
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
        public byte nextByte() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            byte by = this.curr;
            this.curr = (byte)(by + 1);
            return by;
        }

        @Override
        public byte previousByte() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = (byte)(this.curr - 1);
            return this.curr;
        }

        @Override
        public void forEachRemaining(ByteConsumer action) {
            Objects.requireNonNull(action);
            while (this.curr < this.to) {
                action.accept(this.curr);
                this.curr = (byte)(this.curr + 1);
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
                this.curr = (byte)(this.curr + n);
                return n;
            }
            n = this.to - this.curr;
            this.curr = this.to;
            return n;
        }

        @Override
        public int back(int n) {
            if (this.curr - n >= this.from) {
                this.curr = (byte)(this.curr - n);
                return n;
            }
            n = this.curr - this.from;
            this.curr = this.from;
            return n;
        }
    }

    private static class IteratorConcatenator
    implements ByteIterator {
        final ByteIterator[] a;
        int offset;
        int length;
        int lastOffset = -1;

        public IteratorConcatenator(ByteIterator[] a, int offset, int length) {
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
        public byte nextByte() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastOffset = this.offset;
            byte next = this.a[this.lastOffset].nextByte();
            this.advance();
            return next;
        }

        @Override
        public void forEachRemaining(ByteConsumer action) {
            while (this.length > 0) {
                this.lastOffset = this.offset;
                this.a[this.lastOffset].forEachRemaining(action);
                this.advance();
            }
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Byte> action) {
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
    implements ByteIterator {
        protected final ByteIterator i;

        public UnmodifiableIterator(ByteIterator i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public byte nextByte() {
            return this.i.nextByte();
        }

        @Override
        public void forEachRemaining(ByteConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Byte> action) {
            this.i.forEachRemaining(action);
        }
    }

    public static class UnmodifiableBidirectionalIterator
    implements ByteBidirectionalIterator {
        protected final ByteBidirectionalIterator i;

        public UnmodifiableBidirectionalIterator(ByteBidirectionalIterator i) {
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
        public byte nextByte() {
            return this.i.nextByte();
        }

        @Override
        public byte previousByte() {
            return this.i.previousByte();
        }

        @Override
        public void forEachRemaining(ByteConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Byte> action) {
            this.i.forEachRemaining(action);
        }
    }

    public static class UnmodifiableListIterator
    implements ByteListIterator {
        protected final ByteListIterator i;

        public UnmodifiableListIterator(ByteListIterator i) {
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
        public byte nextByte() {
            return this.i.nextByte();
        }

        @Override
        public byte previousByte() {
            return this.i.previousByte();
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
        public void forEachRemaining(ByteConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Byte> action) {
            this.i.forEachRemaining(action);
        }
    }

    public static class EmptyIterator
    implements ByteListIterator,
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
        public byte nextByte() {
            throw new NoSuchElementException();
        }

        @Override
        public byte previousByte() {
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
        public void forEachRemaining(ByteConsumer action) {
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Byte> action) {
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
    implements ByteListIterator {
        protected AbstractIndexBasedListIterator(int minPos, int initialPos) {
            super(minPos, initialPos);
        }

        protected abstract void add(int var1, byte var2);

        protected abstract void set(int var1, byte var2);

        @Override
        public boolean hasPrevious() {
            return this.pos > this.minPos;
        }

        @Override
        public byte previousByte() {
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
        public void add(byte k) {
            this.add(this.pos++, k);
            this.lastReturned = -1;
        }

        @Override
        public void set(byte k) {
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
    extends AbstractByteIterator {
        protected final int minPos;
        protected int pos;
        protected int lastReturned;

        protected AbstractIndexBasedIterator(int minPos, int initialPos) {
            this.minPos = minPos;
            this.pos = initialPos;
        }

        protected abstract byte get(int var1);

        protected abstract void remove(int var1);

        protected abstract int getMaxPos();

        @Override
        public boolean hasNext() {
            return this.pos < this.getMaxPos();
        }

        @Override
        public byte nextByte() {
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
        public void forEachRemaining(ByteConsumer action) {
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

