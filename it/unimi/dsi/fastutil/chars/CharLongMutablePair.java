/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.chars.CharLongPair;
import java.io.Serializable;
import java.util.Objects;

public class CharLongMutablePair
implements CharLongPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected char left;
    protected long right;

    public CharLongMutablePair(char left, long right) {
        this.left = left;
        this.right = right;
    }

    public static CharLongMutablePair of(char left, long right) {
        return new CharLongMutablePair(left, right);
    }

    @Override
    public char leftChar() {
        return this.left;
    }

    @Override
    public CharLongMutablePair left(char l) {
        this.left = l;
        return this;
    }

    @Override
    public long rightLong() {
        return this.right;
    }

    @Override
    public CharLongMutablePair right(long r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharLongPair) {
            return this.left == ((CharLongPair)other).leftChar() && this.right == ((CharLongPair)other).rightLong();
        }
        if (other instanceof Pair) {
            return Objects.equals(Character.valueOf(this.left), ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.long2int(this.right);
    }

    public String toString() {
        return "<" + this.leftChar() + "," + this.rightLong() + ">";
    }
}

