/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;

public final class DoubleIterables {
    private DoubleIterables() {
    }

    public static long size(DoubleIterable iterable) {
        long c = 0L;
        DoubleIterator doubleIterator = iterable.iterator();
        while (doubleIterator.hasNext()) {
            double dummy = (Double)doubleIterator.next();
            ++c;
        }
        return c;
    }
}

