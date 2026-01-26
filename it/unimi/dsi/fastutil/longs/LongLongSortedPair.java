/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.longs.LongLongImmutableSortedPair;
import it.unimi.dsi.fastutil.longs.LongLongPair;
import java.io.Serializable;

public interface LongLongSortedPair
extends LongLongPair,
SortedPair<Long>,
Serializable {
    public static LongLongSortedPair of(long left, long right) {
        return LongLongImmutableSortedPair.of(left, right);
    }

    default public boolean contains(long e) {
        return e == this.leftLong() || e == this.rightLong();
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains((Long)o);
    }
}

