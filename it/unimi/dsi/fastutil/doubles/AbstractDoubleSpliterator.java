/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleConsumer;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;

public abstract class AbstractDoubleSpliterator
implements DoubleSpliterator {
    protected AbstractDoubleSpliterator() {
    }

    @Override
    public final boolean tryAdvance(DoubleConsumer action) {
        return this.tryAdvance((java.util.function.DoubleConsumer)action);
    }

    @Override
    public final void forEachRemaining(DoubleConsumer action) {
        this.forEachRemaining((java.util.function.DoubleConsumer)action);
    }
}

