/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongSpliterator;

public abstract class AbstractLongSpliterator
implements LongSpliterator {
    protected AbstractLongSpliterator() {
    }

    @Override
    public final boolean tryAdvance(LongConsumer action) {
        return this.tryAdvance((java.util.function.LongConsumer)action);
    }

    @Override
    public final void forEachRemaining(LongConsumer action) {
        this.forEachRemaining((java.util.function.LongConsumer)action);
    }
}

