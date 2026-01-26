/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import it.unimi.dsi.fastutil.ints.IntIterator;

public abstract class AbstractIntIterator
implements IntIterator {
    protected AbstractIntIterator() {
    }

    @Override
    public final void forEachRemaining(IntConsumer action) {
        this.forEachRemaining((java.util.function.IntConsumer)action);
    }
}

