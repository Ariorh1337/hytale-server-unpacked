/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharReferencePair;
import java.io.Serializable;
import java.util.Objects;

public class CharReferenceImmutablePair<V>
implements CharReferencePair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final char left;
    protected final V right;

    public CharReferenceImmutablePair(char left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> CharReferenceImmutablePair<V> of(char left, V right) {
        return new CharReferenceImmutablePair<V>(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public V right() {
        return this.right;
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

