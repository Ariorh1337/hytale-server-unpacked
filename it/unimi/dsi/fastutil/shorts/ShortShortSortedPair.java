/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.shorts.ShortShortImmutableSortedPair;
import it.unimi.dsi.fastutil.shorts.ShortShortPair;
import java.io.Serializable;

public interface ShortShortSortedPair
extends ShortShortPair,
SortedPair<Short>,
Serializable {
    public static ShortShortSortedPair of(short left, short right) {
        return ShortShortImmutableSortedPair.of(left, right);
    }

    default public boolean contains(short e) {
        return e == this.leftShort() || e == this.rightShort();
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains((Short)o);
    }
}

