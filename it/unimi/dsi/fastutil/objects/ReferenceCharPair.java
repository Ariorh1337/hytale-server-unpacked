/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceCharImmutablePair;

public interface ReferenceCharPair<K>
extends Pair<K, Character> {
    public char rightChar();

    @Override
    @Deprecated
    default public Character right() {
        return Character.valueOf(this.rightChar());
    }

    default public ReferenceCharPair<K> right(char r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ReferenceCharPair<K> right(Character l) {
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

    default public ReferenceCharPair<K> second(char r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceCharPair<K> second(Character l) {
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

    default public ReferenceCharPair<K> value(char r) {
        return this.right(r);
    }

    @Deprecated
    default public ReferenceCharPair<K> value(Character l) {
        return this.value(l.charValue());
    }

    public static <K> ReferenceCharPair<K> of(K left, char right) {
        return new ReferenceCharImmutablePair<K>(left, right);
    }
}

