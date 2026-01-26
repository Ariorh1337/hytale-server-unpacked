/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceIntImmutablePair;

public interface ReferenceIntPair<K>
extends Pair<K, Integer> {
    public int rightInt();

    @Override
    @Deprecated
    default public Integer right() {
        return this.rightInt();
    }

    default public ReferenceIntPair<K> right(int r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ReferenceIntPair<K> right(Integer l) {
        return this.right((int)l);
    }

    default public int secondInt() {
        return this.rightInt();
    }

    @Override
    @Deprecated
    default public Integer second() {
        return this.secondInt();
    }

    default public ReferenceIntPair<K> second(int r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceIntPair<K> second(Integer l) {
        return this.second((int)l);
    }

    default public int valueInt() {
        return this.rightInt();
    }

    @Override
    @Deprecated
    default public Integer value() {
        return this.valueInt();
    }

    default public ReferenceIntPair<K> value(int r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceIntPair<K> value(Integer l) {
        return this.value((int)l);
    }

    public static <K> ReferenceIntPair<K> of(K left, int right) {
        return new ReferenceIntImmutablePair<K>(left, right);
    }
}

