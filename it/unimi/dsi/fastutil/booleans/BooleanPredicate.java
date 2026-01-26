/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface BooleanPredicate
extends Predicate<Boolean> {
    @Override
    public boolean test(boolean var1);

    public static BooleanPredicate identity() {
        return b -> b;
    }

    public static BooleanPredicate negation() {
        return b -> !b;
    }

    @Override
    @Deprecated
    default public boolean test(Boolean t) {
        return this.test((boolean)t);
    }

    default public BooleanPredicate and(BooleanPredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) && other.test(t);
    }

    @Override
    @Deprecated
    default public Predicate<Boolean> and(Predicate<? super Boolean> other) {
        return Predicate.super.and(other);
    }

    default public BooleanPredicate negate() {
        return t -> !this.test(t);
    }

    default public BooleanPredicate or(BooleanPredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) || other.test(t);
    }

    @Override
    @Deprecated
    default public Predicate<Boolean> or(Predicate<? super Boolean> other) {
        return Predicate.super.or(other);
    }
}

