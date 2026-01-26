/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharBooleanImmutablePair;
import java.util.Comparator;

public interface CharBooleanPair
extends Pair<Character, Boolean> {
    public char leftChar();

    @Override
    @Deprecated
    default public Character left() {
        return Character.valueOf(this.leftChar());
    }

    default public CharBooleanPair left(char l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharBooleanPair left(Character l) {
        return this.left(l.charValue());
    }

    default public char firstChar() {
        return this.leftChar();
    }

    @Override
    @Deprecated
    default public Character first() {
        return Character.valueOf(this.firstChar());
    }

    default public CharBooleanPair first(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharBooleanPair first(Character l) {
        return this.first(l.charValue());
    }

    default public char keyChar() {
        return this.firstChar();
    }

    @Override
    @Deprecated
    default public Character key() {
        return Character.valueOf(this.keyChar());
    }

    default public CharBooleanPair key(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharBooleanPair key(Character l) {
        return this.key(l.charValue());
    }

    public boolean rightBoolean();

    @Override
    @Deprecated
    default public Boolean right() {
        return this.rightBoolean();
    }

    default public CharBooleanPair right(boolean r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharBooleanPair right(Boolean l) {
        return this.right((boolean)l);
    }

    default public boolean secondBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean second() {
        return this.secondBoolean();
    }

    default public CharBooleanPair second(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public CharBooleanPair second(Boolean l) {
        return this.second((boolean)l);
    }

    default public boolean valueBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean value() {
        return this.valueBoolean();
    }

    default public CharBooleanPair value(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public CharBooleanPair value(Boolean l) {
        return this.value((boolean)l);
    }

    public static CharBooleanPair of(char left, boolean right) {
        return new CharBooleanImmutablePair(left, right);
    }

    public static Comparator<CharBooleanPair> lexComparator() {
        return (x, y) -> {
            int t = Character.compare(x.leftChar(), y.leftChar());
            if (t != 0) {
                return t;
            }
            return Boolean.compare(x.rightBoolean(), y.rightBoolean());
        };
    }
}

