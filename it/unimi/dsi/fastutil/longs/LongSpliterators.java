/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.longs.AbstractLongSpliterator;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongBigListIterator;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongComparators;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.LongPredicate;

public final class LongSpliterators {
    static final int BASE_SPLITERATOR_CHARACTERISTICS = 256;
    public static final int COLLECTION_SPLITERATOR_CHARACTERISTICS = 320;
    public static final int LIST_SPLITERATOR_CHARACTERISTICS = 16720;
    public static final int SET_SPLITERATOR_CHARACTERISTICS = 321;
    private static final int SORTED_CHARACTERISTICS = 20;
    public static final int SORTED_SET_SPLITERATOR_CHARACTERISTICS = 341;
    public static final EmptySpliterator EMPTY_SPLITERATOR = new EmptySpliterator();

    private LongSpliterators() {
    }

    public static LongSpliterator singleton(long element) {
        return new SingletonSpliterator(element);
    }

    public static LongSpliterator singleton(long element, LongComparator comparator) {
        return new SingletonSpliterator(element, comparator);
    }

    public static LongSpliterator wrap(long[] array, int offset, int length) {
        LongArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliterator(array, offset, length, 0);
    }

    public static LongSpliterator wrap(long[] array) {
        return new ArraySpliterator(array, 0, array.length, 0);
    }

    public static LongSpliterator wrap(long[] array, int offset, int length, int additionalCharacteristics) {
        LongArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliterator(array, offset, length, additionalCharacteristics);
    }

    public static LongSpliterator wrapPreSorted(long[] array, int offset, int length, int additionalCharacteristics, LongComparator comparator) {
        LongArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliteratorWithComparator(array, offset, length, additionalCharacteristics, comparator);
    }

    public static LongSpliterator wrapPreSorted(long[] array, int offset, int length, LongComparator comparator) {
        return LongSpliterators.wrapPreSorted(array, offset, length, 0, comparator);
    }

    public static LongSpliterator wrapPreSorted(long[] array, LongComparator comparator) {
        return LongSpliterators.wrapPreSorted(array, 0, array.length, comparator);
    }

    public static LongSpliterator asLongSpliterator(Spliterator i) {
        if (i instanceof LongSpliterator) {
            return (LongSpliterator)i;
        }
        if (i instanceof Spliterator.OfLong) {
            return new PrimitiveSpliteratorWrapper((Spliterator.OfLong)i);
        }
        return new SpliteratorWrapper(i);
    }

    public static LongSpliterator asLongSpliterator(Spliterator i, LongComparator comparatorOverride) {
        if (i instanceof LongSpliterator) {
            throw new IllegalArgumentException("Cannot override comparator on instance that is already a " + LongSpliterator.class.getSimpleName());
        }
        if (i instanceof Spliterator.OfLong) {
            return new PrimitiveSpliteratorWrapperWithComparator((Spliterator.OfLong)i, comparatorOverride);
        }
        return new SpliteratorWrapperWithComparator(i, comparatorOverride);
    }

    public static void onEachMatching(LongSpliterator spliterator, LongPredicate predicate, java.util.function.LongConsumer action) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(action);
        spliterator.forEachRemaining(value -> {
            if (predicate.test(value)) {
                action.accept(value);
            }
        });
    }

    public static LongSpliterator fromTo(long from, long to) {
        return new IntervalSpliterator(from, to);
    }

    public static LongSpliterator concat(LongSpliterator ... a) {
        return LongSpliterators.concat(a, 0, a.length);
    }

    public static LongSpliterator concat(LongSpliterator[] a, int offset, int length) {
        return new SpliteratorConcatenator(a, offset, length);
    }

    public static LongSpliterator asSpliterator(LongIterator iter, long size, int additionalCharacterisitcs) {
        return new SpliteratorFromIterator(iter, size, additionalCharacterisitcs);
    }

    public static LongSpliterator asSpliteratorFromSorted(LongIterator iter, long size, int additionalCharacterisitcs, LongComparator comparator) {
        return new SpliteratorFromIteratorWithComparator(iter, size, additionalCharacterisitcs, comparator);
    }

    public static LongSpliterator asSpliteratorUnknownSize(LongIterator iter, int characterisitcs) {
        return new SpliteratorFromIterator(iter, characterisitcs);
    }

    public static LongSpliterator asSpliteratorFromSortedUnknownSize(LongIterator iter, int additionalCharacterisitcs, LongComparator comparator) {
        return new SpliteratorFromIteratorWithComparator(iter, additionalCharacterisitcs, comparator);
    }

    public static LongIterator asIterator(LongSpliterator spliterator) {
        return new IteratorFromSpliterator(spliterator);
    }

    public static LongSpliterator wrap(ByteSpliterator spliterator) {
        return new ByteSpliteratorWrapper(spliterator);
    }

    public static LongSpliterator wrap(ShortSpliterator spliterator) {
        return new ShortSpliteratorWrapper(spliterator);
    }

    public static LongSpliterator wrap(CharSpliterator spliterator) {
        return new CharSpliteratorWrapper(spliterator);
    }

    public static LongSpliterator wrap(IntSpliterator spliterator) {
        return new IntSpliteratorWrapper(spliterator);
    }

    private static class SingletonSpliterator
    implements LongSpliterator {
        private final long element;
        private final LongComparator comparator;
        private boolean consumed = false;
        private static final int CHARACTERISTICS = 17749;

        public SingletonSpliterator(long element) {
            this(element, null);
        }

        public SingletonSpliterator(long element, LongComparator comparator) {
            this.element = element;
            this.comparator = comparator;
        }

        @Override
        public boolean tryAdvance(java.util.function.LongConsumer action) {
            Objects.requireNonNull(action);
            if (this.consumed) {
                return false;
            }
            this.consumed = true;
            action.accept(this.element);
            return true;
        }

        @Override
        public LongSpliterator trySplit() {
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
        public void forEachRemaining(java.util.function.LongConsumer action) {
            Objects.requireNonNull(action);
            if (!this.consumed) {
                this.consumed = true;
                action.accept(this.element);
            }
        }

        @Override
        public LongComparator getComparator() {
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
    implements LongSpliterator {
        private static final int BASE_CHARACTERISTICS = 16720;
        final long[] array;
        private final int offset;
        private int length;
        private int curr;
        final int characteristics;

        public ArraySpliterator(long[] array, int offset, int length, int additionalCharacteristics) {
            this.array = array;
            this.offset = offset;
            this.length = length;
            this.characteristics = 0x4150 | additionalCharacteristics;
        }

        @Override
        public boolean tryAdvance(java.util.function.LongConsumer action) {
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
        public LongSpliterator trySplit() {
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
        public void forEachRemaining(java.util.function.LongConsumer action) {
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
        private final LongComparator comparator;

        public ArraySpliteratorWithComparator(long[] array, int offset, int length, int additionalCharacteristics, LongComparator comparator) {
            super(array, offset, length, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        @Override
        protected ArraySpliteratorWithComparator makeForSplit(int newOffset, int newLength) {
            return new ArraySpliteratorWithComparator(this.array, newOffset, newLength, this.characteristics, this.comparator);
        }

        @Override
        public LongComparator getComparator() {
            return this.comparator;
        }
    }

    private static class PrimitiveSpliteratorWrapper
    implements LongSpliterator {
        final Spliterator.OfLong i;

        public PrimitiveSpliteratorWrapper(Spliterator.OfLong i) {
            this.i = i;
        }

        @Override
        public boolean tryAdvance(java.util.function.LongConsumer action) {
            return this.i.tryAdvance(action);
        }

        @Override
        public void forEachRemaining(java.util.function.LongConsumer action) {
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
        public LongComparator getComparator() {
            return LongComparators.asLongComparator(this.i.getComparator());
        }

        @Override
        public LongSpliterator trySplit() {
            Spliterator.OfLong innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new PrimitiveSpliteratorWrapper(innerSplit);
        }
    }

    private static class SpliteratorWrapper
    implements LongSpliterator {
        final Spliterator<Long> i;

        public SpliteratorWrapper(Spliterator<Long> i) {
            this.i = i;
        }

        @Override
        public boolean tryAdvance(LongConsumer action) {
            return this.i.tryAdvance(action);
        }

        @Override
        public boolean tryAdvance(java.util.function.LongConsumer action) {
            Objects.requireNonNull(action);
            return this.i.tryAdvance(action instanceof Consumer ? (Consumer<Long>)((Object)action) : action::accept);
        }

        @Override
        @Deprecated
        public boolean tryAdvance(Consumer<? super Long> action) {
            return this.i.tryAdvance(action);
        }

        @Override
        public void forEachRemaining(LongConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        public void forEachRemaining(java.util.function.LongConsumer action) {
            Objects.requireNonNull(action);
            this.i.forEachRemaining(action instanceof Consumer ? (Consumer<Long>)((Object)action) : action::accept);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Long> action) {
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
        public LongComparator getComparator() {
            return LongComparators.asLongComparator(this.i.getComparator());
        }

        @Override
        public LongSpliterator trySplit() {
            Spliterator<Long> innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new SpliteratorWrapper(innerSplit);
        }
    }

    private static class PrimitiveSpliteratorWrapperWithComparator
    extends PrimitiveSpliteratorWrapper {
        final LongComparator comparator;

        public PrimitiveSpliteratorWrapperWithComparator(Spliterator.OfLong i, LongComparator comparator) {
            super(i);
            this.comparator = comparator;
        }

        @Override
        public LongComparator getComparator() {
            return this.comparator;
        }

        @Override
        public LongSpliterator trySplit() {
            Spliterator.OfLong innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new PrimitiveSpliteratorWrapperWithComparator(innerSplit, this.comparator);
        }
    }

    private static class SpliteratorWrapperWithComparator
    extends SpliteratorWrapper {
        final LongComparator comparator;

        public SpliteratorWrapperWithComparator(Spliterator<Long> i, LongComparator comparator) {
            super(i);
            this.comparator = comparator;
        }

        @Override
        public LongComparator getComparator() {
            return this.comparator;
        }

        @Override
        public LongSpliterator trySplit() {
            Spliterator<Long> innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new SpliteratorWrapperWithComparator(innerSplit, this.comparator);
        }
    }

    private static class IntervalSpliterator
    implements LongSpliterator {
        private static final int DONT_SPLIT_THRESHOLD = 2;
        private static final long MAX_SPLIT_SIZE = 0x40000000L;
        private static final int CHARACTERISTICS = 17749;
        private long curr;
        private long to;

        public IntervalSpliterator(long from, long to) {
            this.curr = from;
            this.to = to;
        }

        @Override
        public boolean tryAdvance(java.util.function.LongConsumer action) {
            if (this.curr >= this.to) {
                return false;
            }
            action.accept(this.curr++);
            return true;
        }

        @Override
        public void forEachRemaining(java.util.function.LongConsumer action) {
            Objects.requireNonNull(action);
            while (this.curr < this.to) {
                action.accept(this.curr);
                ++this.curr;
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
        public LongComparator getComparator() {
            return null;
        }

        @Override
        public LongSpliterator trySplit() {
            long remaining = this.to - this.curr;
            long mid = this.curr + (remaining >> 1);
            if (remaining > 0x80000000L || remaining < 0L) {
                mid = this.curr + 0x40000000L;
            }
            if (remaining >= 0L && remaining <= 2L) {
                return null;
            }
            long old_curr = this.curr;
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
            long newCurr = this.curr + n;
            if (newCurr <= this.to && newCurr >= this.curr) {
                this.curr = newCurr;
                return n;
            }
            n = this.to - this.curr;
            this.curr = this.to;
            return n;
        }
    }

    private static class SpliteratorConcatenator
    implements LongSpliterator {
        private static final int EMPTY_CHARACTERISTICS = 16448;
        private static final int CHARACTERISTICS_NOT_SUPPORTED_WHILE_MULTIPLE = 5;
        final LongSpliterator[] a;
        int offset;
        int length;
        long remainingEstimatedExceptCurrent = Long.MAX_VALUE;
        int characteristics = 0;

        public SpliteratorConcatenator(LongSpliterator[] a, int offset, int length) {
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
        public boolean tryAdvance(java.util.function.LongConsumer action) {
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
        public void forEachRemaining(java.util.function.LongConsumer action) {
            while (this.length > 0) {
                this.a[this.offset].forEachRemaining(action);
                this.advanceNextSpliterator();
            }
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Long> action) {
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
        public LongComparator getComparator() {
            if (this.length == 1 && (this.characteristics & 4) != 0) {
                return this.a[this.offset].getComparator();
            }
            throw new IllegalStateException();
        }

        @Override
        public LongSpliterator trySplit() {
            switch (this.length) {
                case 0: {
                    return null;
                }
                case 1: {
                    LongSpliterator split = this.a[this.offset].trySplit();
                    this.characteristics = this.a[this.offset].characteristics();
                    return split;
                }
                case 2: {
                    LongSpliterator split = this.a[this.offset++];
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
    implements LongSpliterator {
        private static final int BATCH_INCREMENT_SIZE = 1024;
        private static final int BATCH_MAX_SIZE = 0x2000000;
        private final LongIterator iter;
        final int characteristics;
        private final boolean knownSize;
        private long size = Long.MAX_VALUE;
        private int nextBatchSize = 1024;
        private LongSpliterator delegate = null;

        SpliteratorFromIterator(LongIterator iter, int characteristics) {
            this.iter = iter;
            this.characteristics = 0x100 | characteristics;
            this.knownSize = false;
        }

        SpliteratorFromIterator(LongIterator iter, long size, int additionalCharacteristics) {
            this.iter = iter;
            this.knownSize = true;
            this.size = size;
            this.characteristics = (additionalCharacteristics & 0x1000) != 0 ? 0x100 | additionalCharacteristics : 0x4140 | additionalCharacteristics;
        }

        @Override
        public boolean tryAdvance(java.util.function.LongConsumer action) {
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
            action.accept(this.iter.nextLong());
            return true;
        }

        @Override
        public void forEachRemaining(java.util.function.LongConsumer action) {
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

        protected LongSpliterator makeForSplit(long[] batch, int len) {
            return LongSpliterators.wrap(batch, 0, len, this.characteristics);
        }

        @Override
        public LongSpliterator trySplit() {
            if (!this.iter.hasNext()) {
                return null;
            }
            int batchSizeEst = this.knownSize && this.size > 0L ? (int)Math.min((long)this.nextBatchSize, this.size) : this.nextBatchSize;
            long[] batch = new long[batchSizeEst];
            int actualSeen = 0;
            while (actualSeen < batchSizeEst && this.iter.hasNext()) {
                batch[actualSeen++] = this.iter.nextLong();
                --this.size;
            }
            if (batchSizeEst < this.nextBatchSize && this.iter.hasNext()) {
                batch = Arrays.copyOf(batch, this.nextBatchSize);
                while (this.iter.hasNext() && actualSeen < this.nextBatchSize) {
                    batch[actualSeen++] = this.iter.nextLong();
                    --this.size;
                }
            }
            this.nextBatchSize = Math.min(0x2000000, this.nextBatchSize + 1024);
            LongSpliterator split = this.makeForSplit(batch, actualSeen);
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
            if (this.iter instanceof LongBigListIterator) {
                long skipped2 = ((LongBigListIterator)this.iter).skip(n);
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
        private final LongComparator comparator;

        SpliteratorFromIteratorWithComparator(LongIterator iter, int additionalCharacteristics, LongComparator comparator) {
            super(iter, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        SpliteratorFromIteratorWithComparator(LongIterator iter, long size, int additionalCharacteristics, LongComparator comparator) {
            super(iter, size, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        @Override
        public LongComparator getComparator() {
            return this.comparator;
        }

        @Override
        protected LongSpliterator makeForSplit(long[] array, int len) {
            return LongSpliterators.wrapPreSorted(array, 0, len, this.characteristics, this.comparator);
        }
    }

    private static final class IteratorFromSpliterator
    implements LongIterator,
    LongConsumer {
        private final LongSpliterator spliterator;
        private long holder = 0L;
        private boolean hasPeeked = false;

        IteratorFromSpliterator(LongSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public void accept(long item) {
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
        public long nextLong() {
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
        public void forEachRemaining(java.util.function.LongConsumer action) {
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
    implements LongSpliterator {
        final ByteSpliterator spliterator;

        public ByteSpliteratorWrapper(ByteSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public boolean tryAdvance(java.util.function.LongConsumer action) {
            Objects.requireNonNull(action);
            return this.spliterator.tryAdvance(action::accept);
        }

        @Override
        public void forEachRemaining(java.util.function.LongConsumer action) {
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
        public LongSpliterator trySplit() {
            ByteSpliterator possibleSplit = this.spliterator.trySplit();
            if (possibleSplit == null) {
                return null;
            }
            return new ByteSpliteratorWrapper(possibleSplit);
        }
    }

    private static final class ShortSpliteratorWrapper
    implements LongSpliterator {
        final ShortSpliterator spliterator;

        public ShortSpliteratorWrapper(ShortSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public boolean tryAdvance(java.util.function.LongConsumer action) {
            Objects.requireNonNull(action);
            return this.spliterator.tryAdvance(action::accept);
        }

        @Override
        public void forEachRemaining(java.util.function.LongConsumer action) {
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
        public LongSpliterator trySplit() {
            ShortSpliterator possibleSplit = this.spliterator.trySplit();
            if (possibleSplit == null) {
                return null;
            }
            return new ShortSpliteratorWrapper(possibleSplit);
        }
    }

    private static final class CharSpliteratorWrapper
    implements LongSpliterator {
        final CharSpliterator spliterator;

        public CharSpliteratorWrapper(CharSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public boolean tryAdvance(java.util.function.LongConsumer action) {
            Objects.requireNonNull(action);
            return this.spliterator.tryAdvance(action::accept);
        }

        @Override
        public void forEachRemaining(java.util.function.LongConsumer action) {
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
        public LongSpliterator trySplit() {
            CharSpliterator possibleSplit = this.spliterator.trySplit();
            if (possibleSplit == null) {
                return null;
            }
            return new CharSpliteratorWrapper(possibleSplit);
        }
    }

    private static final class IntSpliteratorWrapper
    implements LongSpliterator {
        final IntSpliterator spliterator;

        public IntSpliteratorWrapper(IntSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public boolean tryAdvance(java.util.function.LongConsumer action) {
            Objects.requireNonNull(action);
            return this.spliterator.tryAdvance(action::accept);
        }

        @Override
        public void forEachRemaining(java.util.function.LongConsumer action) {
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
        public LongSpliterator trySplit() {
            IntSpliterator possibleSplit = this.spliterator.trySplit();
            if (possibleSplit == null) {
                return null;
            }
            return new IntSpliteratorWrapper(possibleSplit);
        }
    }

    public static class EmptySpliterator
    implements LongSpliterator,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = 8379247926738230492L;
        private static final int CHARACTERISTICS = 16448;

        protected EmptySpliterator() {
        }

        @Override
        public boolean tryAdvance(java.util.function.LongConsumer action) {
            return false;
        }

        @Override
        @Deprecated
        public boolean tryAdvance(Consumer<? super Long> action) {
            return false;
        }

        @Override
        public LongSpliterator trySplit() {
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
        public void forEachRemaining(java.util.function.LongConsumer action) {
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Long> action) {
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
        public LongSpliterator trySplit() {
            LongSpliterator maybeSplit = super.trySplit();
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
    extends AbstractLongSpliterator {
        protected int pos;

        protected AbstractIndexBasedSpliterator(int initialPos) {
            this.pos = initialPos;
        }

        protected abstract long get(int var1);

        protected abstract int getMaxPos();

        protected abstract LongSpliterator makeForSplit(int var1, int var2);

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
        public boolean tryAdvance(java.util.function.LongConsumer action) {
            if (this.pos >= this.getMaxPos()) {
                return false;
            }
            action.accept(this.get(this.pos++));
            return true;
        }

        @Override
        public void forEachRemaining(java.util.function.LongConsumer action) {
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
        public LongSpliterator trySplit() {
            int max = this.getMaxPos();
            int splitPoint = this.computeSplitPoint();
            if (splitPoint == this.pos || splitPoint == max) {
                return null;
            }
            this.splitPointCheck(splitPoint, max);
            int oldPos = this.pos;
            LongSpliterator maybeSplit = this.makeForSplit(oldPos, splitPoint);
            if (maybeSplit != null) {
                this.pos = splitPoint;
            }
            return maybeSplit;
        }
    }
}

