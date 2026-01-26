/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

@FunctionalInterface
public interface CharPredicate
extends Predicate<Character>,
IntPredicate {
    @Override
    public boolean test(char var1);

    @Override
    @Deprecated
    default public boolean test(int t) {
        return this.test(SafeMath.safeIntToChar(t));
    }

    @Override
    @Deprecated
    default public boolean test(Character t) {
        return this.test(t.charValue());
    }

    default public CharPredicate and(CharPredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) && other.test(t);
    }

    @Override
    default public CharPredicate and(IntPredicate other) {
        return this.and(other instanceof CharPredicate ? (CharPredicate)other : other::test);
    }

    @Override
    @Deprecated
    default public Predicate<Character> and(Predicate<? super Character> other) {
        return Predicate.super.and(other);
    }

    @Override
    default public CharPredicate negate() {
        return t -> !this.test(t);
    }

    default public CharPredicate or(CharPredicate other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) || other.test(t);
    }

    @Override
    default public CharPredicate or(IntPredicate other) {
        return this.or(other instanceof CharPredicate ? (CharPredicate)other : other::test);
    }

    @Override
    @Deprecated
    default public Predicate<Character> or(Predicate<? super Character> other) {
        return Predicate.super.or(other);
    }
}

