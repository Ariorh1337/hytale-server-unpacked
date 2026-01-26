/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import it.unimi.dsi.fastutil.ints.IntSpliterator;

public abstract class AbstractIntSpliterator
implements IntSpliterator {
    protected AbstractIntSpliterator() {
    }

    @Override
    public final boolean tryAdvance(IntConsumer action) {
        return this.tryAdvance((java.util.function.IntConsumer)action);
    }

    @Override
    public final void forEachRemaining(IntConsumer action) {
        this.forEachRemaining((java.util.function.IntConsumer)action);
    }
}

