/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

@FunctionalInterface
public interface ShortPredicate
extends Predicate<Short>,
IntPredicate {
    @Override
    public boolean test(short var1);

    @Override
    @Deprecated
    default public boolean test(int t) {
        return this.test(SafeMath.safeIntToShort(t));
    }

    @Override
    @Deprecated
    default public boolean test(Short t) {
        return this.test((short)t);
    }

    default public ShortPredicate and(ShortPredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) && other.test(t);
    }

    @Override
    default public ShortPredicate and(IntPredicate other) {
        return this.and(other instanceof ShortPredicate ? (ShortPredicate)other : other::test);
    }

    @Override
    @Deprecated
    default public Predicate<Short> and(Predicate<? super Short> other) {
        return Predicate.super.and(other);
    }

    @Override
    default public ShortPredicate negate() {
        return t -> !this.test(t);
    }

    default public ShortPredicate or(ShortPredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) || other.test(t);
    }

    @Override
    default public ShortPredicate or(IntPredicate other) {
        return this.or(other instanceof ShortPredicate ? (ShortPredicate)other : other::test);
    }

    @Override
    @Deprecated
    default public Predicate<Short> or(Predicate<? super Short> other) {
        return Predicate.super.or(other);
    }
}

