/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;

public final class IntIterables {
    private IntIterables() {
    }

    public static long size(IntIterable iterable) {
        long c = 0L;
        IntIterator intIterator = iterable.iterator();
        while (intIterator.hasNext()) {
            int dummy = (Integer)intIterator.next();
            ++c;
        }
        return c;
    }
}

