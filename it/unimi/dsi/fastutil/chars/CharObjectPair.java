/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharObjectImmutablePair;
import java.util.Comparator;

public interface CharObjectPair<V>
extends Pair<Character, V> {
    public char leftChar();

    @Override
    @Deprecated
    default public Character left() {
        return Character.valueOf(this.leftChar());
    }

    default public CharObjectPair<V> left(char l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharObjectPair<V> left(Character l) {
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

    default public CharObjectPair<V> first(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharObjectPair<V> first(Character l) {
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

    default public CharObjectPair<V> key(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharObjectPair<V> key(Character l) {
        return this.key(l.charValue());
    }

    public static <V> CharObjectPair<V> of(char left, V right) {
        return new CharObjectImmutablePair<V>(left, right);
    }

    public static <V> Comparator<CharObjectPair<V>> lexComparator() {
        return (x, y) -> {
            int t = Character.compare(x.leftChar(), y.leftChar());
            if (t != 0) {
                return t;
            }
            return ((Comparable)x.right()).compareTo(y.right());
        };
    }
}

