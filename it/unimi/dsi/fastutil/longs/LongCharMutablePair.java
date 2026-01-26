/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongCharPair;
import java.io.Serializable;
import java.util.Objects;

public class LongCharMutablePair
implements LongCharPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected long left;
    protected char right;

    public LongCharMutablePair(long left, char right) {
        this.left = left;
        this.right = right;
    }

    public static LongCharMutablePair of(long left, char right) {
        return new LongCharMutablePair(left, right);
    }

    @Override
    public long leftLong() {
        return this.left;
    }

    @Override
    public LongCharMutablePair left(long l) {
        this.left = l;
        return this;
    }

    @Override
    public char rightChar() {
        return this.right;
    }

    @Override
    public LongCharMutablePair right(char r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof LongCharPair) {
            return this.left == ((LongCharPair)other).leftLong() && this.right == ((LongCharPair)other).rightChar();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(Character.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.long2int(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftLong() + "," + this.rightChar() + ">";
    }
}

