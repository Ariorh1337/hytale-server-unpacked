/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;

public final class LongIterables {
    private LongIterables() {
    }

    public static long size(LongIterable iterable) {
        long c = 0L;
        LongIterator longIterator = iterable.iterator();
        while (longIterator.hasNext()) {
            long dummy = (Long)longIterator.next();
            ++c;
        }
        return c;
    }
}

