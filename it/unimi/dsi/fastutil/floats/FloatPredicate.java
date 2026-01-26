/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

@FunctionalInterface
public interface FloatPredicate
extends Predicate<Float>,
DoublePredicate {
    @Override
    public boolean test(float var1);

    @Override
    @Deprecated
    default public boolean test(double t) {
        return this.test(SafeMath.safeDoubleToFloat(t));
    }

    @Override
    @Deprecated
    default public boolean test(Float t) {
        return this.test(t.floatValue());
    }

    default public FloatPredicate and(FloatPredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) && other.test(t);
    }

    @Override
    default public FloatPredicate and(DoublePredicate other) {
        return this.and(other instanceof FloatPredicate ? (FloatPredicate)other : other::test);
    }

    @Override
    @Deprecated
    default public Predicate<Float> and(Predicate<? super Float> other) {
        return Predicate.super.and(other);
    }

    @Override
    default public FloatPredicate negate() {
        return t -> !this.test(t);
    }

    default public FloatPredicate or(FloatPredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) || other.test(t);
    }

    @Override
    default public FloatPredicate or(DoublePredicate other) {
        return this.or(other instanceof FloatPredicate ? (FloatPredicate)other : other::test);
    }

    @Override
    @Deprecated
    default public Predicate<Float> or(Predicate<? super Float> other) {
        return Predicate.super.or(other);
    }
}

