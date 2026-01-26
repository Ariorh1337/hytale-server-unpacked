/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntBytePair;
import java.io.Serializable;
import java.util.Objects;

public class IntByteImmutablePair
implements IntBytePair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final int left;
    protected final byte right;

    public IntByteImmutablePair(int left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static IntByteImmutablePair of(int left, byte right) {
        return new IntByteImmutablePair(left, right);
    }

    @Override
    public int leftInt() {
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
        if (other instanceof IntBytePair) {
            return this.left == ((IntBytePair)other).leftInt() && this.right == ((IntBytePair)other).rightByte();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftInt() + "," + this.rightByte() + ">";
    }
}

