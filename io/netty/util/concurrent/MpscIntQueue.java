/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public interface MpscIntQueue {
    public static MpscIntQueue create(int size, int emptyValue) {
        return new MpscAtomicIntegerArrayQueue(size, emptyValue);
    }

    public boolean offer(int var1);

    public int poll();

    public int drain(int var1, IntConsumer var2);

    public int fill(int var1, IntSupplier var2);

    public boolean isEmpty();

    public int size();

    public static final class MpscAtomicIntegerArrayQueue
    extends AtomicIntegerArray
    implements MpscIntQueue {
        private static final long serialVersionUID = 8740338425124821455L;
        private static final AtomicLongFieldUpdater<MpscAtomicIntegerArrayQueue> PRODUCER_INDEX = AtomicLongFieldUpdater.newUpdater(MpscAtomicIntegerArrayQueue.class, "producerIndex");
        private static final AtomicLongFieldUpdater<MpscAtomicIntegerArrayQueue> PRODUCER_LIMIT = AtomicLongFieldUpdater.newUpdater(MpscAtomicIntegerArrayQueue.class, "producerLimit");
        private static final AtomicLongFieldUpdater<MpscAtomicIntegerArrayQueue> CONSUMER_INDEX = AtomicLongFieldUpdater.newUpdater(MpscAtomicIntegerArrayQueue.class, "consumerIndex");
        private final int mask;
        private final int emptyValue;
        private volatile long producerIndex;
        private volatile long producerLimit;
        private volatile long consumerIndex;

        public MpscAtomicIntegerArrayQueue(int capacity, int emptyValue) {
            super(MathUtil.safeFindNextPositivePowerOfTwo(capacity));
            if (emptyValue != 0) {
                this.emptyValue = emptyValue;
                int end = this.length() - 1;
                for (int i = 0; i < end; ++i) {
                    this.lazySet(i, emptyValue);
                }
                this.getAndSet(end, emptyValue);
            } else {
                this.emptyValue = 0;
            }
            this.mask = this.length() - 1;
        }

        @Override
        public boolean offer(int value) {
            long pIndex;
            if (value == this.emptyValue) {
                throw new IllegalArgumentException("Cannot offer the \"empty\" value: " + this.emptyValue);
            }
            int mask = this.mask;
            long producerLimit = this.producerLimit;
            do {
                if ((pIndex = this.producerIndex) < producerLimit) continue;
                long cIndex = this.consumerIndex;
                producerLimit = cIndex + (long)mask + 1L;
                if (pIndex >= producerLimit) {
                    return false;
                }
                PRODUCER_LIMIT.lazySet(this, producerLimit);
            } while (!PRODUCER_INDEX.compareAndSet(this, pIndex, pIndex + 1L));
            int offset = (int)(pIndex & (long)mask);
            this.lazySet(offset, value);
            return true;
        }

        @Override
        public int poll() {
            long cIndex = this.consumerIndex;
            int offset = (int)(cIndex & (long)this.mask);
            int value = this.get(offset);
            if (this.emptyValue == value) {
                if (cIndex != this.producerIndex) {
                    while (this.emptyValue == (value = this.get(offset))) {
                    }
                } else {
                    return this.emptyValue;
                }
            }
            this.lazySet(offset, this.emptyValue);
            CONSUMER_INDEX.lazySet(this, cIndex + 1L);
            return value;
        }

        @Override
        public int drain(int limit, IntConsumer consumer) {
            Objects.requireNonNull(consumer, "consumer");
            ObjectUtil.checkPositiveOrZero(limit, "limit");
            if (limit == 0) {
                return 0;
            }
            int mask = this.mask;
            long cIndex = this.consumerIndex;
            for (int i = 0; i < limit; ++i) {
                long index = cIndex + (long)i;
                int offset = (int)(index & (long)mask);
                int value = this.get(offset);
                if (this.emptyValue == value) {
                    return i;
                }
                this.lazySet(offset, this.emptyValue);
                CONSUMER_INDEX.lazySet(this, index + 1L);
                consumer.accept(value);
            }
            return limit;
        }

        @Override
        public int fill(int limit, IntSupplier supplier) {
            long available;
            int actualLimit;
            long pIndex;
            Objects.requireNonNull(supplier, "supplier");
            ObjectUtil.checkPositiveOrZero(limit, "limit");
            if (limit == 0) {
                return 0;
            }
            int mask = this.mask;
            long capacity = mask + 1;
            long producerLimit = this.producerLimit;
            do {
                if ((available = producerLimit - (pIndex = this.producerIndex)) > 0L) continue;
                long cIndex = this.consumerIndex;
                producerLimit = cIndex + capacity;
                available = producerLimit - pIndex;
                if (available <= 0L) {
                    return 0;
                }
                PRODUCER_LIMIT.lazySet(this, producerLimit);
            } while (!PRODUCER_INDEX.compareAndSet(this, pIndex, pIndex + (long)(actualLimit = Math.min((int)available, limit))));
            for (int i = 0; i < actualLimit; ++i) {
                int offset = (int)(pIndex + (long)i & (long)mask);
                this.lazySet(offset, supplier.getAsInt());
            }
            return actualLimit;
        }

        @Override
        public boolean isEmpty() {
            long cIndex = this.consumerIndex;
            long pIndex = this.producerIndex;
            return cIndex >= pIndex;
        }

        @Override
        public int size() {
            long pIndex;
            long before;
            long after = this.consumerIndex;
            do {
                before = after;
                pIndex = this.producerIndex;
            } while (before != (after = this.consumerIndex));
            long size = pIndex - after;
            return size < 0L ? 0 : (size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)size);
        }
    }
}

