/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharBooleanPair;
import java.io.Serializable;
import java.util.Objects;

public class CharBooleanMutablePair
implements CharBooleanPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected char left;
    protected boolean right;

    public CharBooleanMutablePair(char left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public static CharBooleanMutablePair of(char left, boolean right) {
        return new CharBooleanMutablePair(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public CharBooleanMutablePair left(char l) {
        this.left = l;
        return this;
    }

    @Override
    public boolean rightBoolean() {
        return this.right;
    }

    @Override
    public CharBooleanMutablePair right(boolean r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharBooleanPair) {
            return this.left == ((CharBooleanPair)other).leftChar() && this.right == ((CharBooleanPair)other).rightBoolean();
        }
        if (other instanceof Pair) {
            return Objects.equals(Character.valueOf(this.left), ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + (this.right ? 1231 : 1237);
    }

    public String toString() {
        return "<" + this.leftChar() + "," + this.rightBoolean() + ">";
    }
}

