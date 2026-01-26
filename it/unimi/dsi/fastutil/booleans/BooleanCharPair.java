/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanCharImmutablePair;
import java.util.Comparator;

public interface BooleanCharPair
extends Pair<Boolean, Character> {
    public boolean leftBoolean();

    @Override
    @Deprecated
    default public Boolean left() {
        return this.leftBoolean();
    }

    default public BooleanCharPair left(boolean l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanCharPair left(Boolean l) {
        return this.left((boolean)l);
    }

    default public boolean firstBoolean() {
        return this.leftBoolean();
    }

    @Override
    @Deprecated
    default public Boolean first() {
        return this.firstBoolean();
    }

    default public BooleanCharPair first(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanCharPair first(Boolean l) {
        return this.first((boolean)l);
    }

    default public boolean keyBoolean() {
        return this.firstBoolean();
    }

    @Override
    @Deprecated
    default public Boolean key() {
        return this.keyBoolean();
    }

    default public BooleanCharPair key(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanCharPair key(Boolean l) {
        return this.key((boolean)l);
    }

    public char rightChar();

    @Override
    @Deprecated
    default public Character right() {
        return Character.valueOf(this.rightChar());
    }

    default public BooleanCharPair right(char r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanCharPair right(Character l) {
        return this.right(l.charValue());
    }

    default public char secondChar() {
        return this.rightChar();
    }

    @Override
    @Deprecated
    default public Character second() {
        return Character.valueOf(this.secondChar());
    }

    default public BooleanCharPair second(char r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanCharPair second(Character l) {
        return this.second(l.charValue());
    }

    default public char valueChar() {
        return this.rightChar();
    }

    @Override
    @Deprecated
    default public Character value() {
        return Character.valueOf(this.valueChar());
    }

    default public BooleanCharPair value(char r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanCharPair value(Character l) {
        return this.value(l.charValue());
    }

    public static BooleanCharPair of(boolean left, char right) {
        return new BooleanCharImmutablePair(left, right);
    }

    public static Comparator<BooleanCharPair> lexComparator() {
        return (x, y) -> {
            int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
            if (t != 0) {
                return t;
            }
            return Character.compare(x.rightChar(), y.rightChar());
        };
    }
}

