/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongIterator;

public abstract class AbstractLongIterator
implements LongIterator {
    protected AbstractLongIterator() {
    }

    @Override
    public final void forEachRemaining(LongConsumer action) {
        this.forEachRemaining((java.util.function.LongConsumer)action);
    }
}

