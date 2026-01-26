/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectLongImmutablePair;
import java.util.Comparator;

public interface ObjectLongPair<K>
extends Pair<K, Long> {
    public long rightLong();

    @Override
    @Deprecated
    default public Long right() {
        return this.rightLong();
    }

    default public ObjectLongPair<K> right(long r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ObjectLongPair<K> right(Long l) {
        return this.right((long)l);
    }

    default public long secondLong() {
        return this.rightLong();
    }

    @Override
    @Deprecated
    default public Long second() {
        return this.secondLong();
    }

    default public ObjectLongPair<K> second(long r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectLongPair<K> second(Long l) {
        return this.second((long)l);
    }

    default public long valueLong() {
        return this.rightLong();
    }

    @Override
    @Deprecated
    default public Long value() {
        return this.valueLong();
    }

    default public ObjectLongPair<K> value(long r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectLongPair<K> value(Long l) {
        return this.value((long)l);
    }

    public static <K> ObjectLongPair<K> of(K left, long right) {
        return new ObjectLongImmutablePair<K>(left, right);
    }

    public static <K> Comparator<ObjectLongPair<K>> lexComparator() {
        return (x, y) -> {
            int t = ((Comparable)x.left()).compareTo(y.left());
            if (t != 0) {
                return t;
            }
            return Long.compare(x.rightLong(), y.rightLong());
        };
    }
}

