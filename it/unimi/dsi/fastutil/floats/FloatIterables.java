/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatIterable;
import it.unimi.dsi.fastutil.floats.FloatIterator;

public final class FloatIterables {
    private FloatIterables() {
    }

    public static long size(FloatIterable iterable) {
        long c = 0L;
        FloatIterator floatIterator = iterable.iterator();
        while (floatIterator.hasNext()) {
            float dummy = ((Float)floatIterator.next()).floatValue();
            ++c;
        }
        return c;
    }
}

