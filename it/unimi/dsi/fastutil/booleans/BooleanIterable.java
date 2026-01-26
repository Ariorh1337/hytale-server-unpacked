/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import java.util.Objects;
import java.util.function.Consumer;

public interface BooleanIterable
extends Iterable<Boolean> {
    public BooleanIterator iterator();

    default public BooleanSpliterator spliterator() {
        return BooleanSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
    }

    default public void forEach(BooleanConsumer action) {
        Objects.requireNonNull(action);
        this.iterator().forEachRemaining(action);
    }

    @Override
    @Deprecated
    default public void forEach(Consumer<? super Boolean> action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof BooleanConsumer ? (BooleanConsumer)action : action::accept);
    }
}

