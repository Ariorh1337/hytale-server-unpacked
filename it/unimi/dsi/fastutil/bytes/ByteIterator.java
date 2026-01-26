/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ByteIterator
extends PrimitiveIterator<Byte, ByteConsumer> {
    public byte nextByte();

    @Override
    @Deprecated
    default public Byte next() {
        return this.nextByte();
    }

    @Override
    default public void forEachRemaining(ByteConsumer action) {
        Objects.requireNonNull(action);
        while (this.hasNext()) {
            action.accept(this.nextByte());
        }
    }

    @Override
    default public void forEachRemaining(IntConsumer action) {
        Objects.requireNonNull(action);
        this.forEachRemaining(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Byte> action) {
        this.forEachRemaining(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextByte();
        }
        return n - i - 1;
    }
}

