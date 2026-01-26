/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortObjectImmutablePair;
import java.util.Comparator;

public interface ShortObjectPair<V>
extends Pair<Short, V> {
    public short leftShort();

    @Override
    @Deprecated
    default public Short left() {
        return this.leftShort();
    }

    default public ShortObjectPair<V> left(short l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ShortObjectPair<V> left(Short l) {
        return this.left((short)l);
    }

    default public short firstShort() {
        return this.leftShort();
    }

    @Override
    @Deprecated
    default public Short first() {
        return this.firstShort();
    }

    default public ShortObjectPair<V> first(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortObjectPair<V> first(Short l) {
        return this.first((short)l);
    }

    default public short keyShort() {
        return this.firstShort();
    }

    @Override
    @Deprecated
    default public Short key() {
        return this.keyShort();
    }

    default public ShortObjectPair<V> key(short l) {
        return this.left(l);
    }

    @Deprecated
    default public ShortObjectPair<V> key(Short l) {
        return this.key((short)l);
    }

    public static <V> ShortObjectPair<V> of(short left, V right) {
        return new ShortObjectImmutablePair<V>(left, right);
    }

    public static <V> Comparator<ShortObjectPair<V>> lexComparator() {
        return (x, y) -> {
            int t = Short.compare(x.leftShort(), y.leftShort());
            if (t != 0) {
                return t;
            }
            return ((Comparable)x.right()).compareTo(y.right());
        };
    }
}

