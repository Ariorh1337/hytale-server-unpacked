/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface DoublePredicate
extends Predicate<Double>,
java.util.function.DoublePredicate {
    @Override
    @Deprecated
    default public boolean test(Double t) {
        return this.test(t.doubleValue());
    }

    @Override
    default public DoublePredicate and(java.util.function.DoublePredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) && other.test(t);
    }

    default public DoublePredicate and(DoublePredicate other) {
        return this.and((java.util.function.DoublePredicate)other);
    }

    @Override
    @Deprecated
    default public Predicate<Double> and(Predicate<? super Double> other) {
        return Predicate.super.and(other);
    }

    @Override
    default public DoublePredicate negate() {
        return t -> !this.test(t);
    }

    @Override
    default public DoublePredicate or(java.util.function.DoublePredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) || other.test(t);
    }

    default public DoublePredicate or(DoublePredicate other) {
        return this.or((java.util.function.DoublePredicate)other);
    }

    @Override
    @Deprecated
    default public Predicate<Double> or(Predicate<? super Double> other) {
        return Predicate.super.or(other);
    }
}

