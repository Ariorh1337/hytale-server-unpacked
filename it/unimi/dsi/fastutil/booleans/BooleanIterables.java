/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanIterable;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;

public final class BooleanIterables {
    private BooleanIterables() {
    }

    public static long size(BooleanIterable iterable) {
        long c = 0L;
        BooleanIterator booleanIterator = iterable.iterator();
        while (booleanIterator.hasNext()) {
            boolean dummy = (Boolean)booleanIterator.next();
            ++c;
        }
        return c;
    }
}

