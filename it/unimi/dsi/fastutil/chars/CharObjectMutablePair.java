/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharObjectPair;
import java.io.Serializable;
import java.util.Objects;

public class CharObjectMutablePair<V>
implements CharObjectPair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected char left;
    protected V right;

    public CharObjectMutablePair(char left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> CharObjectMutablePair<V> of(char left, V right) {
        return new CharObjectMutablePair<V>(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public CharObjectMutablePair<V> left(char l) {
        this.left = l;
        return this;
    }

    @Override
    public V right() {
        return this.right;
    }

    public CharObjectMutablePair<V> right(V r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharObjectPair) {
            return this.left == ((CharObjectPair)other).leftChar() && Objects.equals(this.right, ((CharObjectPair)other).right());
        }
        if (other instanceof Pair) {
            return Objects.equals(Character.valueOf(this.left), ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + (this.right == null ? 0 : this.right.hashCode());
    }

    public String toString() {
        return "<" + this.leftChar() + "," + this.right() + ">";
    }
}

