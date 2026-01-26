/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface LongPredicate
extends Predicate<Long>,
java.util.function.LongPredicate {
    @Override
    @Deprecated
    default public boolean test(Long t) {
        return this.test(t.longValue());
    }

    @Override
    default public LongPredicate and(java.util.function.LongPredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) && other.test(t);
    }

    default public LongPredicate and(LongPredicate other) {
        return this.and((java.util.function.LongPredicate)other);
    }

    @Override
    @Deprecated
    default public Predicate<Long> and(Predicate<? super Long> other) {
        return Predicate.super.and(other);
    }

    @Override
    default public LongPredicate negate() {
        return t -> !this.test(t);
    }

    @Override
    default public LongPredicate or(java.util.function.LongPredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) || other.test(t);
    }

    default public LongPredicate or(LongPredicate other) {
        return this.or((java.util.function.LongPredicate)other);
    }

    @Override
    @Deprecated
    default public Predicate<Long> or(Predicate<? super Long> other) {
        return Predicate.super.or(other);
    }
}

