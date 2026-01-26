/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanObjectImmutablePair;
import java.util.Comparator;

public interface BooleanObjectPair<V>
extends Pair<Boolean, V> {
    public boolean leftBoolean();

    @Override
    @Deprecated
    default public Boolean left() {
        return this.leftBoolean();
    }

    default public BooleanObjectPair<V> left(boolean l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanObjectPair<V> left(Boolean l) {
        return this.left((boolean)l);
    }

    default public boolean firstBoolean() {
        return this.leftBoolean();
    }

    @Override
    @Deprecated
    default public Boolean first() {
        return this.firstBoolean();
    }

    default public BooleanObjectPair<V> first(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanObjectPair<V> first(Boolean l) {
        return this.first((boolean)l);
    }

    default public boolean keyBoolean() {
        return this.firstBoolean();
    }

    @Override
    @Deprecated
    default public Boolean key() {
        return this.keyBoolean();
    }

    default public BooleanObjectPair<V> key(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanObjectPair<V> key(Boolean l) {
        return this.key((boolean)l);
    }

    public static <V> BooleanObjectPair<V> of(boolean left, V right) {
        return new BooleanObjectImmutablePair<V>(left, right);
    }

    public static <V> Comparator<BooleanObjectPair<V>> lexComparator() {
        return (x, y) -> {
            int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
            if (t != 0) {
                return t;
            }
            return ((Comparable)x.right()).compareTo(y.right());
        };
    }
}

