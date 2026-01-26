/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.AbstractCharSpliterator;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharBigListIterator;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharComparators;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharPredicate;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

public final class CharSpliterators {
    static final int BASE_SPLITERATOR_CHARACTERISTICS = 256;
    public static final int COLLECTION_SPLITERATOR_CHARACTERISTICS = 320;
    public static final int LIST_SPLITERATOR_CHARACTERISTICS = 16720;
    public static final int SET_SPLITERATOR_CHARACTERISTICS = 321;
    private static final int SORTED_CHARACTERISTICS = 20;
    public static final int SORTED_SET_SPLITERATOR_CHARACTERISTICS = 341;
    public static final EmptySpliterator EMPTY_SPLITERATOR = new EmptySpliterator();

    private CharSpliterators() {
    }

    public static CharSpliterator singleton(char element) {
        return new SingletonSpliterator(element);
    }

    public static CharSpliterator singleton(char element, CharComparator comparator) {
        return new SingletonSpliterator(element, comparator);
    }

    public static CharSpliterator wrap(char[] array, int offset, int length) {
        CharArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliterator(array, offset, length, 0);
    }

    public static CharSpliterator wrap(char[] array) {
        return new ArraySpliterator(array, 0, array.length, 0);
    }

    public static CharSpliterator wrap(char[] array, int offset, int length, int additionalCharacteristics) {
        CharArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliterator(array, offset, length, additionalCharacteristics);
    }

    public static CharSpliterator wrapPreSorted(char[] array, int offset, int length, int additionalCharacteristics, CharComparator comparator) {
        CharArrays.ensureOffsetLength(array, offset, length);
        return new ArraySpliteratorWithComparator(array, offset, length, additionalCharacteristics, comparator);
    }

    public static CharSpliterator wrapPreSorted(char[] array, int offset, int length, CharComparator comparator) {
        return CharSpliterators.wrapPreSorted(array, offset, length, 0, comparator);
    }

    public static CharSpliterator wrapPreSorted(char[] array, CharComparator comparator) {
        return CharSpliterators.wrapPreSorted(array, 0, array.length, comparator);
    }

    public static CharSpliterator asCharSpliterator(Spliterator i) {
        if (i instanceof CharSpliterator) {
            return (CharSpliterator)i;
        }
        return new SpliteratorWrapper(i);
    }

    public static CharSpliterator asCharSpliterator(Spliterator i, CharComparator comparatorOverride) {
        if (i instanceof CharSpliterator) {
            throw new IllegalArgumentException("Cannot override comparator on instance that is already a " + CharSpliterator.class.getSimpleName());
        }
        if (i instanceof Spliterator.OfInt) {
            return new PrimitiveSpliteratorWrapperWithComparator((Spliterator.OfInt)i, comparatorOverride);
        }
        return new SpliteratorWrapperWithComparator(i, comparatorOverride);
    }

    public static CharSpliterator narrow(Spliterator.OfInt i) {
        return new PrimitiveSpliteratorWrapper(i);
    }

    public static IntSpliterator widen(CharSpliterator i) {
        return IntSpliterators.wrap(i);
    }

    public static void onEachMatching(CharSpliterator spliterator, CharPredicate predicate, CharConsumer action) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(action);
        spliterator.forEachRemaining(value -> {
            if (predicate.test(value)) {
                action.accept(value);
            }
        });
    }

    public static void onEachMatching(CharSpliterator spliterator, IntPredicate predicate, IntConsumer action) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(action);
        spliterator.forEachRemaining(value -> {
            if (predicate.test(value)) {
                action.accept(value);
            }
        });
    }

    public static CharSpliterator fromTo(char from, char to) {
        return new IntervalSpliterator(from, to);
    }

    public static CharSpliterator concat(CharSpliterator ... a) {
        return CharSpliterators.concat(a, 0, a.length);
    }

    public static CharSpliterator concat(CharSpliterator[] a, int offset, int length) {
        return new SpliteratorConcatenator(a, offset, length);
    }

    public static CharSpliterator asSpliterator(CharIterator iter, long size, int additionalCharacterisitcs) {
        return new SpliteratorFromIterator(iter, size, additionalCharacterisitcs);
    }

    public static CharSpliterator asSpliteratorFromSorted(CharIterator iter, long size, int additionalCharacterisitcs, CharComparator comparator) {
        return new SpliteratorFromIteratorWithComparator(iter, size, additionalCharacterisitcs, comparator);
    }

    public static CharSpliterator asSpliteratorUnknownSize(CharIterator iter, int characterisitcs) {
        return new SpliteratorFromIterator(iter, characterisitcs);
    }

    public static CharSpliterator asSpliteratorFromSortedUnknownSize(CharIterator iter, int additionalCharacterisitcs, CharComparator comparator) {
        return new SpliteratorFromIteratorWithComparator(iter, additionalCharacterisitcs, comparator);
    }

    public static CharIterator asIterator(CharSpliterator spliterator) {
        return new IteratorFromSpliterator(spliterator);
    }

    private static class SingletonSpliterator
    implements CharSpliterator {
        private final char element;
        private final CharComparator comparator;
        private boolean consumed = false;
        private static final int CHARACTERISTICS = 17749;

        public SingletonSpliterator(char element) {
            this(element, null);
        }

        public SingletonSpliterator(char element, CharComparator comparator) {
            this.element = element;
            this.comparator = comparator;
        }

        @Override
        public boolean tryAdvance(CharConsumer action) {
            Objects.requireNonNull(action);
            if (this.consumed) {
                return false;
            }
            this.consumed = true;
            action.accept(this.element);
            return true;
        }

        @Override
        public CharSpliterator trySplit() {
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
        public void forEachRemaining(CharConsumer action) {
            Objects.requireNonNull(action);
            if (!this.consumed) {
                this.consumed = true;
                action.accept(this.element);
            }
        }

        @Override
        public CharComparator getComparator() {
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
    implements CharSpliterator {
        private static final int BASE_CHARACTERISTICS = 16720;
        final char[] array;
        private final int offset;
        private int length;
        private int curr;
        final int characteristics;

        public ArraySpliterator(char[] array, int offset, int length, int additionalCharacteristics) {
            this.array = array;
            this.offset = offset;
            this.length = length;
            this.characteristics = 0x4150 | additionalCharacteristics;
        }

        @Override
        public boolean tryAdvance(CharConsumer action) {
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
        public CharSpliterator trySplit() {
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
        public void forEachRemaining(CharConsumer action) {
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
        private final CharComparator comparator;

        public ArraySpliteratorWithComparator(char[] array, int offset, int length, int additionalCharacteristics, CharComparator comparator) {
            super(array, offset, length, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        @Override
        protected ArraySpliteratorWithComparator makeForSplit(int newOffset, int newLength) {
            return new ArraySpliteratorWithComparator(this.array, newOffset, newLength, this.characteristics, this.comparator);
        }

        @Override
        public CharComparator getComparator() {
            return this.comparator;
        }
    }

    private static class SpliteratorWrapper
    implements CharSpliterator {
        final Spliterator<Character> i;

        public SpliteratorWrapper(Spliterator<Character> i) {
            this.i = i;
        }

        @Override
        public boolean tryAdvance(CharConsumer action) {
            return this.i.tryAdvance(action);
        }

        @Override
        @Deprecated
        public boolean tryAdvance(Consumer<? super Character> action) {
            return this.i.tryAdvance(action);
        }

        @Override
        public void forEachRemaining(CharConsumer action) {
            this.i.forEachRemaining(action);
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Character> action) {
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
        public CharComparator getComparator() {
            return CharComparators.asCharComparator(this.i.getComparator());
        }

        @Override
        public CharSpliterator trySplit() {
            Spliterator<Character> innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new SpliteratorWrapper(innerSplit);
        }
    }

    private static class PrimitiveSpliteratorWrapperWithComparator
    extends PrimitiveSpliteratorWrapper {
        final CharComparator comparator;

        public PrimitiveSpliteratorWrapperWithComparator(Spliterator.OfInt i, CharComparator comparator) {
            super(i);
            this.comparator = comparator;
        }

        @Override
        public CharComparator getComparator() {
            return this.comparator;
        }

        @Override
        public CharSpliterator trySplit() {
            Spliterator.OfInt innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new PrimitiveSpliteratorWrapperWithComparator(innerSplit, this.comparator);
        }
    }

    private static class SpliteratorWrapperWithComparator
    extends SpliteratorWrapper {
        final CharComparator comparator;

        public SpliteratorWrapperWithComparator(Spliterator<Character> i, CharComparator comparator) {
            super(i);
            this.comparator = comparator;
        }

        @Override
        public CharComparator getComparator() {
            return this.comparator;
        }

        @Override
        public CharSpliterator trySplit() {
            Spliterator<Character> innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new SpliteratorWrapperWithComparator(innerSplit, this.comparator);
        }
    }

    private static class PrimitiveSpliteratorWrapper
    implements CharSpliterator {
        final Spliterator.OfInt i;

        public PrimitiveSpliteratorWrapper(Spliterator.OfInt i) {
            this.i = i;
        }

        @Override
        public boolean tryAdvance(CharConsumer action) {
            return this.i.tryAdvance(action);
        }

        @Override
        public void forEachRemaining(CharConsumer action) {
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
        public CharComparator getComparator() {
            Comparator comp = this.i.getComparator();
            return (left, right) -> comp.compare(Integer.valueOf(left), Integer.valueOf(right));
        }

        @Override
        public CharSpliterator trySplit() {
            Spliterator.OfInt innerSplit = this.i.trySplit();
            if (innerSplit == null) {
                return null;
            }
            return new PrimitiveSpliteratorWrapper(innerSplit);
        }
    }

    private static class IntervalSpliterator
    implements CharSpliterator {
        private static final int DONT_SPLIT_THRESHOLD = 2;
        private static final int CHARACTERISTICS = 17749;
        private char curr;
        private char to;

        public IntervalSpliterator(char from, char to) {
            this.curr = from;
            this.to = to;
        }

        @Override
        public boolean tryAdvance(CharConsumer action) {
            if (this.curr >= this.to) {
                return false;
            }
            char c = this.curr;
            this.curr = (char)(c + '\u0001');
            action.accept(c);
            return true;
        }

        @Override
        public void forEachRemaining(CharConsumer action) {
            Objects.requireNonNull(action);
            while (this.curr < this.to) {
                action.accept(this.curr);
                this.curr = (char)(this.curr + '\u0001');
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
        public CharComparator getComparator() {
            return null;
        }

        @Override
        public CharSpliterator trySplit() {
            int remaining = this.to - this.curr;
            char mid = (char)(this.curr + (remaining >> 1));
            if (remaining >= 0 && remaining <= 2) {
                return null;
            }
            char old_curr = this.curr;
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
                this.curr = SafeMath.safeLongToChar(newCurr);
                return n;
            }
            n = this.to - this.curr;
            this.curr = this.to;
            return n;
        }
    }

    private static class SpliteratorConcatenator
    implements CharSpliterator {
        private static final int EMPTY_CHARACTERISTICS = 16448;
        private static final int CHARACTERISTICS_NOT_SUPPORTED_WHILE_MULTIPLE = 5;
        final CharSpliterator[] a;
        int offset;
        int length;
        long remainingEstimatedExceptCurrent = Long.MAX_VALUE;
        int characteristics = 0;

        public SpliteratorConcatenator(CharSpliterator[] a, int offset, int length) {
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
        public boolean tryAdvance(CharConsumer action) {
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
        public void forEachRemaining(CharConsumer action) {
            while (this.length > 0) {
                this.a[this.offset].forEachRemaining(action);
                this.advanceNextSpliterator();
            }
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Character> action) {
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
        public CharComparator getComparator() {
            if (this.length == 1 && (this.characteristics & 4) != 0) {
                return this.a[this.offset].getComparator();
            }
            throw new IllegalStateException();
        }

        @Override
        public CharSpliterator trySplit() {
            switch (this.length) {
                case 0: {
                    return null;
                }
                case 1: {
                    CharSpliterator split = this.a[this.offset].trySplit();
                    this.characteristics = this.a[this.offset].characteristics();
                    return split;
                }
                case 2: {
                    CharSpliterator split = this.a[this.offset++];
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
    implements CharSpliterator {
        private static final int BATCH_INCREMENT_SIZE = 1024;
        private static final int BATCH_MAX_SIZE = 0x2000000;
        private final CharIterator iter;
        final int characteristics;
        private final boolean knownSize;
        private long size = Long.MAX_VALUE;
        private int nextBatchSize = 1024;
        private CharSpliterator delegate = null;

        SpliteratorFromIterator(CharIterator iter, int characteristics) {
            this.iter = iter;
            this.characteristics = 0x100 | characteristics;
            this.knownSize = false;
        }

        SpliteratorFromIterator(CharIterator iter, long size, int additionalCharacteristics) {
            this.iter = iter;
            this.knownSize = true;
            this.size = size;
            this.characteristics = (additionalCharacteristics & 0x1000) != 0 ? 0x100 | additionalCharacteristics : 0x4140 | additionalCharacteristics;
        }

        @Override
        public boolean tryAdvance(CharConsumer action) {
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
            action.accept(this.iter.nextChar());
            return true;
        }

        @Override
        public void forEachRemaining(CharConsumer action) {
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

        protected CharSpliterator makeForSplit(char[] batch, int len) {
            return CharSpliterators.wrap(batch, 0, len, this.characteristics);
        }

        @Override
        public CharSpliterator trySplit() {
            if (!this.iter.hasNext()) {
                return null;
            }
            int batchSizeEst = this.knownSize && this.size > 0L ? (int)Math.min((long)this.nextBatchSize, this.size) : this.nextBatchSize;
            char[] batch = new char[batchSizeEst];
            int actualSeen = 0;
            while (actualSeen < batchSizeEst && this.iter.hasNext()) {
                batch[actualSeen++] = this.iter.nextChar();
                --this.size;
            }
            if (batchSizeEst < this.nextBatchSize && this.iter.hasNext()) {
                batch = Arrays.copyOf(batch, this.nextBatchSize);
                while (this.iter.hasNext() && actualSeen < this.nextBatchSize) {
                    batch[actualSeen++] = this.iter.nextChar();
                    --this.size;
                }
            }
            this.nextBatchSize = Math.min(0x2000000, this.nextBatchSize + 1024);
            CharSpliterator split = this.makeForSplit(batch, actualSeen);
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
            if (this.iter instanceof CharBigListIterator) {
                long skipped2 = ((CharBigListIterator)this.iter).skip(n);
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
        private final CharComparator comparator;

        SpliteratorFromIteratorWithComparator(CharIterator iter, int additionalCharacteristics, CharComparator comparator) {
            super(iter, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        SpliteratorFromIteratorWithComparator(CharIterator iter, long size, int additionalCharacteristics, CharComparator comparator) {
            super(iter, size, additionalCharacteristics | 0x14);
            this.comparator = comparator;
        }

        @Override
        public CharComparator getComparator() {
            return this.comparator;
        }

        @Override
        protected CharSpliterator makeForSplit(char[] array, int len) {
            return CharSpliterators.wrapPreSorted(array, 0, len, this.characteristics, this.comparator);
        }
    }

    private static final class IteratorFromSpliterator
    implements CharIterator,
    CharConsumer {
        private final CharSpliterator spliterator;
        private char holder = '\u0000';
        private boolean hasPeeked = false;

        IteratorFromSpliterator(CharSpliterator spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public void accept(char item) {
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
        public char nextChar() {
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
        public void forEachRemaining(CharConsumer action) {
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

    public static class EmptySpliterator
    implements CharSpliterator,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = 8379247926738230492L;
        private static final int CHARACTERISTICS = 16448;

        protected EmptySpliterator() {
        }

        @Override
        public boolean tryAdvance(CharConsumer action) {
            return false;
        }

        @Override
        @Deprecated
        public boolean tryAdvance(Consumer<? super Character> action) {
            return false;
        }

        @Override
        public CharSpliterator trySplit() {
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
        public void forEachRemaining(CharConsumer action) {
        }

        @Override
        @Deprecated
        public void forEachRemaining(Consumer<? super Character> action) {
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
        public CharSpliterator trySplit() {
            CharSpliterator maybeSplit = super.trySplit();
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
    extends AbstractCharSpliterator {
        protected int pos;

        protected AbstractIndexBasedSpliterator(int initialPos) {
            this.pos = initialPos;
        }

        protected abstract char get(int var1);

        protected abstract int getMaxPos();

        protected abstract CharSpliterator makeForSplit(int var1, int var2);

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
        public boolean tryAdvance(CharConsumer action) {
            if (this.pos >= this.getMaxPos()) {
                return false;
            }
            action.accept(this.get(this.pos++));
            return true;
        }

        @Override
        public void forEachRemaining(CharConsumer action) {
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
        public CharSpliterator trySplit() {
            int max = this.getMaxPos();
            int splitPoint = this.computeSplitPoint();
            if (splitPoint == this.pos || splitPoint == max) {
                return null;
            }
            this.splitPointCheck(splitPoint, max);
            int oldPos = this.pos;
            CharSpliterator maybeSplit = this.makeForSplit(oldPos, splitPoint);
            if (maybeSplit != null) {
                this.pos = splitPoint;
            }
            return maybeSplit;
        }
    }
}

