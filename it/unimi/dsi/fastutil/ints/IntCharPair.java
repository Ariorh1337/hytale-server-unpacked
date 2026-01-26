/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntCharImmutablePair;
import java.util.Comparator;

public interface IntCharPair
extends Pair<Integer, Character> {
    public int leftInt();

    @Override
    @Deprecated
    default public Integer left() {
        return this.leftInt();
    }

    default public IntCharPair left(int l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntCharPair left(Integer l) {
        return this.left((int)l);
    }

    default public int firstInt() {
        return this.leftInt();
    }

    @Override
    @Deprecated
    default public Integer first() {
        return this.firstInt();
    }

    default public IntCharPair first(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntCharPair first(Integer l) {
        return this.first((int)l);
    }

    default public int keyInt() {
        return this.firstInt();
    }

    @Override
    @Deprecated
    default public Integer key() {
        return this.keyInt();
    }

    default public IntCharPair key(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntCharPair key(Integer l) {
        return this.key((int)l);
    }

    public char rightChar();

    @Override
    @Deprecated
    default public Character right() {
        return Character.valueOf(this.rightChar());
    }

    default public IntCharPair right(char r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntCharPair right(Character l) {
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

    default public IntCharPair second(char r) {
        return this.right(r);
    }

    @Deprecated
    default public IntCharPair second(Character l) {
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

    default public IntCharPair value(char r) {
        return this.right(r);
    }

    @Deprecated
    default public IntCharPair value(Character l) {
        return this.value(l.charValue());
    }

    public static IntCharPair of(int left, char right) {
        return new IntCharImmutablePair(left, right);
    }

    public static Comparator<IntCharPair> lexComparator() {
        return (x, y) -> {
            int t = Integer.compare(x.leftInt(), y.leftInt());
            if (t != 0) {
                return t;
            }
            return Character.compare(x.rightChar(), y.rightChar());
        };
    }
}

