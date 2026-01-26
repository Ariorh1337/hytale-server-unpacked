/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectCharImmutablePair;
import java.util.Comparator;

public interface ObjectCharPair<K>
extends Pair<K, Character> {
    public char rightChar();

    @Override
    @Deprecated
    default public Character right() {
        return Character.valueOf(this.rightChar());
    }

    default public ObjectCharPair<K> right(char r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ObjectCharPair<K> right(Character l) {
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

    default public ObjectCharPair<K> second(char r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectCharPair<K> second(Character l) {
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

    default public ObjectCharPair<K> value(char r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectCharPair<K> value(Character l) {
        return this.value(l.charValue());
    }

    public static <K> ObjectCharPair<K> of(K left, char right) {
        return new ObjectCharImmutablePair<K>(left, right);
    }

    public static <K> Comparator<ObjectCharPair<K>> lexComparator() {
        return (x, y) -> {
            int t = ((Comparable)x.left()).compareTo(y.left());
            if (t != 0) {
                return t;
            }
            return Character.compare(x.rightChar(), y.rightChar());
        };
    }
}

