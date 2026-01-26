/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortIterable;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public final class ShortIterables {
    private ShortIterables() {
    }

    public static long size(ShortIterable iterable) {
        long c = 0L;
        ShortIterator shortIterator = iterable.iterator();
        while (shortIterator.hasNext()) {
            short dummy = (Short)shortIterator.next();
            ++c;
        }
        return c;
    }
}

