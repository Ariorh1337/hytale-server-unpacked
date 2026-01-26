/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharConsumer;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface CharIterator
extends PrimitiveIterator<Character, CharConsumer> {
    public char nextChar();

    @Override
    @Deprecated
    default public Character next() {
        return Character.valueOf(this.nextChar());
    }

    @Override
    default public void forEachRemaining(CharConsumer action) {
        Objects.requireNonNull(action);
        while (this.hasNext()) {
            action.accept(this.nextChar());
        }
    }

    @Override
    default public void forEachRemaining(IntConsumer action) {
        Objects.requireNonNull(action);
        this.forEachRemaining(action instanceof CharConsumer ? (CharConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Character> action) {
        this.forEachRemaining(action instanceof CharConsumer ? (CharConsumer)action : action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextChar();
        }
        return n - i - 1;
    }
}

