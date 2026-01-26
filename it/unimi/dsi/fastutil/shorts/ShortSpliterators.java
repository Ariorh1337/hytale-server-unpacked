/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import it.unimi.dsi.fastutil.shorts.AbstractShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortBigListIterator;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortComparators;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortPredicate;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

public final class ShortSpliterators {
    static final int BASE_SPLITERATOR_CHARACTERISTICS = 256;
    public static final int COLLECTION_SPLITERATOR_CHARACTERISTICS = 320;
    public static final int LIST_SPLITERATOR_CHARACTERISTICS = 16720;
    public static final int SET_SPLITERATOR_CHARACTERISTICS = 321;
    private static final int SORTED_CHARACTERISTICS = 20;
    public static final int SORTED_SET_SPLITERATOR_CHARACTERISTICS = 341;
    public static final EmptySpliterator EMPTY_SPLITERATOR = new EmptySpliterator();

    private ShortSpliterators() {
    }

    public static ShortSpliterator singleton(short element) {
        return new SingletonSpliterator(element);
    }

    public static ShortSpliterator singleton(short element, ShortComparator comparator) {
        return new SingletonSpliterator(element, comparator);
    }

    public static ShortSpliterator wrap(short[] array, int offset, int length) {
        ShortArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliterator(array, offset, length, 0);
    }

    public static ShortSpliterator wrap(short[] array) {
        return new ArraySpliterator(array, 0, array.length, 0);
    }

    public static ShortSpliterator wrap(short[] array, int offset, int length, int additionalCharacteristics) {
        ShortArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliterator(array, offset, length, additionalCharacteristics);
    }

    public static ShortSpliterator wrapPreSorted(short[] array, int offset, int length, int additionalCharacteristics, ShortComparator comparator) {
        ShortArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliteratorWithComparator(array, offset, length, additionalCharacteristics, comparator);
    }

    public static ShortSpliterator wrapPreSorted(short[] array, int offset, int length, ShortComparator comparator) {
        return ShortSpliterators.wrapPreSorted(array, offset, length, 0, comparator);
    }

    public static ShortSpliterator wrapPreSorted(short[] array, ShortComparator comparator) {
        return ShortSpliterators.wrapPreSorted(array, 0, array.length, comparator);
    }

    public static ShortSpliterator asShortSpliterator(Spliterator i) {
        if (i instanceof ShortSpliterator) {
            return (ShortSpliterator)i;
        }
        return new SpliteratorWrapper(i);
    }

    public static ShortSpliterator asShortSpliterator(Spliterator i, ShortComparator comparatorOverride) {
        if (i instanceof ShortSpliterator) {
            throw new IllegalArgumentException("Cannot override comparator on instance that is already a " + ShortSpliterator.class.getSimpleName());
        }
        if (i instanceof Spliterator.OfInt) {
            return new PrimitiveSpliteratorWrapperWithComparator((Spliterator.OfInt)i, comparatorOverride);
        }
        return new SpliteratorWrapperWithComparator(i, comparatorOverride);
    }

    public static ShortSpliterator narrow(Spliterator.OfInt i) {
        return new PrimitiveSpliteratorWrapper(i);
    }

    public static IntSpliterator widen(ShortSpliterator i) {
        return IntSpliterators.wrap(i);
    }

    public static void onEachMatching(ShortSpliterator spliterator, ShortPredicate predicate, ShortConsumer action) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(action);
        spliterator.forEachRemaining(value -> {
            if (predicate.test(value)) {
                action.accept(value);
            }
        });
    }

    public static void onEachMatching(ShortSpliterator spliterator, IntPredicate predicate, IntConsumer action) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(action);
        spliterator.forEachRemaining(value -> {
            if (predicate.test(value)) {
                action.accept(value);
            }
        });
    }

    public static ShortSpliterator fromTo(short from, short to) {
        return new IntervalSpliterator(from, to);
    }

    public static ShortSpliterator concat(ShortSpliterator ... a) {
        return ShortSpliterators.concat(a, 0, a.length);
    }

    public static ShortSpliterator concat(ShortSpliterator[] a, int offset, int length) {
        return new SpliteratorConcatenator(a, offset, length);
    }

    public static ShortSpliterator asSpliterator(ShortIterator iter, long size, int additionalCharacterisitcs) {
        return new SpliteratorFromIterator(iter, size, additionalCharacterisitcs);
    }

    public static ShortSpliterator asSpliteratorFromSorted(ShortIterator iter, long size, int additionalCharacterisitcs, ShortComparator comparator) {
        return new SpliteratorFromIteratorWithComparator(iter, size, additionalCharacterisitcs, comparator);
    }

    public static ShortSpliterator asSpliteratorUnknownSize(ShortIterator iter, int characterisitcs) {
        return new SpliteratorFromIterator(iter, characterisitcs);
    }

    public static ShortSpliterator asSpliteratorFromSortedUnknownSize(ShortIterator iter, int additionalCharacterisitcs, ShortComparator comparator) {
        return new SpliteratorFromIteratorWithComparator(iter, additionalCharacterisitcs, comparator);
    }

    public static ShortIterator asIterator(ShortSpliterator spliterator) {
        return new IteratorFromSpliterator(spliterator);
    }

    public static ShortSpliterator wrap(ByteSpliterator spliterator) {
        return new ByteSpliteratorWrapper(spliterator);
    }

    private static class SingletonSpliterator
    implements ShortSpliterator {
        private final short element;
        private final ShortComparator comparator;
        private boolean consumed = false;
        private static final int CHARACTERISTICS = 17749;

        public SingletonSpliterator(short element) {
            this(element, null);
        }

        public SingletonSpliterator(short element, ShortComparator comparator) {
            this.element = element;
            this.comparator = comparator;
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            Objects.requireNonNull(action);
            if (this.consumed) {
                return false;
            }
            this.consumed = true;
            action.accept(this.element);
            return true;
        }

        @Override
        public ShortSpliterator trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return this.consumed ? 0L : 1L;
        }

        @Override
        public int characteristics() {
            return 17749;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            Objects.requireNonNull(action);
            if (!this.consumed) {
                this.consumed = true;
                action.accept(this.element);
            }
        }

        @Override
        public ShortComparator getComparator() {
            return this.comparator;
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (n == 0L || this.consumed) {
                return 0L;
            }
            this.consumed = true;
            return 1L;
        }
    }

    private static class ArraySpliterator
    implements ShortSpliterator {
        private static final int BASE_CHARACTERISTICS = 16720;
        final short[] array;
        private final int offset;
        private int length;
        private int curr;
        final int characteristics;

        public ArraySpliterator(short[] array, int offset, int length, int additionalCharacteristics) {
            this.array = array;
            this.offset = offset;
            this.length = length;
            this.characteristics = 0x4150 | additionalCharacteristics;
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            if (this.curr >= this.length) {
                return false;
            }
            Objects.requireNonNull(action);
            action.accept(this.array[this.offset + this.curr++]);
            return true;
        }

        @Override
        public long estimateSize() {
            return this.length - this.curr;
        }

        @Override
        public int characteristics() {
            return this.characteristics;
        }

        protected ArraySpliterator makeForSplit(int newOffset, int newLength) {
            return new ArraySpliterator(this.array, newOffset, newLength, this.characteristics);
        }

        @Override
        public ShortSpliterator trySplit() {
            int retLength = this.length - this.curr >> 1;
            if (retLength <= 1) {
                return null;
            }
            int myNewCurr = this.curr + retLength;
            int retOffset = this.offset + this.curr;
            this.curr = myNewCurr;
            return this.makeForSplit(retOffset, retLength);
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
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (this.curr >= this.length) {
                return 0L;
            }
            int remaining = this.length - this.curr;
            if (n < (long)remaining) {
                this.curr = SafeMath.safeLongToInt((long)this.curr + n);
                return n;
            }
            n = remaining;
            this.curr = this.length;
            return n;
        }
    }

    private static class ArraySpliteratorWithComparator
    extends ArraySpliterator {
        private final ShortComparator comparator;

        public ArraySpliteratorWithComparator(short[] array, int offset, int length, int additionalCharacteristics, ShortComparator comparator) {
            super(array, offset, length, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        @Override
        protected ArraySpliteratorWithComparator makeForSplit(int newOffset, int newLength) {
            return new ArraySpliteratorWithComparator(this.array, newOffset, newLength, this.characteristics, this.comparator);
        }

        @Override
        public ShortComparator getComparator() {
            return this.comparator;
        }
    }

    private static class SpliteratorWrapper
    implements ShortSpliterator {
        final Spliterator<Short> i;

        public SpliteratorWrapper(Spliterator<Short> i) {
            this.i = i;
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            return this.i.tryAdvance(action);
        }

        @Override
        @Deprecated
        public boolean tryAdvance(Consumer<? super Short> action) {
            return this.i.tryAdvance(action);
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

        @Override
        public long estimateSize() {
            return this.i.estimateSize();
        }

        @Override
        public int characteristics() {
            return this.i.characteristics();
        }

        @Override
        public ShortComparator getComparator() {
            return ShortComparators.asShortComparator(this.i.getComparator());
        }

        @Override
        public ShortSpliterator trySplit() {
            Spliterator<Short> innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new SpliteratorWrapper(innerSplit);
        }
    }

    private static class PrimitiveSpliteratorWrapperWithComparator
    extends PrimitiveSpliteratorWrapper {
        final ShortComparator comparator;

        public PrimitiveSpliteratorWrapperWithComparator(Spliterator.OfInt i, ShortComparator comparator) {
            super(i);
            this.comparator = comparator;
        }

        @Override
        public ShortComparator getComparator() {
            return this.comparator;
        }

        @Override
        public ShortSpliterator trySplit() {
            Spliterator.OfInt innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new PrimitiveSpliteratorWrapperWithComparator(innerSplit, this.comparator);
        }
    }

    private static class SpliteratorWrapperWithComparator
    extends SpliteratorWrapper {
        final ShortComparator comparator;

        public SpliteratorWrapperWithComparator(Spliterator<Short> i, ShortComparator comparator) {
            super(i);
            this.comparator = comparator;
        }

        @Override
        public ShortComparator getComparator() {
            return this.comparator;
        }

        @Override
        public ShortSpliterator trySplit() {
            Spliterator<Short> innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new SpliteratorWrapperWithComparator(innerSplit, this.comparator);
        }
    }

    private static class PrimitiveSpliteratorWrapper
    implements ShortSpliterator {
        final Spliterator.OfInt i;

        public PrimitiveSpliteratorWrapper(Spliterator.OfInt i) {
            this.i = i;
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            return this.i.tryAdvance(action);
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        public long estimateSize() {
            return this.i.estimateSize();
        }

        @Override
        public int characteristics() {
            return this.i.characteristics();
        }

        @Override
        public ShortComparator getComparator() {
            Comparator comp = this.i.getComparator();
            return (left, right) -> comp.compare(Integer.valueOf(left), Integer.valueOf(right));
        }

        @Override
        public ShortSpliterator trySplit() {
            Spliterator.OfInt innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new PrimitiveSpliteratorWrapper(innerSplit);
        }
    }

    private static class IntervalSpliterator
    implements ShortSpliterator {
        private static final int DONT_SPLIT_THRESHOLD = 2;
        private static final int CHARACTERISTICS = 17749;
        private short curr;
        private short to;

        public IntervalSpliterator(short from, short to) {
            this.curr = from;
            this.to = to;
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            if (this.curr >= this.to) {
                return false;
            }
            short s = this.curr;
            this.curr = (short)(s + 1);
            action.accept(s);
            return true;
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
        public long estimateSize() {
            return this.to - this.curr;
        }

        @Override
        public int characteristics() {
            return 17749;
        }

        @Override
        public ShortComparator getComparator() {
            return null;
        }

        @Override
        public ShortSpliterator trySplit() {
            int remaining = this.to - this.curr;
            short mid = (short)(this.curr + (remaining >> 1));
            if (remaining >= 0 && remaining <= 2) {
                return null;
            }
            short old_curr = this.curr;
            this.curr = mid;
            return new IntervalSpliterator(old_curr, mid);
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (this.curr >= this.to) {
                return 0L;
            }
            long newCurr = (long)this.curr + n;
            if (newCurr <= (long)this.to && newCurr >= (long)this.curr) {
                this.curr = SafeMath.safeLongToShort(newCurr);
                return n;
            }
            n = this.to - this.curr;
            this.curr = this.to;
            return n;
        }
    }

    private static class SpliteratorConcatenator
    implements ShortSpliterator {
        private static final int EMPTY_CHARACTERISTICS = 16448;
        private static final int CHARACTERISTICS_NOT_SUPPORTED_WHILE_MULTIPLE = 5;
        final ShortSpliterator[] a;
        int offset;
        int length;
        long remainingEstimatedExceptCurrent = Long.MAX_VALUE;
        int characteristics = 0;

        public SpliteratorConcatenator(ShortSpliterator[] a, int offset, int length) {
            this.a = a;
            this.offset = offset;
            this.length = length;
            this.remainingEstimatedExceptCurrent = this.recomputeRemaining();
            this.characteristics = this.computeCharacteristics();
        }

        private long recomputeRemaining() {
            int curOffset = this.offset + 1;
            long result = 0L;
            for (int curLength = this.length - 1; curLength > 0; --curLength) {
                long cur = this.a[curOffset++].estimateSize();
                if (cur != Long.MAX_VALUE) continue;
                return Long.MAX_VALUE;
            }
            return result;
        }

        private int computeCharacteristics() {
            if (this.length <= 0) {
                return 16448;
            }
            int current = -1;
            int curLength = this.length;
            int curOffset = this.offset;
            if (curLength > 1) {
                current &= 0xFFFFFFFA;
            }
            while (curLength > 0) {
                current &= this.a[curOffset++].characteristics();
                --curLength;
            }
            return current;
        }

        private void advanceNextSpliterator() {
            if (this.length <= 0) {
                throw new AssertionError((Object)"advanceNextSpliterator() called with none remaining");
            }
            ++this.offset;
            --this.length;
            this.remainingEstimatedExceptCurrent = this.recomputeRemaining();
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            boolean any = false;
            while (this.length > 0) {
                if (this.a[this.offset].tryAdvance(action)) {
                    any = true;
                    break;
                }
                this.advanceNextSpliterator();
            }
            return any;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            while (this.length > 0) {
                this.a[this.offset].forEachRemaining(action);
                this.advanceNextSpliterator();
            }
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Short> action) {
            while (this.length > 0) {
                this.a[this.offset].forEachRemaining(action);
                this.advanceNextSpliterator();
            }
        }

        @Override
        public long estimateSize() {
            if (this.length <= 0) {
                return 0L;
            }
            long est = this.a[this.offset].estimateSize() + this.remainingEstimatedExceptCurrent;
            if (est < 0L) {
                return Long.MAX_VALUE;
            }
            return est;
        }

        @Override
        public int characteristics() {
            return this.characteristics;
        }

        @Override
        public ShortComparator getComparator() {
            if (this.length == 1 && (this.characteristics & 4) != 0) {
                return this.a[this.offset].getComparator();
            }
            throw new IllegalStateException();
        }

        @Override
        public ShortSpliterator trySplit() {
            switch (this.length) {
                case 0: {
                    return null;
                }
                case 1: {
                    ShortSpliterator split = this.a[this.offset].trySplit();
                    this.characteristics = this.a[this.offset].characteristics();
                    return split;
                }
                case 2: {
                    ShortSpliterator split = this.a[this.offset++];
                    --this.length;
                    this.characteristics = this.a[this.offset].characteristics();
                    this.remainingEstimatedExceptCurrent = 0L;
                    return split;
                }
            }
            int mid = this.length >> 1;
            int ret_offset = this.offset;
            int new_offset = this.offset + mid;
            int ret_length = mid;
            int new_length = this.length - mid;
            this.offset = new_offset;
            this.length = new_length;
            this.remainingEstimatedExceptCurrent = this.recomputeRemaining();
            this.characteristics = this.computeCharacteristics();
            return new SpliteratorConcatenator(this.a, ret_offset, ret_length);
        }

        @Override
        public long skip(long n) {
            long skipped = 0L;
            if (this.length <= 0) {
                return 0L;
            }
            while (skipped < n && this.length >= 0) {
                long curSkipped;
                if ((skipped += (curSkipped = this.a[this.offset].skip(n - skipped))) >= n) continue;
                this.advanceNextSpliterator();
            }
            return skipped;
        }
    }

    private static class SpliteratorFromIterator
    implements ShortSpliterator {
        private static final int BATCH_INCREMENT_SIZE = 1024;
        private static final int BATCH_MAX_SIZE = 0x2000000;
        private final ShortIterator iter;
        final int characteristics;
        private final boolean knownSize;
        private long size = Long.MAX_VALUE;
        private int nextBatchSize = 1024;
        private ShortSpliterator delegate = null;

        SpliteratorFromIterator(ShortIterator iter, int characteristics) {
            this.iter = iter;
            this.characteristics = 0x100 | characteristics;
            this.knownSize = false;
        }

        SpliteratorFromIterator(ShortIterator iter, long size, int additionalCharacteristics) {
            this.iter = iter;
            this.knownSize = true;
            this.size = size;
            this.characteristics = (additionalCharacteristics & 0x1000) != 0 ? 0x100 | additionalCharacteristics : 0x4140 | additionalCharacteristics;
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            if (this.delegate != null) {
                boolean hadRemaining = this.delegate.tryAdvance(action);
                if (!hadRemaining) {
                    this.delegate = null;
                }
                return hadRemaining;
            }
            if (!this.iter.hasNext()) {
                return false;
            }
            --this.size;
            action.accept(this.iter.nextShort());
            return true;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            if (this.delegate != null) {
                this.delegate.forEachRemaining(action);
                this.delegate = null;
            }
            this.iter.forEachRemaining(action);
            this.size = 0L;
        }

        @Override
        public long estimateSize() {
            if (this.delegate != null) {
                return this.delegate.estimateSize();
            }
            if (!this.iter.hasNext()) {
                return 0L;
            }
            return this.knownSize && this.size >= 0L ? this.size : Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return this.characteristics;
        }

        protected ShortSpliterator makeForSplit(short[] batch, int len) {
            return ShortSpliterators.wrap(batch, 0, len, this.characteristics);
        }

        @Override
        public ShortSpliterator trySplit() {
            if (!this.iter.hasNext()) {
                return null;
            }
            int batchSizeEst = this.knownSize && this.size > 0L ? (int)Math.min((long)this.nextBatchSize, this.size) : this.nextBatchSize;
            short[] batch = new short[batchSizeEst];
            int actualSeen = 0;
            while (actualSeen < batchSizeEst && this.iter.hasNext()) {
                batch[actualSeen++] = this.iter.nextShort();
                --this.size;
            }
            if (batchSizeEst < this.nextBatchSize && this.iter.hasNext()) {
                batch = Arrays.copyOf(batch, this.nextBatchSize);
                while (this.iter.hasNext() && actualSeen < this.nextBatchSize) {
                    batch[actualSeen++] = this.iter.nextShort();
                    --this.size;
                }
            }
            this.nextBatchSize = Math.min(0x2000000, this.nextBatchSize + 1024);
            ShortSpliterator split = this.makeForSplit(batch, actualSeen);
            if (!this.iter.hasNext()) {
                this.delegate = split;
                return split.trySplit();
            }
            return split;
        }

        @Override
        public long skip(long n) {
            long skippedSoFar;
            int skipped;
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (this.iter instanceof ShortBigListIterator) {
                long skipped2 = ((ShortBigListIterator)this.iter).skip(n);
                this.size -= skipped2;
                return skipped2;
            }
            for (skippedSoFar = 0L; skippedSoFar < n && this.iter.hasNext(); skippedSoFar += (long)skipped) {
                skipped = this.iter.skip(SafeMath.safeLongToInt(Math.min(n, Integer.MAX_VALUE)));
                this.size -= (long)skipped;
            }
            return skippedSoFar;
        }
    }

    private static class SpliteratorFromIteratorWithComparator
    extends SpliteratorFromIterator {
        private final ShortComparator comparator;

        SpliteratorFromIteratorWithComparator(ShortIterator iter, int additionalCharacteristics, ShortComparator comparator) {
            super(iter, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        SpliteratorFromIteratorWithComparator(ShortIterator iter, long size, int additionalCharacteristics, ShortComparator comparator) {
            super(iter, size, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        @Override
        public ShortComparator getComparator() {
            return this.comparator;
        }

        @Override
        protected ShortSpliterator makeForSplit(short[] array, int len) {
            return ShortSpliterators.wrapPreSorted(array, 0, len, this.characteristics, this.comparator);
        }
    }

    private static final class IteratorFromSpliterator
    implements ShortIterator,
    ShortConsumer {
        private final ShortSpliterator spliterator;
        private short holder = 0;
        private boolean hasPeeked = false;

        IteratorFromSpliterator(ShortSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public void accept(short item) {
            this.holder = item;
        }

        @Override
        public boolean hasNext() {
            if (this.hasPeeked) {
                return true;
            }
            boolean hadElement = this.spliterator.tryAdvance(this);
            if (!hadElement) {
                return false;
            }
            this.hasPeeked = true;
            return true;
        }

        @Override
        public short nextShort() {
            if (this.hasPeeked) {
                this.hasPeeked = false;
                return this.holder;
            }
            boolean hadElement = this.spliterator.tryAdvance(this);
            if (!hadElement) {
                throw new NoSuchElementException();
            }
            return this.holder;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            if (this.hasPeeked) {
                this.hasPeeked = false;
                action.accept(this.holder);
            }
            this.spliterator.forEachRemaining(action);
        }

        @Override
        public int skip(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            int skipped = 0;
            if (this.hasPeeked) {
                this.hasPeeked = false;
                this.spliterator.skip(1L);
                ++skipped;
                --n;
            }
            if (n > 0) {
                skipped += SafeMath.safeLongToInt(this.spliterator.skip(n));
            }
            return skipped;
        }
    }

    private static final class ByteSpliteratorWrapper
    implements ShortSpliterator {
        final ByteSpliterator spliterator;

        public ByteSpliteratorWrapper(ByteSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            Objects.requireNonNull(action);
            return this.spliterator.tryAdvance(action::accept);
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            Objects.requireNonNull(action);
            this.spliterator.forEachRemaining(action::accept);
        }

        @Override
        public long estimateSize() {
            return this.spliterator.estimateSize();
        }

        @Override
        public int characteristics() {
            return this.spliterator.characteristics();
        }

        @Override
        public long skip(long n) {
            return this.spliterator.skip(n);
        }

        @Override
        public ShortSpliterator trySplit() {
            ByteSpliterator possibleSplit = this.spliterator.trySplit();
            if (possibleSplit == null) {
                return null;
            }
            return new ByteSpliteratorWrapper(possibleSplit);
        }
    }

    public static class EmptySpliterator
    implements ShortSpliterator,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = 8379247926738230492L;
        private static final int CHARACTERISTICS = 16448;

        protected EmptySpliterator() {
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            return false;
        }

        @Override
        @Deprecated
        public boolean tryAdvance(Consumer<? super Short> action) {
            return false;
        }

        @Override
        public ShortSpliterator trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return 0L;
        }

        @Override
        public int characteristics() {
            return 16448;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Short> action) {
        }

        public Object clone() {
            return EMPTY_SPLITERATOR;
        }

        private Object readResolve() {
            return EMPTY_SPLITERATOR;
        }
    }

    public static abstract class LateBindingSizeIndexBasedSpliterator
    extends AbstractIndexBasedSpliterator {
        protected int maxPos = -1;
        private boolean maxPosFixed;

        protected LateBindingSizeIndexBasedSpliterator(int initialPos) {
            super(initialPos);
            this.maxPosFixed = false;
        }

        protected LateBindingSizeIndexBasedSpliterator(int initialPos, int fixedMaxPos) {
            super(initialPos);
            this.maxPos = fixedMaxPos;
            this.maxPosFixed = true;
        }

        protected abstract int getMaxPosFromBackingStore();

        @Override
        protected final int getMaxPos() {
            return this.maxPosFixed ? this.maxPos : this.getMaxPosFromBackingStore();
        }

        @Override
        public ShortSpliterator trySplit() {
            ShortSpliterator maybeSplit = super.trySplit();
            if (!this.maxPosFixed && maybeSplit != null) {
                this.maxPos = this.getMaxPosFromBackingStore();
                this.maxPosFixed = true;
            }
            return maybeSplit;
        }
    }

    public static abstract class EarlyBindingSizeIndexBasedSpliterator
    extends AbstractIndexBasedSpliterator {
        protected final int maxPos;

        protected EarlyBindingSizeIndexBasedSpliterator(int initialPos, int maxPos) {
            super(initialPos);
            this.maxPos = maxPos;
        }

        @Override
        protected final int getMaxPos() {
            return this.maxPos;
        }
    }

    public static abstract class AbstractIndexBasedSpliterator
    extends AbstractShortSpliterator {
        protected int pos;

        protected AbstractIndexBasedSpliterator(int initialPos) {
            this.pos = initialPos;
        }

        protected abstract short get(int var1);

        protected abstract int getMaxPos();

        protected abstract ShortSpliterator makeForSplit(int var1, int var2);

        protected int computeSplitPoint() {
            return this.pos + (this.getMaxPos() - this.pos) / 2;
        }

        private void splitPointCheck(int splitPoint, int observedMax) {
            if (splitPoint < this.pos || splitPoint > observedMax) {
                throw new IndexOutOfBoundsException("splitPoint " + splitPoint + " outside of range of current position " + this.pos + " and range end " + observedMax);
            }
        }

        @Override
        public int characteristics() {
            return 16720;
        }

        @Override
        public long estimateSize() {
            return (long)this.getMaxPos() - (long)this.pos;
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            if (this.pos >= this.getMaxPos()) {
                return false;
            }
            action.accept(this.get(this.pos++));
            return true;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            int max = this.getMaxPos();
            while (this.pos < max) {
                action.accept(this.get(this.pos));
                ++this.pos;
            }
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            int max = this.getMaxPos();
            if (this.pos >= max) {
                return 0L;
            }
            int remaining = max - this.pos;
            if (n < (long)remaining) {
                this.pos = SafeMath.safeLongToInt((long)this.pos + n);
                return n;
            }
            n = remaining;
            this.pos = max;
            return n;
        }

        @Override
        public ShortSpliterator trySplit() {
            int max = this.getMaxPos();
            int splitPoint = this.computeSplitPoint();
            if (splitPoint == this.pos || splitPoint == max) {
                return null;
            }
            this.splitPointCheck(splitPoint, max);
            int oldPos = this.pos;
            ShortSpliterator maybeSplit = this.makeForSplit(oldPos, splitPoint);
            if (maybeSplit != null) {
                this.pos = splitPoint;
            }
            return maybeSplit;
        }
    }
}

