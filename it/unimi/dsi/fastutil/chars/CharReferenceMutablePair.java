/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharReferencePair;
import java.io.Serializable;
import java.util.Objects;

public class CharReferenceMutablePair<V>
implements CharReferencePair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected char left;
    protected V right;

    public CharReferenceMutablePair(char left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> CharReferenceMutablePair<V> of(char left, V right) {
        return new CharReferenceMutablePair<V>(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public CharReferenceMutablePair<V> left(char l) {
        this.left = l;
        return this;
    }

    @Override
    public V right() {
        return this.right;
    }

    public CharReferenceMutablePair<V> right(V r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharReferencePair) {
            return this.left == ((CharReferencePair)other).leftChar() && this.right == ((CharReferencePair)other).right();
        }
        if (other instanceof Pair) {
            return Objects.equals(Character.valueOf(this.left), ((Pair)other).left()) && this.right == ((Pair)other).right();
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + (this.right == null ? 0 : System.identityHashCode(this.right));
    }

    public String toString() {
        return "<" + this.leftChar() + "," + this.right() + ">";
    }
}

