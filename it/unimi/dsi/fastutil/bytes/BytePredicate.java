/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

@FunctionalInterface
public interface BytePredicate
extends Predicate<Byte>,
IntPredicate {
    @Override
    public boolean test(byte var1);

    @Override
    @Deprecated
    default public boolean test(int t) {
        return this.test(SafeMath.safeIntToByte(t));
    }

    @Override
    @Deprecated
    default public boolean test(Byte t) {
        return this.test((byte)t);
    }

    default public BytePredicate and(BytePredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) && other.test(t);
    }

    @Override
    default public BytePredicate and(IntPredicate other) {
        return this.and(other instanceof BytePredicate ? (BytePredicate)other : other::test);
    }

    @Override
    @Deprecated
    default public Predicate<Byte> and(Predicate<? super Byte> other) {
        return Predicate.super.and(other);
    }

    @Override
    default public BytePredicate negate() {
        return t -> !this.test(t);
    }

    default public BytePredicate or(BytePredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) || other.test(t);
    }

    @Override
    default public BytePredicate or(IntPredicate other) {
        return this.or(other instanceof BytePredicate ? (BytePredicate)other : other::test);
    }

    @Override
    @Deprecated
    default public Predicate<Byte> or(Predicate<? super Byte> other) {
        return Predicate.super.or(other);
    }
}

