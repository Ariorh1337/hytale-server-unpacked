/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharReferenceImmutablePair;

public interface CharReferencePair<V>
extends Pair<Character, V> {
    public char leftChar();

    @Override
    @Deprecated
    default public Character left() {
        return Character.valueOf(this.leftChar());
    }

    default public CharReferencePair<V> left(char l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public CharReferencePair<V> left(Character l) {
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

    default public CharReferencePair<V> first(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharReferencePair<V> first(Character l) {
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

    default public CharReferencePair<V> key(char l) {
        return this.left(l);
    }

    @Deprecated
    default public CharReferencePair<V> key(Character l) {
        return this.key(l.charValue());
    }

    public static <V> CharReferencePair<V> of(char left, V right) {
        return new CharReferenceImmutablePair<V>(left, right);
    }
}

