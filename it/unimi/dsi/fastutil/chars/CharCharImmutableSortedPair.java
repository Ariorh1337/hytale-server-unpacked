/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SortedPair;
import it.unimi.dsi.fastutil.chars.CharCharImmutablePair;
import it.unimi.dsi.fastutil.chars.CharCharSortedPair;
import java.io.Serializable;
import java.util.Objects;

public class CharCharImmutableSortedPair
extends CharCharImmutablePair
implements CharCharSortedPair,
Serializable {
    private static final long serialVersionUID = 0L;

    private CharCharImmutableSortedPair(char left, char right) {
        super(left, right);
    }

    public static CharCharImmutableSortedPair of(char left, char right) {
        if (left <= right) {
            return new CharCharImmutableSortedPair(left, right);
        }
        return new CharCharImmutableSortedPair(right, left);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharCharSortedPair) {
            return this.left == ((CharCharSortedPair)other).leftChar() && this.right == ((CharCharSortedPair)other).rightChar();
        }
        if (other instanceof SortedPair) {
            return Objects.equals(Character.valueOf(this.left), ((SortedPair)other).left()) && Objects.equals(Character.valueOf(this.right), ((SortedPair)other).right());
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + this.leftChar() + "," + this.rightChar() + "}";
    }
}

