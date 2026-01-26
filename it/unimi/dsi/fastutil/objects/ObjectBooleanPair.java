/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectBooleanImmutablePair;
import java.util.Comparator;

public interface ObjectBooleanPair<K>
extends Pair<K, Boolean> {
    public boolean rightBoolean();

    @Override
    @Deprecated
    default public Boolean right() {
        return this.rightBoolean();
    }

    default public ObjectBooleanPair<K> right(boolean r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ObjectBooleanPair<K> right(Boolean l) {
        return this.right((boolean)l);
    }

    default public boolean secondBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean second() {
        return this.secondBoolean();
    }

    default public ObjectBooleanPair<K> second(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectBooleanPair<K> second(Boolean l) {
        return this.second((boolean)l);
    }

    default public boolean valueBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean value() {
        return this.valueBoolean();
    }

    default public ObjectBooleanPair<K> value(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectBooleanPair<K> value(Boolean l) {
        return this.value((boolean)l);
    }

    public static <K> ObjectBooleanPair<K> of(K left, boolean right) {
        return new ObjectBooleanImmutablePair<K>(left, right);
    }

    public static <K> Comparator<ObjectBooleanPair<K>> lexComparator() {
        return (x, y) -> {
            int t = ((Comparable)x.left()).compareTo(y.left());
            if (t != 0) {
                return t;
            }
            return Boolean.compare(x.rightBoolean(), y.rightBoolean());
        };
    }
}

