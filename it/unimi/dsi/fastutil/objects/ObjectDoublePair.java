/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectDoubleImmutablePair;
import java.util.Comparator;

public interface ObjectDoublePair<K>
extends Pair<K, Double> {
    public double rightDouble();

    @Override
    @Deprecated
    default public Double right() {
        return this.rightDouble();
    }

    default public ObjectDoublePair<K> right(double r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ObjectDoublePair<K> right(Double l) {
        return this.right((double)l);
    }

    default public double secondDouble() {
        return this.rightDouble();
    }

    @Override
    @Deprecated
    default public Double second() {
        return this.secondDouble();
    }

    default public ObjectDoublePair<K> second(double r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectDoublePair<K> second(Double l) {
        return this.second((double)l);
    }

    default public double valueDouble() {
        return this.rightDouble();
    }

    @Override
    @Deprecated
    default public Double value() {
        return this.valueDouble();
    }

    default public ObjectDoublePair<K> value(double r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectDoublePair<K> value(Double l) {
        return this.value((double)l);
    }

    public static <K> ObjectDoublePair<K> of(K left, double right) {
        return new ObjectDoubleImmutablePair<K>(left, right);
    }

    public static <K> Comparator<ObjectDoublePair<K>> lexComparator() {
        return (x, y) -> {
            int t = ((Comparable)x.left()).compareTo(y.left());
            if (t != 0) {
                return t;
            }
            return Double.compare(x.rightDouble(), y.rightDouble());
        };
    }
}

