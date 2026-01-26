/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongCharPair;
import java.io.Serializable;
import java.util.Objects;

public class LongCharImmutablePair
implements LongCharPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final long left;
    protected final char right;

    public LongCharImmutablePair(long left, char right) {
        this.left = left;
        this.right = right;
    }

    public static LongCharImmutablePair of(long left, char right) {
        return new LongCharImmutablePair(left, right);
    }

    @Override
    public long leftLong() {
        return this.left;
    }

    @Override
    public char rightChar() {
        return this.right;
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

