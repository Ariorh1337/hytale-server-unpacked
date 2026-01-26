/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleConsumer;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;

public abstract class AbstractDoubleIterator
implements DoubleIterator {
    protected AbstractDoubleIterator() {
    }

    @Override
    public final void forEachRemaining(DoubleConsumer action) {
        this.forEachRemaining((java.util.function.DoubleConsumer)action);
    }
}

