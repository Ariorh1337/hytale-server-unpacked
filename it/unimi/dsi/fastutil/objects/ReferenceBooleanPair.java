/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceBooleanImmutablePair;

public interface ReferenceBooleanPair<K>
extends Pair<K, Boolean> {
    public boolean rightBoolean();

    @Override
    @Deprecated
    default public Boolean right() {
        return this.rightBoolean();
    }

    default public ReferenceBooleanPair<K> right(boolean r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ReferenceBooleanPair<K> right(Boolean l) {
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

    default public ReferenceBooleanPair<K> second(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceBooleanPair<K> second(Boolean l) {
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

    default public ReferenceBooleanPair<K> value(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceBooleanPair<K> value(Boolean l) {
        return this.value((boolean)l);
    }

    public static <K> ReferenceBooleanPair<K> of(K left, boolean right) {
        return new ReferenceBooleanImmutablePair<K>(left, right);
    }
}

