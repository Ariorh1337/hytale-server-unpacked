/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortIntPair;
import java.io.Serializable;
import java.util.Objects;

public class ShortIntImmutablePair
implements ShortIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final short left;
    protected final int right;

    public ShortIntImmutablePair(short left, int right) {
        this.left = left;
        this.right = right;
    }

    public static ShortIntImmutablePair of(short left, int right) {
        return new ShortIntImmutablePair(left, right);
    }

    @Override
    public short leftShort() {
        return this.left;
    }

    @Override
    public int rightInt() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ShortIntPair) {
            return this.left == ((ShortIntPair)other).leftShort() && this.right == ((ShortIntPair)other).rightInt();
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
        return "<" + this.leftShort() + "," + this.rightInt() + ">";
    }
}

