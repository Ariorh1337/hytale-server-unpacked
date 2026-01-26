/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanBytePair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanByteImmutablePair
implements BooleanBytePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final boolean left;
    protected final byte right;

    public BooleanByteImmutablePair(boolean left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static BooleanByteImmutablePair of(boolean left, byte right) {
        return new BooleanByteImmutablePair(left, right);
    }

    @Override
    public boolean leftBoolean() {
        return this.left;
    }

    @Override
    public byte rightByte() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof BooleanBytePair) {
            return this.left == ((BooleanBytePair)other).leftBoolean() && this.right == ((BooleanBytePair)other).rightByte();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left ? 1231 : 1237) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftBoolean() + "," + this.rightByte() + ">";
    }
}

