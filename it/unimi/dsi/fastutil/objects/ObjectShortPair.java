/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectShortImmutablePair;
import java.util.Comparator;

public interface ObjectShortPair<K>
extends Pair<K, Short> {
    public short rightShort();

    @Override
    @Deprecated
    default public Short right() {
        return this.rightShort();
    }

    default public ObjectShortPair<K> right(short r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ObjectShortPair<K> right(Short l) {
        return this.right((short)l);
    }

    default public short secondShort() {
        return this.rightShort();
    }

    @Override
    @Deprecated
    default public Short second() {
        return this.secondShort();
    }

    default public ObjectShortPair<K> second(short r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectShortPair<K> second(Short l) {
        return this.second((short)l);
    }

    default public short valueShort() {
        return this.rightShort();
    }

    @Override
    @Deprecated
    default public Short value() {
        return this.valueShort();
    }

    default public ObjectShortPair<K> value(short r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectShortPair<K> value(Short l) {
        return this.value((short)l);
    }

    public static <K> ObjectShortPair<K> of(K left, short right) {
        return new ObjectShortImmutablePair<K>(left, right);
    }

    public static <K> Comparator<ObjectShortPair<K>> lexComparator() {
        return (x, y) -> {
            int t = ((Comparable)x.left()).compareTo(y.left());
            if (t != 0) {
                return t;
            }
            return Short.compare(x.rightShort(), y.rightShort());
        };
    }
}

