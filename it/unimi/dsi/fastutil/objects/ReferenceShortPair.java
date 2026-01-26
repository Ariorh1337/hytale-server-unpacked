/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceShortImmutablePair;

public interface ReferenceShortPair<K>
extends Pair<K, Short> {
    public short rightShort();

    @Override
    @Deprecated
    default public Short right() {
        return this.rightShort();
    }

    default public ReferenceShortPair<K> right(short r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ReferenceShortPair<K> right(Short l) {
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

    default public ReferenceShortPair<K> second(short r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceShortPair<K> second(Short l) {
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

    default public ReferenceShortPair<K> value(short r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceShortPair<K> value(Short l) {
        return this.value((short)l);
    }

    public static <K> ReferenceShortPair<K> of(K left, short right) {
        return new ReferenceShortImmutablePair<K>(left, right);
    }
}

