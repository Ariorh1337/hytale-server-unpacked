/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.chars.CharCharImmutableSortedPair;
import it.unimi.dsi.fastutil.chars.CharCharPair;
import java.io.Serializable;

public interface CharCharSortedPair
extends CharCharPair,
SortedPair<Character>,
Serializable {
    public static CharCharSortedPair of(char left, char right) {
        return CharCharImmutableSortedPair.of(left, right);
    }

    default public boolean contains(char e) {
        return e == this.leftChar() || e == this.rightChar();
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains(((Character)o).charValue());
    }
}

