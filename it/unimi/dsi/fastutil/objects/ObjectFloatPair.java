/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectFloatImmutablePair;
import java.util.Comparator;

public interface ObjectFloatPair<K>
extends Pair<K, Float> {
    public float rightFloat();

    @Override
    @Deprecated
    default public Float right() {
        return Float.valueOf(this.rightFloat());
    }

    default public ObjectFloatPair<K> right(float r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ObjectFloatPair<K> right(Float l) {
        return this.right(l.floatValue());
    }

    default public float secondFloat() {
        return this.rightFloat();
    }

    @Override
    @Deprecated
    default public Float second() {
        return Float.valueOf(this.secondFloat());
    }

    default public ObjectFloatPair<K> second(float r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectFloatPair<K> second(Float l) {
        return this.second(l.floatValue());
    }

    default public float valueFloat() {
        return this.rightFloat();
    }

    @Override
    @Deprecated
    default public Float value() {
        return Float.valueOf(this.valueFloat());
    }

    default public ObjectFloatPair<K> value(float r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectFloatPair<K> value(Float l) {
        return this.value(l.floatValue());
    }

    public static <K> ObjectFloatPair<K> of(K left, float right) {
        return new ObjectFloatImmutablePair<K>(left, right);
    }

    public static <K> Comparator<ObjectFloatPair<K>> lexComparator() {
        return (x, y) -> {
            int t = ((Comparable)x.left()).compareTo(y.left());
            if (t != 0) {
                return t;
            }
            return Float.compare(x.rightFloat(), y.rightFloat());
        };
    }
}

