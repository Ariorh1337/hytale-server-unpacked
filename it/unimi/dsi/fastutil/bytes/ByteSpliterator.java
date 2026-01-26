/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface ByteSpliterator
extends Spliterator.OfPrimitive<Byte, ByteConsumer, ByteSpliterator> {
    @Override
    @Deprecated
    default public boolean tryAdvance(Consumer<? super Byte> action) {
        return this.tryAdvance(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Byte> action) {
        this.forEachRemaining(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
    }

    default public long skip(long n) {
        if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        long i = n;
        while (i-- != 0L && this.tryAdvance(unused -> {})) {
        }
        return n - i - 1L;
    }

    @Override
    public ByteSpliterator trySplit();

    default public ByteComparator getComparator() {
        throw new IllegalStateException();
    }
}

