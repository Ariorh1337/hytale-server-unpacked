/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import it.unimi.dsi.fastutil.floats.AbstractFloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatBigListIterator;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatComparators;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatPredicate;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;

public final class FloatSpliterators {
    static final int BASE_SPLITERATOR_CHARACTERISTICS = 256;
    public static final int COLLECTION_SPLITERATOR_CHARACTERISTICS = 320;
    public static final int LIST_SPLITERATOR_CHARACTERISTICS = 16720;
    public static final int SET_SPLITERATOR_CHARACTERISTICS = 321;
    private static final int SORTED_CHARACTERISTICS = 20;
    public static final int SORTED_SET_SPLITERATOR_CHARACTERISTICS = 341;
    public static final EmptySpliterator EMPTY_SPLITERATOR = new EmptySpliterator();

    private FloatSpliterators() {
    }

    public static FloatSpliterator singleton(float element) {
        return new SingletonSpliterator(element);
    }

    public static FloatSpliterator singleton(float element, FloatComparator comparator) {
        return new SingletonSpliterator(element, comparator);
    }

    public static FloatSpliterator wrap(float[] array, int offset, int length) {
        FloatArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliterator(array, offset, length, 0);
    }

    public static FloatSpliterator wrap(float[] array) {
        return new ArraySpliterator(array, 0, array.length, 0);
    }

    public static FloatSpliterator wrap(float[] array, int offset, int length, int additionalCharacteristics) {
        FloatArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliterator(array, offset, length, additionalCharacteristics);
    }

    public static FloatSpliterator wrapPreSorted(float[] array, int offset, int length, int additionalCharacteristics, FloatComparator comparator) {
        FloatArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliteratorWithComparator(array, offset, length, additionalCharacteristics, comparator);
    }

    public static FloatSpliterator wrapPreSorted(float[] array, int offset, int length, FloatComparator comparator) {
        return FloatSpliterators.wrapPreSorted(array, offset, length, 0, comparator);
    }

    public static FloatSpliterator wrapPreSorted(float[] array, FloatComparator comparator) {
        return FloatSpliterators.wrapPreSorted(array, 0, array.length, comparator);
    }

    public static FloatSpliterator asFloatSpliterator(Spliterator i) {
        if (i instanceof FloatSpliterator) {
            return (FloatSpliterator)i;
        }
        return new SpliteratorWrapper(i);
    }

    public static FloatSpliterator asFloatSpliterator(Spliterator i, FloatComparator comparatorOverride) {
        if (i instanceof FloatSpliterator) {
            throw new IllegalArgumentException("Cannot override comparator on instance that is already a " + FloatSpliterator.class.getSimpleName());
        }
        if (i instanceof Spliterator.OfDouble) {
            return new PrimitiveSpliteratorWrapperWithComparator((Spliterator.OfDouble)i, comparatorOverride);
        }
        return new SpliteratorWrapperWithComparator(i, comparatorOverride);
    }

    public static FloatSpliterator narrow(Spliterator.OfDouble i) {
        return new PrimitiveSpliteratorWrapper(i);
    }

    public static DoubleSpliterator widen(FloatSpliterator i) {
        return DoubleSpliterators.wrap(i);
    }

    public static void onEachMatching(FloatSpliterator spliterator, FloatPredicate predicate, FloatConsumer action) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(action);
        spliterator.forEachRemaining(value -> {
            if (predicate.test(value)) {
                action.accept(value);
            }
        });
    }

    public static void onEachMatching(FloatSpliterator spliterator, DoublePredicate predicate, DoubleConsumer action) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(action);
        spliterator.forEachRemaining(value -> {
            if (predicate.test(value)) {
                action.accept(value);
            }
        });
    }

    public static FloatSpliterator concat(FloatSpliterator ... a) {
        return FloatSpliterators.concat(a, 0, a.length);
    }

    public static FloatSpliterator concat(FloatSpliterator[] a, int offset, int length) {
        return new SpliteratorConcatenator(a, offset, length);
    }

    public static FloatSpliterator asSpliterator(FloatIterator iter, long size, int additionalCharacterisitcs) {
        return new SpliteratorFromIterator(iter, size, additionalCharacterisitcs);
    }

    public static FloatSpliterator asSpliteratorFromSorted(FloatIterator iter, long size, int additionalCharacterisitcs, FloatComparator comparator) {
        return new SpliteratorFromIteratorWithComparator(iter, size, additionalCharacterisitcs, comparator);
    }

    public static FloatSpliterator asSpliteratorUnknownSize(FloatIterator iter, int characterisitcs) {
        return new SpliteratorFromIterator(iter, characterisitcs);
    }

    public static FloatSpliterator asSpliteratorFromSortedUnknownSize(FloatIterator iter, int additionalCharacterisitcs, FloatComparator comparator) {
        return new SpliteratorFromIteratorWithComparator(iter, additionalCharacterisitcs, comparator);
    }

    public static FloatIterator asIterator(FloatSpliterator spliterator) {
        return new IteratorFromSpliterator(spliterator);
    }

    public static FloatSpliterator wrap(ByteSpliterator spliterator) {
        return new ByteSpliteratorWrapper(spliterator);
    }

    public static FloatSpliterator wrap(ShortSpliterator spliterator) {
        return new ShortSpliteratorWrapper(spliterator);
    }

    public static FloatSpliterator wrap(CharSpliterator spliterator) {
        return new CharSpliteratorWrapper(spliterator);
    }

    private static class SingletonSpliterator
    implements FloatSpliterator {
        private final float element;
        private final FloatComparator comparator;
        private boolean consumed = false;
        private static final int CHARACTERISTICS = 17749;

        public SingletonSpliterator(float element) {
            this(element, null);
        }

        public SingletonSpliterator(float element, FloatComparator comparator) {
            this.element = element;
            this.comparator = comparator;
        }

        @Override
        public boolean tryAdvance(FloatConsumer action) {
            Objects.requireNonNull(action);
            if (this.consumed) {
                return false;
            }
            this.consumed = true;
            action.accept(this.element);
            return true;
        }

        @Override
        public FloatSpliterator trySplit() {
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
        public void forEachRemaining(FloatConsumer action) {
            Objects.requireNonNull(action);
            if (!this.consumed) {
                this.consumed = true;
                action.accept(this.element);
            }
        }

        @Override
        public FloatComparator getComparator() {
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
    implements FloatSpliterator {
        private static final int BASE_CHARACTERISTICS = 16720;
        final float[] array;
        private final int offset;
        private int length;
        private int curr;
        final int characteristics;

        public ArraySpliterator(float[] array, int offset, int length, int additionalCharacteristics) {
            this.array = array;
            this.offset = offset;
            this.length = length;
            this.characteristics = 0x4150 | additionalCharacteristics;
        }

        @Override
        public boolean tryAdvance(FloatConsumer action) {
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
        public FloatSpliterator trySplit() {
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
        public void forEachRemaining(FloatConsumer action) {
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
        private final FloatComparator comparator;

        public ArraySpliteratorWithComparator(float[] array, int offset, int length, int additionalCharacteristics, FloatComparator comparator) {
            super(array, offset, length, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        @Override
        protected ArraySpliteratorWithComparator makeForSplit(int newOffset, int newLength) {
            return new ArraySpliteratorWithComparator(this.array, newOffset, newLength, this.characteristics, this.comparator);
        }

        @Override
        public FloatComparator getComparator() {
            return this.comparator;
        }
    }

    private static class SpliteratorWrapper
    implements FloatSpliterator {
        final Spliterator<Float> i;

        public SpliteratorWrapper(Spliterator<Float> i) {
            this.i = i;
        }

        @Override
        public boolean tryAdvance(FloatConsumer action) {
            return this.i.tryAdvance(action);
        }

        @Override
        @Deprecated
        public boolean tryAdvance(Consumer<? super Float> action) {
            return this.i.tryAdvance(action);
        }

        @Override
        public void forEachRemaining(FloatConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Float> action) {
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
        public FloatComparator getComparator() {
            return FloatComparators.asFloatComparator(this.i.getComparator());
        }

        @Override
        public FloatSpliterator trySplit() {
            Spliterator<Float> innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new SpliteratorWrapper(innerSplit);
        }
    }

    private static class PrimitiveSpliteratorWrapperWithComparator
    extends PrimitiveSpliteratorWrapper {
        final FloatComparator comparator;

        public PrimitiveSpliteratorWrapperWithComparator(Spliterator.OfDouble i, FloatComparator comparator) {
            super(i);
            this.comparator = comparator;
        }

        @Override
        public FloatComparator getComparator() {
            return this.comparator;
        }

        @Override
        public FloatSpliterator trySplit() {
            Spliterator.OfDouble innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new PrimitiveSpliteratorWrapperWithComparator(innerSplit, this.comparator);
        }
    }

    private static class SpliteratorWrapperWithComparator
    extends SpliteratorWrapper {
        final FloatComparator comparator;

        public SpliteratorWrapperWithComparator(Spliterator<Float> i, FloatComparator comparator) {
            super(i);
            this.comparator = comparator;
        }

        @Override
        public FloatComparator getComparator() {
            return this.comparator;
        }

        @Override
        public FloatSpliterator trySplit() {
            Spliterator<Float> innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new SpliteratorWrapperWithComparator(innerSplit, this.comparator);
        }
    }

    private static class PrimitiveSpliteratorWrapper
    implements FloatSpliterator {
        final Spliterator.OfDouble i;

        public PrimitiveSpliteratorWrapper(Spliterator.OfDouble i) {
            this.i = i;
        }

        @Override
        public boolean tryAdvance(FloatConsumer action) {
            return this.i.tryAdvance(action);
        }

        @Override
        public void forEachRemaining(FloatConsumer action) {
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
        public FloatComparator getComparator() {
            Comparator comp = this.i.getComparator();
            return (left, right) -> comp.compare(Double.valueOf(left), Double.valueOf(right));
        }

        @Override
        public FloatSpliterator trySplit() {
            Spliterator.OfDouble innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new PrimitiveSpliteratorWrapper(innerSplit);
        }
    }

    private static class SpliteratorConcatenator
    implements FloatSpliterator {
        private static final int EMPTY_CHARACTERISTICS = 16448;
        private static final int CHARACTERISTICS_NOT_SUPPORTED_WHILE_MULTIPLE = 5;
        final FloatSpliterator[] a;
        int offset;
        int length;
        long remainingEstimatedExceptCurrent = Long.MAX_VALUE;
        int characteristics = 0;

        public SpliteratorConcatenator(FloatSpliterator[] a, int offset, int length) {
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
        public boolean tryAdvance(FloatConsumer action) {
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
        public void forEachRemaining(FloatConsumer action) {
            while (this.length > 0) {
                this.a[this.offset].forEachRemaining(action);
                this.advanceNextSpliterator();
            }
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Float> action) {
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
        public FloatComparator getComparator() {
            if (this.length == 1 && (this.characteristics & 4) != 0) {
                return this.a[this.offset].getComparator();
            }
            throw new IllegalStateException();
        }

        @Override
        public FloatSpliterator trySplit() {
            switch (this.length) {
                case 0: {
                    return null;
                }
                case 1: {
                    FloatSpliterator split = this.a[this.offset].trySplit();
                    this.characteristics = this.a[this.offset].characteristics();
                    return split;
                }
                case 2: {
                    FloatSpliterator split = this.a[this.offset++];
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
    implements FloatSpliterator {
        private static final int BATCH_INCREMENT_SIZE = 1024;
        private static final int BATCH_MAX_SIZE = 0x2000000;
        private final FloatIterator iter;
        final int characteristics;
        private final boolean knownSize;
        private long size = Long.MAX_VALUE;
        private int nextBatchSize = 1024;
        private FloatSpliterator delegate = null;

        SpliteratorFromIterator(FloatIterator iter, int characteristics) {
            this.iter = iter;
            this.characteristics = 0x100 | characteristics;
            this.knownSize = false;
        }

        SpliteratorFromIterator(FloatIterator iter, long size, int additionalCharacteristics) {
            this.iter = iter;
            this.knownSize = true;
            this.size = size;
            this.characteristics = (additionalCharacteristics & 0x1000) != 0 ? 0x100 | additionalCharacteristics : 0x4140 | additionalCharacteristics;
        }

        @Override
        public boolean tryAdvance(FloatConsumer action) {
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
            action.accept(this.iter.nextFloat());
            return true;
        }

        @Override
        public void forEachRemaining(FloatConsumer action) {
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

        protected FloatSpliterator makeForSplit(float[] batch, int len) {
            return FloatSpliterators.wrap(batch, 0, len, this.characteristics);
        }

        @Override
        public FloatSpliterator trySplit() {
            if (!this.iter.hasNext()) {
                return null;
            }
            int batchSizeEst = this.knownSize && this.size > 0L ? (int)Math.min((long)this.nextBatchSize, this.size) : this.nextBatchSize;
            float[] batch = new float[batchSizeEst];
            int actualSeen = 0;
            while (actualSeen < batchSizeEst && this.iter.hasNext()) {
                batch[actualSeen++] = this.iter.nextFloat();
                --this.size;
            }
            if (batchSizeEst < this.nextBatchSize && this.iter.hasNext()) {
                batch = Arrays.copyOf(batch, this.nextBatchSize);
                while (this.iter.hasNext() && actualSeen < this.nextBatchSize) {
                    batch[actualSeen++] = this.iter.nextFloat();
                    --this.size;
                }
            }
            this.nextBatchSize = Math.min(0x2000000, this.nextBatchSize + 1024);
            FloatSpliterator split = this.makeForSplit(batch, actualSeen);
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
            if (this.iter instanceof FloatBigListIterator) {
                long skipped2 = ((FloatBigListIterator)this.iter).skip(n);
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
        private final FloatComparator comparator;

        SpliteratorFromIteratorWithComparator(FloatIterator iter, int additionalCharacteristics, FloatComparator comparator) {
            super(iter, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        SpliteratorFromIteratorWithComparator(FloatIterator iter, long size, int additionalCharacteristics, FloatComparator comparator) {
            super(iter, size, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        @Override
        public FloatComparator getComparator() {
            return this.comparator;
        }

        @Override
        protected FloatSpliterator makeForSplit(float[] array, int len) {
            return FloatSpliterators.wrapPreSorted(array, 0, len, this.characteristics, this.comparator);
        }
    }

    private static final class IteratorFromSpliterator
    implements FloatIterator,
    FloatConsumer {
        private final FloatSpliterator spliterator;
        private float holder = 0.0f;
        private boolean hasPeeked = false;

        IteratorFromSpliterator(FloatSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public void accept(float item) {
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
        public float nextFloat() {
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
        public void forEachRemaining(FloatConsumer action) {
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
    implements FloatSpliterator {
        final ByteSpliterator spliterator;

        public ByteSpliteratorWrapper(ByteSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public boolean tryAdvance(FloatConsumer action) {
            Objects.requireNonNull(action);
            return this.spliterator.tryAdvance(action::accept);
        }

        @Override
        public void forEachRemaining(FloatConsumer action) {
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
        public FloatSpliterator trySplit() {
            ByteSpliterator possibleSplit = this.spliterator.trySplit();
            if (possibleSplit == null) {
                return null;
            }
            return new ByteSpliteratorWrapper(possibleSplit);
        }
    }

    private static final class ShortSpliteratorWrapper
    implements FloatSpliterator {
        final ShortSpliterator spliterator;

        public ShortSpliteratorWrapper(ShortSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public boolean tryAdvance(FloatConsumer action) {
            Objects.requireNonNull(action);
            return this.spliterator.tryAdvance(action::accept);
        }

        @Override
        public void forEachRemaining(FloatConsumer action) {
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
        public FloatSpliterator trySplit() {
            ShortSpliterator possibleSplit = this.spliterator.trySplit();
            if (possibleSplit == null) {
                return null;
            }
            return new ShortSpliteratorWrapper(possibleSplit);
        }
    }

    private static final class CharSpliteratorWrapper
    implements FloatSpliterator {
        final CharSpliterator spliterator;

        public CharSpliteratorWrapper(CharSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public boolean tryAdvance(FloatConsumer action) {
            Objects.requireNonNull(action);
            return this.spliterator.tryAdvance(action::accept);
        }

        @Override
        public void forEachRemaining(FloatConsumer action) {
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
        public FloatSpliterator trySplit() {
            CharSpliterator possibleSplit = this.spliterator.trySplit();
            if (possibleSplit == null) {
                return null;
            }
            return new CharSpliteratorWrapper(possibleSplit);
        }
    }

    public static class EmptySpliterator
    implements FloatSpliterator,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = 8379247926738230492L;
        private static final int CHARACTERISTICS = 16448;

        protected EmptySpliterator() {
        }

        @Override
        public boolean tryAdvance(FloatConsumer action) {
            return false;
        }

        @Override
        @Deprecated
        public boolean tryAdvance(Consumer<? super Float> action) {
            return false;
        }

        @Override
        public FloatSpliterator trySplit() {
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
        public void forEachRemaining(FloatConsumer action) {
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Float> action) {
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
        public FloatSpliterator trySplit() {
            FloatSpliterator maybeSplit = super.trySplit();
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
    extends AbstractFloatSpliterator {
        protected int pos;

        protected AbstractIndexBasedSpliterator(int initialPos) {
            this.pos = initialPos;
        }

        protected abstract float get(int var1);

        protected abstract int getMaxPos();

        protected abstract FloatSpliterator makeForSplit(int var1, int var2);

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
        public boolean tryAdvance(FloatConsumer action) {
            if (this.pos >= this.getMaxPos()) {
                return false;
            }
            action.accept(this.get(this.pos++));
            return true;
        }

        @Override
        public void forEachRemaining(FloatConsumer action) {
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
        public FloatSpliterator trySplit() {
            int max = this.getMaxPos();
            int splitPoint = this.computeSplitPoint();
            if (splitPoint == this.pos || splitPoint == max) {
                return null;
            }
            this.splitPointCheck(splitPoint, max);
            int oldPos = this.pos;
            FloatSpliterator maybeSplit = this.makeForSplit(oldPos, splitPoint);
            if (maybeSplit != null) {
                this.pos = splitPoint;
            }
            return maybeSplit;
        }
    }
}

